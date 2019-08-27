package test.classification;

import at.aau.softwaredynamics.classifier.entities.SourceCodeChange;
import at.aau.softwaredynamics.classifier.types.*;
import org.junit.Test;

import java.io.IOException;
import java.util.Collection;

import static at.aau.softwaredynamics.classifier.actions.ActionType.INSERT_ACTION;
import static at.aau.softwaredynamics.classifier.actions.ActionType.RENAME_ACTION;
import static org.junit.Assert.assertEquals;

public class GenericsTests extends ClassifierTestBase {

    @Test
    public void canClassifyGenericType() throws IOException {
        String src = "public class Foo { }";
        String dst = "public class Foo<T> { }";
        Collection<SourceCodeChange> changes = classify(src, dst);

        extendedInfoDump(changes);

        assertChangeCount(changes, GenericsChange.class, INSERT_ACTION,1);
        assertEquals(1, changes.size());
    }

    @Test
    public void canClassifyMultipleGenericTypes() throws IOException {
        String src = "public class Foo { }";
        String dst = "public class Foo<T1, T2> { }";
        Collection<SourceCodeChange> changes = classify(src, dst);

        extendedInfoDump(changes);

        assertChangeCount(changes, GenericsChange.class, INSERT_ACTION,2);
        assertEquals(2, changes.size());
    }

    @Test
    public void canClassifyGenericRenaming() throws IOException {
        String src = "public class Foo<T> { }";
        String dst = "public class Foo<F> { }";
        Collection<SourceCodeChange> changes = classify(src, dst);

        extendedInfoDump(changes);

        assertChangeCount(changes, GenericsChange.class, RENAME_ACTION,1);
        assertEquals(1, changes.size());
    }

}
