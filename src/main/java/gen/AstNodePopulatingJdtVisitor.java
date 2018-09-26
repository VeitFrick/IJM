package gen;

import com.github.gumtreediff.tree.ITree;
import org.eclipse.jdt.core.dom.ASTNode;
import org.eclipse.jdt.core.dom.BlockComment;
import org.eclipse.jdt.core.dom.Javadoc;
import org.eclipse.jdt.core.dom.LineComment;

import java.util.ArrayDeque;
import java.util.Deque;

/**
 * Created by thomas on 01.12.2016. based on com.github.gumtreediff.gen.jdt.AbstractJdtVisitor
 */
public class AstNodePopulatingJdtVisitor extends OptimizedJdtVisitor {
    public static final String METADATA_KEY_AstNode = "MK_ASTNODE";

    private Deque<ITree> trees = new ArrayDeque<>();

    public AstNodePopulatingJdtVisitor() {
        super();
    }

    @Override
    public boolean visit(Javadoc node) {
        return false;
    }

    @Override
    public boolean visit(BlockComment node) {
        return false;
    }

    @Override
    public boolean visit(LineComment node) {
        return false;
    }

    @Override
    protected void pushNode(ASTNode n, String label) {
        int type = n.getNodeType();
        String typeName = n.getClass().getSimpleName();

        ITree t = context.createTree(type, label, typeName);
        t.setPos(n.getStartPosition());
        t.setLength(n.getLength());

        t.setMetadata(METADATA_KEY_AstNode, n);

        if (trees.isEmpty())
            context.setRoot(t);
        else {
            ITree parent = trees.peek();
            t.setParentAndUpdateChildren(parent);
        }

        trees.push(t);
    }

    @Override
    protected ITree getCurrentParent() {
        return trees.peek();
    }

    @Override
    protected void popNode() {
        trees.pop();
    }
}
