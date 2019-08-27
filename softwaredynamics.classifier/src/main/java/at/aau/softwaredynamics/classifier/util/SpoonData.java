package at.aau.softwaredynamics.classifier.util;

import at.aau.softwaredynamics.gen.SpoonBuilder;
import com.github.gumtreediff.tree.ITree;
import spoon.reflect.declaration.CtElement;

public class SpoonData {

    public static CtElement getSpoonElement(ITree node) {
        return (CtElement) node.getMetadata(SpoonBuilder.SPOON_OBJECT);
    }
}
