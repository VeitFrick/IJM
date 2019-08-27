package at.aau.softwaredynamics.classifier.types;

import at.aau.softwaredynamics.classifier.actions.*;
import at.aau.softwaredynamics.classifier.util.SpoonData;
import at.aau.softwaredynamics.gen.SpoonBuilder;
import com.github.gumtreediff.matchers.MappingStore;
import com.github.gumtreediff.tree.ITree;
import spoon.reflect.declaration.CtElement;
import spoon.support.reflect.declaration.CtNamedElementImpl;

public class ChangeType {
    protected ITree srcNode;
    protected ITree dstNode;
    protected CtElement srcElement;
    protected CtElement dstElement;
    protected ActionType actionType;
    protected MappingStore mappings;

    public ChangeType() {
        this.actionType = new ActionType();
    }

    public ChangeType(ITree node, MappingStore mappings) {
        this.srcNode = node;
        this.dstNode = node;
        this.srcElement = (CtElement) node.getMetadata(SpoonBuilder.SPOON_OBJECT);
        this.dstElement = this.srcElement;
        this.mappings = mappings;
        this.actionType = new ActionType();
    }

    public ChangeType(ITree srcNode, ITree dstNode, MappingStore mappings) {
        this.srcNode = srcNode;
        this.dstNode = dstNode;
        if(srcNode!=null)
            this.srcElement = (CtElement) srcNode.getMetadata(SpoonBuilder.SPOON_OBJECT);
        if(dstNode!=null)
            this.dstElement = (CtElement) dstNode.getMetadata(SpoonBuilder.SPOON_OBJECT);
        this.mappings = mappings;
        this.actionType = new ActionType();
    }

    public ActionType getActionType() {
        return actionType;
    }

    public void insertClassify() {
        this.actionType = new InsertAction();
    }

    public void deleteClassify() {
        this.actionType = new DeleteAction();
    }

    public void updateClassify() {
        //RENAME ACTION
        if(srcElement instanceof CtNamedElementImpl) {
            if (!((CtNamedElementImpl) srcElement).getSimpleName().equals(((CtNamedElementImpl) dstElement).getSimpleName())) {
                this.actionType = new RenameAction();
            }
        }
        //GENERIC UPDATE
        else{
            this.actionType = new UpdateAction();
        }


    }

    public void moveClassify() {
        //ORDERING CHANGE ACTION
        if (mappings.getDst(srcNode.getParent()) != null && mappings.getDst(srcNode.getParent()).equals(dstNode.getParent())) {
                this.actionType = new OrderingChangeAction();
        }
        //PARENT CHANGE
        else{
            this.actionType = new ParentChangeAction();
        }

    }

    public boolean isUnclassified() {
        return this.getClass().getSimpleName().equals("ChangeType");
    }

    public String getChangeTypeString() {
        return this.getClass().getSimpleName();
    }
    public String getActionTypeString() {
        return this.actionType.toString();
    }

    public boolean equalChangeType(ChangeType obj) {
        return this.getChangeTypeString().equals(obj.getChangeTypeString());
    }

    public boolean equalActionType(ChangeType obj) {
        return this.getActionTypeString().equals(obj.getActionTypeString());
    }

    @Override
    public String toString() {
        String prefix = "";
        if(this.getClass().getSimpleName().equals("ChangeType")) {
            prefix = "UNCLASSIFIED";
        } else {
            prefix = this.getClass().getSimpleName();
        }
        if(!this.actionType.isOfExactType(ActionType.NO_ACTION)) return prefix + "_" + this.actionType.toString();
        else return prefix + "_NO_ACTION";
    }

    public String toHumanReadable() {
        if(actionType.isNoAction()) return getName();

        String action = this.actionType.toHumanReadable();
        return action + " " + getName();

    }

    private String splitCamelCase(String x) {
        return String.join(" ", x.split("(?<!(^|[A-Z]))(?=[A-Z])|(?<!^)(?=[A-Z][a-z])"));
    }

    /**
     * Generates the Name of the Change itself that is human readable
     * @return a String of the Change Type
     * @example Class for a Class change
     */
    public String getName() {
        if (this.isUnclassified()) return "UNCLASSIFIED";

        String change = this.getClass().getSimpleName();
        change.replaceAll("Change$", "");
        change = change.substring(0, change.length() - 6);
        return splitCamelCase(change);
    }

    /**
     * Gets the human readable String representation of the Node that this Change
     * affects directly.
     * @return
     */
    public String getNodeChangeHumanReadable() {
        if (getActionType().isUpdate())
            return getNodeLabel(srcNode) + " to " + getNodeLabel(dstNode);
        if (srcNode != null) return getNodeLabel(srcNode);
        if (dstNode != null) return getNodeLabel(dstNode);
        return "NO NODE ATTACHED";
    }

    protected String getNodeLabel(ITree node) {
        if (node != null) return "MISSING getNodeLabel!" + SpoonData.getSpoonElement(node).getShortRepresentation();
        else return "NODE IS NULL";
    }


    public boolean isSignificant() {
        return false;
    }

    /**
     * Specifies if a change is visible when classifying a commit
     *
     * @return true if visible
     */
    public boolean isVisible() {
        return true;
    }
}
