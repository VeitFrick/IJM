package at.aau.softwaredynamics.util;

import at.aau.softwaredynamics.gen.SpoonLabelFinder;
import at.aau.softwaredynamics.gen.SpoonTreeScanner;
import spoon.reflect.code.CtExpression;
import spoon.reflect.cu.SourcePosition;
import spoon.reflect.declaration.CtElement;
import spoon.reflect.visitor.CtVisitor;
import spoon.support.reflect.code.CtExpressionImpl;

public class CtCondition extends CtExpressionImpl {

    CtExpression<Boolean> cond;

    public CtCondition(CtExpression<Boolean> element) {
        this.setPosition(element.getPosition());
        this.setParent(element.getParent());
        this.cond = element;
    }

    @Override
    public SourcePosition getPosition() {
        return cond.getPosition();
    }

    @Override
    public void accept(CtVisitor visitor) {
        if (visitor instanceof SpoonTreeScanner) {
            SpoonTreeScanner sts = (SpoonTreeScanner) visitor;
            sts.visitCtCondition(this);
        }
        else if (visitor instanceof SpoonLabelFinder) {
            SpoonLabelFinder slf = (SpoonLabelFinder) visitor;
            slf.visitCtCondition(this);
        }

    }

    public CtExpression<Boolean> getCondition() {
        return cond;
    }
}
