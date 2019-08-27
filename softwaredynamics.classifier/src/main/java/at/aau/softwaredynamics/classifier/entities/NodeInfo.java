package at.aau.softwaredynamics.classifier.entities;

import at.aau.softwaredynamics.classifier.util.LineNumberRange;
import com.github.gumtreediff.tree.ITree;
import spoon.reflect.declaration.CtElement;

/**
 * Created by thomas on 19.12.2016.
 */
public class NodeInfo {

    private ITree node;
    LineNumberRange lineNumberRange;

    private transient SourceCodeChange nodeChange;

    public NodeInfo(ITree node, LineNumberRange lineNumbers) {
        this.node = node;
        this.lineNumberRange = lineNumbers;
    }

    public ITree getNode() {
        return this.node;
    }

    public int getStartLineNumber() {
        return this.lineNumberRange.getStartLine();
    }

    public int getEndLineNumber() {
        return this.lineNumberRange.getEndLine();
    }

    public int getStartOffset() {
        return this.lineNumberRange.getStartOffset();
    }

    public int getEndOffset() {
        return this.lineNumberRange.getEndOffset();
    }

    public String getLabel() {
        if (this.node == null)
            return "";

        return this.node.getLabel() == null ? "" : this.node.getLabel();
    }

    public int getId() {
        return this.node == null ? 0 : this.node.getId();
    }

    public int getParentId() {
        if (this.node == null || this.node.getParent() == null)
            return 0;

        return this.node.getParent().getId();
    }

    public int getNodeType() {
        if (this.node == null)
            return 0;

        return this.node.getType();
    }

    public int getPosition() {
        if (this.node == null)
            return 0;

        return this.node.getPos();
    }

    public int getLength() {
        if (this.node == null)
            return 0;

        return this.node.getLength();
    }

    public CtElement getSpoonObject() {
        if (this.node == null)
            return null;
        return (CtElement) this.node.getMetadata("spoon_object");
    }

    public LineNumberRange getLineNumberRange() {
        return lineNumberRange;
    }

    public void setLineNumberRange(LineNumberRange lineNumberRange) {
        this.lineNumberRange = lineNumberRange;
    }

    public SourceCodeChange getNodeChange() {
        return nodeChange;
    }

    public void setNodeChange(SourceCodeChange nodeChange) {
        this.nodeChange = nodeChange;
    }

    static NodeInfo getDummyInfo() {
        return new NodeInfo( null,  new LineNumberRange());
    }
}
