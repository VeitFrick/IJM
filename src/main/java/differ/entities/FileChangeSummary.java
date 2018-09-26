package differ.entities;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by thomas on 12.12.2016.
 */
public class FileChangeSummary {
    private String commit;
    private String parentCommit;
    private String srcFileName;
    private String dstFileName;
    private List<SourceCodeChange> changes = new ArrayList<>();
    private DifferMetrics metrics;

    public FileChangeSummary(String commit, String parentCommit, String srcFileName, String dstFileName) {
        this.commit = commit;
        this.parentCommit = parentCommit;
        this.srcFileName = srcFileName;
        this.dstFileName = dstFileName;
    }

    public String getSrcFileName() {
        return this.srcFileName;
    }

    public String getDstFileName() {
        return this.dstFileName;
    }

    public List<SourceCodeChange> getChanges() {
        return changes;
    }
    public String getCommit() {
        return commit;
    }

    public String getParentCommit() {
        return parentCommit;
    }

    public void setChanges(List<SourceCodeChange> changes) {
        this.changes = changes;
    }

    public DifferMetrics getMetrics() {
        return metrics;
    }

    public void setMetrics(DifferMetrics metrics) {
        this.metrics = metrics;
    }
}
