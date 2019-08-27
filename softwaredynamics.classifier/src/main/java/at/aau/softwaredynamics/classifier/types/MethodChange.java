package at.aau.softwaredynamics.classifier.types;

import at.aau.softwaredynamics.classifier.util.SpoonData;
import com.github.gumtreediff.matchers.MappingStore;
import com.github.gumtreediff.tree.ITree;
import spoon.reflect.declaration.CtMethod;
import spoon.reflect.declaration.ModifierKind;

public class MethodChange extends ChangeType {
    public MethodChange(ITree srcNode, ITree dstNode, MappingStore mappings) {
        super(srcNode, dstNode, mappings);
    }


    public boolean isSignificant() {
        return true;
    }

    @Override
    protected String getNodeLabel(ITree node) {
        StringBuilder sb = new StringBuilder();

        if (node == null) return "NO NODE";

        CtMethod ctMethod = (CtMethod) SpoonData.getSpoonElement(node);
        for (ModifierKind modifier : ctMethod.getModifiers()) {
            sb.append(modifier + " ");
        }
        sb.append(ctMethod.getType() + " ");

        sb.append(ctMethod.getSimpleName());

        return sb.toString();
    }

}
