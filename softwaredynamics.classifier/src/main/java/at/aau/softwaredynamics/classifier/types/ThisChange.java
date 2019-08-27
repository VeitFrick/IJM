package at.aau.softwaredynamics.classifier.types;

import com.github.gumtreediff.matchers.MappingStore;
import com.github.gumtreediff.tree.ITree;

public class ThisChange extends ChangeType {

    public ThisChange(ITree srcNode, ITree dstNode, MappingStore mappings) {
        super(srcNode, dstNode, mappings);
    }
}
