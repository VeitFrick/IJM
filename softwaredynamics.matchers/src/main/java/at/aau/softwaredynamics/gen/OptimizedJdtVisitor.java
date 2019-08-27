package at.aau.softwaredynamics.gen;

import at.aau.softwaredynamics.util.LabelHelper;
import com.github.gumtreediff.gen.jdt.AbstractJdtVisitor;
import org.eclipse.jdt.core.dom.*;

import java.util.ArrayDeque;
import java.util.Deque;

/**
 * Created by thomas on 28.12.2016.
 */
public class OptimizedJdtVisitor extends AbstractJdtVisitor {
    private Deque<ASTNode> processedNodes = new ArrayDeque<>();

    public OptimizedJdtVisitor() {
        super();
    }

    protected void pushNode(ASTNode n) {
        pushNode(n, getLabel(n));
        processedNodes.push(n);
    }

    @Override
    public void preVisit(ASTNode n) {
    }

    @Override
    public boolean visit(AnnotationTypeDeclaration node) {
        pushNode(node);
        return true;
    }

    @Override
    public boolean visit(AnnotationTypeMemberDeclaration node) {
        pushNode(node);
        return true;
    }

    @Override
    public boolean visit(AnonymousClassDeclaration node) {
        pushNode(node);
        return true;
    }

    @Override
    public boolean visit(ArrayAccess node) {
        pushNode(node);
        return true;
    }

    @Override
    public boolean visit(ArrayCreation node) {
        pushNode(node);
        return true;
    }

    @Override
    public boolean visit(ArrayInitializer node) {
        pushNode(node);
        return true;
    }

    @Override
    public boolean visit(ArrayType node) {
        pushNode(node);
        return true;
    }

    @Override
    public boolean visit(AssertStatement node) {
        pushNode(node);
        return true;
    }

    @Override
    public boolean visit(Assignment node) {
        pushNode(node);
        return true;
    }

    @Override
    public boolean visit(Block node) {
        pushNode(node);
        return true;
    }

    @Override
    public boolean visit(BlockComment node) {
        //pushNode(node);
        return false;
    }

    @Override
    public boolean visit(BooleanLiteral node) {
        pushNode(node);
        return true;
    }

    @Override
    public boolean visit(BreakStatement node) {
        pushNode(node);
        return true;
    }

    @Override
    public boolean visit(CastExpression node) {
        pushNode(node);
        return true;
    }

    @Override
    public boolean visit(CatchClause node) {
        pushNode(node);
        return true;
    }

    @Override
    public boolean visit(CharacterLiteral node) {
        pushNode(node);
        return true;
    }

    @Override
    public boolean visit(ClassInstanceCreation node) {
        pushNode(node);
        return true;
    }

    @Override
    public boolean visit(CompilationUnit node) {
        pushNode(node);
        return true;
    }

    @Override
    public boolean visit(ConditionalExpression node) {
        pushNode(node);
        return true;
    }

    @Override
    public boolean visit(ConstructorInvocation node) {
        pushNode(node);
        return true;
    }

    @Override
    public boolean visit(ContinueStatement node) {
        pushNode(node);
        return true;
    }

    @Override
    public boolean visit(CreationReference node) {
        pushNode(node);
        return true;
    }

    @Override
    public boolean visit(Dimension node) {
        pushNode(node);
        return true;
    }

    @Override
    public boolean visit(DoStatement node) {
        pushNode(node);
        return true;
    }

    @Override
    public boolean visit(EmptyStatement node) {
        pushNode(node);
        return true;
    }

    @Override
    public boolean visit(EnhancedForStatement node) {
        pushNode(node);
        return true;
    }

    @Override
    public boolean visit(EnumConstantDeclaration node) {
        pushNode(node);
        return true;
    }

    @Override
    public boolean visit(EnumDeclaration node) {
        pushNode(node);
        return true;
    }

    @Override
    public boolean visit(ExpressionMethodReference node) {
        pushNode(node);
        return true;
    }

    @Override
    public boolean visit(ExpressionStatement node) {
        pushNode(node);
        return true;
    }

    @Override
    public boolean visit(FieldAccess node) {
        pushNode(node);
        return true;
    }

    @Override
    public boolean visit(FieldDeclaration node) {
        pushNode(node);
        return true;
    }

    @Override
    public boolean visit(ForStatement node) {
        pushNode(node);
        return true;
    }

    @Override
    public boolean visit(IfStatement node) {
        pushNode(node);
        return true;
    }

    @Override
    public boolean visit(InfixExpression node) {
        pushNode(node);
        return true;
    }

    @Override
    public boolean visit(Initializer node) {
        pushNode(node);
        return true;
    }

    @Override
    public boolean visit(InstanceofExpression node) {
        pushNode(node);
        return true;
    }

    @Override
    public boolean visit(IntersectionType node) {
        pushNode(node);
        return true;
    }

    @Override
    public boolean visit(LabeledStatement node) {
        pushNode(node);
        return true;
    }

    @Override
    public boolean visit(LambdaExpression node) {
        pushNode(node);
        return true;
    }

    @Override
    public boolean visit(LineComment node) {
        pushNode(node);
        return true;
    }

    @Override
    public boolean visit(MarkerAnnotation node) {
        pushNode(node);
        return true;
    }

    @Override
    public boolean visit(MemberRef node) {
        pushNode(node);
        return true;
    }

    @Override
    public boolean visit(MemberValuePair node) {
        pushNode(node);
        return true;
    }

    @Override
    public boolean visit(MethodRef node) {
        pushNode(node);
        return true;
    }

    @Override
    public boolean visit(MethodRefParameter node) {
        pushNode(node);
        return true;
    }

    @Override
    public boolean visit(MethodDeclaration node) {
        pushNode(node);
        return true;
    }

    @Override
    public boolean visit(MethodInvocation node) {
        pushNode(node);
        return true;
    }

    @Override
    public boolean visit(Modifier node) {
        pushNode(node);
        return true;
    }

    @Override
    public boolean visit(NameQualifiedType node) {
        pushNode(node);
        return true;
    }

    @Override
    public boolean visit(NormalAnnotation node) {
        pushNode(node);
        return true;
    }

    @Override
    public boolean visit(NullLiteral node) {
        pushNode(node);
        return true;
    }

    @Override
    public boolean visit(NumberLiteral node) {
        pushNode(node);
        return true;
    }

    @Override
    public boolean visit(PackageDeclaration node) {
        pushNode(node);
        return true;
    }

    @Override
    public boolean visit(ParenthesizedExpression node) {
        pushNode(node);
        return true;
    }

    @Override
    public boolean visit(PostfixExpression node) {
        pushNode(node);
        return true;
    }

    @Override
    public boolean visit(PrefixExpression node) {
        pushNode(node);
        return true;
    }

    @Override
    public boolean visit(QualifiedType node) {
        pushNode(node);
        return true;
    }

    @Override
    public boolean visit(ReturnStatement node) {
        pushNode(node);
        return true;
    }

    @Override
    public boolean visit(SimpleName node) {
        if (
            // Review (Thomas): do not push if is name of method invocation
            node.getParent() instanceof MethodInvocation && ((MethodInvocation)node.getParent()).getName() == node
            // Review (Thomas): do not push if is name of method decl
            || node.getParent() instanceof MethodDeclaration && ((MethodDeclaration)node.getParent()).getName() == node
            // Review (Thomas): do not push if is name of type decl
            || node.getParent() instanceof TypeDeclaration && ((TypeDeclaration)node.getParent()).getName() == node
            // Review (Thomas): do not push if is name of var delc fragemnt
            || node.getParent() instanceof VariableDeclarationFragment
            ) {
            return false;
        }

        pushNode(node);
        return true;
    }

    @Override
    public boolean visit(SingleMemberAnnotation node) {
        pushNode(node);
        return true;
    }

    @Override
    public boolean visit(SingleVariableDeclaration node) {
        pushNode(node);
        return true;
    }

    @Override
    public boolean visit(StringLiteral node) {
        pushNode(node);
        return true;
    }

    @Override
    public boolean visit(SuperConstructorInvocation node) {
        pushNode(node);
        return true;
    }

    @Override
    public boolean visit(SuperFieldAccess node) {
        pushNode(node);
        return true;
    }

    @Override
    public boolean visit(SuperMethodInvocation node) {
        pushNode(node);
        return true;
    }

    @Override
    public boolean visit(SuperMethodReference node) {
        pushNode(node);
        return true;
    }

    @Override
    public boolean visit(SwitchCase node) {
        pushNode(node);
        return true;
    }

    @Override
    public boolean visit(SwitchStatement node) {
        pushNode(node);
        return true;
    }

    @Override
    public boolean visit(SynchronizedStatement node) {
        pushNode(node);
        return true;
    }

    @Override
    public boolean visit(TextElement node) {
        pushNode(node);
        return true;
    }

    @Override
    public boolean visit(ThisExpression node) {
        pushNode(node);
        return true;
    }

    @Override
    public boolean visit(ThrowStatement node) {
        pushNode(node);
        return true;
    }

    @Override
    public boolean visit(TryStatement node) {
        pushNode(node);
        return true;
    }

    @Override
    public boolean visit(TypeDeclaration node) {
        pushNode(node);
        return true;
    }

    @Override
    public boolean visit(TypeDeclarationStatement node) {
        pushNode(node);
        return true;
    }

    @Override
    public boolean visit(TypeLiteral node) {
        pushNode(node);
        return true;
    }

    @Override
    public boolean visit(TypeMethodReference node) {
        pushNode(node);
        return true;
    }

    @Override
    public boolean visit(TypeParameter node) {
        pushNode(node);
        return true;
    }

    @Override
    public boolean visit(UnionType node) {
        pushNode(node);
        return true;
    }

    @Override
    public boolean visit(VariableDeclarationExpression node) {
        pushNode(node);
        return true;
    }

    @Override
    public boolean visit(VariableDeclarationStatement node) {
        pushNode(node);
        return true;
    }

    @Override
    public boolean visit(VariableDeclarationFragment node) {
        pushNode(node);
        return true;
    }

    @Override
    public boolean visit(WhileStatement node) {
        pushNode(node);
        return true;
    }

    @Override
    public boolean visit(WildcardType node) {
        pushNode(node);
        return true;
    }

    @Override
    public boolean visit(Javadoc node) {
        // Review (Thomas): ignore JavaDoc to reduce tree size and to prevent matching errors due to matched JavaDoc nodes
        return false;
    }

    @Override
    public boolean visit(TagElement e) {
        e.toString();
        return true;
    }

    @Override
    public boolean visit(ImportDeclaration node) {
        // Review (Thomas): cut off tree
        pushNode(node);
        return false;
    }

    @Override
    public boolean visit(SimpleType node) {
        // Review (Thomas): cut off tree
        pushNode(node);
        return false;
    }

    @Override
    public boolean visit(PrimitiveType node) {
        // Review (Thomas): cut off tree
        pushNode(node);
        return false;
    }

    @Override
    public boolean visit(ParameterizedType node) {
        // Review (Thomas): cut off tree
        pushNode(node);
        return false;
    }

    @Override
    public boolean visit(QualifiedName node) {
        pushNode(node);
        return false;
    }

    @Override
    public void postVisit(ASTNode n) {
        if (processedNodes.peek() == n)
        {
            processedNodes.pop();
            popNode();
        }
    }

    protected String getLabel(ASTNode n) {
        if (n instanceof Name) return ((Name) n).getFullyQualifiedName();

        // Review (Thomas): changed assignment of label for types
        if (n instanceof Type) return  LabelHelper.getTypeLabel((Type)n);
//        if (n instanceof Type) return  n.toString();

        if (n instanceof Modifier) return n.toString();
        if (n instanceof StringLiteral) return ((StringLiteral) n).getEscapedValue();
        if (n instanceof NumberLiteral) return ((NumberLiteral) n).getToken();
        if (n instanceof CharacterLiteral) return ((CharacterLiteral) n).getEscapedValue();
        if (n instanceof BooleanLiteral) return n.toString();
        if (n instanceof InfixExpression) return ((InfixExpression) n).getOperator().toString();
        if (n instanceof PrefixExpression) return ((PrefixExpression) n).getOperator().toString();
        if (n instanceof PostfixExpression) return ((PostfixExpression) n).getOperator().toString();
        if (n instanceof Assignment) return ((Assignment) n).getOperator().toString();

        if (n instanceof TextElement) return n.toString();
        if (n instanceof TagElement) return ((TagElement) n).getTagName();

        // Review (Thomas): add label to import statements
        if (n instanceof ImportDeclaration) return ((ImportDeclaration)n).getName().getFullyQualifiedName();
        // Review (Thomas): add label to method invocations
        if (n instanceof MethodInvocation) return ((MethodInvocation)n).getName().getIdentifier();

        // Review (Martin): added for testing the differ
        if (n instanceof MethodDeclaration) return ((MethodDeclaration) n).getName().getIdentifier();
        if (n instanceof TypeDeclaration) return ((TypeDeclaration) n).getName().getIdentifier();

        // Review (Thomas): add variable name to var delc fragment
        if (n instanceof VariableDeclarationFragment) return ((VariableDeclarationFragment)n).getName().getIdentifier();

        return "";
    }
}

