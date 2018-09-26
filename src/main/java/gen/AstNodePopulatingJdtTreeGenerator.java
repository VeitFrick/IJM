package gen;

import com.github.gumtreediff.gen.jdt.AbstractJdtTreeGenerator;
import com.github.gumtreediff.gen.jdt.AbstractJdtVisitor;

/**
 * Created by thomas on 01.12.2016.
 */
public class AstNodePopulatingJdtTreeGenerator extends AbstractJdtTreeGenerator {

    @Override
    protected AbstractJdtVisitor createVisitor() {
        return new AstNodePopulatingJdtVisitor();
    }
}
