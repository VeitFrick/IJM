package at.aau.softwaredynamics.util;

import at.aau.softwaredynamics.gen.SpoonLabelFinder;
import at.aau.softwaredynamics.gen.SpoonTreeScanner;
import spoon.reflect.code.CtExpression;
import spoon.reflect.code.CtStatement;
import spoon.reflect.cu.SourcePosition;
import spoon.reflect.cu.position.NoSourcePosition;
import spoon.reflect.visitor.CtVisitor;
import spoon.support.reflect.code.CtStatementImpl;

public class CtBranch extends CtStatementImpl {

    CtExpression expression;
    CtStatement statement;

    public CtBranch(CtExpression element) {
        this.setPosition(element.getPosition());
        this.setParent(element.getParent());
        this.expression = element;
    }

    public CtBranch(CtStatement element) {
        this.setPosition(element.getPosition());
        this.setParent(element.getParent());
        this.statement = element;
    }

    @Override
    public SourcePosition getPosition() {
        if(expression != null) return expression.getPosition();
        if(statement != null) return statement.getPosition();
        return NoSourcePosition.NOPOSITION;
    }

    @Override
    public void accept(CtVisitor visitor) {
        if (visitor instanceof SpoonTreeScanner) {
            SpoonTreeScanner sts = (SpoonTreeScanner) visitor;
            sts.visitCtBranch(this);
        }
        else if (visitor instanceof SpoonLabelFinder) {
            SpoonLabelFinder slf = (SpoonLabelFinder) visitor;
            slf.visitCtBranch(this);
        }

    }

    public CtExpression getExpression() {
        return expression;
    }

    public CtStatement getStatement() {
        return statement;
    }
}
