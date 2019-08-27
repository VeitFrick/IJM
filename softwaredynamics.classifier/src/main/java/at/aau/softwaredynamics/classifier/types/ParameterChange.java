package at.aau.softwaredynamics.classifier.types;

import com.github.gumtreediff.matchers.MappingStore;
import com.github.gumtreediff.tree.ITree;
import spoon.reflect.declaration.CtParameter;

public class ParameterChange extends ChangeType {

    public ParameterChange(ITree srcNode, ITree dstNode, MappingStore mappings) {
        super(srcNode, dstNode, mappings);
    }

    @Override
    public String getNodeChangeHumanReadable() {
        CtParameter ctParameter = (CtParameter) this.srcElement;
        return "("+ctParameter.getType().getSimpleName()+") " + ctParameter.getSimpleName();
    }

    @Override
    public boolean isSignificant() {
        return false;
    }
}
