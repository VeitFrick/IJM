package at.aau.softwaredynamics.classifier.types;

import at.aau.softwaredynamics.util.CtModifier;
import com.github.gumtreediff.matchers.MappingStore;
import com.github.gumtreediff.tree.ITree;
import spoon.reflect.code.CtAssignment;
import spoon.reflect.code.CtStatement;

public class ModifierChange extends ChangeType {

    public ModifierChange(ITree srcNode, ITree dstNode, MappingStore mappings) {
        super(srcNode, dstNode, mappings);
    }

    @Override
    public String getNodeChangeHumanReadable() {
        CtModifier ctModifier = (CtModifier) this.srcElement;

        return ctModifier.toString();
    }
}
