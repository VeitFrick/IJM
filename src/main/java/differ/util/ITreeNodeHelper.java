package differ.util;


import com.github.gumtreediff.matchers.MappingStore;
import com.github.gumtreediff.matchers.Matcher;
import com.github.gumtreediff.tree.ITree;
import gen.NodeType;
import util.NodeTypeHelper;

import java.util.List;

/**
 * Created by veit on 16.11.2016.
 */
public class ITreeNodeHelper {

    public static ITree getContainingStatement(ITree node) {
        for(ITree parent : node.getParents()) {
            if (NodeTypeHelper.isStatement(parent)) {
                return parent;
            }
        }

        return null;
    }

    public static boolean isAttributeType(ITree node) {
        return node != null
                && NodeTypeHelper.isOfKind(node, new NodeType[] {NodeType.PRIMITIVE_TYPE, NodeType.SIMPLE_TYPE})
                && node.getParent() != null
                && NodeTypeHelper.isOfKind(node.getParent(), NodeType.FIELD_DECLARATION);
    }

    public static boolean isAttributeName(ITree node) {
        return NodeTypeHelper.isOfKind(node, NodeType.VARIABLE_DECLARATION_FRAGMENT)
                && NodeTypeHelper.isOfKind(node.getParent(), NodeType.FIELD_DECLARATION);
    }

    public static boolean isParentTypeName(ITree node) {
        return NodeTypeHelper.isTypeNode(node)
                && node.getParent() != null
                && node.getParent().getType() == NodeType.TYPE_DECLARATION.getValue();
    }

    public static boolean isMethodReturnType(ITree node) {
        if (node == null || node.getParent() == null || !NodeTypeHelper.isOfKind(node.getParent(), NodeType.METHOD_DECLARATION))
            return false;

        ITree parent = node.getParent();
        int nodePosition = node.positionInParent();

        return  NodeTypeHelper.isOfKind(node, new NodeType[] {
                    NodeType.SIMPLE_TYPE,
                    NodeType.PRIMITIVE_TYPE,
                    NodeType.PARAMETERIZED_TYPE,
                    NodeType.ARRAY_TYPE})
                && parent.getChildren().size() >= nodePosition + 1
                && (
                        // first type
                        nodePosition == 0
                        ||
                        NodeTypeHelper.isOfKind(parent.getChild(nodePosition - 1), NodeType.MODIFIER)
                );
    }

    public static ITree getReturnType(ITree node) {
        if (!NodeTypeHelper.isOfKind(node, NodeType.METHOD_DECLARATION))
            return null;

        int numChildren = node.getChildren().size();

        for (int i = 0; i < numChildren; i++) {
            ITree child = node.getChild(i);
            if (isMethodReturnType(child))
                return child;
        }

        return null;
    }

    public static boolean isMethodParameter(ITree node) {
        return node != null
                && NodeTypeHelper.isOfKind(node,NodeType.SINGLE_VARIABLE_DECLARATION)
                && node.getParent() != null
                && NodeTypeHelper.isOfKind(node.getParent(), NodeType.METHOD_DECLARATION);
    }

    public static boolean isPartOfMethodParameter(ITree node) {
        if (node == null || node.getParent() == null)
            return false;

        if (NodeTypeHelper.isOfKind(node.getParent(), NodeType.SINGLE_VARIABLE_DECLARATION)
                && NodeTypeHelper.isOfKind(node.getParent().getParent(), NodeType.METHOD_DECLARATION))
            return true;

        return false;
    }

    public static String getFullQualifiedName(ITree node) {
        String s = "";
        if(!getMethodName(node).equals("")||!getFieldName(node).equals(""))
            s+=".";
        s += getMethodName(node) + getFieldName(node);
        boolean first = true;
        while(node != getParentClass(node)){
            if(NodeTypeHelper.isOfKind(node,NodeType.TYPE_DECLARATION)){
                if(first){
                    first = false;
                    s = getClassName(node)+s;
                }
                else {
                    s = getClassName(node) + "." + s;
                }
            }

            node = getParentClass(node);
        }
        String pckg = getPackageName(node);
        if(!pckg.equals(""))
            pckg = pckg + ".";
        if(!first)
            return pckg + getClassName(getRootClass(node))+"."+s;
        return pckg + getClassName(getRootClass(node))+s;
    }


    public static ITree getMethodNameNode(ITree node) {
        if (!NodeTypeHelper.isOfKind(node, NodeType.METHOD_DECLARATION))
            return null;

        for(ITree child : node.getChildren())
            if (NodeTypeHelper.isOfKind(child, NodeType.SIMPLE_NAME))
                return child;

        return null;
    }

    public static String getPackageName(ITree node){
        while(!NodeTypeHelper.isOfKind(node,NodeType.COMPILATION_UNIT)) {
            if(node == null)
                return "";
            node=node.getParent();
        }
        for (ITree child: node.getChildren()) {
            if(NodeTypeHelper.isOfKind(child,NodeType.PACKAGE_DECLARATION)){
                for (ITree childOfChild: child.getChildren()) {
                    if(NodeTypeHelper.isOfKind(childOfChild,NodeType.SIMPLE_NAME)){
                        return childOfChild.getLabel();
                    }
                }
            }
        }
        return "";
    }

    /**
     * Returns true if the trees are an exact match
     *
     * @param a
     * @param b
     * @return
     */
    private static boolean equals(ITree a, ITree b) {
        if (a.getType() != b.getType()
                || !a.getLabel().equals(b.getLabel()))
            return false;

        ITree aClone = a.deepCopy();
        ITree bClone = b.deepCopy();

        // we only want the subtree to be matched
        aClone.setParent(null);
        bClone.setParent(null);

        MappingStore ms = new MappingStore();
        Matcher m = new Matcher(aClone, bClone, ms) {
            @Override
            public void match() {
                List<ITree> srcs = this.getSrc().getDescendants();
                List<ITree> dsts = this.getDst().getDescendants();

                if (srcs.size() != dsts.size())
                    return;

                for(int i = 0; i < srcs.size(); i++) {
                    ITree s = srcs.get(i);
                    ITree d = dsts.get(i);

                    if (s.getType() == d.getType()
                            && s.getLabel().equals(d.getLabel()))
                        this.addMapping(s, d);
                }
            }
        };
        m.match();

        // tree have to be an excact match -> ever node has to have a mapping
        for (ITree subTree : aClone.getDescendants())
            if (ms.getDst(subTree) == null)
                return false;

        return true;
    }

    public static ITree getCompilationUnit(ITree node) {
        if (NodeTypeHelper.isOfKind(node, NodeType.COMPILATION_UNIT)) {
            return node;
        }

        return getCompilationUnit(node.getParent());
    }

    public static ITree getContainingType(ITree node) {
        ITree parent = node.getParent();

        if (NodeTypeHelper.isOfKind(parent, NodeType.COMPILATION_UNIT))
            return null;

        if (NodeTypeHelper.isOfKind(parent, NodeType.TYPE_DECLARATION))
            return parent;

        return getContainingType(parent);
    }

    public static ITree getContainingMethod(ITree node) {
        ITree parent = node.getParent();

        if (parent == null)
            return null;

        if (NodeTypeHelper.isOfKind(parent, NodeType.METHOD_DECLARATION))
            return parent;

        return getContainingMethod(parent);
    }

    public static ITree getRootNode(ITree node) {
        ITree parent = node.getParent();

        if (parent == null)
            return null;

        if (NodeTypeHelper.isStructureNode(parent))
            return parent;
        else
            return getRootNode(parent);
    }

    public static boolean isPartOfConditionExpression(ITree node) {
        if (NodeTypeHelper.isOfKind(node, NodeType.BLOCK))
            return false; // is always wrong

        return getConditionExpressionRoot(node) != null;
    }

    public static boolean isElsePath(ITree node){
        return NodeTypeHelper.isOfKind(node.getParent(),NodeType.IF_STATEMENT) && node.getParent().getChildPosition(node) >= 2;
    }

    public static ITree getConditionExpressionRoot(ITree node) {
        ITree parent = node.getParent();

        if (parent == null || NodeTypeHelper.isStructureNode(parent))
            return null;

        if (NodeTypeHelper.isOfKind(parent, new NodeType[] {
                NodeType.IF_STATEMENT, NodeType.WHILE_STATEMENT })) {
            // test if and while
            if (parent.getChild(0) == node && parent.isMatched())
                return parent;
        } else if (NodeTypeHelper.isOfKind(parent, new NodeType[] {
                NodeType.FOR_STATEMENT, NodeType.DO_STATEMENT })) {
            // test for and do
//            if (parent.getChildren().size() > 1 //TODO (TG): eval if this is too fuzzy ...
//                    && parent.getChild(1) == node)
            if(parent.isMatched() && NodeTypeHelper.isOfKind(node, new NodeType[] {
                    NodeType.BOOLEAN_LITERAL, NodeType.INFIX_EXPRESSION, NodeType.METHOD_INVOCATION}))
                return parent;
        }

        return getConditionExpressionRoot(parent);
    }

    public static void printTree(ITree root) {
        printTree(root, 0);
    }

    public static void printTree(ITree currentRoot, int level) {
        System.out.println();
        for(int i = 0; i <  level; i++)
            System.out.print(" ");

        System.out.print(currentRoot.getType() + ": " + currentRoot.getLabel());

        level++;
        for(ITree child : currentRoot.getChildren())
            printTree(child, level);
    }

    public static String getClassName(ITree node){
        String s = "";
        if(!NodeTypeHelper.isOfKind(node, NodeType.TYPE_DECLARATION) || node == null)
            return s;

        // if the type node contains a label return it
        if (!node.getLabel().equals(""))
            return node.getLabel();

        // else search for the first name node
        for (ITree child: node.getChildren()) {
            if(child.getLabel()!=null && NodeTypeHelper.isOfKind(child, NodeType.SIMPLE_NAME)) {
                s += child.getLabel();
                break;
            }
        }
        return s;
    }

    private static String getFieldName(ITree node) { //TODO Test
        if(!NodeTypeHelper.isOfKind(node, NodeType.FIELD_DECLARATION))
            return "";
        String s = null;

        for(int i = 0; i < node.getChildren().size() && s == null; i++)
            if (NodeTypeHelper.isOfKind(node.getChild(i), NodeType.VARIABLE_DECLARATION_FRAGMENT))
                s = getNodeName(node.getChild(i));

        return s;
    }


    public static String getMethodName(ITree node){
        if(!NodeTypeHelper.isOfKind(node, NodeType.METHOD_DECLARATION))
            return "";
        String s = null;
        boolean first = true;

        s = getNodeName(node) + "(";

        for (ITree child: node.getChildren()) {
            if(NodeTypeHelper.isOfKind(child, NodeType.SINGLE_VARIABLE_DECLARATION)){
                for (ITree childOfChild: child.getChildren()) {
                    if (!NodeTypeHelper.isOfKind(childOfChild, NodeType.SIMPLE_NAME)){
                        if (!first)
                            s += ",";
                        s += getNodeName(childOfChild);
                        first = false;
                    }
                }
            }

        }
        return s+")";
    }

    private static String getNodeName(ITree node) {
        String name = null;

        if (!node.getLabel().equals("")) {
            // set the label as name
            name = node.getLabel();
        }
        else {
            // search for the first name node
            for(int i = 0; i < node.getChildren().size() && name == null; i++)
                if (NodeTypeHelper.isOfKind(node.getChild(i), NodeType.SIMPLE_NAME))
                    name = node.getChild(i).getLabel();
        }
        return name;
    }

    public static ITree getRootClass(ITree node){ //If this does not work check for parent 15 or something
        while(node != getParentClass(node)){
            node = getParentClass(node);
        }
            return node;
    }

    public static ITree getParentClass(ITree node){
        if(node==null)
            return null;
        for (ITree parent : node.getParents()) {
            if(NodeTypeHelper.isOfKind(parent, NodeType.TYPE_DECLARATION))
                return parent;
        }
        if(NodeTypeHelper.isOfKind(node,NodeType.TYPE_DECLARATION))
            return node;
        return null;
    }

    public static ITree getParentMethod(ITree node) {
        for (ITree parent : node.getParents()) {
            if(NodeTypeHelper.isOfKind(parent, NodeType.METHOD_DECLARATION))
                return parent;
        }
        return null;
    }

    public static boolean isInMethod(ITree node) {
        for (ITree parent : node.getParents()) {
            if(NodeTypeHelper.isOfKind(parent, NodeType.METHOD_DECLARATION))
                return true;
        }
        return false;
    }
}