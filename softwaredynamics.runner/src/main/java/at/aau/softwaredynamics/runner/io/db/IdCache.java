package at.aau.softwaredynamics.runner.io.db;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by thomas on 07.02.2017.
 */
public class IdCache {
    private Map<String, Long> commits = new ConcurrentHashMap<>();
    private Map<Integer, Long> diffs = new ConcurrentHashMap<>();

    public void addCommit(String commit, long commitId) {
        if (commits.containsKey(commit))
            return;

        commits.put(commit, commitId);
    }

    public long getCommitId(String commit) {
        return commits.getOrDefault(commit, (long) 0);
    }

    public void addDiff(long commitId, String srcFile, String dstFile, long diffId) {
        int diffHash = getDiffHash(commitId, srcFile, dstFile);

        if (diffs.containsKey(diffHash))
            return;

        diffs.put(diffHash, diffId);
    }

    public long getDiffId(long commitId, String srcFile, String dstFile) {
        return diffs.getOrDefault(getDiffHash(commitId, srcFile, dstFile), (long) 0);
    }

    private int getDiffHash(long commitId, String srcFile, String dstFile) {
        return String.join(";", String.valueOf(commitId), srcFile, dstFile).hashCode();
    }
}
