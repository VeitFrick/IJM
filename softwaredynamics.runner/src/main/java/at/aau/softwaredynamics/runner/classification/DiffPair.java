package at.aau.softwaredynamics.runner.classification;

public class DiffPair {
    private String srcFileName;
    private String dstFileName;

    private String srcContent;
    private String dstContent;

    private String gitStatus;

    public DiffPair(String srcFileName, String srcContent, String dstContent) {
        this(srcFileName, srcFileName, srcContent, dstContent);
    }

    public DiffPair(String srcFileName, String dstFileName, String srcContent, String dstContent) {
        this.srcFileName = srcFileName;
        this.dstFileName = dstFileName;
        this.srcContent = srcContent;
        this.dstContent = dstContent;
        this.gitStatus = "none";
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

    public String getSrcContent() {
        return srcContent;
    }

    public void setSrcContent(String srcContent) {
        this.srcContent = srcContent;
    }

    public String getDstContent() {
        return dstContent;
    }

    public void setDstContent(String dstContent) {
        this.dstContent = dstContent;
    }

    public String getGitStatus() {
        return gitStatus;
    }

    public void setGitStatus(String gitStatus) {
        this.gitStatus = gitStatus;
    }
}
