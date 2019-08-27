package at.aau.softwaredynamics.matchers.tests;

import at.aau.softwaredynamics.gen.OptimizedJdtTreeGenerator;
import at.aau.softwaredynamics.matchers.JavaMatchers;
import at.aau.softwaredynamics.matchers.MatcherFactory;
import at.aau.softwaredynamics.matchers.tests.util.TestHelper;
import com.github.gumtreediff.actions.ActionGenerator;
import com.github.gumtreediff.actions.model.Action;
import com.github.gumtreediff.matchers.Matcher;
import com.github.gumtreediff.tree.ITree;
import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;

import java.io.IOException;
import java.util.List;

import static org.junit.Assert.assertEquals;

/**
 * Created by thomas on 27.03.2017.
 */
@Ignore
public class JavaImportsMatcherTests {
    @Test
    public void allIdenticalImportStatementsAreMatched() throws IOException {
        String srcString = "" +
                "import org.apache.activemq.console.util.JmxMBeansUtil;\n" +
                "import org.apache.activemq.console.formatter.GlobalWriter;\n" +
                "import javax.management.MBeanServerConnection;\n" +
                "import javax.management.ObjectName;\n" +
                "import javax.management.ObjectInstance;\n" +
                "import javax.management.remote.JMXServiceURL;\n" +
                "import java.util.List;\n" +
                "import java.util.Iterator;\n" +
                "import java.util.Collection;\n" +
                "import java.util.HashSet;" +
                "";
        String dstString = "" +
                "import java.util.Collection;\n" +
                "import java.util.HashSet;\n" +
                "import java.util.Iterator;\n" +
                "import java.util.List;\n" +
                "import javax.management.MBeanServerConnection;\n" +
                "import javax.management.ObjectInstance;\n" +
                "import javax.management.ObjectName;\n" +
                "import javax.management.remote.JMXServiceURL;\n" +
                "import org.apache.activemq.console.formatter.GlobalWriter;\n" +
                "import org.apache.activemq.console.util.JmxMBeansUtil;" +
                "";

        ITree src = TestHelper.getTreeFromString(srcString, new OptimizedJdtTreeGenerator());
        ITree dst = TestHelper.getTreeFromString(dstString, new OptimizedJdtTreeGenerator());

        Matcher matcher = new MatcherFactory(JavaMatchers.IterativeJavaMatcher.class).createMatcher(src, dst);
        matcher.match();

        assertEquals(11, matcher.getMappingSet().size());
    }

    @Test
    public void newImportIsNotMatched() throws IOException {
        String srcString = "" +
                "import java.util.Collection;" +
                "";
        String dstString = "" +
                "import java.util.Collection;\n" +                              // EXISTS
                "import java.util.HashSet;\n" +                                 // NEW
                "";

        ITree src = TestHelper.getTreeFromString(srcString, new OptimizedJdtTreeGenerator());
        ITree dst = TestHelper.getTreeFromString(dstString, new OptimizedJdtTreeGenerator());

        Matcher matcher = new MatcherFactory(JavaMatchers.IterativeJavaMatcher.class).createMatcher(src, dst);
        matcher.match();

        List<Action> actions = new ActionGenerator(src, dst, matcher.getMappings()).generate();

        assertEquals(1, actions.size());
        assertEquals("INS", actions.get(0).getName());
    }

    @Test
    public void deletedImportIsNotMatched() throws IOException {
        String srcString = "" +
                "import java.util.Collection;" +
                "import java.util.Vector;\n" +                                  // DEL
                "";
        String dstString = "" +
                "import java.util.Collection;\n" +                              // EXISTS
                "";

        ITree src = TestHelper.getTreeFromString(srcString, new OptimizedJdtTreeGenerator());
        ITree dst = TestHelper.getTreeFromString(dstString, new OptimizedJdtTreeGenerator());

        Matcher matcher = new MatcherFactory(JavaMatchers.IterativeJavaMatcher.class).createMatcher(src, dst);
        matcher.match();

        List<Action> actions = new ActionGenerator(src, dst, matcher.getMappings()).generate();

        assertEquals(1, actions.size());
        assertEquals("DEL", actions.get(0).getName());
    }
}
