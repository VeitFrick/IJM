package at.aau.softwaredynamics.runner.io.db.entities;

import javax.persistence.*;

/**
 * Created by thomas on 09.02.2017.
 */
@Entity
@Table( name = "metrics" )
public class Metric {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    private long diffId;
    private long matcherId;
    private long projectid;

    private long treeGenTime;
    private long matchingTime;
    private long actionGenTime;
    private long classifyingTime;

    private int srcNodes;
    private int dstNodes;
    private int actions;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public long getTreeGenTime() {
        return treeGenTime;
    }

    public void setTreeGenTime(long treeGenTime) {
        this.treeGenTime = treeGenTime;
    }

    public long getMatchingTime() {
        return matchingTime;
    }

    public void setMatchingTime(long matchingTime) {
        this.matchingTime = matchingTime;
    }

    public long getActionGenTime() {
        return actionGenTime;
    }

    public void setActionGenTime(long actionGenTime) {
        this.actionGenTime = actionGenTime;
    }

    public long getClassifyingTime() {
        return classifyingTime;
    }

    public void setClassifyingTime(long classifyingTime) {
        this.classifyingTime = classifyingTime;
    }

    public int getSrcNodes() {
        return srcNodes;
    }

    public void setSrcNodes(int srcNodes) {
        this.srcNodes = srcNodes;
    }

    public int getDstNodes() {
        return dstNodes;
    }

    public void setDstNodes(int dstNodes) {
        this.dstNodes = dstNodes;
    }

    public int getActions() {
        return actions;
    }

    public void setActions(int actions) {
        this.actions = actions;
    }

    public long getDiffId() {
        return diffId;
    }

    public void setDiffId(long diffId) {
        this.diffId = diffId;
    }

    public long getMatcherId() {
        return matcherId;
    }

    public void setMatcherId(long matcherId) {
        this.matcherId = matcherId;
    }

    public long getProjectid() {
        return projectid;
    }

    public void setProjectid(long projectid) {
        this.projectid = projectid;
    }
}
