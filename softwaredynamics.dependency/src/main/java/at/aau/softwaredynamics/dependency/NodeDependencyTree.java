package at.aau.softwaredynamics.dependency;

import at.aau.softwaredynamics.gen.SpoonBuilder;
import at.aau.softwaredynamics.util.SpoonType;
import com.github.gumtreediff.tree.ITree;
import org.apache.commons.lang3.StringUtils;
import spoon.reflect.declaration.CtElement;
import spoon.reflect.declaration.CtNamedElement;
import spoon.reflect.declaration.CtType;

import java.util.*;

/**
 * A Tree structure that hold all NodeDependencies of a {@link ITree} node and all children.
 */
public class NodeDependencyTree {

    private CtElement ctElement;
    private ITree iTree;
    private boolean isInSrc;
    private ArrayList<NodeDependency> nodeDependencies;
    private Map<String, Integer> weightedClassDependencies;

    private ArrayList<NodeDependencyTree> children;
    private NodeDependencyTree parent;
    private NodeDependencyTree matchedToNodeDependencyTree;


    /**
     * Constructs a root node
     *
     * @param iTree   the node
     * @param isInSrc true if this is a source tree iTree
     */
    public NodeDependencyTree(ITree iTree, boolean isInSrc) {
        this(iTree, null, isInSrc);
    }

    /**
     * Constructs a new NodeDependency subtree
     *
     * @param iTree   the node
     * @param parent  the parent tree
     * @param isInSrc true if this is a source tree iTree
     */
    public NodeDependencyTree(ITree iTree, NodeDependencyTree parent, boolean isInSrc) {
        this.parent = parent;
        this.ctElement = (CtElement) iTree.getMetadata(SpoonBuilder.SPOON_OBJECT);;
        this.iTree = iTree;
        this.nodeDependencies = new ArrayList<>();
        this.weightedClassDependencies = new HashMap<>();
        this.children = new ArrayList<>();
        this.isInSrc = isInSrc;
    }

    public CtElement getCtElement() {
        return ctElement;
    }

    public void setCtElement(CtElement ctElement) {
        this.ctElement = ctElement;
    }

    public ITree getiTree() {
        return iTree;
    }

    public void setiTree(ITree iTree) {
        this.iTree = iTree;
    }

    public ArrayList<NodeDependencyTree> getChildren() {
        return children;
    }

    public void setChildren(ArrayList<NodeDependencyTree> children) {
        this.children = children;
    }

    public NodeDependencyTree getParent() {
        return parent;
    }

    public void setParent(NodeDependencyTree parent) {
        this.parent = parent;
    }

    public ArrayList<NodeDependency> getDependencies() {
        return nodeDependencies;
    }

    public void setDependencies(ArrayList<NodeDependency> dependencies) {
        this.nodeDependencies = dependencies;
    }

    public boolean isMatched() {
        return this.iTree.isMatched();
    }

    public NodeDependencyTree getMatchedToNodeDependencyTree() {
        return matchedToNodeDependencyTree;
    }

    public void setMatchedToNodeDependencyTree(NodeDependencyTree matchedToNodeDependencyTree) {
        this.matchedToNodeDependencyTree = matchedToNodeDependencyTree;
    }

    public void addToNodeDependencies(NodeDependency dependency) {
        this.nodeDependencies.add(dependency);
        this.weightedClassDependencies.computeIfPresent(dependency.getDependency().getDependentOnClass(), (key, strength) -> strength + 1);
        this.weightedClassDependencies.putIfAbsent(dependency.getDependency().getDependentOnClass(), 1);
    }

    public void addChild(NodeDependencyTree visitedStructNodeDep) {
        this.children.add(visitedStructNodeDep);
    }

    public boolean isRoot() {
        return this.getParent() == null;
    }

    public Set<String> getClassDependencies() {
        Set<String> classDeps = new HashSet<>();
        this.getDependencies().forEach(d -> classDeps.add(d.getDependency().getDependentOnClass()));
        return classDeps;
    }

    public String toString(){

        return "<" + StringUtils.substringAfterLast(ctElement.getClass().toString(), ".").replace("Ct", "").replace("Impl", "") + "> "
                + ((CtNamedElement) this.ctElement).getSimpleName();
    }

    public int getDepth() {
        NodeDependencyTree temp = this;
        int d = 0;

        while (temp.getParent() != null) {
            temp = temp.getParent();
            d++;
        }
        return d;
    }

    public boolean isEmpty() {
        return this.nodeDependencies.isEmpty();
    }

    public List<ClassDependencyStrengthChange> calculateClassDependencyStrengthChange() {
        List<ClassDependencyStrengthChange> strengthChanges = new ArrayList<>();
        this.weightedClassDependencies.forEach((className, strength) -> {
            if (this.matchedToNodeDependencyTree == null ||
                    this.matchedToNodeDependencyTree.weightedClassDependencies.get(className) == null) {
                ClassDependencyStrengthChange classStrengthChange = new ClassDependencyStrengthChange(className, strength, strength);
                if (isInSrc) {
                    classStrengthChange.setDeletedDependency(true);
                } else {
                    classStrengthChange.setAddedDependency(true);
                }
                strengthChanges.add(classStrengthChange);

            } else {
                strengthChanges.add(new ClassDependencyStrengthChange(className, strength, strength - this.matchedToNodeDependencyTree.weightedClassDependencies.get(className)));
            }
        });
        return strengthChanges;
    }

    public Map<String, Integer> getWeightedClassDependencies() {
        return weightedClassDependencies;
    }

    public CtType getContainingType() {
        if (SpoonType.TYPE.isTypeOf(ctElement)) return (CtType) ctElement;
        return ctElement.getParent(CtType.class);
    }
}
