package at.aau.softwaredynamics.classifier.types;

import com.github.gumtreediff.matchers.MappingStore;
import com.github.gumtreediff.tree.ITree;

public class TypeArgumentChange extends ChangeType {

    public TypeArgumentChange(ITree srcNode, ITree dstNode, MappingStore mappings) {
        super(srcNode, dstNode, mappings);
    }
}
