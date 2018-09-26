package runner.git;

/**
 * Created by thomas on 21.03.2017.
 */
public class DiffFilterEntry {
    private final String commit;
    private final String srcFile;
    private final String dstFile;

    public DiffFilterEntry(String commit, String srcFile, String dstFile) {

        this.commit = commit;
        this.srcFile = srcFile;
        this.dstFile = dstFile;
    }


    public String getCommit() {
        return commit;
    }

    public String getSrcFile() {
        return srcFile;
    }

    public String getDstFile() {
        return dstFile;
    }
}
