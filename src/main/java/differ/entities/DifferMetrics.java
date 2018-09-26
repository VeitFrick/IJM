package differ.entities;

/**
 * Created by thomas on 09.02.2017.
 */
public class DifferMetrics {
    private long treeGenerationTime;
    private long matchingTime;
    private long actionGenerationTime;
    private long classifyingTime;
    private int numSrcNodes;
    private int numDstNodes;
    private int numActions;
    private long totalTime;

    public long getTreeGenerationTime() {
        return treeGenerationTime;
    }

    public void setTreeGenerationTime(long treeGenerationTime) {
        this.treeGenerationTime = treeGenerationTime;
    }

    public long getMatchingTime() {
        return matchingTime;
    }

    public void setMatchingTime(long matchingTime) {
        this.matchingTime = matchingTime;
    }

    public long getActionGenerationTime() {
        return actionGenerationTime;
    }

    public void setActionGenerationTime(long actionGenerationTime) {
        this.actionGenerationTime = actionGenerationTime;
    }

    public long getClassifyingTime() {
        return classifyingTime;
    }

    public void setClassifyingTime(long classifyingTime) {
        this.classifyingTime = classifyingTime;
    }

    public long getTotalTime() { return this.totalTime; }

    public void setTotalTime(long totalTime) {
        this.totalTime = totalTime;
    }

    public int getNumSrcNodes() {
        return numSrcNodes;
    }

    public void setNumSrcNodes(int numSrcNodes) {
        this.numSrcNodes = numSrcNodes;
    }

    public int getNumDstNodes() {
        return numDstNodes;
    }

    public void setNumDstNodes(int numDstNodes) {
        this.numDstNodes = numDstNodes;
    }

    public int getNumActions() {
        return numActions;
    }

    public void setNumActions(int numActions) {
        this.numActions = numActions;
    }
}
