package at.aau.softwaredynamics.runner.classification;

import java.io.File;

public class Project {
    private String gitRepoName;
    private String path;
    private String name;

    public Project(String fullPath) {
        this.path = fullPath.substring(0, fullPath.lastIndexOf(File.separatorChar));
        this.name = fullPath.substring(fullPath.lastIndexOf(File.separatorChar) + 1);
    }

    public Project(String path, String name) {
        this.path = path;
        this.name = name;
    }

    public Project(String containingPath, String gitRepoName, String name) {
        this.gitRepoName = gitRepoName;
        this.path = containingPath;
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getGitRepoName() {
        return gitRepoName;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public String getFullPath() {
        return getPath() + File.separatorChar + getName();
    }
}