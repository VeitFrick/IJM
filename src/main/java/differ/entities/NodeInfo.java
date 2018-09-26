package differ.entities;

import com.github.gumtreediff.tree.ITree;
import differ.util.ITreeNodeHelper;
import differ.util.LineNumberRange;

/**
 * Created by thomas on 19.12.2016.
 */
public class NodeInfo {

    private ITree node;
    private int startLineNumber;
    private int startLineOffset;
    private int endLineNumber;
    private int endLineOffset;

    public NodeInfo(ITree node, LineNumberRange lineNumbers) {
        this.node = node;
        this.startLineNumber = lineNumbers.getStartLine();
        this.endLineNumber = lineNumbers.getEndLine();

        this.startLineOffset = lineNumbers.getStartOffset();
        this.endLineOffset = lineNumbers.getEndOffset();
    }

    public ITree getNode() {
        return this.node;
    }

    public int getStartLineNumber() {
        return this.startLineNumber;
    }

    public int getEndLineNumber() {
        return this.endLineNumber;
    }

    public int getStartOffset() {
        return this.startLineOffset;
    }

    public int getEndOffset() {
        return this.endLineOffset;
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

    public String getContainingClassName() {
        if (this.node == null)
            return "";
        return ITreeNodeHelper.getClassName(ITreeNodeHelper.getContainingType(this.getNode()));
    }

    public String getContainingMethodName() {
        if (this.node == null)
            return "";
        return ITreeNodeHelper.getMethodName(ITreeNodeHelper.getContainingMethod(this.getNode()));
    }

    static NodeInfo getDummyInfo() {
        return new NodeInfo( null,  new LineNumberRange());
    }
}
