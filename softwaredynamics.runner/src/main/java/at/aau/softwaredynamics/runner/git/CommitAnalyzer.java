package at.aau.softwaredynamics.runner.git;

import at.aau.softwaredynamics.classifier.AbstractJavaChangeClassifier;
import at.aau.softwaredynamics.classifier.entities.ClassifierMetrics;
import at.aau.softwaredynamics.classifier.entities.FileChangeSummary;
import at.aau.softwaredynamics.runner.io.ChangeWriter;
import at.aau.softwaredynamics.runner.util.ClassifierFactory;
import at.aau.softwaredynamics.runner.util.GitHelper;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.eclipse.jgit.diff.DiffEntry;
import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.revwalk.RevCommit;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.*;

public class CommitAnalyzer implements Runnable {
    private static final int TIMEOUT_ERROR_CODE = -1;
    private static final int MEMORY_ERROR_CODE = -2;
    private static final int OTHER_ERROR_CODE = -3;

    private static final Logger logger = LogManager.getLogger(CommitAnalyzer.class);

    private final Repository repository;
    private final RevCommit commit;
    private final ClassifierFactory factory;
    private final int diffTimeout;
    private ChangeWriter writer;
    private DiffFilter filter;

    public CommitAnalyzer(
            Repository repository,
            RevCommit commit,
            ClassifierFactory factory,
            int diffTimeout,
            ChangeWriter writer) {
        this.repository = repository;
        this.commit = commit;
        this.factory = factory;
        this.diffTimeout = diffTimeout;
        this.writer = writer;
    }
    
    public void setRevisionFilter(DiffFilter filter) {
        this.filter = filter;
    }
    
    @Override
    public void run() {
        RevCommit[] parents = getParents();

        // skip snapshots if not in filter
        if (filter != null && !filter.containsCommit(commit.getName())) {
            return;
        }

        if (parents.length != 1)
        {
            logger.error(String.format("More than 1 parent (not supported!) commit: %1$s", commit.getName()));
            return;
        }

        List<DiffEntry> diffs = GitHelper.getDiffs(repository, commit, parents[0]);

        for (DiffEntry diff : diffs) {
            writer.write(getFileChangeSummary(diff));
        }
    }

    private FileChangeSummary getFileChangeSummary(DiffEntry diff) {
        ExecutorService executorService = Executors.newSingleThreadExecutor();
        FutureTask<FileChangeSummary> task = new FutureTask(() -> processDiff(diff));
        executorService.submit(task);
        try {
            return task.get(this.diffTimeout, TimeUnit.SECONDS);
        } catch (TimeoutException e) {
            FileChangeSummary retVal = createDummySummary(diff, TIMEOUT_ERROR_CODE, this.diffTimeout * 1000);
            logger.info("Diff could not be processed within timeout period", e);
            return retVal;
        } catch (InterruptedException | ExecutionException e){
            logger.error(
                    String.format("Error processing diff commit: %1$s, src: %2$s, dst: %3$s",
                            commit.getName(),
                            getSrcFileName(diff),
                            getDstFileName(diff)),
                    e);
            return createDummySummary(diff, OTHER_ERROR_CODE, this.diffTimeout * 1000);
        } finally {
            executorService.shutdown();
        }
    }

    private FileChangeSummary createDummySummary(DiffEntry diff, int errorCode, long totalTime) {
        FileChangeSummary summary = new FileChangeSummary(commit.getName(), getParentCommitString(), getSrcFileName(diff), getDstFileName(diff));
        ClassifierMetrics metrics = new ClassifierMetrics();
        metrics.setMatchingTime(errorCode);
        metrics.setTreeGenerationTime(errorCode);
        metrics.setActionGenerationTime(errorCode);
        metrics.setClassifyingTime(errorCode);
        metrics.setTotalTime(totalTime);
        summary.setMetrics(metrics);
        return summary;
    }

    private FileChangeSummary processDiff(DiffEntry diff) {
        String commitString = commit.getName();
        String parentCommitString = getParentCommitString();
        String srcFileName = getSrcFileName(diff);
        String dstFileName = getDstFileName(diff);

        FileChangeSummary summary =  new FileChangeSummary(
                commitString,
                parentCommitString,
                srcFileName,
                dstFileName);

        long startTime = System.currentTimeMillis();

        try {
            String srcString = GitHelper.getFileContent(diff.getOldId(), repository);
            String dstString = GitHelper.getFileContent(diff.getNewId(), repository);

            AbstractJavaChangeClassifier classifier = factory.createClassifier();

            try {
                classifier.classify(srcString, dstString);
                summary.setChanges(classifier.getCodeChanges());
                summary.setMetrics(classifier.getMetrics());

                return summary;
            } catch (OutOfMemoryError e) {
                logger.error(
                        String.format("Out of memory: %1$s, src: %2$s, dst: %3$s",
                                commitString,
                                srcFileName,
                                dstFileName),
                        e);
                return createDummySummary(diff, MEMORY_ERROR_CODE, System.currentTimeMillis() - startTime);
            } catch (Throwable t) {
                logger.error(
                        String.format("Error processing diff commit: %1$s, src: %2$s, dst: %3$s",
                                commitString,
                                srcFileName,
                                dstFileName),
                        t);
                return createDummySummary(diff, OTHER_ERROR_CODE, System.currentTimeMillis() - startTime);
            }
        } catch (IOException e) {
            // occurs if file content cannot be loaded
            logger.error("Cannot load file content", e);
            return createDummySummary(diff, OTHER_ERROR_CODE, System.currentTimeMillis() - startTime);
        }
    }

    private String getParentCommitString() {
        RevCommit parent = getParents()[0];
        return parent != null ? parent.getName() : "";
    }

    private RevCommit[] getParents() {
        return commit.getParents().length > 0
                ? commit.getParents()           // diff with parents
                : new RevCommit[] { null };     // diff with null
    }

    private String getSrcFileName(DiffEntry diff) {
        return diff.getOldPath().equals(DiffEntry.DEV_NULL) ? "" : diff.getOldPath();
    }

    private String getDstFileName(DiffEntry diff) {
        return diff.getNewPath().equals(DiffEntry.DEV_NULL) ? "" : diff.getNewPath();
    }
}
