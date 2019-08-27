package at.aau.softwaredynamics.classifier.types;

import at.aau.softwaredynamics.classifier.util.SpoonData;
import com.github.gumtreediff.matchers.MappingStore;
import com.github.gumtreediff.tree.ITree;
import spoon.reflect.declaration.CtConstructor;
import spoon.reflect.declaration.ModifierKind;

public class ConstructorChange extends ChangeType {
    public ConstructorChange(ITree srcNode, ITree dstNode, MappingStore mappings) {
        super(srcNode, dstNode, mappings);
    }

    @Override
    public boolean isSignificant() {
        return true;
    }

    @Override
    protected String getNodeLabel(ITree node) {
        StringBuilder sb = new StringBuilder();

        if (node == null) return "NO NODE";

        CtConstructor ctConstructor = (CtConstructor) SpoonData.getSpoonElement(node);
        for (ModifierKind modifier : ctConstructor.getModifiers()) {
            sb.append(modifier + " ");
        }

        return sb.toString() + ctConstructor.getType().getSimpleName();
    }
}
