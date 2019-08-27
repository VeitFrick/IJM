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
public class ClassAndMethodsTests extends ClassifierTestBase {

    @Test
    public void canGetClassName1() throws IOException {
        String src = "public class Foo {public class Bar{}}";
        String dst = "public class Foo {public class Bar{public class Three{int x = 2;}}}";
        Collection<SourceCodeChange> changes = classify(src, dst);

        extendedInfoDump(changes);

        assertChangeCount(changes, ClassChange.class,INSERT_ACTION,1);
        assertChangeCount(changes, FieldChange.class,INSERT_ACTION,1);
        assertChangeCount(changes, LiteralChange.class,INSERT_ACTION,1);
        assertChangeCount(changes, FieldTypeChange.class,INSERT_ACTION,1);
        assertChangeCount(changes, ModifierChange.class,INSERT_ACTION,1);

        assertEquals(5, changes.size());
    }

    @Test
    public void canGetClassName2() throws IOException {
        String src = "public class Foo {public class Bar{public class Three{}}}";
        String dst = "public class Foo {public class Bar{public class Three{public class Four{int x = 1;}}}}";
        Collection<SourceCodeChange> changes = classify(src, dst);

        extendedInfoDump(changes);

        assertChangeCount(changes, ClassChange.class,INSERT_ACTION,1);
        assertChangeCount(changes, FieldChange.class,INSERT_ACTION,1);
        assertChangeCount(changes, LiteralChange.class,INSERT_ACTION,1);
        assertChangeCount(changes, FieldTypeChange.class,INSERT_ACTION,1);
        assertChangeCount(changes, ModifierChange.class,INSERT_ACTION,1);

        assertEquals(5, changes.size());
    }


    @Test
    public void canGetMethodName0() throws IOException {
        String src = "public class Foo {public class Bar{int x = 1;}}";
        String dst = "public class Foo {public class Bar{public static void hello(double d){int x = 1}}}";
        Collection<SourceCodeChange> changes = classify(src, dst);

        extendedInfoDump(changes);

        assertChangeCount(changes, MethodChange.class,INSERT_ACTION,1);
        assertChangeCount(changes, ReturnTypeChange.class,INSERT_ACTION,1);
        assertChangeCount(changes, ParameterChange.class,INSERT_ACTION,1);
        assertChangeCount(changes, VariableDeclarationChange.class,INSERT_ACTION,1);
        assertChangeCount(changes, ParameterTypeChange.class,INSERT_ACTION,1);
        assertChangeCount(changes, VariableTypeChange.class,INSERT_ACTION,1);
        assertChangeCount(changes, LiteralChange.class,INSERT_ACTION,1);
        assertChangeCount(changes, FieldTypeChange.class,DELETE_ACTION,1);
        assertChangeCount(changes, LiteralChange.class,DELETE_ACTION,1);
        assertChangeCount(changes, FieldChange.class,DELETE_ACTION,1);
        assertChangeCount(changes, ModifierChange.class,INSERT_ACTION,2);


        assertEquals(12, changes.size());
    }

    @Test
    public void canGetMethodName1() throws IOException {
        String src = "public class Foo {public class Bar{public class Buh{}}}";
        String dst = "public class Foo {public class Bar{ public class Buh{public static void hello(double d){}}}}";
        Collection<SourceCodeChange> changes = classify(src, dst);

        extendedInfoDump(changes);
        assertChangeCount(changes, MethodChange.class,INSERT_ACTION,1);
        assertChangeCount(changes, ReturnTypeChange.class,INSERT_ACTION,1);
        assertChangeCount(changes, ParameterChange.class,INSERT_ACTION,1);
        assertChangeCount(changes, ParameterTypeChange.class,INSERT_ACTION,1);
        assertChangeCount(changes, ModifierChange.class,INSERT_ACTION,2);

        assertEquals(6,changes.size());
    }

    @Test
    public void canGetMethodName2() throws IOException {
        String src = "public class Foo {public class Bar{}}";
        String dst = "public class Foo {" +
                        "public class Bar{" +
                            "public void hello(Cola cola){}" +
                            "public String strello(){return \"hi\";}" +
                            "public boolean bello(String s){ return false;}" +
                            "public void hello(double d, double dd){}" +
                        "}" +
                     "}";
        Collection<SourceCodeChange> changes = classify(src, dst);

        extendedInfoDump(changes);

        assertChangeCount(changes, MethodChange.class,INSERT_ACTION,4);
        assertChangeCount(changes, ReturnTypeChange.class,INSERT_ACTION,4);

        assertChangeCount(changes, ParameterChange.class,INSERT_ACTION,4);
        assertChangeCount(changes, ParameterTypeChange.class,INSERT_ACTION,4);

        assertChangeCount(changes, ReturnValueChange.class,INSERT_ACTION,2);
        assertChangeCount(changes, ReturnStatementChange.class,INSERT_ACTION,2);

        assertChangeCount(changes, ModifierChange.class,INSERT_ACTION,4);


        assertEquals(24, changes.size());
    }

    @Test
    public void canGetMethodName3() throws IOException {
        String src = "public class Foo {public class Bar{int x = 1;}}";
        String dst = "public class Foo {\n" +
                "        public class Bar {\n" +
                "            public void hello(double d) {\n" +
                "            }\n" +
                "            public String strello() { return \"hi\"; }\n" +
                "            public boolean bello (String s){\n" +
                "                return false;\n" +
                "            }\n" +
                "            public void hello(double d, double dd) {\n" +
                "            }\n" +
                "        }\n" +
                "    }";
        Collection<SourceCodeChange> changes = classify(src, dst);

        extendedInfoDump(changes);

        assertChangeCount(changes, FieldTypeChange.class,DELETE_ACTION,1);
        assertChangeCount(changes, LiteralChange.class,DELETE_ACTION,1);
        assertChangeCount(changes, FieldChange.class,DELETE_ACTION,1);

        assertChangeCount(changes, MethodChange.class,INSERT_ACTION,4);
        assertChangeCount(changes, ReturnTypeChange.class,INSERT_ACTION,4);

        assertChangeCount(changes, ParameterChange.class,INSERT_ACTION,4);
        assertChangeCount(changes, ParameterTypeChange.class,INSERT_ACTION,4);

        assertChangeCount(changes, ReturnValueChange.class,INSERT_ACTION,2);
        assertChangeCount(changes, ReturnStatementChange.class,INSERT_ACTION,2);

        assertChangeCount(changes, ModifierChange.class,INSERT_ACTION,4);


        assertEquals(27, changes.size());
    }
}
