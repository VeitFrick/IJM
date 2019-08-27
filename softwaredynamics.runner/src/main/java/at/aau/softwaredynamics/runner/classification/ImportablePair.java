package at.aau.softwaredynamics.runner.classification;

public class ImportablePair {
    private int id;
    private String baseUrl;
    private String commit;
    private String parentCommit;

    private String srcFileName;
    private String dstFileName;

    public ImportablePair(int id, String baseUrl, String commit, String parentCommit, String srcFileName, String dstFileName) {
        this.id = id;
        this.baseUrl = baseUrl;
        this.commit = commit;
        this.parentCommit = parentCommit;
        this.srcFileName = srcFileName;
        this.dstFileName = dstFileName;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getBaseUrl() {
        return baseUrl;
    }

    public void setBaseUrl(String baseUrl) {
        this.baseUrl = baseUrl;
    }

    public String getCommit() {
        return commit;
    }

    public void setCommit(String commit) {
        this.commit = commit;
    }

    public String getParentCommit() {
        return parentCommit;
    }

    public void setParentCommit(String parentCommit) {
        this.parentCommit = parentCommit;
    }

    public String getSrcFileName() {
        return srcFileName;
    }

    public void setSrcFileName(String srcFileName) {
        this.srcFileName = srcFileName;
    }

    public String getDstFileName() {
        return dstFileName;
    }

    public void setDstFileName(String dstFileName) {
        this.dstFileName = dstFileName;
    }
}
