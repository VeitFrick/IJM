package at.aau.softwaredynamics.classifier.types;

import at.aau.softwaredynamics.classifier.actions.ActionType;
import com.github.gumtreediff.matchers.MappingStore;
import com.github.gumtreediff.tree.ITree;

public class InvocationChange extends StatementChange {

    public InvocationChange(ITree srcNode, ITree dstNode, MappingStore mappings) {
        super(srcNode, dstNode, mappings);
    }

    @Override
    public String toHumanReadable() {
        if(actionType.isOfExactType(ActionType.INSERT_ACTION)) return "Invoked";
        return super.toHumanReadable();
    }
}
