package test.classification;


import at.aau.softwaredynamics.classifier.entities.SourceCodeChange;
import at.aau.softwaredynamics.classifier.types.*;
import org.junit.Ignore;
import org.junit.Test;

import java.io.IOException;
import java.util.Collection;

import static at.aau.softwaredynamics.classifier.actions.ActionType.*;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

/**
 * Created by thomas on 17.11.2016.
 */
public class BodyStatementsTests extends ClassifierTestBase {

    /*
    Loops
     */
    @Test
    public void canExtractLoopStatementAddToMethod() throws IOException {
        String src = "public class Foo { public void bar() {} }";
        String dst = "public class Foo { public void bar() {while(true){System.out.print(\"Hi\");}} }";

        Collection<SourceCodeChange> changes = classify(src, dst);
        extendedInfoDump(changes);

        assertChangeCount(changes, LoopChange.class, INSERT_ACTION, 1);
        assertChangeCount(changes, LiteralChange.class, INSERT_ACTION, 2);
        assertChangeCount(changes, InvocationChange.class, INSERT_ACTION, 1);
        assertChangeCount(changes, FieldReadChange.class, INSERT_ACTION, 1);
        assertChangeCount(changes, ArgumentChange.class, INSERT_ACTION, 1);
        assertChangeCount(changes, TargetChange.class, INSERT_ACTION, 1);
        assertChangeCount(changes, ConditionChange.class, INSERT_ACTION, 1);


        assertEquals(8, changes.size());
    }

    @Test
    public void methodParameterAddIsNOTextractedForArgumentlessMethodInvocations() throws IOException {
        String src = "public class Foo { public void bar() { } }";
        String dst = "public class Foo { public void bar() { a.b.bar(); } }";

        Collection<SourceCodeChange> changes = classify(src, dst);
        extendedInfoDump(changes);
        assertChangeCount(changes, InvocationChange.class, INSERT_ACTION, 1);
        assertChangeCount(changes, TargetChange.class, INSERT_ACTION, 1);

        assertEquals(2, changes.size());
    }

    @Test
    public void canExtractMethodInvocationArgumentAddWithVariable() throws IOException {
        String src = "public class Foo { public void bar() { a.b.bar(); } }";
        String dst = "public class Foo { public void bar() { a.b.bar(x); } }";

        Collection<SourceCodeChange> changes = classify(src, dst);

        extendedInfoDump(changes);
        assertChangeCount(changes, ArgumentChange.class, INSERT_ACTION, 1);
        assertChangeCount(changes, FieldReadChange.class, INSERT_ACTION, 1);
        assertEquals(2, changes.size());
    }

    @Test
    public void canExtractMethodInvocationArgumentAddWithNumberLiteral() throws IOException {
        String src = "public class Foo { public void bar() { a.b.bar(); } }";
        String dst = "public class Foo { public void bar() { a.b.bar(1); } }";

        Collection<SourceCodeChange> changes = classify(src, dst);

        extendedInfoDump(changes);
        assertChangeCount(changes, ArgumentChange.class, INSERT_ACTION, 1);
        assertChangeCount(changes, LiteralChange.class, INSERT_ACTION, 1);
        assertChangeCount(changes, InvocationChange.class, UPDATE_ACTION, 1);
        assertEquals(3, changes.size());
    }

    @Test
    public void canExtractMethodInvocationArgumentAddWithMethodCall() throws IOException {
        String src = "public class Foo { public void bar() { a.b.bar(); } }";
        String dst = "public class Foo { public void bar() { a.b.bar(TEST.someMethod()); } }";

        Collection<SourceCodeChange> changes = classify(src, dst);

        extendedInfoDump(changes);
        assertChangeCount(changes, ArgumentChange.class, INSERT_ACTION, 1);
        assertChangeCount(changes, TargetChange.class, INSERT_ACTION, 1);
        assertChangeCount(changes, InvocationChange.class, INSERT_ACTION, 1);
        assertEquals(3, changes.size());
    }

    @Test
    public void canExtractMethodInvocationParameterAddWithMultipleNestedMethodCalls() throws IOException {
        String src = "public class Foo { public void bar() { a.b.bar(); } }";
        String dst = "public class Foo { public void bar() { a.b.bar(blubb.foo(TEST.someMethod())); } }";

        Collection<SourceCodeChange> changes = classify(src, dst);

        extendedInfoDump(changes);
        assertChangeCount(changes, ArgumentChange.class, INSERT_ACTION, 2);
        assertChangeCount(changes, TargetChange.class, INSERT_ACTION, 1);
        assertChangeCount(changes, FieldReadChange.class, INSERT_ACTION, 1);
        assertChangeCount(changes, InvocationChange.class, INSERT_ACTION, 2);
        assertEquals(6, changes.size());
    }

    @Test
    public void canExtractLoopStatementDelete() throws IOException {
        String src = "public class Foo { public void bar() {while(true){System.out.print(\"Hi\");}} }";
        String dst = "public class Foo { public void bar() {} }";

        Collection<SourceCodeChange> changes = classify(src, dst);

        extendedInfoDump(changes);
        assertChangeCount(changes, LiteralChange.class, DELETE_ACTION, 2);
        assertChangeCount(changes, ConditionChange.class, DELETE_ACTION, 1);
        assertChangeCount(changes, TargetChange.class, DELETE_ACTION, 1);
        assertChangeCount(changes, FieldReadChange.class, DELETE_ACTION, 1);
        assertChangeCount(changes, ArgumentChange.class, DELETE_ACTION, 1);
        assertChangeCount(changes, InvocationChange.class, DELETE_ACTION, 1);
        assertChangeCount(changes, InvocationChange.class, DELETE_ACTION, 1);
        assertChangeCount(changes, LoopChange.class, DELETE_ACTION, 1);

        assertEquals(8, changes.size());
    }

    @Test
    public void canExtractLoopStatementDelete1() throws IOException {
        String src = "public class Foo { public void bar() {while(true){}} }";
        String dst = "public class Foo { public void bar() {} }";

        Collection<SourceCodeChange> changes = classify(src, dst);

        extendedInfoDump(changes);
        assertChangeCount(changes, LoopChange.class, DELETE_ACTION, 1);
        assertChangeCount(changes, LiteralChange.class, DELETE_ACTION, 1);
        assertChangeCount(changes, ConditionChange.class, DELETE_ACTION, 1);

        assertEquals(3, changes.size());
    }

    @Test
    public void canExtractLoopStatementDelete2() throws IOException {
        String src = "public class Foo { public void bar() {int i = 12; int b = 14; while(true) { System.out.print(\"Hi\"); } } }";
        String dst = "public class Foo { public void bar() {int i = 12; int b = 14;} }";

        Collection<SourceCodeChange> changes = classify(src, dst);

        extendedInfoDump(changes);
        assertChangeCount(changes, LoopChange.class, DELETE_ACTION, 1);
        assertChangeCount(changes, ArgumentChange.class, DELETE_ACTION, 1);
        assertChangeCount(changes, LiteralChange.class, DELETE_ACTION, 2);
        assertChangeCount(changes, TargetChange.class, DELETE_ACTION, 1);
        assertChangeCount(changes, FieldReadChange.class, DELETE_ACTION, 1);
        assertChangeCount(changes, InvocationChange.class, DELETE_ACTION, 1);
        assertChangeCount(changes, ConditionChange.class, DELETE_ACTION, 1);


        assertEquals(8, changes.size());
    }

    @Test
    public void canExtractLoopStatementDelete3() throws IOException {
        String src = "public class Foo { public void bar() {int i = 12; int b = 14; while(true){i = 2;}} }";
        String dst = "public class Foo { public void bar() {int i = 12; int b = 14;} }";

        Collection<SourceCodeChange> changes = classify(src, dst);

        extendedInfoDump(changes);
        assertChangeCount(changes, LoopChange.class, DELETE_ACTION, 1);
        assertChangeCount(changes, VariableWriteChange.class, DELETE_ACTION, 1);
        assertChangeCount(changes, LiteralChange.class, DELETE_ACTION, 2);
        assertChangeCount(changes, StatementChange.class, DELETE_ACTION, 1);
        assertChangeCount(changes, ConditionChange.class, DELETE_ACTION, 1);

        assertEquals(6, changes.size());
    }

    /*
    Statement Adds
     */

    @Test
    public void canExtractSingleStatementAddToMethod() throws IOException {
        String src = "public class Foo { public void bar() {} }";
        String dst = "public class Foo { public void bar() { int i = 17; } }";

        Collection<SourceCodeChange> changes = classify(src, dst);

        extendedInfoDump(changes);
        assertChangeCount(changes, VariableDeclarationChange.class, INSERT_ACTION, 1);
        assertChangeCount(changes, LiteralChange.class, INSERT_ACTION, 1);
        assertChangeCount(changes, VariableTypeChange.class, INSERT_ACTION, 1);

        assertEquals(3, changes.size());
    }

    @Test
    public void canExtractMultipleStatementAddToMethod() throws IOException {
        String src = "public class Foo { public void bar() {} }";
        String dst = "public class Foo { public void bar() { int i = 17; double d = 20.5; } }";

        Collection<SourceCodeChange> changes = classify(src, dst);

        extendedInfoDump(changes);
        assertChangeCount(changes, VariableDeclarationChange.class, INSERT_ACTION, 2);
        assertChangeCount(changes, LiteralChange.class, INSERT_ACTION, 2);
        assertChangeCount(changes, VariableTypeChange.class, INSERT_ACTION, 2);

        assertEquals(6, changes.size());
    }

    @Test
    public void canExtractSingleStatementAddToConstructor() throws IOException {
        String src = "public class Foo { public Foo() {} }";
        String dst = "public class Foo { public Foo() { int i = 17; } }";

        Collection<SourceCodeChange> changes = classify(src, dst);

        extendedInfoDump(changes);
        assertChangeCount(changes, VariableDeclarationChange.class, INSERT_ACTION, 1);
        assertChangeCount(changes, LiteralChange.class, INSERT_ACTION, 1);
        assertChangeCount(changes, VariableTypeChange.class, INSERT_ACTION, 1);

        assertEquals(3, changes.size());
    }

    @Test
    public void canExtractMultipleStatementAddToConstructor() throws IOException {
        String src = "public class Foo { public Foo() {} }";
        String dst = "public class Foo { public Foo() { int i = 17; double d = 20.5; } }";

        Collection<SourceCodeChange> changes = classify(src, dst);

        extendedInfoDump(changes);
        assertChangeCount(changes, VariableDeclarationChange.class, INSERT_ACTION, 2);
        assertChangeCount(changes, LiteralChange.class, INSERT_ACTION, 2);
        assertChangeCount(changes, VariableTypeChange.class, INSERT_ACTION, 2);

        assertEquals(6, changes.size());
    }

    /*
    Statement Deletes
     */

    @Test //TODO Multiple actions Bug - Otherwise fine
    public void canExtractSingleStatementDeleteFromMethod() throws IOException {
        String src = "public class Foo { public void bar() { int i = 17; } }";
        String dst = "public class Foo { public void bar() {} }";

        Collection<SourceCodeChange> changes = classify(src, dst);

        extendedInfoDump(changes);
        assertChangeCount(changes, VariableDeclarationChange.class, DELETE_ACTION, 1);
        assertChangeCount(changes, LiteralChange.class, DELETE_ACTION, 1);
        assertChangeCount(changes, VariableTypeChange.class, DELETE_ACTION, 1);

        assertEquals(3, changes.size());
    }

    @Test //TODO Multiple actions Bug - Otherwise fine
    public void canExtractMultipleStatementDeleteFromMethod() throws IOException {
        String src = "public class Foo { public void bar() { int i = 17; double d = 20.5; } }";
        String dst = "public class Foo { public void bar() {} }";

        Collection<SourceCodeChange> changes = classify(src, dst);

        extendedInfoDump(changes);
        assertChangeCount(changes, VariableDeclarationChange.class, DELETE_ACTION, 2);
        assertChangeCount(changes, LiteralChange.class, DELETE_ACTION, 2);
        assertChangeCount(changes, VariableTypeChange.class, DELETE_ACTION, 2);

        assertEquals(6, changes.size());
    }

    @Test //TODO Multiple actions Bug - Otherwise fine
    public void canExtractSingeStatementDeleteFromConstructor() throws IOException {
        String src = "public class Foo { public Foo() { int i = 17; } }";
        String dst = "public class Foo { public Foo() {} }";

        Collection<SourceCodeChange> changes = classify(src, dst);

        extendedInfoDump(changes);
        assertChangeCount(changes, VariableDeclarationChange.class, DELETE_ACTION, 1);
        assertChangeCount(changes, LiteralChange.class, DELETE_ACTION, 1);
        assertChangeCount(changes, VariableTypeChange.class, DELETE_ACTION, 1);

        assertEquals(3, changes.size());
    }


    @Test //TODO Multiple actions Bug - Otherwise fine
    public void canExtractMultipleStatementDeleteFromConstructor() throws IOException {
        String src = "public class Foo { public Foo() { int i = 17; double d = 20.5; } }";
        String dst = "public class Foo { public Foo() {} }";

        Collection<SourceCodeChange> changes = classify(src, dst);

        extendedInfoDump(changes);
        assertChangeCount(changes, VariableDeclarationChange.class, DELETE_ACTION, 2);
        assertChangeCount(changes, LiteralChange.class, DELETE_ACTION, 2);
        assertChangeCount(changes, VariableTypeChange.class, DELETE_ACTION, 2);

        assertEquals(6, changes.size());
    }

    /*
    Statement Updates
     */

    @Ignore("fixme")
    @Test
    public void addToIfBlockBodyOnlyResultsInStatementInsert() throws IOException {
        String src = "public class Foo { public bar() {" +
                "   if (broker != null) {" +
                "       broker.stop();" +
                "   }" +
                "}";

        String dst = "public class Foo { public bar() {" +
                "   if (broker != null) {" +
                "       broker.stop();" +
                "       broker = null;" +
                "   }" +
                "}";

        Collection<SourceCodeChange> changes = classify(src, dst);

        extendedInfoDump(changes);
        assertChangeCount(changes, VariableWriteChange.class, INSERT_ACTION, 1);
        assertChangeCount(changes, LiteralChange.class, INSERT_ACTION, 1);

        assertEquals(2, changes.size());
    }

    @Test //It's not a rename anymore, since it's not a declaration.
    public void canExtractVariableUpdate() throws IOException {
        String src = "public class Foo {" +
                "   private boolean isDestroyed() {" +
                "       return destoryed;" +
                "   }" +
                "}";
        String dst = "public class Foo {" +
                "   private boolean isDestroyed() {" +
                "       return destroyed;" +
                "   }" +
                "}";

        Collection<SourceCodeChange> changes = classify(src, dst, true);


        extendedInfoDump(changes);
        assertChangeCount(changes, ReturnValueChange.class, UPDATE_ACTION, 1);
        assertEquals(1, changes.size());
    }

    @Test
    public void canExtracSingleStatementUpdateInConstructor() throws IOException {
        String src = "public class Foo { public Foo() { int i = 17;} }";
        String dst = "public class Foo { public Foo() { int i = 18;} }";

        Collection<SourceCodeChange> changes = classify(src, dst, true);

        extendedInfoDump(changes);
        assertChangeCount(changes, LiteralChange.class, UPDATE_ACTION, 1);
        assertEquals(1, changes.size());
    }

    @Test
    public void canExtractMultipleStatementUpdateInConstructor() throws IOException {
        String src = "public class Foo { public Foo() { int i = 17; double d = 20.5; } }";
        String dst = "public class Foo { public Foo() { int i = 18; double d = 44.4;} }";

        Collection<SourceCodeChange> changes = classify(src, dst, true);

        extendedInfoDump(changes);
        assertChangeCount(changes, LiteralChange.class, UPDATE_ACTION, 2);
        assertEquals(2, changes.size());
    }

    @Test
    public void canExtracSingleStatementUpdateInMethod() throws IOException {
        String src = "public class Foo { public void bar() { int i = 17; } }";
        String dst = "public class Foo { public void bar() { int i = 12; } }";

        Collection<SourceCodeChange> changes = classify(src, dst);

        extendedInfoDump(changes);
        assertChangeCount(changes, LiteralChange.class, UPDATE_ACTION, 1);
        assertEquals(1, changes.size());
    }

    @Test
    public void canExtracMultipleStatementUpdateInMethod() throws IOException {
        String src = "public class Foo { public void bar() { int i = 17; double d = 20.5; } }";
        String dst = "public class Foo { public void bar() { int i = 12; double d = 20.1; } }";

        Collection<SourceCodeChange> changes = classify(src, dst);

        extendedInfoDump(changes);
        assertChangeCount(changes, LiteralChange.class, UPDATE_ACTION, 2);
        assertEquals(2, changes.size());
    }

    @Test
    public void canExtractUpdateForDifferentOverload() throws IOException {
        String src = "public class Foo { public void bar() { Bar.test(17); } }";
        String dst = "public class Foo { public void bar() { Bar.test(17, 2d); } }";

        Collection<SourceCodeChange> changes = classify(src, dst);

        extendedInfoDump(changes);
        assertChangeCount(changes, ArgumentChange.class, INSERT_ACTION, 1);
        assertChangeCount(changes, InvocationChange.class, UPDATE_ACTION, 1);
        assertChangeCount(changes, LiteralChange.class, INSERT_ACTION, 1);
        assertEquals(3, changes.size());

    }

    @Test
    public void canExtractMethodInvocationaForRecursiveCall() throws IOException {
        String src = "public class Foo { public boolean bar(int i) { if (i == 0) return true; else return false; } }";
        String dst = "public class Foo { public boolean bar(int i) { if (i == 0) return true; else return this.bar(i--); } }";

        Collection<SourceCodeChange> changes = classify(src, dst);

        extendedInfoDump(changes);

        assertChangeCount(changes, ThisChange.class, INSERT_ACTION, 1);
        assertChangeCount(changes, ArgumentChange.class, INSERT_ACTION, 1);
        assertChangeCount(changes, InvocationChange.class, INSERT_ACTION, 1);
        assertChangeCount(changes, StatementChange.class, INSERT_ACTION, 2); //2 because another one is also a statement
        assertChangeCount(changes, VariableWriteChange.class, INSERT_ACTION, 1);
        assertChangeCount(changes, ReturnValueChange.class, DELETE_ACTION, 1);

        assertEquals(6, changes.size());
    }

    /*
    Statement Ordering Change
     */

    @Test
    public void canExtracSingleStatementOrderingChangeInConstructor() throws IOException {
        String src = "public class Foo { public Foo() { double d = 20.5; int i = 17;} }";
        String dst = "public class Foo { public Foo() { int i = 17; double d = 20.5;} }";

        Collection<SourceCodeChange> changes = classify(src, dst);

        extendedInfoDump(changes);
        assertChangeCount(changes, VariableDeclarationChange.class, ORDERING_CHANGE_ACTION, 1);
        assertEquals(1, changes.size());
    }

    @Test
    public void canExtracMultipleStatementOrderingChangeInConstructor() throws IOException {
        String src = "public class Foo { public Foo() { int i = 17; double d = 20.5;  int x = 12; double z = 20.3;} }";
        String dst = "public class Foo { public Foo() { double d = 20.5; int i = 17;  double z = 20.3; int x = 12;} }";

        Collection<SourceCodeChange> changes = classify(src, dst);

        extendedInfoDump(changes);
        assertChangeCount(changes, VariableDeclarationChange.class, ORDERING_CHANGE_ACTION, 2);
        assertEquals(2, changes.size());
    }

    @Test
    public void canExtracSingleStatementOrderingChangeInMethod() throws IOException {
        String src = "public class Foo { public void bar() { double d = 20.5; int i = 17; } }";
        String dst = "public class Foo { public void bar() { int i = 17; double d = 20.5; } }";

        Collection<SourceCodeChange> changes = classify(src, dst);

        extendedInfoDump(changes);
        assertChangeCount(changes, VariableDeclarationChange.class, ORDERING_CHANGE_ACTION, 1);
        assertEquals(1, changes.size());
    }

    @Test
    public void canExtracMultipleStatementOrderingChangeInMethod() throws IOException {
        String src = "public class Foo { public void bar() { int i = 17; double d = 20.5; int x = 12; double z = 20.3;} }";
        String dst = "public class Foo { public void bar() { double d = 20.5; int i = 17; double z = 20.3; int x = 12;} }";

        Collection<SourceCodeChange> changes = classify(src, dst);

        extendedInfoDump(changes);
        assertChangeCount(changes, VariableDeclarationChange.class, ORDERING_CHANGE_ACTION, 2);
        assertEquals(2, changes.size());
    }

    @Test
    public void canExtractStatementOderingChange() throws IOException {
        String src = "public class Foo { public void bar(P p) { p.doSomething(); p.init(); } }";
        String dst = "public class Foo { public void bar(P p) { p.init(); p.doSomething(); } }";

        Collection<SourceCodeChange> changes = classify(src, dst);

        extendedInfoDump(changes);
        assertChangeCount(changes, InvocationChange.class, ORDERING_CHANGE_ACTION, 1);
        assertEquals(1, changes.size());
    }

    /*
    Change Parent
     */
    @Test
    public void canExtractParentChangedBlock() throws IOException {
        String src = "public class Foo { public void bar() { int i = 17; } }";
        String dst = "public class Foo { public void bar() { if (true) { int i = 17; } } }";

        Collection<SourceCodeChange> changes = classify(src, dst);

        extendedInfoDump(changes);
        assertChangeCount(changes, VariableDeclarationChange.class, PARENT_CHANGE_ACTION, 1);
        assertChangeCount(changes, IfBranchChange.class, INSERT_ACTION, 1);
        assertChangeCount(changes, ConditionChange.class, INSERT_ACTION, 1);
        assertChangeCount(changes, LiteralChange.class, INSERT_ACTION, 1);
        assertEquals(4, changes.size());
    }

    @Test
    public void canExtractParentChangedNoBlock() throws IOException {
        String src = "public class Foo { public void bar() {int i = 0; i = 17; } }";
        String dst = "public class Foo { public void bar() {int i = 0; if (true)  i = 17; } }";

        Collection<SourceCodeChange> changes = classify(src, dst);

        extendedInfoDump(changes);
        assertChangeCount(changes, StatementChange.class, PARENT_CHANGE_ACTION, 1);
        assertChangeCount(changes, IfBranchChange.class, INSERT_ACTION, 1);
        assertChangeCount(changes, ConditionChange.class, INSERT_ACTION, 1);
        assertChangeCount(changes, LiteralChange.class, INSERT_ACTION, 1);

        assertEquals(4, changes.size());
    }

    @Test //TODO refine this test
    public void genericChangeDoesNOTResultInParentChange() throws IOException {
        String src = "public class Foo { public void bar() { " +
                "   FieldMatrix<Fraction> m = new Array2DRowFieldMatrix<Fraction>(subTestData);" +
                "   FieldVector<Fraction> mRow0 = new ArrayFieldVector<Fraction>(subRow0[0]);" +
                "   FieldVector<Fraction> mRow3 = new ArrayFieldVector<Fraction>(subRow3[0]);" +
                " } " +
                "}";
        String dst = "public class Foo { public void bar() { " +
                "   FieldMatrix<Fraction> m = new Array2DRowFieldMatrix<>(subTestData);" +
                "   FieldVector<Fraction> mRow0 = new ArrayFieldVector<>(subRow0[0]);" +
                "   FieldVector<Fraction> mRow3 = new ArrayFieldVector<>(subRow3[0]);" +
                " } " +
                "}";

        Collection<SourceCodeChange> changes = classify(src, dst);

        extendedInfoDump(changes);

        assertChangeCount(changes, StatementChange.class, UPDATE_ACTION, 3);

        assertEquals(3, changes.size());
    }

    @Test
    public void canClassifyMovedIf() throws IOException {
        String src = "public class Foo { " +
                "   public void bar() { " +
                "       if (true) { throw new Exception(); }" +
                "   } " +
                "}";

        String dst = "public class Foo { " +
                "   public void bar() { " +
                "       if (false) { if (true) { throw new Exception(); } }" +
                "   } " +
                "}";

        Collection<SourceCodeChange> changes = classify(src, dst);

        extendedInfoDump(changes);

        assertChangeCount(changes, IfBranchChange.class, PARENT_CHANGE_ACTION, 1);
        assertChangeCount(changes, IfBranchChange.class, INSERT_ACTION, 1);
        assertChangeCount(changes, ConditionChange.class, INSERT_ACTION, 1);
        assertChangeCount(changes, LiteralChange.class, INSERT_ACTION, 1);

        assertEquals(4, changes.size());
    }

    @Test
    @Ignore // It's a matching issue
    public void canClassifyMovedIfWithChangeInCondition() throws IOException {
        String src = "public class Foo { " +
                "   public void bar() { " +
                "       if (true) { throw new Exception(); }" +
                "   } " +
                "}";

        String dst = "public class Foo { " +
                "   public void bar() { " +
                "        if (false) { if (false) { throw new Exception(); } }" +
                "   } " +
                "}";

        Collection<SourceCodeChange> changes = classify(src, dst);

        extendedInfoDump(changes);

        assertEquals(4, changes.size());
    }
}

