package test.classification;

import at.aau.softwaredynamics.classifier.entities.SourceCodeChange;
import at.aau.softwaredynamics.classifier.types.*;
import static at.aau.softwaredynamics.classifier.actions.ActionType.*;
import org.junit.Test;

import java.io.IOException;
import java.util.Collection;


import static org.junit.Assert.assertEquals;

/**
 * Created by Veit on 28.11.2016.
 */
public class MethodInvocationTests extends ClassifierTestBase {

    @Test
    public void canClassifyLambdaAdd() throws IOException {
        String src = "public class Foo {" +
                "private JButton changePathButton;" +
                " public void bar() {" +
                "changePathButton1.addActionListener(null);" +
                " } }";
        String dst = "public class Foo {" +
                "private JButton changePathButton;" +
                " public void bar() {" +
                "changePathButton1.addActionListener(e -> {\n" +
                "        });" +
                " } }";


        Collection<SourceCodeChange> changes = classify(src, dst);

        // assertChangeCount(changes, ChangeType.LAMBDA_EXPRESSION_INSERT, 1);
    }

    @Test
    public void canClassifyLambdaDelete() throws IOException {
        String src = "public class Foo {" +
                "private JButton changePathButton;" +
                " public void bar() {" +
                "changePathButton1.addActionListener(e -> {\n" +
                "        });" +
                " } }";
        String dst = "public class Foo {" +
                "private JButton changePathButton;" +
                " public void bar() {" +
                "changePathButton1.addActionListener(null);" +
                " } }";

        Collection<SourceCodeChange> changes = classify(src, dst);
        // assertChangeCount(changes, ChangeType.LAMBDA_EXPRESSION_DELETE, 1);
    }

    @Test
    public void canClassifyAdditionalMethodInvocation() throws IOException {
        String src = "public class Foo" +
                "{" +
                "int i = 3;}";
        String dst = "public class Foo " +
                "{" +
                "int i = bar(3);" +
                "}";
        Collection<SourceCodeChange> changes = classify(src, dst);

        extendedInfoDump(changes);
        assertChangeCount(changes, InvocationChange.class,INSERT_ACTION,1);
        assertChangeCount(changes, ArgumentChange.class,INSERT_ACTION,1);
        assertChangeCount(changes, LiteralChange.class,DELETE_ACTION,1);
        assertChangeCount(changes, LiteralChange.class,INSERT_ACTION,1);


        assertEquals(4,changes.size());
    }

    @Test
    public void canClassifyAdditionalMethodInvocation1() throws IOException {
        String src = "public class Foo { private static int staticField; }";
        String dst = "public class Foo { private static int staticField = bar(3); }";
        Collection<SourceCodeChange> changes = classify(src, dst);

        extendedInfoDump(changes);
        assertChangeCount(changes, InvocationChange.class,INSERT_ACTION,1);
        assertChangeCount(changes, ArgumentChange.class,INSERT_ACTION,1);
        assertChangeCount(changes, LiteralChange.class,INSERT_ACTION,1);

        assertEquals(3,changes.size());    }

    @Test
    public void canClassifyDeletedMethodInvocation() throws IOException {
        String src = "public class Foo {int i = bar(3);}";
        String dst = "public class Foo {int i = 3;}";
        Collection<SourceCodeChange> changes = classify(src, dst);

        extendedInfoDump(changes);
        assertChangeCount(changes, InvocationChange.class,DELETE_ACTION,1);
        assertChangeCount(changes, LiteralChange.class,DELETE_ACTION,1);
        assertChangeCount(changes, ArgumentChange.class,DELETE_ACTION,1);
        assertChangeCount(changes, LiteralChange.class,INSERT_ACTION,1);

        assertEquals(4,changes.size());
    }

    @Test
    public void canClassifyDeletedMethodInvocation1() throws IOException {
        String src = "public class Foo { private static int staticField = bar(3);}";
        String dst = "public class Foo { private static int staticField; }";
        Collection<SourceCodeChange> changes = classify(src, dst);

        extendedInfoDump(changes);
        assertChangeCount(changes, InvocationChange.class,DELETE_ACTION,1);
        assertChangeCount(changes, LiteralChange.class,DELETE_ACTION,1);
        assertChangeCount(changes, ArgumentChange.class,DELETE_ACTION,1);

        assertEquals(3,changes.size());
    }

    @Test
    public void canClassifyChangedMethodInvocation1() throws IOException {
        String src = "public class Foo { private static int staticField = bar(3); }";
        String dst = "public class Foo { private static int staticField = bar(2); }";
        Collection<SourceCodeChange> changes = classify(src, dst);

        extendedInfoDump(changes);
        assertChangeCount(changes, ArgumentChange.class,UPDATE_ACTION,1);
        assertChangeCount(changes, LiteralChange.class,UPDATE_ACTION,1);
        assertEquals(2,changes.size());

    }

    @Test
    public void canClassifyChangedMethodInvocation2() throws IOException {
        String src = "public class Foo { private static int staticField = bar(3); }";
        String dst = "public class Foo { private static int staticField = bar(3, 2); }";
        Collection<SourceCodeChange> changes = classify(src, dst, true);

        extendedInfoDump(changes);
        assertChangeCount(changes, ArgumentChange.class,INSERT_ACTION,1);
        assertChangeCount(changes, LiteralChange.class,INSERT_ACTION,1);
        assertChangeCount(changes, InvocationChange.class,UPDATE_ACTION,1);
        assertEquals(3,changes.size());
    }

    @Test
    public void canClassifyChangedMethodInvocation3() throws IOException {
        String src = "public class Foo { private static int staticField = bar(3, 2);}";
        String dst = "public class Foo { private static int staticField = bar(2);}";
        Collection<SourceCodeChange> changes = classify(src, dst, true);

        extendedInfoDump(changes);
        assertChangeCount(changes, ArgumentChange.class,DELETE_ACTION,1);
        assertChangeCount(changes, LiteralChange.class,DELETE_ACTION,1);
        assertChangeCount(changes, InvocationChange.class,UPDATE_ACTION,1);
        assertEquals(3,changes.size());

    }

    @Test
    public void canClassifyChangedMethodInvocation4() throws IOException {
        String src = "public class Foo { Getreidemehlroller gmr = new Getreidemehlroller(); public void test() { bar(3, gmr); } }";
        String dst = "public class Foo { Getreidemehlroller gmr = new Getreidemehlroller(); public void test() { bar(3); } }";
        Collection<SourceCodeChange> changes = classify(src, dst);

        extendedInfoDump(changes);
        assertChangeCount(changes, ArgumentChange.class,DELETE_ACTION,1);
        assertChangeCount(changes, InvocationChange.class,UPDATE_ACTION,1);
        assertChangeCount(changes, FieldReadChange.class,DELETE_ACTION,1);
        assertEquals(3,changes.size());
    }

    @Test
    public void canClassifyChangedMethodInvocation5() throws IOException {
        String src = "public class Foo { Getreidemehlroller gmr = new Getreidemehlroller(); public void test() { bar(3, someMethod()); } }";
        String dst = "public class Foo { Getreidemehlroller gmr = new Getreidemehlroller(); public void test() { bar(3); } }";
        Collection<SourceCodeChange> changes = classify(src, dst);

        extendedInfoDump(changes);
        assertChangeCount(changes, InvocationChange.class,DELETE_ACTION,1);
        assertChangeCount(changes, ArgumentChange.class,DELETE_ACTION,1);
        assertChangeCount(changes, InvocationChange.class,UPDATE_ACTION,1);
        assertEquals(3,changes.size());

    }

    @Test
    public void canClassifyChangedMethodInvocation6() throws IOException {
        String src = "public class Foo {Getreidemehlroller gmr = new Getreidemehlroller(); public void test() { bar(3); } }";
        String dst = "public class Foo {Getreidemehlroller gmr = new Getreidemehlroller(); public void test() { bar(3, gmr); } }";
        Collection<SourceCodeChange> changes = classify(src, dst);

        extendedInfoDump(changes);
        assertChangeCount(changes, ArgumentChange.class,INSERT_ACTION,1);
        assertChangeCount(changes, InvocationChange.class,UPDATE_ACTION,1);
        assertChangeCount(changes, FieldReadChange.class,INSERT_ACTION,1);
        assertEquals(3,changes.size());
    }
}
