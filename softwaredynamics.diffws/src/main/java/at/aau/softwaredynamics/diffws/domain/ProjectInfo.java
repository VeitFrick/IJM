package at.aau.softwaredynamics.diffws.domain;

import java.util.Collection;

public class ProjectInfo {
    String path;
    Collection<String> modules;
    Collection<String> commitHashes;

    public ProjectInfo(String path) {
        this.path = path;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public Collection<String> getModules() {
        return modules;
    }

    public void setModules(Collection<String> modules) {
        this.modules = modules;
    }

    public Collection<String> getCommitHashes() {
        return commitHashes;
    }

    public void setCommitHashes(Collection<String> commitHashes) {
        this.commitHashes = commitHashes;
    }
}
