package at.aau.softwaredynamics.runner.git;

import at.aau.softwaredynamics.dependency.CommitPair;
import at.aau.softwaredynamics.dependency.DependencyChanges;
import at.aau.softwaredynamics.dependency.ProgressWritable;
import at.aau.softwaredynamics.runner.output.OutputWriter;

import java.util.List;
import java.util.concurrent.BlockingQueue;

import static at.aau.softwaredynamics.runner.git.FullProjectAnalyzer.logToConsole;

public class DepDiffWorker implements Runnable {

    private DiffCoordinator coordinator;
    private OutputWriter outputWriter;
    protected BlockingQueue<ProgressWritable> doneQueue;

    public DepDiffWorker(DiffCoordinator coordinator, OutputWriter outputWriter) {
        this.coordinator = coordinator;
        this.outputWriter = outputWriter;
        this.doneQueue = coordinator.getWorkDone();
    }

    public OutputWriter getOutputWriter() {
        return outputWriter;
    }

    public void setOutputWriter(OutputWriter outputWriter) {
        this.outputWriter = outputWriter;
    }

    @Override
    public void run() {
        while (coordinator.hasWork()) {
            try {
                CommitPair commitPair = this.coordinator.requestWork(Thread.currentThread().getName());
                if (commitPair == null) break; // race condition, someone took our work
                ProjectSnapshot srcSnapshot = this.coordinator.getGenerator().getSnapshot(commitPair.getSrcCommit());
                ProjectSnapshot dstSnapshot = this.coordinator.getGenerator().getSnapshot(commitPair.getDstCommit());
                if (srcSnapshot == null || dstSnapshot == null) {
                    logToConsole("Cannot create changes if one of the snapshots is null! Working on: " + commitPair, Thread.currentThread().getName(), "ERROR");
                    continue;
                }
                List<DependencyChanges> dependencyChanges = dstSnapshot.calculateDependencyChangesBetweenSnapshots(srcSnapshot);

                // tell generator that we have used these commits
                this.coordinator.getGenerator().useCommit(commitPair.getSrcCommit().getId());
                this.coordinator.getGenerator().useCommit(commitPair.getDstCommit().getId());

                if (outputWriter != null) {
                    outputWriter.writeDependencyInformation(dependencyChanges, commitPair.getSrcCommit(), commitPair.getDstCommit(), commitPair.getSubpath(), commitPair.getProjectName(), System.currentTimeMillis());
                }

                logToConsole("Produced changes for pair " + commitPair, Thread.currentThread().getName(), "WORKER");
                coordinator.incrementDiffsDone();
                doneQueue.add(commitPair);
            } catch (Exception e) { // Pokémon exception handling
                logToConsole(e.getMessage(), Thread.currentThread().getName(), "ERROR");
            }
        }
        logToConsole("No more new work, thread will terminate. (pushing poison pill)", Thread.currentThread().getName(), "WORKER");
        doneQueue.add(new CommitPair(null, null,null,null));
    }
}
