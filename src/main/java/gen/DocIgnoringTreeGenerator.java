package gen;

import com.github.gumtreediff.gen.jdt.AbstractJdtTreeGenerator;
import com.github.gumtreediff.gen.jdt.AbstractJdtVisitor;

/**
 * Created by thomas on 09.05.2017.
 */
public class DocIgnoringTreeGenerator extends AbstractJdtTreeGenerator {
    @Override
    protected AbstractJdtVisitor createVisitor() {
        return new DocIgnoringTreeVisitor();
    }
}
