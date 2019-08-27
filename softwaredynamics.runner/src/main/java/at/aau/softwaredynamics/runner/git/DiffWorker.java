package at.aau.softwaredynamics.runner.git;

import at.aau.softwaredynamics.classifier.entities.SourceCodeChange;
import at.aau.softwaredynamics.runner.io.ProgressWritable;
import at.aau.softwaredynamics.runner.meta.CommitPair;
import at.aau.softwaredynamics.runner.output.OutputWriter;

import java.util.List;
import java.util.Map;
import java.util.concurrent.BlockingQueue;

import static at.aau.softwaredynamics.runner.git.FullProjectAnalyzer.logToConsole;

/**
 * Takes two snapshots of a project
 * creates its model and extracts all differences
 * Differences are written to the outputWriter
 */
public class DiffWorker implements Runnable {

    private DiffCoordinator coordinator;
    private OutputWriter outputWriter;
    protected BlockingQueue<ProgressWritable> doneQueue;

    public DiffWorker(DiffCoordinator coordinator, OutputWriter outputWriter) {
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
                    logToConsole("Cannot create changes if one of the snapshots is null!", Thread.currentThread().getName(), "ERROR");
                    continue;
                }
                Map<String, List<SourceCodeChange>> fileToChangesMap = dstSnapshot.calculateSourceCodeChangesBetweenSnapshots(srcSnapshot);

                // tell generator that we have used these commits
                this.coordinator.getGenerator().useCommit(commitPair.getSrcCommit().getId());
                this.coordinator.getGenerator().useCommit(commitPair.getDstCommit().getId());

                if (outputWriter != null) {
                    outputWriter.writeChangeInformation(fileToChangesMap, commitPair.getSrcCommit(), commitPair.getDstCommit(), commitPair.getSubpath(), commitPair.getProjectName(), System.currentTimeMillis());
                }

                logToConsole("Produced changes for pair " + commitPair, Thread.currentThread().getName(), "WORKER");
                coordinator.incrementDiffsDone();
                doneQueue.add(commitPair);
            } catch (Exception e) { // Pokémon exception handling
                logToConsole("An error ccured in DiffWorker\n" + e.toString() + "\n"+e.getStackTrace(), Thread.currentThread().getName(), "ERROR");
            }
        }
        logToConsole("No more new work, thread will terminate. (pushing poison pill)", Thread.currentThread().getName(), "INFO");
        doneQueue.add(new CommitPair(null, null, null, null));
    }
}
