package at.aau.softwaredynamics.runner.git;

import at.aau.softwaredynamics.classifier.AbstractJavaChangeClassifier;
import at.aau.softwaredynamics.classifier.entities.ClassifierMetrics;
import at.aau.softwaredynamics.classifier.entities.FileChangeSummary;
import at.aau.softwaredynamics.runner.io.ChangeWriter;
import at.aau.softwaredynamics.runner.util.ClassifierFactory;
import org.apache.commons.io.FileUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.*;

/**
 * Creates a normal diff between two Files
 * and writes the output into a {@link ChangeWriter}
 */
public class FilePairAnalyzer implements Runnable {

    private static final int TIMEOUT_ERROR_CODE = -1;
    private static final int MEMORY_ERROR_CODE = -2;
    private static final int OTHER_ERROR_CODE = -3;
    private static final String NO_COMMIT = "NO_COMMIT";

    private static final Logger logger = LogManager.getLogger(FilePairAnalyzer.class);

    private File src;
    private File dst;
    private final ClassifierFactory factory;
    private final int diffTimeout;
    private ChangeWriter writer;

    public FilePairAnalyzer(
            File src,
            File dst,
            ClassifierFactory factory,
            int diffTimeout,
            ChangeWriter writer) {
        this.src = src;
        this.dst = dst;
        this.factory = factory;
        this.diffTimeout = diffTimeout;
        this.writer = writer;
    }

    @Override
    public void run() {
        writer.write(getFileChangeSummary(src, dst));
    }

    private FileChangeSummary getFileChangeSummary(File src, File dst) {
        ExecutorService executorService = Executors.newSingleThreadExecutor();
        FutureTask<FileChangeSummary> task = new FutureTask(() -> processFiles(src, dst));
        executorService.submit(task);
        try {
            return task.get(this.diffTimeout, TimeUnit.SECONDS);
        } catch (TimeoutException e) {
            FileChangeSummary retVal = createDummySummary(src, dst, TIMEOUT_ERROR_CODE, this.diffTimeout * 1000);
            logger.info("Diff could not be processed within timeout period", e);
            return retVal;
        } catch (InterruptedException | ExecutionException e) {
            logger.error(
                    String.format("Error processing diff: src: %1$s, dst: %2$s",
                            src.getAbsolutePath(),
                            dst.getAbsolutePath()),
                    e);
            return createDummySummary(src, dst, OTHER_ERROR_CODE, this.diffTimeout * 1000);
        } finally {
            executorService.shutdown();
        }
    }

    private FileChangeSummary createDummySummary(File src, File dst, int errorCode, long totalTime) {
        FileChangeSummary summary = new FileChangeSummary(NO_COMMIT, NO_COMMIT, src.getName(), dst.getName());
        ClassifierMetrics metrics = new ClassifierMetrics();
        metrics.setMatchingTime(errorCode);
        metrics.setTreeGenerationTime(errorCode);
        metrics.setActionGenerationTime(errorCode);
        metrics.setClassifyingTime(errorCode);
        metrics.setTotalTime(totalTime);
        summary.setMetrics(metrics);
        return summary;
    }

    private FileChangeSummary processFiles(File src, File dst) {
        String srcFileName = src.getName();
        String dstFileName = dst.getName();

        FileChangeSummary summary = new FileChangeSummary(
                NO_COMMIT,
                NO_COMMIT,
                srcFileName,
                dstFileName);

        long startTime = System.currentTimeMillis();

        try {
            String srcString = FileUtils.readFileToString(src, StandardCharsets.UTF_8);
            String dstString = FileUtils.readFileToString(dst, StandardCharsets.UTF_8);

            AbstractJavaChangeClassifier classifier = factory.createClassifier();

            try {
                classifier.classify(srcString, dstString);
                summary.setChanges(classifier.getCodeChanges());
                summary.setMetrics(classifier.getMetrics());

                return summary;
            } catch (OutOfMemoryError e) {
                logger.error(
                        String.format("Out of memory: src: %1$s, dst: %2$s",
                                srcFileName,
                                dstFileName),
                        e);
                return createDummySummary(src, dst, MEMORY_ERROR_CODE, System.currentTimeMillis() - startTime);
            } catch (Throwable t) {
                logger.error(
                        String.format("Error processing diff: src: %1$s, dst: %2$s",
                                srcFileName,
                                dstFileName),
                        t);
                return createDummySummary(src, dst, OTHER_ERROR_CODE, System.currentTimeMillis() - startTime);
            }
        } catch (IOException e) {
            // occurs if file content cannot be loaded
            logger.error("Cannot load file content", e);
            return createDummySummary(src, dst, OTHER_ERROR_CODE, System.currentTimeMillis() - startTime);
        }
    }
}
