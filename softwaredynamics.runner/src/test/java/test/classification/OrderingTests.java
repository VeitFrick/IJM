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
public class OrderingTests extends ClassifierTestBase {

    /*
    Orderings
     */

    @Test
    public void canClassifySimpleOrderingChanges0() throws IOException {
        String src = "public class Foo { public void bar(double d) {vader(vader((int)d));} public int vader(int x) {return x;} }";
        String dst = "public class Foo { public int vader(int x) {return x;} public void bar(double d) {vader(vader((int)d));} }";

        Collection<SourceCodeChange> changes = classify(src, dst);
        extendedInfoDump(changes);

        assertChangeCount(changes, MethodChange.class,ORDERING_CHANGE_ACTION,1);

        assertEquals(1, changes.size());
    }

    @Test
    public void canClassifySimpleOrderingChanges1() throws IOException {
        String src = "public class Foo {" +
                "public void bar(double d, Integer g) {vader(vader((int)d));}" +
                "public int vader(int x) {return x;}" +
                "}";
        String dst = "public class Foo {" +
                "public int vader(int x) {return x;}" +
                "public void bar(Integer g, double d) {vader(vader((int)d));}" +
                "}";

        Collection<SourceCodeChange> changes = classify(src, dst);
        extendedInfoDump(changes);

        assertChangeCount(changes, MethodChange.class,ORDERING_CHANGE_ACTION,1);
        assertChangeCount(changes, ParameterChange.class,ORDERING_CHANGE_ACTION,1);

        assertEquals(2, changes.size());
    }

}

