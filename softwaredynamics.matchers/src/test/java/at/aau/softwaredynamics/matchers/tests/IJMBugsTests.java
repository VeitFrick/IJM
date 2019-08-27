package at.aau.softwaredynamics.matchers.tests;

import at.aau.softwaredynamics.gen.NodeType;
import at.aau.softwaredynamics.gen.OptimizedJdtTreeGenerator;
import at.aau.softwaredynamics.matchers.JavaMatchers;
import at.aau.softwaredynamics.matchers.tests.util.TestHelper;
import at.aau.softwaredynamics.util.NodeTypeHelper;
import com.github.gumtreediff.actions.ActionGenerator;
import com.github.gumtreediff.actions.model.Action;
import com.github.gumtreediff.matchers.Mapping;
import com.github.gumtreediff.matchers.MappingStore;
import com.github.gumtreediff.matchers.Matcher;
import com.github.gumtreediff.tree.ITree;
import org.junit.Ignore;
import org.junit.Test;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;

/**
 * Created by thomas on 27.06.2017.
 */
@Ignore
public class IJMBugsTests {

    @Test
    public void bug_9() throws IOException {
        ITree src = TestHelper.getTree("hbase_1_src", new OptimizedJdtTreeGenerator());
        ITree dst = TestHelper.getTree("hbase_1_dst", new OptimizedJdtTreeGenerator());

        Matcher m = new JavaMatchers.IterativeJavaMatcher_V1(src, dst, new MappingStore());
        m.match();

        List<Mapping> innerTypes = m.getMappingSet().stream()
                .filter(x -> x.getFirst().getParent() != null
                        && NodeTypeHelper.isOfKind(x.getFirst(), NodeType.TYPE_DECLARATION)
                        && NodeTypeHelper.isOfKind(x.getFirst().getParent(), NodeType.TYPE_DECLARATION))
                .collect(Collectors.toList());

        List<Action> actions = new ActionGenerator(src, dst, m.getMappings()).generate();

        assertNotEquals(0, innerTypes.size());
        assertEquals(1,
                innerTypes.stream()
                        .filter(x -> x.getFirst().getLabel().equals("HConnectionImplementation"))
                        .collect(Collectors.toList()).size());
    }
}
