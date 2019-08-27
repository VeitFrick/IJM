package at.aau.softwaredynamics.classifier.types;

import at.aau.softwaredynamics.classifier.util.SpoonData;
import com.github.gumtreediff.matchers.MappingStore;
import com.github.gumtreediff.tree.ITree;
import spoon.reflect.code.CtAssignment;
import spoon.reflect.code.CtStatement;


public class StatementChange extends ChangeType {

    public StatementChange(ITree srcNode, ITree dstNode, MappingStore mappings) {
        super(srcNode, dstNode, mappings);
    }

    @Override
    public String getName() {
        if(srcElement instanceof CtAssignment) {
            return "Assignment";
        }
        return "Statement";
    }

    @Override
    protected String getNodeLabel(ITree node) {
        StringBuilder sb = new StringBuilder();

        CtStatement ctStatement = (CtStatement) SpoonData.getSpoonElement(node);
        if(ctStatement instanceof CtAssignment) {
            // TODO remove the of because an UPDATE makes it look weird.
            sb.append("of " + ((CtAssignment) ctStatement).getAssigned().toString());
        } else {
            sb.append(ctStatement.toString());
        }

        return sb.toString();
    }
}
