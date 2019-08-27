package at.aau.softwaredynamics.runner.meta;

import at.aau.softwaredynamics.runner.io.ProgressWritable;
import org.eclipse.jgit.lib.ObjectId;
import org.eclipse.jgit.revwalk.RevCommit;

public class Commit implements ProgressWritable {
    private RevCommit revCommit;
    private boolean isDone;
    private String projectName;
    private String subpath;


    public Commit(RevCommit revCommit, String subpath, String projectName) {
        this.revCommit = revCommit;
        this.subpath = subpath;
        this.projectName = projectName;
    }

    public RevCommit getRevCommit() {
        return revCommit;
    }

    public boolean isDone() {
        return isDone;
    }

    public void setDone(boolean done) {
        isDone = done;
    }

    public boolean hasId(ObjectId commit) {
        return commit.toObjectId().equals(commit);
    }

    @Override
    public String toString() {
        return "Commit { " + revCommit.getName() + " }";
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
        return revCommit == null;
    }

    @Override
    public String getProgressString() {
        return this.getRevCommit().getName() + "\n";
    }
}
