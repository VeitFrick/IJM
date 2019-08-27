package at.aau.softwaredynamics.gen;

import com.github.gumtreediff.gen.jdt.*;

/**
 * Created by thomas on 14.03.2017.
 */
public class OptimizedJdtTreeGenerator extends AbstractJdtTreeGenerator {

    @Override
    protected AbstractJdtVisitor createVisitor() {
        return new OptimizedJdtVisitor();
    }
}
