package at.aau.softwaredynamics.classifier.types;

import at.aau.softwaredynamics.classifier.util.SpoonData;
import com.github.gumtreediff.matchers.MappingStore;
import com.github.gumtreediff.tree.ITree;
import spoon.reflect.declaration.CtClass;
import spoon.reflect.declaration.ModifierKind;

public class ClassChange extends ChangeType {

    public ClassChange(ITree srcNode, ITree dstNode, MappingStore mappings) {
        super(srcNode, dstNode, mappings);
    }

    public boolean isSignificant() {
        return true;
    }

    @Override
    protected String getNodeLabel(ITree node) {
        StringBuilder sb = new StringBuilder();

        if (node == null) return "NO NODE";

        CtClass ctClass = (CtClass) SpoonData.getSpoonElement(node);
        for (ModifierKind modifier : ctClass.getModifiers()) {
            sb.append(modifier + " ");
        }
        sb.append(ctClass.getSimpleName());
        return sb.toString();
    }
}
