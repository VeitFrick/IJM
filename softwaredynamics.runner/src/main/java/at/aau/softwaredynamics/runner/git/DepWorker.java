package at.aau.softwaredynamics.runner.git;

import at.aau.softwaredynamics.dependency.Commit;
import at.aau.softwaredynamics.dependency.DependencyChanges;
import at.aau.softwaredynamics.dependency.ProgressWritable;
import at.aau.softwaredynamics.runner.output.OutputWriter;

import java.util.List;
import java.util.concurrent.BlockingQueue;

import static at.aau.softwaredynamics.runner.git.FullProjectAnalyzer.logToConsole;

/**
 * Takes a single snapshot of a project
 * creates its model and extracts all dependencies
 * Dependencies are written to the outputWriter
 */
public class DepWorker implements Runnable {

    private DepCoordinator coordinator;
    private OutputWriter outputWriter;
    protected BlockingQueue<ProgressWritable> doneQueue;

    public DepWorker(DepCoordinator coordinator, OutputWriter outputWriter) {
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

    /**
     * When an object implementing interface <code>Runnable</code> is used
     * to create a thread, starting the thread causes the object's
     * <code>run</code> method to be called in that separately executing
     * thread.
     * <p>
     * The general contract of the method <code>run</code> is that it may
     * take any action whatsoever.
     *
     * @see Thread#run()
     */
    @Override
    public void run() {
        while (coordinator.hasWork()) {
            try {
                Commit commit = this.coordinator.requestWork(Thread.currentThread().getName());
                if (commit == null) break; // race condition, someone took our work
                ProjectSnapshot snapshot = this.coordinator.getGenerator().generateSnapshot(commit.getRevCommit());
                if (snapshot == null) {
                    logToConsole("Cannot create dependencies the snapshot is null!", Thread.currentThread().getName(), "ERROR");
                    continue;
                }
                List<DependencyChanges> dependencyChanges = snapshot.calculateDependencyChangesBetweenSnapshots(snapshot); // please don't ask
                // this is a fake changes list, every dependency will be a change with type "UNCHANGED"

                // tell generator that we have used this commit
//                this.coordinator.getGenerator().useCommit(commit.getRevCommit().getId());

                if (outputWriter != null) {
                    outputWriter.writeDependencyInformation(dependencyChanges, commit.getRevCommit(), null, commit.getSubpath(), commit.getProjectName(), System.currentTimeMillis());
                }

                logToConsole("Extracted dependencies for " + commit.getRevCommit().getName(), Thread.currentThread().getName(), "WORKER");
                coordinator.incrementCommitsDone();
                doneQueue.add(commit);
            } catch (Exception e) { // Pokemon exception handling
                logToConsole(e.getMessage(), Thread.currentThread().getName(), "ERROR");
            }
        }
        logToConsole("No more new work, worker thread will terminate. (pushing poison pill)", Thread.currentThread().getName(), "INFO");
        doneQueue.add(new Commit(null, null, null));
    }
}
