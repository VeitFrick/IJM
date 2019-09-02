package at.aau.softwaredynamics.dependency;

import at.aau.softwaredynamics.classifier.util.LineNumberHelper;
import at.aau.softwaredynamics.gen.SpoonBuilder;
import at.aau.softwaredynamics.util.SpoonType;
import com.github.gumtreediff.matchers.MappingStore;
import com.github.gumtreediff.tree.ITree;
import spoon.reflect.declaration.CtElement;

import java.util.Stack;

/**
 * Holds source and destination trees of two revisions of a source file
 */
public class DependencyPairContainer {

    private NodeDependencyTree rootStrucSrc;
    private NodeDependencyTree rootStrucDst;

    private ITree rootSrc;
    private ITree rootDst;
    private DependencyFilter filter;

    private LineNumberHelper lineNumberHelper;


    public DependencyPairContainer(ITree rootNodeSrc, ITree rootNodeDst, DependencyFilter filter, MappingStore mappings, String srcString, String dstString) {
        this.filter = filter;
        this.rootSrc = rootNodeSrc;
        this.rootDst = rootNodeDst;

        lineNumberHelper = new LineNumberHelper(srcString, dstString, mappings);

        Stack<NodeDependencyTree> visitorStack = new Stack<>();
        rootStrucSrc = visit(rootSrc, visitorStack, true);
        rootStrucDst = visit(rootDst, visitorStack, false);

        if (rootStrucSrc != null && rootStrucDst != null) {
            if (mappings.has(rootStrucSrc.getiTree(), rootStrucDst.getiTree())) {
                rootStrucSrc.setMatchedToNodeDependencyTree(rootStrucDst);
                rootStrucDst.setMatchedToNodeDependencyTree(rootStrucSrc);
            }
            for (NodeDependencyTree strucSrc : rootStrucSrc.getChildren()) {
                if (strucSrc.isMatched()) {
                    for (NodeDependencyTree strucDst : rootStrucDst.getChildren()) {
                        if (strucDst.isMatched() && mappings.has(strucSrc.getiTree(), strucDst.getiTree())) {
                            strucSrc.setMatchedToNodeDependencyTree(strucDst);
                            strucDst.setMatchedToNodeDependencyTree(strucSrc);
                        }
                    }
                }
            }
        }
    }

    private NodeDependencyTree visit(ITree visitedNode, Stack<NodeDependencyTree> visitorStack, boolean visitSrc) {
        CtElement ctElement = (CtElement) visitedNode.getMetadata(SpoonBuilder.SPOON_OBJECT);
        NodeDependencyTree temp = null;
        if (isInterestinCtElement(ctElement)) {
            NodeDependencyTree visitedStructNodeDep;
            if (visitorStack.empty()) {
                visitedStructNodeDep = new NodeDependencyTree(visitedNode, visitSrc);
                temp = visitedStructNodeDep;
            } else {
                visitedStructNodeDep = new NodeDependencyTree(visitedNode, visitorStack.peek(), visitSrc);
                visitorStack.peek().addChild(visitedStructNodeDep);
            }
            visitorStack.push(visitedStructNodeDep);
        }
        NodeDependency dependency;
        if(visitorStack.empty()){
            dependency = new NodeDependency(visitedNode, filter,null,lineNumberHelper.getLineNumbers(visitedNode,visitSrc));
        }
        else{
            dependency = new NodeDependency(visitedNode, filter, visitorStack.peek(), lineNumberHelper.getLineNumbers(visitedNode, visitSrc));
        }
        if (dependency.getDependency() != null) {
            for (NodeDependencyTree elementOnStack : visitorStack) {
                elementOnStack.addToNodeDependencies(dependency);
            }
        }

        for (ITree child: visitedNode.getChildren()) {
            NodeDependencyTree temp2 = visit(child, visitorStack, visitSrc);
            if (temp == null && temp2 != null) {
                temp = temp2;
            }
        }

        if (isInterestinCtElement(ctElement)) {
            visitorStack.pop();
        }
        return temp;
    }

    public DependencyFilter getFilter() {
        return filter;
    }

    public void setFilter(DependencyFilter filter) {
        this.filter = filter;
    }

    private boolean isInterestinCtElement(CtElement ctElement) {
        return SpoonType.TYPE.isTypeOf(ctElement) || SpoonType.METHOD.isTypeOf(ctElement) || SpoonType.CONSTRUCTOR.isTypeOf(ctElement) || SpoonType.ENUM.isTypeOf(ctElement) || SpoonType.INTERFACE.isTypeOf(ctElement);
    }

    public NodeDependencyTree getRootStrucSrc() {
        return rootStrucSrc;
    }

    public NodeDependencyTree getRootStrucDst() {
        return rootStrucDst;
    }
}
