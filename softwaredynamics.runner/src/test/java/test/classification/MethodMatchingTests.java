package test.classification;

import at.aau.softwaredynamics.classifier.actions.DeleteAction;
import at.aau.softwaredynamics.classifier.actions.MoveAction;
import at.aau.softwaredynamics.classifier.actions.UpdateAction;
import at.aau.softwaredynamics.classifier.entities.SourceCodeChange;
import at.aau.softwaredynamics.classifier.types.*;
import at.aau.softwaredynamics.runner.io.fs.FileHandler;
import org.junit.Test;

import java.io.IOException;
import java.util.Collection;

import static at.aau.softwaredynamics.classifier.actions.ActionType.*;
import static org.junit.Assert.assertEquals;

/**
 * Created by Veit
 */
public class MethodMatchingTests extends ClassifierTestBase {

    @Test
    public void canClassifyMethodMatching_1() throws IOException {
        FileHandler fh = new FileHandler("./src/test/resources", "test");
        String src = fh.loadFile("methodmatching_src");
        String dst = fh.loadFile("methodmatching_dst");

        Collection<SourceCodeChange> changes = classify(src, dst);
        extendedInfoDump(changes);
        assertChangeCount(changes, MethodChange.class,INSERT_ACTION,1);
        assertChangeCount(changes, MethodChange.class, DeleteAction.class,0);
        assertChangeCount(changes, MethodChange.class, UpdateAction.class,0);
        assertChangeCount(changes, MethodChange.class, MoveAction.class,0);
    }

}
