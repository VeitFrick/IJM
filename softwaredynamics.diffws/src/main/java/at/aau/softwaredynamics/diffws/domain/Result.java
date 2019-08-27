package at.aau.softwaredynamics.diffws.domain;

import at.aau.softwaredynamics.gen.NodeType;

public class Result {
    private String actionType;

    private Integer srcId;

    //relative positions
    private Integer srcStartLine;
    private Integer srcStartLineOffset;
    private Integer srcEndLine;
    private Integer srcEndLineOffset;

    private Integer dstId;
    //relative positions
    private Integer dstStartLine;
    private Integer dstStartLineOffset;
    private Integer dstEndLine;
    private Integer dstEndLineOffset;

    //metadata
    private Metadata metadata;

    public Result()
    {

    }

    public Result(String actionType, Integer srcId, Integer dstId) {
        this.actionType = actionType;
        this.srcId = srcId;
        this.dstId = dstId;
    }

    public String getActionType() {
        return actionType;
    }

    public void setActionType(String actionType) {
        this.actionType = actionType;
    }

    public Integer getSrcId() {
        return srcId;
    }

    public void setSrcId(Integer srcId) {
        this.srcId = srcId;
    }

    public Integer getDstId() {
        return dstId;
    }

    public void setDstId(Integer dstId) {
        this.dstId = dstId;
    }

    public Integer getSrcStartLine() {
        return srcStartLine;
    }

    public void setSrcStartLine(Integer srcStartLine) {
        this.srcStartLine = srcStartLine;
    }

    public Integer getSrcStartLineOffset() {
        return srcStartLineOffset;
    }

    public void setSrcStartLineOffset(Integer srcStartLineOffset) {
        this.srcStartLineOffset = srcStartLineOffset;
    }

    public Integer getSrcEndLine() {
        return srcEndLine;
    }

    public void setSrcEndLine(Integer srcEndLine) {
        this.srcEndLine = srcEndLine;
    }

    public Integer getSrcEndLineOffset() {
        return srcEndLineOffset;
    }

    public void setSrcEndLineOffset(Integer srcEndLineOffset) {
        this.srcEndLineOffset = srcEndLineOffset;
    }

    public Integer getDstStartLine() {
        return dstStartLine;
    }

    public void setDstStartLine(Integer dstStartLine) {
        this.dstStartLine = dstStartLine;
    }

    public Integer getDstStartLineOffset() {
        return dstStartLineOffset;
    }

    public void setDstStartLineOffset(Integer dstStartLineOffset) {
        this.dstStartLineOffset = dstStartLineOffset;
    }

    public Integer getDstEndLine() {
        return dstEndLine;
    }

    public void setDstEndLine(Integer dstEndLine) {
        this.dstEndLine = dstEndLine;
    }

    public Integer getDstEndLineOffset() {
        return dstEndLineOffset;
    }

    public void setDstEndLineOffset(Integer dstEndLineOffset) {
        this.dstEndLineOffset = dstEndLineOffset;
    }

    public void setDstRelativePosition(int startLine, int startOffset, int endLine, int endOffset) {
        this.dstStartLine = startLine;
        this.dstStartLineOffset = startOffset;
        this.dstEndLine = endLine;
        this.dstEndLineOffset = endOffset;
    }

    public void setSrcRelativePosition(int startLine, int startOffset, int endLine, int endOffset) {
        this.srcStartLine = startLine;
        this.srcStartLineOffset = startOffset;
        this.srcEndLine = endLine;
        this.srcEndLineOffset = endOffset;
    }

    public Metadata getMetadata() {
        return metadata;
    }

    public void setMetadata(Metadata metadata) {
        this.metadata = metadata;
    }
}
