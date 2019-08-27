package test.classification;

import at.aau.softwaredynamics.classifier.actions.UpdateAction;
import at.aau.softwaredynamics.classifier.entities.SourceCodeChange;
import at.aau.softwaredynamics.classifier.types.*;
import org.junit.Ignore;
import org.junit.Test;

import java.io.IOException;
import java.util.Collection;

import static at.aau.softwaredynamics.classifier.actions.ActionType.DELETE_ACTION;
import static at.aau.softwaredynamics.classifier.actions.ActionType.INSERT_ACTION;
import static at.aau.softwaredynamics.classifier.actions.ActionType.UPDATE_ACTION;
import static org.junit.Assert.assertEquals;

/**
 * Created by thomas on 18.11.2016.
 */
public class BodyConditionsTests extends ClassifierTestBase {

    @Test
    public void canExtractSingleAlternativePartInsert() throws IOException {
        String src = "public class Foo { public void bar() { if(true){int i = 8;} } }";
        String dst = "public class Foo { public void bar() { if(true){int i = 8;} else{ } }";

        Collection<SourceCodeChange> changes = classify(src, dst);
        extendedInfoDump(changes);
        assertChangeCount(changes, ElseBranchChange.class, INSERT_ACTION, 1);
        assertEquals(1, changes.size());
    }

    @Test
    public void canExtractSingleAlternativePartInsert1() throws IOException {
        String src = "public class Foo { public int bar() { if(true) return 1; return 2; } }";
        String dst = "public class Foo { public int bar() { if(true) return 1; else return 4; return 2; }";

        Collection<SourceCodeChange> changes = classify(src, dst);
        extendedInfoDump(changes);
        assertChangeCount(changes, ElseBranchChange.class, INSERT_ACTION, 1);
        assertChangeCount(changes, ReturnStatementChange.class, INSERT_ACTION, 1);
        assertChangeCount(changes, ReturnValueChange.class, INSERT_ACTION, 1);
        assertEquals(changes.size(), 3);
    }

    @Test //Tests Only on Inserted Else!
    public void canExtractSingleAlternativePartInsertElseIfBlock() throws IOException {
        String src = "public class Foo { public int bar() { if(true) return 1; return 2; } }";
        String dst = "public class Foo { public int bar() { if(true) return 1; else if(18 == 33) {return 4;}; return 2; } }";

        Collection<SourceCodeChange> changes = classify(src, dst);
        extendedInfoDump(changes);

        assertChangeCount(changes, ElseBranchChange.class, INSERT_ACTION, 1); // else
//        assertChangeCount(changes, IfBranchChange.class, INSERT_ACTION, 1); // else
//        assertChangeCount(changes, ReturnStatementChange.class, INSERT_ACTION, 1); // return
//        assertChangeCount(changes, ReturnValueChange.class, INSERT_ACTION, 1); // 4
//        assertChangeCount(changes, ConditionChange.class, INSERT_ACTION, 1); // 18 == 33
//        assertChangeCount(changes, LiteralChange.class, INSERT_ACTION, 2); // 18, 33
//
//        assertEquals(4, changes.size());
        // assertChangeCount(changes, ChangeType.ALTERNATIVE_PART_INSERT, 1);  //else ...
        // assertChangeCount(changes, ChangeType.CONDITION_EXPRESSION_INSERT, 1);  // 18 == 33
        // assertChangeCount(changes, ChangeType.STATEMENT_INSERT, 1);  // if ()
        // assertChangeCount(changes, ChangeType.RETURN_STATEMENT_INSERT, 1);  // return 4

    }

    @Test
    // bad example for IJM, doesn't match the if (GT and MtDiff do)
    public void canExtractSingleAlternativePartInsertElseIfNoBlock() throws IOException {
        String src = "public class Foo { public int bar() { if(true) return 1; return 2; } }";
        String dst = "public class Foo { public int bar() { if(true) return 1; else if(18 == 33) return 4; return 2; }}";

        Collection<SourceCodeChange> changes = classify(src, dst);
        extendedInfoDump(changes);

        assertChangeCount(changes, ElseBranchChange.class, INSERT_ACTION, 1); // else
        assertChangeCountMin(changes, IfBranchChange.class, INSERT_ACTION, 1); // if


        assertChangeCount(changes, ConditionChange.class, INSERT_ACTION, 1); // 18 == 33
        assertChangeCount(changes, LiteralChange.class, INSERT_ACTION, 2); // 18, 33

        assertChangeCount(changes, ReturnStatementChange.class, INSERT_ACTION, 1); // return
        assertChangeCount(changes, ReturnValueChange.class, INSERT_ACTION, 1); // 2
    }

    /*
     * ALTERNATIVE_PART_DELETE
     */
    @Test
    public void canExtractSingleAlternativePartDelete() throws IOException {
        String src = "public class Foo { public void bar() { if(true){int i = 8;} else{} }";
        String dst = "public class Foo { public void bar() { if(true){int i = 8;} } }";

        Collection<SourceCodeChange> changes = classify(src, dst);

        extendedInfoDump(changes);
        assertEquals(1, changes.size());
        assertChangeCount(changes, ElseBranchChange.class, DELETE_ACTION, 1); // else
        assertChangeCount(changes, BranchChange.class, DELETE_ACTION, 1); // else is also branch
    }

    @Test
    public void canExtractSingleAlternativePartDelete1() throws IOException {
        String src = "public class Foo { public int bar() { if(true) return 1; else return 4; return 2; }";
        String dst = "public class Foo { public int bar() { if(true) return 1; return 2; } }";

        Collection<SourceCodeChange> changes = classify(src, dst, true);

        extendedInfoDump(changes);
        assertEquals(3, changes.size());
        assertChangeCount(changes, ElseBranchChange.class, DELETE_ACTION, 1);
        assertChangeCount(changes, ReturnStatementChange.class, DELETE_ACTION, 1);
        assertChangeCount(changes, ReturnValueChange.class, DELETE_ACTION, 1);
    }

    @Test //Tests Only on Delted Else!
    public void canExtractSingleAlternativePartDeleteElseIf() throws IOException {
        String src = "public class Foo { public int bar() { if(true) return 1; else if(17 == 12) return 4; return 2; }";
        String dst = "public class Foo { public int bar() { if(true) return 1; return 2; } }";

        Collection<SourceCodeChange> changes = classify(src, dst);

        extendedInfoDump(changes);

        // else if + condition deleted
        assertChangeCount(changes, ElseBranchChange.class, DELETE_ACTION, 1);
//        assertChangeCount(changes, IfBranchChange.class, DELETE_ACTION, 1);
//        assertChangeCount(changes, ConditionChange.class, DELETE_ACTION, 1);
//        assertChangeCount(changes, LiteralChange.class, DELETE_ACTION, 2); // 17, 12
//        //return deleted
//        assertChangeCount(changes, ReturnStatementChange.class, DELETE_ACTION, 1);
//        assertChangeCount(changes, ReturnValueChange.class, DELETE_ACTION, 1);

//        assertEquals(7, changes.size());
    }

    /*
     * CONDITION_EXPRESSION_CHANGE
     */
    @Test
    public void canExtractSingleConditionExpressionChange() throws IOException {
        String src = "public class Foo { public void bar() { if(1 > 0){int i = 8;}}}";
        String dst = "public class Foo { public void bar() { if(true){int i = 8;}}}";

        Collection<SourceCodeChange> changes = classify(src, dst, true);

        extendedInfoDump(changes);

        // assertChangeCount(changes, ChangeType.CONDITION_EXPRESSION_CHANGE, 1);
        assertChangeCount(changes, LiteralChange.class, DELETE_ACTION, 2); // 1, 0
        assertChangeCount(changes, LiteralChange.class, INSERT_ACTION, 1); // true
        assertChangeCount(changes, ExpressionChange.class, DELETE_ACTION, 1); // 1 > 0
        assertChangeCount(changes, ConditionChange.class, UPDATE_ACTION, 1); // true



        assertEquals(5, changes.size());
    }

    @Test
    public void canExtractConditionExpressionChange() throws IOException {
        String src = "public class Foo { public void bar() { if(true){int i = 8;}}}";
        String dst = "public class Foo { public void bar() { if(true && SOMECLASS.foo()){int i = 8;}}}";

        Collection<SourceCodeChange> changes = classify(src, dst);

        // assertChangeCount(changes, ChangeType.CONDITION_EXPRESSION_CHANGE, 1);
    }

    @Test
    public void canExtractConditionExpressionChange1() throws IOException {
        String src = "public class Foo { public void bar() { if(true && SOMECLASS.foo()){int i = 8;}}}";
        String dst = "public class Foo { public void bar() { if(true){int i = 8;}}}";

        Collection<SourceCodeChange> changes = classify(src, dst);

        extendedInfoDump(changes);
        assertChangeCount(changes, ConditionChange.class, UPDATE_ACTION,1); // && ... removed
        assertChangeCount(changes, InvocationChange.class, DELETE_ACTION,1); // .foo()
        assertChangeCount(changes, TargetChange.class, DELETE_ACTION,1); //accessing SOMECLASS
    }

    @Test //TODO Check this test case
    public void canExtractConditionExpressionChang750661e2() throws IOException {
        String src = "public class Foo { public void bar() { if(true && SOMECLASS.foo()){int i = 8;}}}";
        String dst = "public class Foo { public void bar() { if(true && SOMEOTHERCLASS.foo().chained()){int i = 8;}}}";

        Collection<SourceCodeChange> changes = classify(src, dst);

        extendedInfoDump(changes);
        // assertChangeCount(changes, ChangeType.CONDITION_EXPRESSION_CHANGE, 1);
        // assertChangeCount(changes, ChangeType.METHOD_INVOCATION_INSERT, 1);
        // assertChangeCount(changes, ChangeType.METHOD_INVOCATION_INSERT, 1);
    }

    @Test
    public void canExtractChangeFromForWithoutInitializer() throws IOException {
        String src = "public class Foo { public void bar() { for(;true;) {} } }";
        String dst = "public class Foo { public void bar() { for(;;) {} } }";

        Collection<SourceCodeChange> changes = classify(src, dst);

        extendedInfoDump(changes);
        // assertChangeCount(changes, ChangeType.CONDITION_EXPRESSION_CHANGE, 1);
    }

    @Test
    public void canExtractChangesInForCondition_0() throws IOException {
        String src = "public class Foo { public void bar() { for(;yes();) {} } }";
        String dst = "public class Foo { public void bar() { for(;;) {} } }";

        Collection<SourceCodeChange> changes = classify(src, dst);

        // assertChangeCount(changes, ChangeType.CONDITION_EXPRESSION_CHANGE, 1);
        // assertChangeCount(changes, ChangeType.METHOD_INVOCATION_DELETE, 1);
    }
}
