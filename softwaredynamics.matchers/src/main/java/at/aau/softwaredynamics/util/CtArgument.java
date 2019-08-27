package at.aau.softwaredynamics.util;

import at.aau.softwaredynamics.gen.SpoonLabelFinder;
import at.aau.softwaredynamics.gen.SpoonTreeScanner;
import spoon.reflect.code.CtExpression;
import spoon.reflect.code.CtStatement;
import spoon.reflect.cu.SourcePosition;
import spoon.reflect.declaration.CtElement;
import spoon.reflect.visitor.CtVisitor;
import spoon.support.reflect.code.CtExpressionImpl;
import spoon.support.reflect.code.CtStatementImpl;

public class CtArgument extends CtExpressionImpl {

    CtExpression element;

    public CtArgument(CtExpression element) {
        this.setPosition(element.getPosition());
        this.setParent(element.getParent());
        this.element = element;
    }

    @Override
    public SourcePosition getPosition() {
        return element.getPosition();
    }

    @Override
    public void accept(CtVisitor visitor) {
        if (visitor instanceof SpoonTreeScanner) {
            SpoonTreeScanner sts = (SpoonTreeScanner) visitor;
            sts.visitCtArgument(this);
        }
        else if (visitor instanceof SpoonLabelFinder) {
            SpoonLabelFinder slf = (SpoonLabelFinder) visitor;
            slf.visitCtArgument(this);
        }

    }

    public CtExpression getElement() {
        return element;
    }
}
