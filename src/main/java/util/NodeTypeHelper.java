package util;

import com.github.gumtreediff.tree.ITree;
import gen.AstNodePopulatingJdtVisitor;
import gen.NodeType;
import org.eclipse.jdt.core.dom.*;

/**
 * Created by veit on 16.11.2016.
 */
public class NodeTypeHelper {
    public static boolean isTypeNode(ITree node) {
        return isOfKind(node, new NodeType[] {
            NodeType.SIMPLE_TYPE,
            NodeType.PRIMITIVE_TYPE,
            NodeType.PARAMETERIZED_TYPE,
        });
    }

    public static boolean isStructureNode(ITree node) {
        NodeType[] structureNodeTypes = new NodeType[] {

                NodeType.METHOD_DECLARATION,
                NodeType.FIELD_DECLARATION,
                NodeType.PACKAGE_DECLARATION,
                NodeType.TYPE_DECLARATION,
                NodeType.ENUM_DECLARATION,
        };

        return isOfKind(node, structureNodeTypes);
    }

    public static boolean isOfKind(ITree node, NodeType kind) {
        return isOfKind(node, new NodeType[] {kind});
    }

    public static boolean isOfKind(ITree node, NodeType[] kinds) {
        if (node != null) {
            for(NodeType kind : kinds){
                if (kind.getValue() == node.getType())
                    return true;
        }}

        return false;
    }

    public boolean isComment(ITree node) {
        NodeType[] statementKinds = new NodeType[] {
                NodeType.BLOCK_COMMENT,
                NodeType.JAVADOC,
                NodeType.LINE_COMMENT
        };
        return isOfKind(node, statementKinds);
    }

    public static boolean isStatement(ITree node) {
        NodeType[] statementKinds = new NodeType[] {
                NodeType.EXPRESSION_STATEMENT,
                NodeType.ASSERT_STATEMENT,
                NodeType.ASSIGNMENT,
                NodeType.BREAK_STATEMENT,
                NodeType.CATCH_CLAUSE,
                NodeType.CLASS_INSTANCE_CREATION,
                NodeType.CONSTRUCTOR_INVOCATION,
                NodeType.CONTINUE_STATEMENT,
                NodeType.DO_STATEMENT,
                NodeType.FOR_STATEMENT,
                NodeType.ENHANCED_FOR_STATEMENT,
                NodeType.IF_STATEMENT,
                NodeType.LABELED_STATEMENT,
                NodeType.METHOD_INVOCATION,
                NodeType.RETURN_STATEMENT,
                NodeType.SWITCH_CASE,
                NodeType.SWITCH_STATEMENT,
                NodeType.SYNCHRONIZED_STATEMENT,
                NodeType.THROW_STATEMENT,
                NodeType.TRY_STATEMENT,
                NodeType.VARIABLE_DECLARATION_STATEMENT,
                NodeType.WHILE_STATEMENT,
                NodeType.PREFIX_EXPRESSION,
                NodeType.POSTFIX_EXPRESSION,
                NodeType.VARIABLE_DECLARATION_EXPRESSION,
        };

        return isOfKind(node, statementKinds);
    }

    public static NodeType getByValue(int kindValue) {
        for(NodeType kind : NodeType.values())
            if (kind.getValue() == kindValue)
                return kind;

        return null;
    }

    public static boolean isParentInterface(ITree node) {
        if (node.getParent() != null
            && isOfKind(node.getParent(), NodeType.TYPE_DECLARATION)) {

            String label = node.getLabel();

            TypeDeclaration typeDeclaration =
                    (TypeDeclaration) node.getParent().getMetadata(AstNodePopulatingJdtVisitor.METADATA_KEY_AstNode);

            if (typeDeclaration != null) {
                for(Object type : typeDeclaration.superInterfaceTypes()) {
                    if (label.equals(type.toString()))
                        return true;
                }
            }
        }

        return false; //TODO Implement
    }

    public static boolean isLoop(ITree node) {
        NodeType[] statementKinds = new NodeType[] {
                NodeType.DO_STATEMENT,
                NodeType.FOR_STATEMENT,
                NodeType.WHILE_STATEMENT,
                NodeType.ENHANCED_FOR_STATEMENT,
        };

        return isOfKind(node, statementKinds);
    }

    public static boolean isOfSpecialInterest(ITree node) {
        NodeType[] statementKinds = new NodeType[] {
                NodeType.CONSTRUCTOR_INVOCATION,
                NodeType.METHOD_INVOCATION,
                NodeType.CLASS_INSTANCE_CREATION,
                NodeType.DO_STATEMENT,
                NodeType.FOR_STATEMENT,
                NodeType.WHILE_STATEMENT,
        };

        return isOfKind(node, statementKinds);
    }

    public static boolean isLiteral(ITree node) {
        NodeType[] statementKinds = new NodeType[] {
                NodeType.BOOLEAN_LITERAL,
                NodeType.CHARACTER_LITERAL,
                NodeType.NULL_LITERAL,
                NodeType.NUMBER_LITERAL,
                NodeType.STRING_LITERAL,
                NodeType.TYPE_LITERAL,
        };

        return isOfKind(node, statementKinds);
    }

    public static boolean isPartOfLoop(ITree node) {
        if (isOfKind(node, NodeType.SINGLE_VARIABLE_DECLARATION)) {
            return NodeTypeHelper.isLoop(node.getParent());
        }

        return false;
    }
}