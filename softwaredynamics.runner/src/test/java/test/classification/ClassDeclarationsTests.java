package test.classification;

import at.aau.softwaredynamics.classifier.actions.InsertAction;
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
 * Created by thomas on 18.11.2016.
 */
public class ClassDeclarationsTests extends ClassifierTestBase {

    @Test
    public void canClassifyAdditionalClass() throws IOException {
        String src = "";
        String dst = "public class Foo3 {}";
        Collection<SourceCodeChange> changes = classify(src, dst);
        extendedInfoDump(changes);
        assertChangeCount(changes, ClassChange.class,INSERT_ACTION,1);
        assertChangeCount(changes, ModifierChange.class,INSERT_ACTION,1);
        assertEquals(changes.size(),2);
    }

    @Test
    public void canClassifyInsertModifier() throws IOException {
        String src = "public class Foo {}";
        String dst = "public abstract class Foo {}";
        Collection<SourceCodeChange> changes = classify(src, dst);
        extendedInfoDump(changes);
        assertChangeCount(changes, ModifierChange.class,INSERT_ACTION,1);
        assertEquals(changes.size(),1);
    }

    @Test
    public void canClassifyUpdateModifier() throws IOException {
        String src = "public class Foo {}";
        String dst = "protected class Foo {}";
        Collection<SourceCodeChange> changes = classify(src, dst);
        extendedInfoDump(changes);
        assertChangeCount(changes, ModifierChange.class,UPDATE_ACTION,1);
        assertEquals(changes.size(),1);
    }

    @Test
    public void canClassifyDeleteModifier() throws IOException {
        String src = "public class Foo {}";
        String dst = "class Foo {}";
        Collection<SourceCodeChange> changes = classify(src, dst);
        extendedInfoDump(changes);
        assertChangeCount(changes, ModifierChange.class,DELETE_ACTION,1);
        assertEquals(changes.size(),1);
    }

    @Test
    public void canClassifyAdditionalClass1() throws IOException {
        String src = "";
        String dst = "public class Foo3 {class Bar{}}";
        Collection<SourceCodeChange> changes = classify(src, dst);
        extendedInfoDump(changes);
        assertChangeCount(changes, ClassChange.class,INSERT_ACTION,2);
        assertChangeCount(changes, ModifierChange.class, INSERT_ACTION,1);
        assertEquals(changes.size(),3);
    }

    @Test
    public void canClassifyRemovedClass() throws IOException {
        String src = "public class Foo3 {}";
        String dst = "";
        Collection<SourceCodeChange> changes = classify(src, dst);
        extendedInfoDump(changes);
        assertChangeCount(changes, ClassChange.class,DELETE_ACTION,1);
        assertChangeCount(changes,ModifierChange.class, DELETE_ACTION,1);
        assertEquals(changes.size(),2);
    }

    @Test
    public void canClassifyRemovedClassInner() throws IOException {
        String src = "public class Foo3 { public class Bar {} }";
        String dst = "public class Foo3 {}";
        Collection<SourceCodeChange> changes = classify(src, dst);
        extendedInfoDump(changes);
        assertChangeCount(changes, ClassChange.class,DELETE_ACTION,1);
        assertChangeCount(changes, ModifierChange.class, DELETE_ACTION,1);
        assertEquals(changes.size(),2);
    }

    @Test
    public void canClassifyClassRenaming() throws IOException {
        String src = "public class Foo { class Foo2{}}";
        String dst = "public class Foo3 { class Bar{}}";
        Collection<SourceCodeChange> changes = classify(src, dst, true);
        extendedInfoDump(changes);
        assertChangeCount(changes, ClassChange.class,RENAME_ACTION,2);
        assertEquals(changes.size(),2);
    }

    @Test
    public void canClassifyParentClassChange() throws IOException {
        String src = "public class Foo extends A{}";
        String dst = "public class Foo extends B{}";
        Collection<SourceCodeChange> changes = classify(src, dst, true);
        extendedInfoDump(changes);
        assertChangeCount(changes, InheritanceChange.class,UPDATE_ACTION,1);
        assertEquals(changes.size(),1);
    }

    @Test
    public void canClassifyParentClassDelete() throws IOException {
        String src = "public class Foo extends A{}";
        String dst = "public class Foo{}";
        Collection<SourceCodeChange> changes = classify(src, dst);
        extendedInfoDump(changes);
        assertChangeCount(changes, InheritanceChange.class,DELETE_ACTION,1);
        assertEquals(changes.size(),1);
    }

    @Test //TODO CHECK PARAMETRIZED CLASSES IN GENERAL
    public void noMisclassifyGenericChange() throws IOException {
        String src = "public class Foo<A> {}";
        String dst = "public class Foo<B> {}";
        Collection<SourceCodeChange> changes = classify(src, dst);
        extendedInfoDump(changes);
        assertChangeCount(changes, InheritanceChange.class,DELETE_ACTION,0);
        assertChangeCount(changes, InheritanceChange.class,UPDATE_ACTION,0);
        assertChangeCount(changes, InheritanceChange.class,RENAME_ACTION,0);
        // assertChangeCount(changes, ChangeType.PARENT_CLASS_DELETE, 0);
    }

    @Test
    public void canClassifyParentClassInsert() throws IOException {
        String src = "public class Foo{}";
        String dst = "public class Foo extends B {}";
        Collection<SourceCodeChange> changes = classify(src, dst);
        extendedInfoDump(changes);
        assertChangeCount(changes, InheritanceChange.class,INSERT_ACTION,1);
        assertEquals(changes.size(),1);
    }

    @Test
    public void AddClassWithoutSuperClassDoesNotReturnParentClassInsert() throws IOException {
        String src = "";
        String dst = "public class Foo { private Foo _foo; }";
        Collection<SourceCodeChange> changes = classify(src, dst);
        extendedInfoDump(changes);
        assertChangeCount(changes, InheritanceChange.class,INSERT_ACTION,0);
        // assertChangeCount(changes, ChangeType.PARENT_CLASS_INSERT, 0);
    }

    @Test
    public void CanClassifyParentClassInsertInner() throws IOException {
        String src = "public class Foo { private class Bar {}}";
        String dst = "public class Foo { private class Bar extends A {}}";
        Collection<SourceCodeChange> changes = classify(src, dst);
        extendedInfoDump(changes);
        assertChangeCount(changes, InheritanceChange.class,INSERT_ACTION,1);
        assertEquals(changes.size(),1);
    }

    @Test
    public void CanClassifyParentInterfaceChange() throws IOException {
        String src = "public class Foo implements A{}";
        String dst = "public class Foo implements B{}";
        Collection<SourceCodeChange> changes = classify(src, dst);

        extendedInfoDump(changes);
        assertChangeCount(changes, InterfaceChange.class,UPDATE_ACTION,1);
        assertEquals(changes.size(),1);
    }

    @Test
    public void CanClassifyParentInterfaceDelete() throws IOException {
        String src = "public class Foo implements A{}";
        String dst = "public class Foo{}";
        Collection<SourceCodeChange> changes = classify(src, dst);

        extendedInfoDump(changes);
        assertChangeCount(changes, InterfaceChange.class,DELETE_ACTION,1);
        assertEquals(changes.size(),1);
    }

    @Test
    public void CanClassifyParentInterfaceInsert() throws IOException {
        String src = "public class Foo{}";
        String dst = "public class Foo implements B{}";
        Collection<SourceCodeChange> changes = classify(src, dst);
        extendedInfoDump(changes);
        assertChangeCount(changes, InterfaceChange.class,INSERT_ACTION,1);
        assertEquals(changes.size(),1);
    }

    @Test
    public void CanClassifyMultipleParentInterfaceInsert() throws IOException {
        String src = "public class Foo implements A {}";
        String dst = "public class Foo implements A,B,C {}";
        Collection<SourceCodeChange> changes = classify(src, dst);
        extendedInfoDump(changes);
        assertChangeCount(changes, InterfaceChange.class,INSERT_ACTION,2);
        assertEquals(changes.size(),2);
    }

    @Test //FIXME Unclassified change for now
    public void CanClassifyInterfaceInheritance_ForQualifiedType() throws IOException {
        String src = "public interface Foo{}";
        String dst = "public interface Foo extends java.lang.Cloneable {}";
        Collection<SourceCodeChange> changes = classify(src, dst, true);
        extendedInfoDump(changes);
        assertChangeCount(changes, InheritanceChange.class,INSERT_ACTION,1);
        assertEquals(changes.size(),1);
    }

    @Test
    public void CanClassifyParentInterfaceInsert_ForQualifiedType() throws IOException {
        String src = "public class Foo{}";
        String dst = "public class Foo implements java.lang.Cloneable{}";
        Collection<SourceCodeChange> changes = classify(src, dst);

        extendedInfoDump(changes);
        assertChangeCount(changes, InterfaceChange.class,INSERT_ACTION,1);
        assertEquals(changes.size(),1);
    }

    @Test
    public void CanClassifyParentInterfaceInsert_ForParamterizedType() throws IOException {
        String src = "public class Foo{}";
        String dst = "public class Foo implements A.B.C<Integer> {}";
        Collection<SourceCodeChange> changes = classify(src, dst);

        extendedInfoDump(changes);
        assertChangeCount(changes, InterfaceChange.class,INSERT_ACTION,1); //Maybe INS PARAM. Type or something thelike as well
        assertEquals(changes.size(),1);
    }

    @Test
    public void CanClassifyRemovedFunctionality() throws IOException {
        String src = "public class Foo {public int bar(){return 42;}}";
        String dst = "public class Foo {}";
        Collection<SourceCodeChange> changes = classify(src, dst);
        extendedInfoDump(changes);
        assertChangeCount(changes, MethodChange.class,DELETE_ACTION,1);
        assertChangeCount(changes, ModifierChange.class,DELETE_ACTION,1);
        assertChangeCount(changes, ReturnStatementChange.class, DELETE_ACTION,1);
        assertChangeCount(changes, ReturnValueChange.class, DELETE_ACTION,1);
        assertChangeCount(changes, ReturnTypeChange.class, DELETE_ACTION,1);
        assertEquals(5,changes.size());
    }

    @Test
    public void CanClassifyAddedFunctionality() throws IOException {
        String src = "public class Foo {}";
        String dst = "public class Foo {public int bar() {return 42;}}";
        Collection<SourceCodeChange> changes = classify(src, dst);

        extendedInfoDump(changes);
        assertChangeCount(changes, MethodChange.class, INSERT_ACTION,1); //bar
        assertChangeCount(changes, ReturnStatementChange.class,INSERT_ACTION,1); //int
        assertChangeCount(changes, ReturnTypeChange.class,INSERT_ACTION,1); //int return 42
        assertChangeCount(changes, ReturnValueChange.class,INSERT_ACTION,1); //42
        assertChangeCount(changes, ModifierChange.class,INSERT_ACTION,1);
        assertEquals(changes.size(),5);
    }

    @Test
    public void CanClassifyMuliMethodAdd() throws IOException {
        String src = "public class Foo {}";
        String dst = "public class Foo {" +
                "public int bar() { return 42; }" +
                "public int bar1(int i, int j) { return 42; }" +
                "}";

        Collection<SourceCodeChange> changes = classify(src, dst);
        extendedInfoDump(changes);
        assertChangeCount(changes, MethodChange.class, INSERT_ACTION,2);
        assertChangeCount(changes, ReturnStatementChange.class,INSERT_ACTION,2);
        assertChangeCount(changes, ReturnTypeChange.class,INSERT_ACTION,2);
        assertChangeCount(changes, ParameterChange.class,INSERT_ACTION,2);
        assertChangeCount(changes, ReturnValueChange.class,INSERT_ACTION,2);
        assertChangeCount(changes, ParameterTypeChange.class,INSERT_ACTION,2);
        assertChangeCount(changes, ModifierChange.class,INSERT_ACTION,2);
        assertEquals(changes.size(),14);
    }

    @Test
    public void CanClassifyMuliMethodAddWithThrow() throws IOException {
        String src = "public class Foo {}";
        String dst = "public class Foo {" +
                "public int bar() throws Exception { return 42; }" +
                "public int bar1(int i, int j) throws Exception { return 42; }" +
                "}";

        Collection<SourceCodeChange> changes = classify(src, dst);

        extendedInfoDump(changes);
        assertChangeCount(changes, MethodChange.class, INSERT_ACTION,2);
        assertChangeCount(changes, ReturnStatementChange.class,INSERT_ACTION,2);
        assertChangeCount(changes, ReturnTypeChange.class,INSERT_ACTION,2);
        assertChangeCount(changes, ParameterChange.class,INSERT_ACTION,2);
        assertChangeCount(changes, ReturnValueChange.class,INSERT_ACTION,2);
        assertChangeCount(changes, ParameterTypeChange.class,INSERT_ACTION,2);
        assertChangeCount(changes, ThrowableChange.class, INSERT_ACTION,2);
        assertEquals(changes.size(),16);
    }
}
