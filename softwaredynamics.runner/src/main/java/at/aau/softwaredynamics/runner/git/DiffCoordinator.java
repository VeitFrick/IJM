package at.aau.softwaredynamics.runner.git;

import at.aau.softwaredynamics.runner.io.ProgressWritable;
import at.aau.softwaredynamics.runner.meta.CommitPair;
import org.eclipse.jgit.lib.ObjectId;

import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import static at.aau.softwaredynamics.runner.git.FullProjectAnalyzer.logToConsole;

/**
 * Coordinates DepDiffWorkers
 */
public class DiffCoordinator {
    private SnapshotGenerator generator;
    private Set<CommitPair> work;
    private Map<String, CommitPair> workMapping;
    private AtomicInteger pairsDone = new AtomicInteger(0);
    CoordinatorProgressRunner progressRunner;
    BlockingQueue<ProgressWritable> workDone;
    private long startingTime = System.currentTimeMillis();

    public DiffCoordinator(SnapshotGenerator generator, List<CommitPair> work, int numOfWorkers) {
        this.generator = generator;
        this.work = Collections.synchronizedSet(new HashSet<>(work));
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

    private static String sanitizeFilename(String inputName) {
        return inputName.replaceAll("[^a-zA-Z0-9-_\\.]", "_");
    }

    public BlockingQueue<ProgressWritable> getWorkDone() {
        return workDone;
    }

    public SnapshotGenerator getGenerator() {
        return generator;
    }

    /**
     * Gets the optimal CommitPair to diff next
     * based on generator cache. If no
     *
     * @param workerName Name of the worker
     * @return null if no work is available
     */
    public CommitPair requestWork(String workerName) {

        synchronized (this) {
            ObjectId lowestRemainingUsages = generator.getCachedCommitWithLowestRemainingUsages();

            Iterator<CommitPair> workIterator = work.iterator();
            while (workIterator.hasNext()) {
                CommitPair commitPair = workIterator.next();
                if (commitPair.containsCommit(lowestRemainingUsages)) {
                    work.remove(commitPair);
                    workMapping.put(workerName, commitPair);
                    return commitPair;
                }
            }
            // no work in cache fallback
            Optional<CommitPair> pairOptional = work.stream().findAny();
            if (pairOptional.isPresent()) {
                work.remove(pairOptional.get());
                workMapping.put(workerName, pairOptional.get());
                return pairOptional.get();
            }
        }

        return null;
    }

    public void applyProgress(String pathToProgressFile) throws IOException {
        progressRunner.applyProgressToCommitPairs(new File(pathToProgressFile), this.work);
    }

    boolean hasWork() {
        return !work.isEmpty();
    }

    public void incrementDiffsDone(){
        int diffsDone = this.pairsDone.incrementAndGet();
        double avgTimePerDiff = (System.currentTimeMillis() - startingTime) / diffsDone;
        long millis = (long) (avgTimePerDiff * work.size());
        String remainingTime = String.format("%d min, %d sec",
                TimeUnit.MILLISECONDS.toMinutes(millis),
                TimeUnit.MILLISECONDS.toSeconds(millis) -
                        TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(millis))
        );
        logToConsole(work.size() + " pairs to do -- Remaining est. time " + remainingTime, "DiffCoordinator", "PROGRESS");
    }
}
