package at.aau.softwaredynamics.util;

import com.github.gumtreediff.actions.model.Action;
import com.github.gumtreediff.tree.ITree;

public class Meta extends Action {

    private final Boolean hasSrcNode;
    private final Boolean hasDstNode;

    public Meta(ITree node, Boolean hasSrcNode, Boolean hasDstNode) {
        super(node);
        this.hasSrcNode = hasSrcNode;
        this.hasDstNode = hasDstNode;
    }

    public String getName() {
        return "MET";
    }

    public String toString() {
        return this.getName();
    }

    public Boolean hasSrcNode() {
        return hasSrcNode;
    }

    public Boolean hasDstNode() {
        return hasDstNode;
    }
}
