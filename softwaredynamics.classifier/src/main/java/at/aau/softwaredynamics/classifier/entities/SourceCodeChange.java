package at.aau.softwaredynamics.classifier.entities;

import at.aau.softwaredynamics.classifier.types.ChangeType;
import at.aau.softwaredynamics.classifier.util.SpoonData;
import at.aau.softwaredynamics.gen.SpoonBuilder;
import at.aau.softwaredynamics.util.Meta;
import com.github.gumtreediff.actions.model.Action;
import com.github.gumtreediff.tree.ITree;
import spoon.reflect.code.CtStatement;
import spoon.reflect.declaration.CtClass;
import spoon.reflect.declaration.CtElement;
import spoon.reflect.declaration.CtExecutable;

import java.util.*;


/**
 * Created by veit on 16.11.2016.
 */
public class SourceCodeChange {

    public static HashMap<ITree, List<SourceCodeChange>> treeIDChangeMap = new HashMap<>();

    private Action action;
    private ChangeType changeType;

    private NodeInfo srcNodeInfo;
    private NodeInfo dstNodeInfo;

    private SourceCodeChange srcParentChange;
    private SourceCodeChange dstParentChange;

    //List<NodeDependency> dependencies;

    private CtExecutable containingMethodSrc;
    private CtStatement containingStatementSrc;
    private CtClass containingClassSrc;

    private CtExecutable containingMethodDst;
    private CtStatement containingStatementDst;
    private CtClass containingClassDst;

    public SourceCodeChange(Action action, NodeInfo srcNodeInfo, NodeInfo dstNodeInfo) {
        this.action = action;
        this.srcNodeInfo = srcNodeInfo;
        this.dstNodeInfo = dstNodeInfo;

        this.srcNodeInfo.setNodeChange(this);
        this.dstNodeInfo.setNodeChange(this);

        //dependencies = new ArrayList<>();

        this.changeType = new ChangeType(null, null, null);

        associateToNodeMap(this.srcNodeInfo.getNode(), this);
        associateToNodeMap(this.dstNodeInfo.getNode(), this);

    }

    private static void associateToNodeMap(ITree node, SourceCodeChange change) {
        if (node == null) return;
        List<SourceCodeChange> codeChanges;
        if (SourceCodeChange.treeIDChangeMap.get(node) != null)
            codeChanges = SourceCodeChange.treeIDChangeMap.get(node);
        else
            codeChanges = new ArrayList<>();

        if (!codeChanges.contains(change))
            codeChanges.add(change);
        SourceCodeChange.treeIDChangeMap.put(node, codeChanges);

    }

    public static String changeTypeDump(Collection<SourceCodeChange> changes) {
        String output = "";

        output += "Changes Size: " + changes.size();
        output += "\n";
        for (SourceCodeChange change : changes) {
            try {
                output += "ActionType: " + change.getAction().getName() + " ChangeType: " + change.getChangeType().getName() + " NodeID: " + change.getNode().getId() + " CtElement: " + ((CtElement) change.getNode().getMetadata(SpoonBuilder.SPOON_OBJECT)).getClass().getSimpleName() + " Hash: " + change.hashCode();
                output += "\n";
            }
            catch (NullPointerException np){
                System.err.print("changeTypeDump cought a " + np);
            }

        }
        return output;
    }

    public static void extendedInfoDump(Collection<SourceCodeChange> changes) {
        System.out.println('\n');
        System.out.println("Changes Size: " + changes.size());
        for (SourceCodeChange change : changes) {

            System.out.println("ActionType: " + change.getAction().getName() + " ChangeType: " + change.getChangeType().getName() + " NodeType: " + change.getNodeType() + " CtElement: " + ((CtElement) change.getNode().getMetadata(SpoonBuilder.SPOON_OBJECT)).getClass().getSimpleName() + " Hash: " + change.hashCode());
            System.out.println(change.getNode().getMetadata(SpoonBuilder.SPOON_OBJECT));
            if (change.getSrcParentChange() != null)
                System.out.println(" (Parent(src) " + change.getSrcParentChange().hashCode());
            if (change.getDstParentChange() != null)
                System.out.println(" (Parent(dst) " + change.getDstParentChange().hashCode());
            if (change.getContainingStatementSrc() != null)
                System.out.println("Cont. Statement (src):  " + change.getContainingStatementSrc().toString());
            if (change.getContainingMethodSrc() != null)
                System.out.println("Cont. Method (src):  " + change.getContainingMethodSrc().getSimpleName());
            if (change.getContainingClassSrc() != null)
                System.out.println("Cont. Class (src):  " + (change.getContainingClassSrc()).getSimpleName());
            if (change.getContainingStatementDst() != null)
                System.out.println("Cont. Statement (dst):  " + change.getContainingStatementDst().toString());
            if (change.getContainingMethodDst() != null)
                System.out.println("Cont. Method (dst):  " + change.getContainingMethodDst().getSimpleName());
            if (change.getContainingClassDst() != null)
                System.out.println("Cont. Class (dst):  " + change.getContainingClassDst().getSimpleName());


//            if (ITreeNodeHelper.isInMethod(change.getNode()))
//                System.out.print("   MethodName: " + ITreeNodeHelper.getMethodName(ITreeNodeHelper.getParentMethod(change.getNode())));
            System.out.print(" Src: " + change.getSrcInfo().getStartLineNumber() + "-" + change.getSrcInfo().getEndLineNumber());
            System.out.print(" Dst: " + change.getDstInfo().getStartLineNumber() + "-" + change.getDstInfo().getEndLineNumber());

            System.out.println('\n');
            System.out.println('\n');
        }
    }

    public CtExecutable getContainingMethodSrc() {
        return containingMethodSrc;
    }

    public void setContainingMethodSrc(CtExecutable containingMethodSrc) {
        this.containingMethodSrc = containingMethodSrc;
    }

    public CtStatement getContainingStatementSrc() {
        return containingStatementSrc;
    }

    public void setContainingStatementSrc(CtStatement containingStatementSrc) {
        this.containingStatementSrc = containingStatementSrc;
    }

    public CtClass getContainingClassSrc() {
        return containingClassSrc;
    }

    public void setContainingClassSrc(CtClass containingClassSrc) {
        this.containingClassSrc = containingClassSrc;
    }

    public CtExecutable getContainingMethodDst() {
        return containingMethodDst;
    }

    public void setContainingMethodDst(CtExecutable containingMethodDst) {
        this.containingMethodDst = containingMethodDst;
    }

    public CtStatement getContainingStatementDst() {
        return containingStatementDst;
    }

    public void setContainingStatementDst(CtStatement containingStatementDst) {
        this.containingStatementDst = containingStatementDst;
    }

    public CtClass getContainingClassDst() {
        return containingClassDst;
    }

    public void setContainingClassDst(CtClass containingClassDst) {
        this.containingClassDst = containingClassDst;
    }

    public boolean isUnclassified() {
        return changeType.isUnclassified();
    }

    public Action getAction() {
        return this.action;
    }

    public ChangeType getChangeType() {
        return changeType;
    }

    public void setChangeType(ChangeType changeType) {
        this.changeType = changeType;
    }

//    public String getActionTypeName() {
//        return this.action.getName();
//    }

    public ITree getNode() {
        return this.action.getNode();
    }

    public int getNodeType() {
        return this.action.getNode().getType();
    }

    public int getPosition() {
        return this.action.getNode().getPos();
    }

    public int getLength() {
        return this.action.getNode().getLength();
    }

    public NodeInfo getSrcInfo() {
        return this.srcNodeInfo;
    }

    public NodeInfo getDstInfo() {
        return this.dstNodeInfo;
    }

    public SourceCodeChange getSrcParentChange() {
        return srcParentChange;
    }

    public void setSrcParentChange(SourceCodeChange srcParentChange) {
        this.srcParentChange = srcParentChange;
    }

    public SourceCodeChange getDstParentChange() {
        return dstParentChange;
    }

    public void setDstParentChange(SourceCodeChange dstParentChange) {
        this.dstParentChange = dstParentChange;
    }

    public List<SourceCodeChange> getChildrenChanges() {
        return getChildrenChanges(null);
    }

    public Set<SourceCodeChange> getDirectChildrenChanges() {
        return getDirectChildrenChanges(null);
    }

    public Set<SourceCodeChange> getDirectChildrenChanges(ChangeType type) {

        List<ITree> nodes = this.extractDirectChildNodes(this.srcNodeInfo.getNode());
        nodes.addAll(this.extractDirectChildNodes(this.dstNodeInfo.getNode()));
        Set<SourceCodeChange> changes = new HashSet<>();
        for (ITree node : nodes) {
            if (treeIDChangeMap.containsKey(node)) {
                List<SourceCodeChange> nodeChanges = SourceCodeChange.treeIDChangeMap.get(node);
                for (SourceCodeChange nodeChange : nodeChanges) {
                    if ((type == null || type.equalChangeType(nodeChange.getChangeType())))
                        changes.add(nodeChange);
                }
            }

        }
        return changes;
    }

    public List<SourceCodeChange> getChildrenChanges(ChangeType type) {

        List<ITree> nodes = this.extractChildNodes(this.srcNodeInfo.getNode());
        nodes.addAll(this.extractChildNodes(this.dstNodeInfo.getNode()));
        List<SourceCodeChange> changes = new ArrayList<>();
        for (ITree node : nodes) {
            if (treeIDChangeMap.containsKey(node)) {
                List<SourceCodeChange> nodeChanges = SourceCodeChange.treeIDChangeMap.get(node);
                for (SourceCodeChange nodeChange : nodeChanges) {
                    if (type == null || type.equalChangeType(nodeChange.getChangeType()))
                        changes.add(nodeChange);
                }
            }
        }
        return changes;
    }

    public Set<SourceCodeChange> getParentChanges() {

        return getParentChanges(null);
    }

    public Set<SourceCodeChange> getParentChanges(ChangeType type) {

        List<ITree> nodes = this.extractParentNodes(this.srcNodeInfo.getNode());
        nodes.addAll(this.extractParentNodes(this.dstNodeInfo.getNode()));
        Set<SourceCodeChange> changes = new HashSet<>();
        for (ITree node : nodes) {
            if (treeIDChangeMap.containsKey(node)) {
                List<SourceCodeChange> nodeChanges = SourceCodeChange.treeIDChangeMap.get(node);
                for (SourceCodeChange nodeChange : nodeChanges) {
                    if (type == null || type.equalChangeType(nodeChange.getChangeType()))
                        changes.add(nodeChange);
                }
            }
        }
        return changes;
    }

    private List<ITree> extractChildNodes(ITree tree) {
        List<ITree> nodes = new ArrayList<>();
        if (tree == null || tree.getChildren() == null) return nodes;

        nodes.addAll(tree.getChildren());
        for (ITree node : tree.getChildren()) {
            nodes.addAll(this.extractChildNodes(node));
        }
        return nodes;
    }

    private List<ITree> extractDirectChildNodes(ITree tree) {
        List<ITree> nodes = new ArrayList<>();
        if (tree == null || tree.getChildren() == null) return nodes;
        nodes.addAll(tree.getChildren());
        return nodes;
    }

    private List<ITree> extractParentNodes(ITree tree) {
        List<ITree> nodes = new ArrayList<>();
        if (tree == null || tree.getChildren() == null) return nodes;

        nodes.addAll(tree.getParents());
        for (ITree node : tree.getParents()) {
            nodes.addAll(this.extractParentNodes(node));
        }
        return nodes;
    }

    @Override
    public String toString() {
        SourceCodeChange change = this;
        String outString = "";
        try {
            outString += "ActionType: " + change.getAction().getName() + " ChangeType: " + change.getChangeType().getName() + " NodeType: " + change.getNodeType() + " CtElement: " + ((CtElement) change.getNode().getMetadata(SpoonBuilder.SPOON_OBJECT)).getClass().getSimpleName() + " Hash: " + change.hashCode();
            outString += change.getNode().getMetadata(SpoonBuilder.SPOON_OBJECT).toString();
        } catch (Exception e) {

        }
        if (change.getSrcParentChange() != null)
            outString += " (Parent(src) " + change.getSrcParentChange().hashCode();
        if (change.getDstParentChange() != null)
            outString += " (Parent(dst) " + change.getDstParentChange().hashCode();
        if (change.getContainingStatementSrc() != null)
            outString += "Cont. Statement (src):  " + change.getContainingStatementSrc().toString();
        if (change.getContainingMethodSrc() != null)
            outString += "Cont. Method (src):  " + change.getContainingMethodSrc().getSimpleName();
        if (change.getContainingClassSrc() != null)
            outString += "Cont. Class (src):  " + (change.getContainingClassSrc()).getSimpleName();
        if (change.getContainingStatementDst() != null)
            outString += "Cont. Statement (dst):  " + change.getContainingStatementDst().toString();
        if (change.getContainingMethodDst() != null)
            outString += "Cont. Method (dst):  " + change.getContainingMethodDst().getSimpleName();
        if (change.getContainingClassDst() != null)
            outString += "Cont. Class (dst):  " + change.getContainingClassDst().getSimpleName();


//        if (ITreeNodeHelper.isInMethod(change.getNode()))
//            outString += "   MethodName: " + ITreeNodeHelper.getMethodName(ITreeNodeHelper.getParentMethod(change.getNode()));
        outString += " Src: " + change.getSrcInfo().getStartLineNumber() + "-" + change.getSrcInfo().getEndLineNumber();
        outString += " Dst: " + change.getDstInfo().getStartLineNumber() + "-" + change.getDstInfo().getEndLineNumber();

        outString += "\n";
        outString += "\n";
        return outString;
    }

    public String toStringCompact() {
        SourceCodeChange change = this;
        String outString = "";
        try {
            outString += "ActionType: " + change.getAction().getName() + " " +
                    "ChangeType: " + change.getChangeType().getName();
            //outString += change.getNode().getMetadata(SpoonBuilder.SPOON_OBJECT).toString();
        } catch (Exception e) {

        }
        return outString;
    }

    public String toStringSummary() {
        String s = this.getChangeType().toHumanReadable() + " (" + this.getNode().getShortLabel() + ")";
//        if(this.changeType.getClass().equals(MethodChange.class)){
//            s+= this.toString();
//        }
//        if(this.getContainingMethodDst()!=null)
//            s+= " in Method " + this.getContainingMethodDst().getSignature();
        return s;
    }

    public String toSimpleLabel() {
        String s = "";
        if ((this.getChangeType() != null)) {
            s += this.getChangeType().getName();
        } else {
            s += SpoonData.getSpoonElement(this.getNode()).getShortRepresentation();
        }
        s += " " + this.changeType.getNodeChangeHumanReadable();
//        if(this.getChangeType().getActionType().isMoveOrUpdate())
//        {
//            s += " to " + this.getDstInfo().getNode().getShortLabel();
//        }

        return s;
    }

    public String toOrderedString(int level, int maxlevel) {
        String s = "";

        if (!this.isUnclassified()) {
            for (int i = 0; i <= level; i++) {
                s += "    ";
            }
            s += this.toStringSummary() + "\n";
        }

        for (SourceCodeChange scc : getDirectChildrenChanges()) {
            if (maxlevel >= level + 1)
                s += scc.toOrderedString(level + 1, maxlevel);
        }

        return s;
    }

    public CodeChangeTree getSignificantChildrenChanges() {
        CodeChangeTree root = null;
        // case that this is already significant
        if (this.getChangeType().isSignificant()) {
            root = new CodeChangeTree(0, this);
            generateSignificantChildrenList(root, this);
            return root;
        }
        // if not find the first significant child
        for (SourceCodeChange change : getChildrenChanges()) {
            //this assumes changes in getChildrenChanges are ordered by level!
            if (change.getChangeType().isSignificant()) {
                root = new CodeChangeTree(0, change);
                generateSignificantChildrenList(root, change);
                break;
            }
        }

        return root;
    }

    private void generateSignificantChildrenList(CodeChangeTree parent, SourceCodeChange current) {
        for (SourceCodeChange scc : current.getDirectChildrenChanges()) {
            if (scc.getChangeType().isSignificant() && !parent.childrenContains(scc)) {
                CodeChangeTree child = parent.addChildren(scc);
                generateSignificantChildrenList(child, scc);
            } else {
                generateSignificantChildrenList(parent, scc);
            }

        }
    }

    @Override
    public int hashCode() {
        return Objects.hash(changeType.toString(), srcNodeInfo.getId(), dstNodeInfo.getId());
    }

    @Override
    public boolean equals(Object o) {
        if (o == this) return true;
        if (!(o instanceof SourceCodeChange)) {
            return false;
        }
        SourceCodeChange scc = (SourceCodeChange) o;
        return (this.changeType.toString().equals(scc.changeType.toString()) || scc.getAction() instanceof Meta) &&
                Objects.equals(this.dstNodeInfo.getNode(), scc.dstNodeInfo.getNode()) &&
                Objects.equals(this.srcNodeInfo.getNode(), scc.srcNodeInfo.getNode());
    }


    public String getCtElementName() {
        return getCtElement() != null ? getCtElement().getClass().getSimpleName() : "None";
    }

    public CtElement getCtElement() {
        return (CtElement) this.getNode().getMetadata(SpoonBuilder.SPOON_OBJECT);
    }
}

