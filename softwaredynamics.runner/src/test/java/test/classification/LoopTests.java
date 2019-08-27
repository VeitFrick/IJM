package test.classification;

import at.aau.softwaredynamics.classifier.entities.SourceCodeChange;
import at.aau.softwaredynamics.classifier.types.*;
import org.junit.Test;

import java.io.IOException;
import java.util.Collection;

import static at.aau.softwaredynamics.classifier.actions.ActionType.*;
import static org.junit.Assert.assertEquals;

/**
 * Created by thomas on 17.11.2016.
 */
public class LoopTests extends ClassifierTestBase {

    /*
    Loops
     */

    @Test
    public void loopParameterTest0() throws IOException {
        String src = "public class Foo{\r\n" +
                "    public void bar(){\r\n" +
                "        for (ITree parent : node.getParents()) {\r\n" +
                "            if (NodeTypeHelper.isStatement(parent) && !NodeTypeHelper.isOfKind(parent, NodeType.IF_STATEMENT) && mappings.getSrc(parent) != null) {\r\n" +
                "                change.setChangeType(ChangeType.STATEMENT_UPDATE);\r\n" +
                "                break; \r\n" +
                "            }\r\n" +
                "        }\r\n" +
                "    }\r\n" +
                "}";
        String dst = "public class Foo{\r\n" +
                "    public void bar(){\r\n" +
                "    }\r\n" +
                "}";

        Collection<SourceCodeChange> changes = classify(src, dst);

        extendedInfoDump(changes);

        assertChangeCount(changes, LoopChange.class,DELETE_ACTION,1);

        //TODO Maybe make a whole test out of it. Loop works.

    }

    @Test
    public void loopParameterTest1() throws IOException {
        String src = "public class Foo{\r\n" +
                "    public void bar(){\r\n" +
                "        for (ITree parent : node.getParents()) {\r\n" +
                "        }\r\n" +
                "    }\r\n" +
                "}";
        String dst = "public class Foo{\r\n" +
                "    public void bar(){\r\n" +
                "        for (ITree tnerap : node.getParents()) {\r\n" +
                "        }\r\n" +
                "    }\r\n" +
                "}";

        Collection<SourceCodeChange> changes = classify(src, dst);


        extendedInfoDump(changes);
        assertChangeCount(changes, VariableDeclarationChange.class,UPDATE_ACTION,1);
        assertEquals(1, changes.size());
    }

    @Test
    public void loopParameterTest2() throws IOException {
        String src = "public class Foo{\r\n" +
                "    public void bar(){\r\n" +
                "        for (ITree parent : node.getParents()) {\r\n" +
                "        }\r\n" +
                "    }\r\n" +
                "}";
        String dst = "public class Foo{\r\n" +
                "    public void bar(){\r\n" +
                "        for (Eerit parent : node.getParents()) {\r\n" +
                "        }\r\n" +
                "    }\r\n" +
                "}";

        Collection<SourceCodeChange> changes = classify(src, dst);

        extendedInfoDump(changes);
        assertChangeCount(changes, VariableTypeChange.class,UPDATE_ACTION,1);
        assertEquals(1, changes.size());
    }

    @Test
    public void loopParameterTest3() throws IOException {
        String src = "public class Foo{\r\n" +
                "    public void bar(){\r\n" +
                "        for (ITree parent : node.getParents()) {\r\n" +
                "        }\r\n" +
                "    }\r\n" +
                "}";
        String dst = "public class Foo{\r\n" +
                "    public void bar(){\r\n" +
                "        for (ITree parent : node.getPlushes()) {\r\n" +
                "        }\r\n" +
                "    }\r\n" +
                "}";

        Collection<SourceCodeChange> changes = classify(src, dst);

        extendedInfoDump(changes);
        assertChangeCount(changes, InvocationChange.class,UPDATE_ACTION,1);
        assertEquals(1, changes.size());
    }


    @Test
    public void loopParameterTest4() throws IOException {
        String src = "public class Foo{\r\n" +
                "    public void bar(){\r\n" +
                "        for (int i = 0; i<12; i++) {\r\n" +
                "        }\r\n" +
                "    }\r\n" +
                "}";
        String dst = "public class Foo{\r\n" +
                "    public void bar(){\r\n" +
                "        for (;;) {\r\n" +
                "        }\r\n" +
                "    }\r\n" +
                "}";

        Collection<SourceCodeChange> changes = classify(src, dst);

        extendedInfoDump(changes);
        assertChangeCount(changes, VariableDeclarationChange.class,DELETE_ACTION,1);
        assertChangeCount(changes, VariableTypeChange.class,DELETE_ACTION,1);
        assertChangeCount(changes, VariableWriteChange.class,DELETE_ACTION,1);
        assertChangeCount(changes, VariableReadChange.class,DELETE_ACTION,1);
        assertChangeCount(changes, ExpressionChange.class,DELETE_ACTION,1);
        assertChangeCount(changes, ConditionChange.class,DELETE_ACTION,1);
        assertChangeCount(changes, LiteralChange.class,DELETE_ACTION,2);
        assertChangeCount(changes, StatementChange.class,DELETE_ACTION,2); //ONE OF THEM IS THE "Variable Declaration Change" since it is also a "Statement Change"

        assertEquals(9, changes.size());
    }

    @Test
    public void loopParameterTest5() throws IOException {
        String src = "public class Foo{\r\n" +
                "    public void bar(){\r\n" +
                "        for (int i = 0; i<12; i++) {\r\n" +
                "        }\r\n" +
                "    }\r\n" +
                "}";
        String dst = "public class Foo{\r\n" +
                "    public void bar(){\r\n" +
                "        for (int i = 1; i<12; i++) {\r\n" +
                "        }\r\n" +
                "    }\r\n" +
                "}";

        Collection<SourceCodeChange> changes = classify(src, dst);


        extendedInfoDump(changes);

        assertChangeCount(changes, LiteralChange.class,UPDATE_ACTION,1);

        assertEquals(1, changes.size());
    }

    @Test
    public void loopParameterTest6() throws IOException {
        String src = "public class Foo{\r\n" +
                "    public void bar(){\r\n" +
                "        for (int i = 0; i<12; i++) {\r\n" +
                "        }\r\n" +
                "    }\r\n" +
                "}";
        String dst = "public class Foo{\r\n" +
                "    public void bar(){\r\n" +
                "        for (int i = 0; i<11; i++) {\r\n" +
                "        }\r\n" +
                "    }\r\n" +
                "}";

        Collection<SourceCodeChange> changes = classify(src, dst);

        extendedInfoDump(changes);

        assertChangeCount(changes, LiteralChange.class,UPDATE_ACTION,1);
        assertChangeCount(changes, ConditionChange.class,UPDATE_ACTION,1);

        assertEquals(2, changes.size());
}

    @Test
    public void loopParameterTest7() throws IOException {
        String src = "public class Foo{\r\n" +
                "    public void bar(){\r\n" +
                "        for (int i = 0; i<12; i++) {\r\n" +
                "        }\r\n" +
                "    }\r\n" +
                "}";
        String dst = "public class Foo{\r\n" +
                "    public void bar(){\r\n" +
                "        for (int i = 0; i<12; i--) {\r\n" +
                "        }\r\n" +
                "    }\r\n" +
                "}";

        Collection<SourceCodeChange> changes = classify(src, dst);


        extendedInfoDump(changes);

        assertChangeCount(changes, StatementChange.class,UPDATE_ACTION,1);

        assertEquals(1, changes.size());
    }


}

