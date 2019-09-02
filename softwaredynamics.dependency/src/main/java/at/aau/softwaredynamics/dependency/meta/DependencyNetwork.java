package at.aau.softwaredynamics.dependency.meta;

import java.util.List;

public class DependencyNetwork {

    String commit;
    List<String> parentCommits;
    Integer commitTime;
    List<Node> nodes;
    List<DependencyLink> links;

    public DependencyNetwork() {
    }

    public List<Node> getNodes() {
        return nodes;
    }

    public void setNodes(List<Node> nodes) {
        this.nodes = nodes;
    }

    public List<DependencyLink> getLinks() {
        return links;
    }

    public void setLinks(List<DependencyLink> links) {
        this.links = links;
    }

    public String getCommit() {
        return commit;
    }

    public void setCommit(String commit) {
        this.commit = commit;
    }

    public List<String> getParentCommits() {
        return parentCommits;
    }

    public void setParentCommits(List<String> parentCommits) {
        this.parentCommits = parentCommits;
    }

    public Integer getCommitTime() {
        return commitTime;
    }

    public void setCommitTime(Integer commitTime) {
        this.commitTime = commitTime;
    }
}
