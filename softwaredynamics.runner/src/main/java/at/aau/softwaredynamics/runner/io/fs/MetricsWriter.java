package at.aau.softwaredynamics.runner.io.fs;

import at.aau.softwaredynamics.classifier.entities.ClassifierMetrics;
import at.aau.softwaredynamics.classifier.entities.FileChangeSummary;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;

/**
 * Created by thomas on 30.07.2017.
 */
public class MetricsWriter extends AbstractCsvWriter {
    private static final Logger logger = LogManager.getLogger(MetricsWriter.class);

    public MetricsWriter(String filePath, String fileName) throws IOException {
        super(filePath, fileName);
    }

    @Override
    public void write(FileChangeSummary change) {
        ClassifierMetrics metrics = change.getMetrics();
        try {
            this.writeLine(new String[] {
                    change.getCommit(),
                    change.getSrcFileName(),
                    change.getDstFileName(),
                    String.valueOf(metrics.getTreeGenerationTime()),
                    String.valueOf(metrics.getMatchingTime()),
                    String.valueOf(metrics.getActionGenerationTime()),
                    String.valueOf(metrics.getClassifyingTime()),
                    String.valueOf(metrics.getTotalTime())
            });
        } catch (IOException e) {
            logger.error(e);
        }
    }
    @Override
    protected String[] getHeader() {
        return new String[] { "Commit", "SrcFile", "DstFile", "TreeGenTime", "MatchingTime", "ActionGenTime", "ClassifyingTime", "TotalTime"};
    }
}
