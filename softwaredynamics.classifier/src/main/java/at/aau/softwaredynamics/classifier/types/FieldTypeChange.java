package at.aau.softwaredynamics.classifier.types;

import com.github.gumtreediff.matchers.MappingStore;
import com.github.gumtreediff.tree.ITree;

public class FieldTypeChange extends ChangeType {

    public FieldTypeChange(ITree srcNode, ITree dstNode, MappingStore mappings) {
        super(srcNode, dstNode, mappings);
    }
}
