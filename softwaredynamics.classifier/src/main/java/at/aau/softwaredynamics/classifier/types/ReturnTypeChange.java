package at.aau.softwaredynamics.classifier.types;

import at.aau.softwaredynamics.classifier.util.SpoonData;
import com.github.gumtreediff.matchers.MappingStore;
import com.github.gumtreediff.tree.ITree;
import spoon.reflect.declaration.CtMethod;

public class ReturnTypeChange extends ChangeType {

    public ReturnTypeChange(ITree srcNode, ITree dstNode, MappingStore mappings) {
        super(srcNode, dstNode, mappings);
    }

    @Override
    protected String getNodeLabel(ITree node) {
        StringBuilder sb = new StringBuilder();

        CtMethod ctMethod = (CtMethod) SpoonData.getSpoonElement(node.getParent());
        sb.append(ctMethod.getType());

        return sb.toString();
    }

    @Override
    public boolean isVisible() {
        return actionType.isUpdate() || actionType.isMove();
    }
}
