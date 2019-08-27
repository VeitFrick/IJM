package at.aau.softwaredynamics.classifier.types;

import at.aau.softwaredynamics.classifier.util.SpoonData;
import com.github.gumtreediff.matchers.MappingStore;
import com.github.gumtreediff.tree.ITree;
import spoon.reflect.declaration.CtField;
import spoon.reflect.declaration.ModifierKind;
import spoon.reflect.reference.CtTypeReference;

public class FieldChange extends ChangeType {

    private CtTypeReference type;

    public FieldChange(ITree srcNode, ITree dstNode, MappingStore mappings) {
        super(srcNode, dstNode, mappings);
    }

    public CtTypeReference getType() {
        return type;
    }

    public void setType(CtTypeReference type) {
        this.type = type;
    }

    @Override
    public String getName() {
        return this.type.getSimpleName() + " Field";
    }

    @Override
    protected String getNodeLabel(ITree node) {
        StringBuilder sb = new StringBuilder();

        CtField ctField = (CtField) SpoonData.getSpoonElement(node);
        for (ModifierKind modifier : ctField.getModifiers()) {
            sb.append(modifier + " ");
        }
        sb.append(ctField.getSimpleName());

        return sb.toString();
    }
}
