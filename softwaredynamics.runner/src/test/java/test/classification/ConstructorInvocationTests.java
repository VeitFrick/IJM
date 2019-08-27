package test.classification;

import at.aau.softwaredynamics.classifier.entities.SourceCodeChange;
import at.aau.softwaredynamics.classifier.types.*;
import org.junit.Test;

import java.io.IOException;
import java.util.Collection;

import static at.aau.softwaredynamics.classifier.actions.ActionType.*;
import static org.junit.Assert.assertEquals;

/**
 * Created by Veit on 28.11.2016.
 */
public class ConstructorInvocationTests extends ClassifierTestBase {

    @Test
    public void canClassifyAdditionalConstructorInvocation() throws IOException {
        String src = "public class Foo {}";
        String dst = "public class Foo {Bar i = new Bar();}";
        Collection<SourceCodeChange> changes = classify(src, dst);
        extendedInfoDump(changes);

        assertChangeCount(changes, FieldTypeChange.class,INSERT_ACTION,1);
        assertChangeCount(changes, FieldChange.class,INSERT_ACTION,1);
        assertChangeCount(changes, ConstructorInvocationChange.class,INSERT_ACTION,1);


        assertEquals(3, changes.size());

    }

    @Test
    public void canClassifyAdditionalConstructorInvocationAndParameter() throws IOException {
        String src = "public class Foo {}";
        String dst = "public class Foo {Bar i = new Bar(new Exterminatus());}";
        Collection<SourceCodeChange> changes = classify(src, dst);

        extendedInfoDump(changes);

        assertChangeCount(changes, FieldTypeChange.class,INSERT_ACTION,1);
        assertChangeCount(changes, FieldChange.class,INSERT_ACTION,1);
        assertChangeCount(changes, ArgumentChange.class,INSERT_ACTION,1);
        assertChangeCount(changes, ConstructorInvocationChange.class,INSERT_ACTION,2);


        assertEquals(5, changes.size());
    }

    @Test
    public void canClassifyAdditionalConstructorInvocation1() throws IOException {
        String src = "public class Foo {public Foo(){System.out.println(\"Hi!\");} public void bar(){}}";
        String dst = "public class Foo {public Foo(){System.out.println(\"Hi!\");} public void bar(){Foo f = new Foo();}}";
        Collection<SourceCodeChange> changes = classify(src, dst);

        extendedInfoDump(changes);

        assertChangeCount(changes, VariableDeclarationChange.class,INSERT_ACTION,1);
        assertChangeCount(changes, VariableTypeChange.class,INSERT_ACTION,1);
        assertChangeCount(changes, ConstructorInvocationChange.class,INSERT_ACTION,1);


        assertEquals(3, changes.size());
    }

    @Test
    public void canClassifyDeletedConstructorInvocation() throws IOException {
        String src = "public class Foo {Bar i = new Bar();}";
        String dst = "public class Foo {}";
        Collection<SourceCodeChange> changes = classify(src, dst);

        extendedInfoDump(changes);

        assertChangeCount(changes, FieldTypeChange.class,DELETE_ACTION,1);
        assertChangeCount(changes, FieldChange.class,DELETE_ACTION,1);
        assertChangeCount(changes, ConstructorInvocationChange.class,DELETE_ACTION,1);


        assertEquals(3, changes.size());
    }

    @Test
    public void canClassifyChangedConstructorInvocation2() throws IOException {
        String src = "public class Foo {Bar i = new Bar(1);}";
        String dst = "public class Foo {Bar i = new Bar(1, 2);}";
        Collection<SourceCodeChange> changes = classify(src, dst);

        extendedInfoDump(changes);

        assertChangeCount(changes, ArgumentChange.class,INSERT_ACTION,1);
        assertChangeCount(changes, LiteralChange.class,INSERT_ACTION,1);
        assertChangeCount(changes, ConstructorInvocationChange.class,UPDATE_ACTION,1);


        assertEquals(3, changes.size());
    }

    @Test
    public void canClassifyChangedConstructorInvocation25() throws IOException {
        String src = "public class Foo {Bar i = new Bar(1); public Foo() {} }";
        String dst = "public class Foo {Bar i = new Bar(1, 2); public Foo(int i) { this(); } public Foo() {}}";
        Collection<SourceCodeChange> changes = classify(src, dst);
        extendedInfoDump(changes);

        assertChangeCount(changes, LiteralChange.class,INSERT_ACTION,1);
        assertChangeCount(changes, ConstructorChange.class,INSERT_ACTION,1);
        assertChangeCount(changes, ConstructorInvocationChange.class,UPDATE_ACTION,1);
        assertChangeCount(changes, ModifierChange.class, INSERT_ACTION,1);
        assertChangeCount(changes, ParameterTypeChange.class,INSERT_ACTION,1);
        assertChangeCount(changes, ParameterChange.class,INSERT_ACTION,1);
        assertChangeCount(changes, ThisInvocationChange.class,INSERT_ACTION,1);
        assertChangeCount(changes, ArgumentChange.class,INSERT_ACTION,1);


        assertEquals(8, changes.size());

    }

    //FIXME
    @Test
    public void canClassifyChangedConstructorInvocation3() throws IOException {
        String src = "public class Foo {Bar i = new Bar();}";
        String dst = "public class Foo {Bar i = new Bar(1);}";
        Collection<SourceCodeChange> changes = classify(src, dst);
        extendedInfoDump(changes);
        assertChangeCount(changes, ArgumentChange.class, INSERT_ACTION, 1);
    }

    //FIXME
    @Test
    public void canClassifyChangedConstructorInvocation4() throws IOException {
        String src = "public class Foo {Bar i = new Bar(3, 2);}";
        String dst = "public class Foo {Bar i = new Bar(2);}";
        Collection<SourceCodeChange> changes = classify(src, dst);

        //assertChangeCount(changes, ChangeType.ARGUMENT_DELETE, 1);
    }

    @Test //TODO Where is the "Type Access Delete" coming From? (FOO)
    public void canClassifyChangedConstructorInvocation5() throws IOException {
        String src = "public class Foo { Getreidemehlroller gmr = new Getreidemehlroller(); Bar i = new Bar(3, gmr);}";
        String dst = "public class Foo { Getreidemehlroller gmr = new Getreidemehlroller(); Bar i = new Bar(3);}";
        Collection<SourceCodeChange> changes = classify(src, dst);
        extendedInfoDump(changes);

        // assertChangeCount(changes, ChangeType.ARGUMENT_DELETE, 1);

    }

    @Test
    public void canClassifyChangedConstructorInvocation6() throws IOException {
        String src = "public class Foo {Getreidemehlroller gmr = new Getreidemehlroller(); Bar i = new Barar(3);}";
        String dst = "public class Foo {Getreidemehlroller gmr = new Getreidemehlroller(); Bar i = new Barar(3, gmr);}";
        Collection<SourceCodeChange> changes = classify(src, dst);

        // assertChangeCount(changes, ChangeType.ARGUMENT_INSERT, 1);
    }
}
