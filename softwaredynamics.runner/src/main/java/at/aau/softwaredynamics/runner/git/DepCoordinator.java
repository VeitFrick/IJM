package at.aau.softwaredynamics.runner.git;

import at.aau.softwaredynamics.dependency.Commit;
import at.aau.softwaredynamics.dependency.ProgressWritable;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ConcurrentLinkedDeque;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Coordinates DepWorkers
 */
public class DepCoordinator {
    private SnapshotGenerator generator;
    private ConcurrentLinkedDeque<Commit> work;
    private Map<String, Commit> workMapping;
    private AtomicInteger commitsDone = new AtomicInteger(0);
    CoordinatorProgressRunner progressRunner;
    BlockingQueue<ProgressWritable> workDone;
    private long startingTime = System.currentTimeMillis();

    public DepCoordinator(SnapshotGenerator generator, List<Commit> work, int numOfWorkers) {
        this.generator = generator;
        this.work = new ConcurrentLinkedDeque<>(work);
        this.workMapping = new HashMap<>();
        this.workDone = new LinkedBlockingQueue<>();
        try {
            this.progressRunner = new CoordinatorProgressRunner(workDone, sanitizeFilename(generator.getProjectName()) + "_" + sanitizeFilename(generator.getSubPath()), numOfWorkers);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Needs to be run after
     * a) initializing an object of this class to start fresh
     * b) calling {@link this.applyProgress} to start with progress
     */
    public void startCoordinator() {
        new Thread(progressRunner).start();
    }

    public static String sanitizeFilename(String inputName) {
        return inputName.replaceAll("[^a-zA-Z0-9-_\\.]", "_");
    }

    public BlockingQueue<ProgressWritable> getWorkDone() {
        return workDone;
    }

    public SnapshotGenerator getGenerator() {
        return generator;
    }

    /**
     * Gets the optimal Commit to diff next
     * based on generator cache. If no
     *
     * @param workerName Name of the worker
     * @return null if no work is available
     */
    public Commit requestWork(String workerName) {
        Commit commit = work.pop();
        workMapping.put(workerName, commit);
        return commit;
    }

    public void applyProgress(String pathToProgressFile) throws IOException {
        progressRunner.applyProgressToCommits(new File(pathToProgressFile), this.work);
    }

    boolean hasWork() {
        return !work.isEmpty();
    }

    public void incrementCommitsDone() {
        int currentCommitsDone = this.commitsDone.incrementAndGet();
        double avgTimePerCommit = (System.currentTimeMillis() - startingTime) / currentCommitsDone;
        long millis = (long) (avgTimePerCommit * work.size());
        String remainingTime = String.format("%d min, %d sec",
                TimeUnit.MILLISECONDS.toMinutes(millis),
                TimeUnit.MILLISECONDS.toSeconds(millis) -
                        TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(millis))
        );
        System.out.println("[PROGRESS]  " + work.size() + " commits to do -- Remaining est. time " + remainingTime);
    }
}
