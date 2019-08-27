package test.classification;

import at.aau.softwaredynamics.classifier.entities.SourceCodeChange;
import at.aau.softwaredynamics.classifier.types.*;
import org.junit.Test;

import java.io.IOException;
import java.util.Collection;

import static at.aau.softwaredynamics.classifier.actions.ActionType.*;
import static org.junit.Assert.assertEquals;

/**
 * Created by thomas on 18.11.2016.
 */
public class MethodDeclarationTests extends ClassifierTestBase {

    /*
RETURN_TYPE_CHANGES
 */
    @Test
    public void canExtractSingleReturnTypeChange() throws IOException {
        String src = "public class Foo { public int bar() { return 9; } }";
        String dst = "public class Foo { public float bar() { return 9f; } }";

        Collection<SourceCodeChange> changes = classify(src, dst);

        extendedInfoDump(changes);
        assertChangeCount(changes, ReturnTypeChange.class,UPDATE_ACTION,1);
        assertChangeCount(changes, ReturnValueChange.class,UPDATE_ACTION,1);
        assertEquals(2, changes.size());
    }

    @Test
    public void canExtractSingleReturnTypeChange1() throws IOException {
        String src = "public class Foo { public int bar() { return 9; } }";
        String dst = "public class Foo { public Barr bar() { return 9; } }";

        Collection<SourceCodeChange> changes = classify(src, dst);
        extendedInfoDump(changes);

        assertChangeCount(changes, ReturnTypeChange.class,UPDATE_ACTION,1);
        assertEquals(1, changes.size());
    }

    @Test
    public void canExtractSingleReturnTypeInsert() throws IOException {
        String src = "public class Foo { public void bar() {} }";
        String dst = "public class Foo { public float bar() { return 9f; } }";

        Collection<SourceCodeChange> changes = classify(src, dst);

        extendedInfoDump(changes);
        assertChangeCount(changes, ReturnTypeChange.class,UPDATE_ACTION,1);
        assertChangeCount(changes, ReturnValueChange.class,INSERT_ACTION,1);
        assertChangeCount(changes, ReturnStatementChange.class,INSERT_ACTION,1);
        assertEquals(3, changes.size());
    }

    @Test
    public void canExtractSingleReturnTypeDelete() throws IOException {
        String src = "public class Foo { public int bar() { return 9; } }";
        String dst = "public class Foo { public void bar() {} }";

        Collection<SourceCodeChange> changes = classify(src, dst);
        extendedInfoDump(changes);
        assertChangeCount(changes, ReturnTypeChange.class,UPDATE_ACTION,1);
        assertChangeCount(changes, ReturnValueChange.class,DELETE_ACTION,1);
        assertChangeCount(changes, ReturnStatementChange.class,DELETE_ACTION,1);
        assertEquals(3, changes.size());
    }

    @Test
    public void canExtractSingleReturnTypeDelete2() throws IOException {
        String src = "public class Foo { public int bar() { } }";
        String dst = "public class Foo { public void bar() { } }";

        Collection<SourceCodeChange> changes = classify(src, dst);

        extendedInfoDump(changes);

        assertChangeCount(changes, ReturnTypeChange.class,UPDATE_ACTION,1);
        assertEquals(1,changes.size());
    }

    @Test
    public void canExtractSingleReturnTypeDelete1() throws IOException {
        String src = "public class Foo { public Barr bar() { } }";
        String dst = "public class Foo { public void bar() { } }";

        Collection<SourceCodeChange> changes = classify(src, dst);

        extendedInfoDump(changes);
        assertChangeCount(changes, ReturnTypeChange.class,UPDATE_ACTION,1);
        assertEquals(1, changes.size());
    }

    @Test
    public void canExtractReturnTypeChangeForComplexTypes() throws IOException {
        String src = "public class Foo { " +
                "private MappingStore get(TreeContext src, TreeContext dst) {\n" +
                "        MappingStore retVal = new MappingStore();\n" +
                "        Matcher m = new CompositeMatchers.ModifiedClassicGumtree(src.getRoot(), dst.getRoot(), new MappingStore());\n" +
                "        m.match();\n" +
                "        return retVal;\n" +
                "    }" +
                "}";
        String dst = "public class Foo { " +
                "private Matcher get(TreeContext src, TreeContext dst) {\n" +
                "        MappingStore retVal = new MappingStore();\n" +
                "        Matcher m = new CompositeMatchers.ModifiedClassicGumtree(src.getRoot(), dst.getRoot(), new MappingStore());\n" +
                "        m.match();\n" +
                "        return m;\n" +
                "    }" +
                "}";

        Collection<SourceCodeChange> changes = classify(src, dst);

        extendedInfoDump(changes);
        assertChangeCount(changes, ReturnTypeChange.class,UPDATE_ACTION,1);
        assertChangeCount(changes, ReturnValueChange.class,UPDATE_ACTION,1);
        assertEquals(2, changes.size());

        // assertChangeCount(changes, ChangeType.RETURN_TYPE_CHANGE, 1); // See MethodDeclarationTests#canExtractReturnTypeChangeInStaticMethod
    }

    @Test
    public void canExtractReturnTypeChangeInStaticMethod() throws IOException {
        String src = "public class Foo { " +
                "private static MappingStore get(TreeContext src, TreeContext dst) {\n" +
                "        MappingStore retVal = new MappingStore();\n" +
                "        Matcher m = new CompositeMatchers.ModifiedClassicGumtree(src.getRoot(), dst.getRoot(), new MappingStore());\n" +
                "        m.match();\n" +
                "        return retVal;\n" +
                "    }" +
                "}";
        String dst = "public class Foo { " +
                "private static Matcher get(TreeContext src, TreeContext dst) {\n" +
                "        MappingStore retVal = new MappingStore();\n" +
                "        Matcher m = new CompositeMatchers.ModifiedClassicGumtree(src.getRoot(), dst.getRoot(), new MappingStore());\n" +
                "        m.match();\n" +
                "        return m;\n" +
                "    }" +
                "}";

        Collection<SourceCodeChange> changes = classify(src, dst);

        extendedInfoDump(changes);
        assertChangeCount(changes, ReturnTypeChange.class,UPDATE_ACTION,1);
        assertChangeCount(changes, ReturnValueChange.class,UPDATE_ACTION,1);
        assertEquals(2, changes.size());
    }

    @Test
    public void canExtractReturnTypeChangeInStaticMethodWithoutVisibiltyModifier() throws IOException {
        String src = "public class Foo { " +
                "static MappingStore get(TreeContext src, TreeContext dst) {\n" +
                "        MappingStore retVal = new MappingStore();\n" +
                "        Matcher m = new CompositeMatchers.ModifiedClassicGumtree(src.getRoot(), dst.getRoot(), new MappingStore());\n" +
                "        m.match();\n" +
                "        return retVal;\n" +
                "    }" +
                "}";
        String dst = "public class Foo { " +
                "static Matcher get(TreeContext src, TreeContext dst) {\n" +
                "        MappingStore retVal = new MappingStore();\n" +
                "        Matcher m = new CompositeMatchers.ModifiedClassicGumtree(src.getRoot(), dst.getRoot(), new MappingStore());\n" +
                "        m.match();\n" +
                "        return m;\n" +
                "    }" +
                "}";

        Collection<SourceCodeChange> changes = classify(src, dst);

        extendedInfoDump(changes);
        assertChangeCount(changes, ReturnTypeChange.class,UPDATE_ACTION,1);
        assertChangeCount(changes, ReturnValueChange.class,UPDATE_ACTION,1);
        assertEquals(2, changes.size());     }

    /*
    Method Renaming
     */
    @Test
    public void canExtractSingleMethodRename() throws IOException {
        String src = "public class Foo { public void bar() {} }";
        String dst = "public class Foo { public void bar2() {} }";

        Collection<SourceCodeChange> changes = classify(src, dst);


        assertChangeCount(changes, MethodChange.class,RENAME_ACTION,1);
        assertEquals(1,changes.size());

    }

    /*
    Parameter Changes
    */
    @Test
    public void canExtractSingleParameterDelete() throws IOException {
        String src = "public class Foo { public void bar(int i) {} }";
        String dst = "public class Foo { public void bar() {} }";

        Collection<SourceCodeChange> changes = classify(src, dst);

        assertChangeCount(changes, ParameterChange.class,DELETE_ACTION,1);
        assertChangeCount(changes, ParameterTypeChange.class,DELETE_ACTION,1);
        assertEquals(2,changes.size());
    }

    @Test
    public void canExtractSingleParameterInsert() throws IOException {
        String src = "public class Foo { public void bar() {} }";
        String dst = "public class Foo { public void bar(int i) {} }";

        Collection<SourceCodeChange> changes = classify(src, dst);

        extendedInfoDump(changes);

        assertChangeCount(changes, ParameterChange.class,INSERT_ACTION,1);
        assertChangeCount(changes, ParameterTypeChange.class,INSERT_ACTION,1);
        assertEquals(2,changes.size());
    }

    @Test
    public void canExtractSingleParameterTypeChange() throws IOException {
        String src = "public class Foo { public void bar(int i) {} }";
        String dst = "public class Foo { public void bar(double i) {} }";

        Collection<SourceCodeChange> changes = classify(src, dst);

        assertChangeCount(changes, ParameterTypeChange.class,UPDATE_ACTION,1);
        assertEquals(1,changes.size());
    }

    @Test //TODO this needs to be adressed in the SPOON -> ITree conversion
    public void canExctractParameterTypeChangeForParameterizedType() throws IOException {
        String src = "public class Foo { public void bar(List param) {} }";
        String dst = "public class Foo { public void bar(List<String> param) {} }";

        Collection<SourceCodeChange> changes = classify(src, dst,true);


        extendedInfoDump(changes);
//        assertChangeCount(changes, ParameterTypeChange.class,UPDATE_ACTION,1); // maybe this should be updated as well.
        assertChangeCount(changes, TypeArgumentChange.class,INSERT_ACTION,1); // List gets Type Argument
        assertEquals(1,changes.size());
    }

    @Test
    public void canExctractParameterTypeChangeForArrayType() throws IOException {
        String src = "public class Foo { public void bar(String[] param) {} }";
        String dst = "public class Foo { public void bar(List<String> param) {} }";

        Collection<SourceCodeChange> changes = classify(src, dst);

        extendedInfoDump(changes);
        assertChangeCount(changes, ParameterTypeChange.class,UPDATE_ACTION,1); // Parameter changes type
        assertChangeCount(changes, TypeArgumentChange.class, INSERT_ACTION,1); // Changed type gets a TypeArgument
        assertEquals(2,changes.size());
    }

    @Test
    public void canExtractParameterTypeChangeFromConstructor() throws IOException {
        String src = "public class Foo { public Foo(int i) {} }";
        String dst = "public class Foo { public Foo(double i) {} }";

        Collection<SourceCodeChange> changes = classify(src, dst);

        extendedInfoDump(changes);
        assertChangeCount(changes, ParameterTypeChange.class,UPDATE_ACTION,1);
        assertEquals(1,changes.size());
    }

    @Test
    public void canExtractSingleParameterRename() throws IOException {
        String src = "public class Foo { public void bar(int i) {} }";
        String dst = "public class Foo { public void bar(int d) {} }";

        Collection<SourceCodeChange> changes = classify(src, dst);

        extendedInfoDump(changes);
        assertChangeCount(changes, ParameterChange.class, RENAME_ACTION,1);
        assertEquals(1,changes.size());
    }

    @Test
    public void canExtractSingleParameterFullChange() throws IOException {
        String src = "public class Foo { public void bar(int i) {} }";
        String dst = "public class Foo { public void bar(double d) {} }";

        Collection<SourceCodeChange> changes = classify(src, dst);
        extendedInfoDump(changes);


        assertChangeCount(changes, ParameterChange.class, RENAME_ACTION,1);
        assertChangeCount(changes, ParameterTypeChange.class, UPDATE_ACTION,1);
        assertEquals(2,changes.size());
    }

    @Test
    public void canExtractSingleParameterOrderingChange() throws IOException {
        String src = "public class Foo { public void bar(int i, double d) {} }";
        String dst = "public class Foo { public void bar(double d, int i) {} }";

        Collection<SourceCodeChange> changes = classify(src, dst);
        extendedInfoDump(changes);

        assertChangeCount(changes, ParameterChange.class, ORDERING_CHANGE_ACTION,1);
        assertEquals(1, changes.size());
    }

    @Test
    public void canExtractSingleParameterOrderingChange1() throws IOException {
        String src = "public class Foo { public void bar(Foo foo, double d) {} }";
        String dst = "public class Foo { public void bar(double d, Foo foo) {} }";

        Collection<SourceCodeChange> changes = classify(src, dst);
        extendedInfoDump(changes);

        assertChangeCount(changes, ParameterChange.class, ORDERING_CHANGE_ACTION,1);
        assertEquals(1, changes.size());
    }

    @Test
    public void canExtractMultipleParameterOrderingChange() throws IOException {
        String src = "public class Foo { public void bar(Foo foo, double d, int i, float f) {} }";
        String dst = "public class Foo { public void bar(double d, Foo foo, float f, int i) {} }";

        Collection<SourceCodeChange> changes = classify(src, dst, true);
        extendedInfoDump(changes);

        assertChangeCount(changes, ParameterChange.class, ORDERING_CHANGE_ACTION,2);
        assertEquals(2, changes.size());
    }

    @Test
    public void AddThrowsIsClassifiedIsIgnored() throws IOException {
        String src = "public class Foo {public int bar() {return 42;}}";
        String dst = "public class Foo {public int bar() throws IOException {return 42;}}";
        Collection<SourceCodeChange> changes = classify(src, dst);

        extendedInfoDump(changes);

        //assertChangeCount(changes, ExceptionChange.class, INSERT_ACTION,2);
        assertEquals(1, changes.size());

        //assertEquals(changes.size(),0); // WHY IS (WAS) THERE NO CHANGE EXPECTED HERE?
    }

}
