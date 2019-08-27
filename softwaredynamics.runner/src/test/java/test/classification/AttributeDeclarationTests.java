package test.classification;

import at.aau.softwaredynamics.classifier.entities.SourceCodeChange;
import at.aau.softwaredynamics.classifier.types.FieldChange;
import at.aau.softwaredynamics.classifier.types.FieldTypeChange;
import at.aau.softwaredynamics.classifier.types.ModifierChange;
import org.junit.Ignore;
import org.junit.Test;

import java.io.IOException;
import java.util.Collection;

import static at.aau.softwaredynamics.classifier.actions.ActionType.*;
import static junit.framework.TestCase.assertNotNull;
import static org.junit.Assert.assertEquals;

/**
 * Created by thomas on 18.11.2016.
 */
public class AttributeDeclarationTests extends ClassifierTestBase {

    @Test
    public void canExtractSingleAttributeRenaming() throws IOException {
        String src = "public class Foo { public int bar; } }";
        String dst = "public class Foo { public int bar2; } }";

        Collection<SourceCodeChange> changes = classify(src, dst, true);
        extendedInfoDump(changes);
        assertChangeCount(changes, FieldChange.class, RENAME_ACTION, 1);
        assertEquals(1, changes.size());
    }

    @Test
    public void canExtractSingleAttributeRenaming1() throws IOException {
        String src = "public class Foo { public int bar = 17; } }";
        String dst = "public class Foo { public int bar2 = 17; } }";

        Collection<SourceCodeChange> changes = classify(src, dst);

        extendedInfoDump(changes);
        assertChangeCount(changes, FieldChange.class, RENAME_ACTION, 1);
        assertEquals(1, changes.size());
    }

    @Test
    public void canExtractSingleAttributeTypeChange() throws IOException {
        String src = "public class Foo { public int bar; } }";
        String dst = "public class Foo { public float bar; } }";

        Collection<SourceCodeChange> changes = classify(src, dst);

        extendedInfoDump(changes);
        assertChangeCount(changes, FieldTypeChange.class, UPDATE_ACTION, 1);
        assertEquals(1, changes.size());
    }

    @Test
    public void canExtractSingleAttributeTypeChange1() throws IOException {
        String src = "public class Foo { public Bar bar; } }";
        String dst = "public class Foo { public Bar1 bar; } }";

        Collection<SourceCodeChange> changes = classify(src, dst);

        extendedInfoDump(changes);
        assertChangeCount(changes, FieldTypeChange.class, UPDATE_ACTION, 1);
        assertEquals(1, changes.size());
    }

    @Test
    public void canExtractSingleAttributeTypeChange2() throws IOException {
        String src = "public class Foo { public int bar; } }";
        String dst = "public class Foo { public Bar bar; } }";

        Collection<SourceCodeChange> changes = classify(src, dst);

        extendedInfoDump(changes);
        assertChangeCount(changes, FieldTypeChange.class, UPDATE_ACTION, 1);
        assertEquals(1, changes.size());
    }

    @Test
    public void canExtractSingleAttributeTypeChange3() throws IOException {
        String src = "public class Foo { public Bar bar; } }";
        String dst = "public class Foo { public int bar; } }";

        Collection<SourceCodeChange> changes = classify(src, dst);

        extendedInfoDump(changes);
        assertChangeCount(changes, FieldTypeChange.class, UPDATE_ACTION, 1);
        assertEquals(1, changes.size());
    }

    @Test
    public void canExtractSingleAdditionalObject() throws IOException {
        String src = "public class Foo {} }";
        String dst = "public class Foo { public int bar; } }";

        Collection<SourceCodeChange> changes = classify(src, dst);


        extendedInfoDump(changes);
        assertChangeCount(changes, FieldChange.class, INSERT_ACTION, 1);
        assertChangeCount(changes, ModifierChange.class, INSERT_ACTION, 1);
        assertChangeCount(changes, FieldTypeChange.class, INSERT_ACTION, 1);
        assertEquals(3, changes.size());
    }

    @Test
    public void canExtractSingleRemovedObject() throws IOException {
        String src = "public class Foo {public int bar;} }";
        String dst = "public class Foo {} }";

        Collection<SourceCodeChange> changes = classify(src, dst);

        extendedInfoDump(changes);
        assertChangeCount(changes, FieldChange.class, DELETE_ACTION, 1);
        assertChangeCount(changes, ModifierChange.class, DELETE_ACTION, 1);
        assertChangeCount(changes, FieldTypeChange.class, DELETE_ACTION, 1);
        assertEquals(3, changes.size());
    }

    @Test
    @Ignore //Ignore this test - line numbers don't interest me right now
    public void attributeWithCommentHasCorrectLineNumber() throws IOException {
        String src = "public class Foo {" +
                "/**\n" +
                "     * Static random number generator shared by GA implementation classes. Set the randomGenerator seed to get.\n" +
                "     * reproducible results. Use {@link #setRandomGenerator(RandomGenerator)} to supply an alternative to the default\n" +
                "     * JDK-provided PRNG.\n" +
                "     */\n" +
                "    //@GuardedBy(\"this\")\n" +
                "    private static RandomGenerator  randomGenerator = new JDKRandomGenerator();" +
                "}";
        String dst = "public class Foo {" +
                "/**\n" +
                "     * Static random number generator shared by GA implementation classes.\n" +
                "     * Use {@link #setRandomGenerator(UniformRandomProvider)} to supply an\n" +
                "     * alternative to the default PRNG, and/or select a specific seed.\n" +
                "     */\n" +
                "    //@GuardedBy(\"this\")\n" +
                "    private static UniformRandomProvider randomGenerator = RandomSource.create(RandomSource.WELL_19937_C);" +
                "}";

        Collection<SourceCodeChange> changes = classify(src, dst);
        SourceCodeChange target = null;

//        for (SourceCodeChange change : changes) {
//            if (change.getChangeType() == ChangeType.ATTRIBUTE_TYPE_CHANGE) {
//                target = change;
//            }
//        }

        assertNotNull(target);
        assertEquals(7, target.getSrcInfo().getStartLineNumber());
    }

}
