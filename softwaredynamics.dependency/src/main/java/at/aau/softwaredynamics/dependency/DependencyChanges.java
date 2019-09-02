package at.aau.softwaredynamics.dependency;

import at.aau.softwaredynamics.classifier.entities.SourceCodeChange;
import com.github.gumtreediff.actions.model.Action;
import com.github.gumtreediff.actions.model.Update;
import com.github.gumtreediff.matchers.MappingStore;
import com.github.gumtreediff.tree.ITree;
import org.apache.commons.lang3.tuple.MutablePair;
import org.apache.commons.lang3.tuple.Pair;
import spoon.reflect.declaration.CtClass;

import java.util.*;

/**
 *
 */
public class DependencyChanges {

    private MappingStore mappings;
    private DependencyPairContainer depStruct;

    private int countViewedElementsSrc = 0;
    private int countViewedElementsDst = 0;

    private Map<ITree, Set<String>> addedClassDependeciesPerNode;
    private Map<ITree, Set<String>> removedClassDependeciesPerNode;

    private Map<ITree, List<ClassDependencyStrengthChange>> strengthChanges;

    private List<Action> actions;

    public DependencyChanges(MappingStore mappings, List<Action> actions) {
        addedClassDependeciesPerNode = new HashMap<>();
        removedClassDependeciesPerNode = new HashMap<>();
        this.mappings = mappings;
        this.actions = actions;
        strengthChanges = new HashMap<>();
    }

    public void extractDependencies(ITree srcRoot, ITree dstRoot, String srcString, String dstString, DependencyFilter filter) {

        depStruct = new DependencyPairContainer(srcRoot, dstRoot, filter, mappings, srcString, dstString);
        countViewedElementsSrc = srcRoot.getSize();
        countViewedElementsDst = dstRoot.getSize();

        // Iterate over all significant trees in SRC
        Pair<Set<String>, Set<String>> depSrcRootDeps = getAddedAndRemovedDependenciesSrc(depStruct.getRootStrucSrc());
        if (depStruct.getRootStrucSrc() != null) {
            addedClassDependeciesPerNode.put(depStruct.getRootStrucSrc().getiTree(), depSrcRootDeps.getLeft());
            removedClassDependeciesPerNode.put(depStruct.getRootStrucSrc().getiTree(), depSrcRootDeps.getRight());

            for (NodeDependencyTree child : depStruct.getRootStrucSrc().getChildren()) {
                Pair<Set<String>, Set<String>> dependenciesPerNode = getAddedAndRemovedDependenciesSrc(child);
                addedClassDependeciesPerNode.put(child.getiTree(), dependenciesPerNode.getLeft());
                removedClassDependeciesPerNode.put(child.getiTree(), dependenciesPerNode.getRight());
            }
        }


        Pair<Set<String>, Set<String>> depDstRootDeps = getAddedDependenciesDstOnly(depStruct.getRootStrucDst());
        if (depStruct.getRootStrucDst() != null) {
            strengthChanges.put(depStruct.getRootStrucDst().getiTree(), depStruct.getRootStrucDst().calculateClassDependencyStrengthChange());
            if (depDstRootDeps != null) {
                addedClassDependeciesPerNode.put(depStruct.getRootStrucDst().getiTree(), depDstRootDeps.getLeft());
                removedClassDependeciesPerNode.put(depStruct.getRootStrucDst().getiTree(), depDstRootDeps.getRight());
            }
            for (NodeDependencyTree child : depStruct.getRootStrucDst().getChildren()) {
                Pair<Set<String>, Set<String>> dependenciesPerNode = getAddedDependenciesDstOnly(child);
                if (dependenciesPerNode != null) {
                    addedClassDependeciesPerNode.put(child.getiTree(), dependenciesPerNode.getLeft());
                    removedClassDependeciesPerNode.put(child.getiTree(), dependenciesPerNode.getRight());
                }
                strengthChanges.put(child.getiTree(), child.calculateClassDependencyStrengthChange());
            }
        }
    }

    /**
     * @return Returns fully qualified dst name of the element that was affected
     */
    private String getRootName() {
        return ((CtClass) this.depStruct.getRootStrucDst().getCtElement()).getQualifiedName();
    }


    /**
     * Gets all the newly introduced class dependencies and completely removed ones for this node
     *
     * @return a pair of set of newly introduced class dependencies / completely removed ones (fqdn)
     */
    private Pair<Set<String>, Set<String>> getAddedAndRemovedDependenciesSrc(NodeDependencyTree srcStructure) {

        Set<String> srcClassDeps = new HashSet<>();
        // if there is not source, return null
        if (srcStructure == null) return null;

        srcClassDeps.addAll(srcStructure.getClassDependencies());

        Set<String> dstClassDeps = new HashSet<>();
        if (srcStructure.getMatchedToNodeDependencyTree() != null) {
            dstClassDeps.addAll(srcStructure.getMatchedToNodeDependencyTree().getClassDependencies()); //TODO NPE ismatched is true but getMatchedToStructuralNodeDep returns null. This should not ever happen.
        }

        Set<String> intersection = new HashSet<>(srcClassDeps); // use the copy constructor
        intersection.retainAll(dstClassDeps);

        Set<String> removedDeps = new HashSet<>(srcClassDeps);
        removedDeps.removeAll(intersection);
        Set<String> addedDeps = new HashSet<>(dstClassDeps);
        addedDeps.removeAll(intersection);

        return new MutablePair<>(addedDeps, removedDeps);
    }

    private Pair<Set<String>, Set<String>> getAddedDependenciesDstOnly(NodeDependencyTree dstStructure) {
        Set<String> dstClassDeps = new HashSet<>();
        if (dstStructure == null) return null;
        dstClassDeps.addAll(dstStructure.getClassDependencies());

        if (dstStructure.isMatched()) {
            return null; // ignore matched because we get them in SRC deps already
        }

        Set<String> removedDeps = new HashSet<>(); // cannot have removed deps in dst
        Set<String> addedDeps = dstClassDeps;

        return new MutablePair<>(addedDeps, removedDeps);
    }

    public DependencyPairContainer getDepStruct() {
        return depStruct;
    }

    public Map<ITree, Set<String>> getAddedClassDependeciesPerNode() {
        return addedClassDependeciesPerNode;
    }

    public Map<ITree, Set<String>> getRemovedClassDependeciesPerNode() {
        return removedClassDependeciesPerNode;
    }

    public Map<ITree, List<ClassDependencyStrengthChange>> getStrengthChanges() {
        return strengthChanges;
    }

    /**
       Returns a List of all NodeDependencies that have been inserted;
    */
    public List<NodeDependency> getAllInsertedNodeDependencies() {
        if (getDepStruct().getRootStrucDst() == null) return new ArrayList<>();
        ArrayList<NodeDependency> insDeps = new ArrayList<>(getDepStruct().getRootStrucDst().getDependencies());

        insDeps.removeIf(dep -> {
            if (dep.getNode().isMatched()) {
                for (Action action : actions) {
                    if (dep.getNode().equals(mappings.getDst(action.getNode())))
                        // remove them if the action is not an update
                        return !(action instanceof Update);
                }
                return true;
            } else {
                // all dependencies on the right side without a matched node are inserted, so keep them
                return false;
            }
        });

        return insDeps;
    }

    /**
     Returns a List of all NodeDependencies that have been deleted;
    */
    public List<NodeDependency> getAllDeletedNodeDependencies() {
        if (getDepStruct().getRootStrucSrc() == null) return new ArrayList<>();
        ArrayList<NodeDependency> delDeps = new ArrayList<>(getDepStruct().getRootStrucSrc().getDependencies());

        delDeps.removeIf(dep -> {
            if (dep.getNode().isMatched()) {
                for (Action action : actions) {
                    if (dep.getNode().equals(action.getNode()))
                        // remove them if the action is not an update
                        return !(action instanceof Update);
                }
                return true;
            } else {
                // all dependencies on the left side without a matched node are inserted, so keep them
                return false;
            }
        });
        return delDeps;
    }

    public List<NodeDependency> getAllUnchangedNodeDependenciesSource() {
        if (getDepStruct().getRootStrucSrc() == null) return new ArrayList<>();
        ArrayList<NodeDependency> unchangedDeps = new ArrayList<>(getDepStruct().getRootStrucSrc().getDependencies());
        // remove all unmatched nodes, leaving us with only unchanged dependencies
        unchangedDeps.removeIf(dep -> !dep.getNode().isMatched());

        unchangedDeps.removeIf(dep -> {
            if (dep.getNode().isMatched()) {
                for (Action action : actions) {
                    if (dep.getNode().equals(action.getNode()))
                        // remove them if there is an action
                        return (action instanceof Update);
                }
                return false;
            } else {
                // not matched dependencies cannot be unchanged
                return true;
            }
        });
        return unchangedDeps;
    }

    public List<NodeDependency> getAllUnchangedNodeDependenciesDestination() {
        if (getDepStruct().getRootStrucDst() == null) return new ArrayList<>();
        ArrayList<NodeDependency> unchangedDeps = new ArrayList<>(getDepStruct().getRootStrucDst().getDependencies());
        // remove all unmatched nodes, leaving us with only unchanged dependencies
        unchangedDeps.removeIf(dep -> {
            if (dep.getNode().isMatched()) {
                for (Action action : actions) {
                    if (dep.getNode().equals(mappings.getDst(action.getNode())))
                        // remove them if there is an action
                        return (action instanceof Update);

                }
                return false;
            } else {
                // not matched dependencies cannot be unchanged
                return true;
            }
        });
        return unchangedDeps;
    }

    /*
     * TODO: Do the following:
     * The whole following code parts should be refactored, as they mix analysis with output!
     * If can be they should be completely deleted!
     */


    /**
     * Returns a OVERVIEW of all Changes AND all dependencies TODO: Make this two separate methods
     * @param depList
     * @return
     */
    public String getDependencyChangeOverview(List<SourceCodeChange> depList, String separator) {

        String returnString = "# Checked Elements (SRC): " + countViewedElementsSrc + "\n";
        returnString += "# Checked Elements (DST): " + countViewedElementsDst + "\n";

        returnString += printDependencyChangeOverviewRecDst(depStruct.getRootStrucDst());
        returnString += printDependencyChangeOverviewRecSrc(depStruct.getRootStrucSrc());


        for (SourceCodeChange scc: depList) {
//            returnString += scc.getDependencies().get(0).toOutputString(separator); //TODO: Fix overview
            if(scc.getSrcInfo().getNode()!=null){
                returnString+= separator +scc.getSrcInfo().getStartLineNumber()+separator + scc.getSrcInfo().getStartOffset()+separator+scc.getSrcInfo().getEndLineNumber()+separator +scc.getSrcInfo().getEndOffset()+separator+"source\n";
            } else{
                returnString+=separator+scc.getDstInfo().getStartLineNumber()+separator + scc.getDstInfo().getStartOffset()+separator+scc.getDstInfo().getEndLineNumber()+separator +scc.getDstInfo().getEndOffset()+separator+"destination\n";
            }
        }
        return returnString;
    }

    private String printDependencyChangeOverviewRecSrc(NodeDependencyTree nodeDependencyTree) {
        String returnString = "";
        if (nodeDependencyTree == null) {
            returnString += "Dependency structure does not exist!\n";
            return returnString;
        }
        String tabs = "";
        for (int i = 0; i < nodeDependencyTree.getDepth(); i++) {
            tabs += "\t";
        }
        if (!nodeDependencyTree.isMatched()) {
            returnString += tabs + nodeDependencyTree.toString() + "\n";
            returnString += getListOfDependenciesForStructNode(nodeDependencyTree, tabs);
        }
        for (NodeDependencyTree child : nodeDependencyTree.getChildren()) {
            returnString += printDependencyChangeOverviewRecSrc(child);
        }
        return returnString;
    }

    private String printDependencyChangeOverviewRecDst(NodeDependencyTree nodeDependencyTree) {
        String returnString = "", tabs = "";

        if (nodeDependencyTree == null) {
            returnString += "Dependency structure does not exist!\n";
            return returnString;
        }

        // Add Tabs for Depth
        for (int i = 0; i < nodeDependencyTree.getDepth(); i++) {
            tabs += "\t";
        }
        returnString += "#" + tabs + nodeDependencyTree.toString() + "\n";
        returnString += getListOfDependenciesForStructNode(nodeDependencyTree, tabs);
        if (nodeDependencyTree.isMatched()) {
            returnString += getListOfDependenciesForStructNode(nodeDependencyTree.getMatchedToNodeDependencyTree(), tabs);
        }
        for (NodeDependencyTree child : nodeDependencyTree.getChildren()) {
            returnString += printDependencyChangeOverviewRecDst(child);
        }
        return returnString;
    }

    private String getListOfDependenciesForStructNode(NodeDependencyTree nodeDependencyTree, String tabs) {
        String retString = "";

        if (nodeDependencyTree == null)
            return "";

        if (addedClassDependeciesPerNode.get(nodeDependencyTree.getiTree()) != null || removedClassDependeciesPerNode.get(nodeDependencyTree.getiTree()) != null) {
            //retString+=(getStringForITree(nodeDependencyTree.getiTree(),tabs));
        } else {
            if (strengthChanges.get(nodeDependencyTree.getiTree()) != null)
                for (ClassDependencyStrengthChange change : strengthChanges.get(nodeDependencyTree.getiTree())) {
//                    retString+=(Color.WHITE + tabs + "\t - " + change.toString() + Color.RESET+"\n");
                    retString+="#" + (tabs + "\t " + change.toString() + "\n");
                }
        }

        return retString;
    }
}
