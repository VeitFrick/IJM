package at.aau.softwaredynamics.dependency;

import org.eclipse.jgit.lib.ObjectId;
import org.eclipse.jgit.revwalk.RevCommit;

public class CommitPair implements ProgressWritable {
    private RevCommit srcCommit;
    private RevCommit dstCommit;
    private boolean isDone;
    private String projectName;
    private String subpath;


    public CommitPair(RevCommit srcCommit, RevCommit dstCommit, String subpath, String projectName) {
        this.srcCommit = srcCommit;
        this.dstCommit = dstCommit;
        this.subpath = subpath;
        this.projectName = projectName;
    }

    public RevCommit getSrcCommit() {
        return srcCommit;
    }

    public RevCommit getDstCommit() {
        return dstCommit;
    }

    public boolean isDone() {
        return isDone;
    }

    public void setDone(boolean done) {
        isDone = done;
    }

    public boolean containsCommit(ObjectId commit) {
        return srcCommit.toObjectId().equals(commit)
                || dstCommit.toObjectId().equals(commit);
    }

    @Override
    public String toString() {
        return "CommitPair { src: " + srcCommit.getName() + ", dst: " + dstCommit.getName() + " }";
    }

    public String getProjectName() {
        return projectName;
    }

    public void setProjectName(String projectName) {
        this.projectName = projectName;
    }

    public String getSubpath() {
        return subpath;
    }

    public void setSubpath(String subpath) {
        this.subpath = subpath;
    }

    public boolean isNull() {
        return srcCommit == null && dstCommit == null;
    }

    @Override
    public String getProgressString() {
        return this.getSrcCommit().getName() + "|" + this.getDstCommit().getName() + "\n";
    }
}
