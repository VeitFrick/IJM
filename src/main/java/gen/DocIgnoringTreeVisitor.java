package gen;

import com.github.gumtreediff.gen.jdt.JdtVisitor;
import org.eclipse.jdt.core.dom.BlockComment;
import org.eclipse.jdt.core.dom.Javadoc;

/**
 * Created by thomas on 09.05.2017.
 */
public class DocIgnoringTreeVisitor extends JdtVisitor {

    public DocIgnoringTreeVisitor() {
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
}
