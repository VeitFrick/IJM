package at.aau.softwaredynamics.classifier.types;

import at.aau.softwaredynamics.classifier.actions.ActionType;
import at.aau.softwaredynamics.classifier.actions.InsertAction;
import com.github.gumtreediff.matchers.MappingStore;
import com.github.gumtreediff.tree.ITree;

public class ConditionChange extends ChangeType {

    public ConditionChange(ITree srcNode, ITree dstNode, MappingStore mappings) {
        super(srcNode, dstNode, mappings);
    }
}
