package at.aau.softwaredynamics.classifier.entities;

import at.aau.softwaredynamics.classifier.actions.ActionType;
import spoon.reflect.declaration.CtModifiable;
import spoon.reflect.declaration.ModifierKind;

import java.util.*;

public class CodeChangeTree {

    private Integer level;
    private SourceCodeChange sourceCodeChange;
    private CodeChangeTree parent;
    private List<CodeChangeTree> children;
    private List<ActionType> actions;
    private boolean visible;

    public CodeChangeTree(Integer level, SourceCodeChange sourceCodeChange) {
        this.level = level;
        this.sourceCodeChange = sourceCodeChange;
        this.children = new ArrayList<>();
        this.actions = new ArrayList<>();
        this.actions.add(sourceCodeChange.getChangeType().getActionType());
        this.actions.removeIf((ActionType::isNoAction));
        this.visible = true;
    }

    public Integer getLevel() {
        return level;
    }

    public void setLevel(Integer level) {
        this.level = level;
    }

    public SourceCodeChange getSourceCodeChange() {
        return sourceCodeChange;
    }

    public void setSourceCodeChange(SourceCodeChange sourceCodeChange) {
        this.sourceCodeChange = sourceCodeChange;
    }

    public CodeChangeTree getParent() {
        return parent;
    }

    public void setParent(CodeChangeTree parent) {
        this.parent = parent;
    }

    public void setVisible(boolean visible) {
        this.visible = visible;
    }

    public void addChildren(CodeChangeTree child) {
        child.setParent(this);
//        if (!child.isVisible()) {
//            return;
//        }
        this.children.add(child);
    }

    public CodeChangeTree addChildren(SourceCodeChange child) {
        CodeChangeTree cc = new CodeChangeTree(this.level+1, child);
        this.addChildren(cc);
        return cc;
    }

    public void delete() {
        this.getParent().removeChild(this);
    }

    public void removeChild(CodeChangeTree child) {
        List<CodeChangeTree> newChildren = new ArrayList<>();
        for (CodeChangeTree changeTree : this.getChildren()) {
            if (!changeTree.equals(child)) newChildren.add(changeTree);
        }
        this.setChildren(newChildren);
    }

    public List<CodeChangeTree> getChildren() {
        return children;
    }

    boolean childrenContains(SourceCodeChange scc) {
        for (CodeChangeTree child : children) {
            if (child.sourceCodeChange.equals(scc)) return true;
        }
        return false;
    }

    public String toTreeString() {
        return  toTreeString(null);
    }

    public String toTreeString(List<Integer> parents) {
        if(parents == null) parents = new ArrayList<>();
        String s = "";

        s += this.toSimpleLabel();
//        s += this.sourceCodeChange.toSimpleLabel();
        for (int i = 0; i < children.size(); i++) {
            s+= "\n";
            for (int j = 0; j < level + 1; j++) {
                if(j == level) {
                    if (i == children.size()-1) s+= "└";
                    else s += "├";
                    s += "── ";
                }
                else if (parents.contains(j)) {
                    s += "│   ";
                }
                else s+= "    ";
            }
            ArrayList<Integer> newParents = new ArrayList<>();
            newParents.addAll(parents);
            if(i != children.size()-1) newParents.add(level);
            s += children.get(i).toTreeString(newParents);
        }
        return s;
    }

    public String toSimpleLabel() {

        StringBuilder s = new StringBuilder();
        if (!isVisible()) s.append("[INVIS]");
        if (actions == null) return s.toString();

        // All actions
        for (int i = 0; i < actions.size(); i++) {
            ActionType action = actions.get(i);
            if (!action.isNoAction()) s.append(action.toHumanReadable());
            if (i < actions.size() - 1) s.append(", ");
            else s.append(" ");
        }
        // followed by the source code change
        s.append(this.sourceCodeChange.toSimpleLabel());
        return s.toString();
    }

    @Override
    public String toString() {
        return "CodeChangeTree{" +
                "level=" + level +
                ", sourceCodeChange=" + sourceCodeChange.toStringCompact() +
                '}';
    }

    public void expandSignificantNode(int amount) {
        expandInsignificantChanges(this, amount);
        for (CodeChangeTree child : children) {
            if(child.getSourceCodeChange().getChangeType().isSignificant()) child.expandSignificantNode(amount);
        }
    }

    public static void expandInsignificantChanges(CodeChangeTree tree, int amount) {
        if (amount == 0) return;

        for (SourceCodeChange change : tree.getSourceCodeChange().getDirectChildrenChanges()) {
            if(!change.getChangeType().isSignificant()) {
                CodeChangeTree node = new CodeChangeTree(tree.level + 1, change);
                CodeChangeTree.expandInsignificantChanges(node, amount - 1);
                tree.addChildren(node);
            }
        }
    }

    public List<ActionType> getActions() {
        return actions;
    }

    public void setActions(List<ActionType> actions) {
        this.actions = actions;
    }

    private void setChildren(List<CodeChangeTree> children) {
        children.forEach(codeChangeTree -> setParent(this));
//        children.removeIf(codeChangeTree -> !isVisible());
        this.children = children;
    }

    public void mergeEqualNodes() {
        mergeEqualNodesRec(this);
    }

    private void mergeEqualNodesRec(CodeChangeTree cct) {
        if (cct.getChildren() == null) return;
        List<CodeChangeTree> tree = cct.getChildren();
        for (int i = 0; i < tree.size(); i++) {
            for (int j = 0; j < tree.size(); j++) {
                if (i != j) {
                    CodeChangeTree outer = tree.get(i);
                    CodeChangeTree inner = tree.get(j);
                    if (inner == null || outer == null) continue;
                    if (Objects.equals(inner.getSourceCodeChange().getSrcInfo().getNode(), outer.getSourceCodeChange().getSrcInfo().getNode()) &&
                            Objects.equals(inner.getSourceCodeChange().getDstInfo().getNode(), outer.getSourceCodeChange().getDstInfo().getNode())) {
                        //TODO maybe transfer more information?
                        // transfer all actions from inner to outer nodes
                        outer.getActions().addAll(inner.getActions());
                        // remove invalid actions
                        outer.getActions().removeIf(ActionType::isNoAction);
                        // delete inner node
                        outer.getParent().removeChild(inner);
                        tree.set(i, null);
                    }
                }
            }
        }
        for (CodeChangeTree changeTree : tree) {
            if (changeTree != null) mergeEqualNodesRec(changeTree);
        }

    }

    /**
     * With this as the root node, recursively removes all children that contain any
     * of the modifiers given
     *
     * @param removedModifiers - list of modifiers to filter
     */
    public void filterModifiers(List<ModifierKind> removedModifiers) {
        filterModifiersRec(this, removedModifiers);
    }

    private void filterModifiersRec(CodeChangeTree cct, List<ModifierKind> removedModifiers) {
        if (cct.getChildren() == null) return;

        for (CodeChangeTree child : cct.getChildren()) {
            CtModifiable srcElement;
            CtModifiable dstElement;
            try {
                srcElement = (CtModifiable) child.getSourceCodeChange().getSrcInfo().getSpoonObject();
                dstElement = (CtModifiable) child.getSourceCodeChange().getDstInfo().getSpoonObject();

            } catch (Exception e) {
//                System.out.println("Object is not modifiable.");
                continue;
            }

            Set<ModifierKind> modifiers = new HashSet<>();
            if (srcElement == null && dstElement == null) continue;
            else if (srcElement != null && dstElement != null) {
                modifiers.addAll(srcElement.getModifiers());
                modifiers.retainAll(dstElement.getModifiers());
            } else if (srcElement != null) modifiers.addAll(srcElement.getModifiers());
            else if (dstElement != null) modifiers.addAll(dstElement.getModifiers());

            //comment

            if (Collections.disjoint(modifiers, removedModifiers)) {
                filterModifiersRec(child, removedModifiers);
            } else {
                child.delete();
            }
        }
    }

    /**
     * Defines that a Change is only visible to the viewer if it is relevant
     * Criteria for hiding: Same action as the parent and NO move or update
     *
     * @return if a subtree should be added to the final tree
     */
    public boolean isVisible() {
        // either the change or the node is not visible => return false
        return getSourceCodeChange().getChangeType().isVisible() && visible; //this is enough to not show the node
//
//        if (this.sourceCodeChange.getChangeType().getActionType().isMove() ||
//                this.sourceCodeChange.getChangeType().getActionType().isUpdate() ||
//                this.sourceCodeChange.getChangeType().isSignificant())
//            return true;


//        try {
//            if (this.getParent().getSourceCodeChange().getChangeType().getActionTypeString().equals(this.getSourceCodeChange().getChangeType().getActionTypeString()))
//                //TODO return false;
//                return false;
//        } catch (Exception e) {
//            return true;
//        }
    }

    /**
     * Checks if this node is significant or differs from their parents actions
     *
     * @return true if
     */
    public boolean isTopmostActionNode() {
        return sourceCodeChange.getChangeType().isSignificant() ||
                !this.getActions().equals(parent.getActions());
    }

    /**
     * Checks if this tree node has only children of the same action.
     * For example: A insert node that only has inserted subnodes
     *
     * @return true if ALL children are of the same action
     * false if NOT, or if this node has more than a single action
     */
    public boolean hasOnlyChildrenOfSameAction() {
        if (actions.size() != 1) return false;

        return childrenAreOfAction(this, this.actions.get(0));
    }

    private boolean childrenAreOfAction(CodeChangeTree node, ActionType action) {

        for (CodeChangeTree child : node.getChildren()) {
            for (ActionType type : child.getActions()) {
                if (!action.equals(type)) {
                    return false;
                }
            }
        }
        for (CodeChangeTree child : node.getChildren()) {
            if (childrenAreOfAction(child, action) == false) return false;
        }

        return true;
    }

    /**
     * Hides all children of a tree node
     */
    public void hideAllChildren(boolean hideSignificant) {
        for (CodeChangeTree child : this.getChildren()) {
            if (!child.getSourceCodeChange().getChangeType().isSignificant() || hideSignificant) {
                child.setVisible(false);
            }
            child.hideAllChildren(hideSignificant);
        }
    }

    /**
     * Hides all child nodes that have the same action type as this node as long as they
     * are not significant.
     * This way completely inserted/deleted nodes will not have any children.
     */
    public void hideChildrenOfSameActions() {
        hideChildrenOfSameActions(this);
    }

    private void hideChildrenOfSameActions(CodeChangeTree tree) {
        if (tree.hasOnlyChildrenOfSameAction()) {
            tree.hideAllChildren(false);
        } else {
            for (CodeChangeTree child : tree.getChildren()) {
                hideChildrenOfSameActions(child);
            }
        }
    }

    /**
     * Deletes all invisble nodes from the tree
     */
    public void pruneInvisible() {
        pruneInvisible(this);
    }

    private void pruneInvisible(CodeChangeTree tree) {
        for (CodeChangeTree child : tree.getChildren()) {
            if (!child.isVisible()) {
                child.delete();
            } else {
                pruneInvisible(child);
            }
        }
    }
}