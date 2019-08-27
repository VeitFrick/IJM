package at.aau.softwaredynamics.matchers.tests;

import at.aau.softwaredynamics.gen.OptimizedJdtTreeGenerator;
import at.aau.softwaredynamics.matchers.JavaMatchers;
import at.aau.softwaredynamics.matchers.MatcherFactory;
import at.aau.softwaredynamics.matchers.tests.util.TestHelper;
import com.github.gumtreediff.actions.ActionGenerator;
import com.github.gumtreediff.actions.model.Action;
import com.github.gumtreediff.matchers.Mapping;
import com.github.gumtreediff.matchers.Matcher;
import com.github.gumtreediff.tree.ITree;
import org.junit.Ignore;
import org.junit.Test;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

import static junit.framework.TestCase.assertNotNull;
import static junit.framework.TestCase.assertTrue;
import static org.junit.Assert.assertEquals;

/**
 * Created by thomas on 01.03.2017.
 */
@Ignore
public class IterativeJavaMatcherTest {
    @Test
    public void canMatchFileWithInnerClass() throws IOException {
        ITree src = TestHelper.getTree("ci_1_src", new OptimizedJdtTreeGenerator());
        ITree dst = TestHelper.getTree("ci_1_dst", new OptimizedJdtTreeGenerator());

        Matcher m = new MatcherFactory(JavaMatchers.IterativeJavaMatcher.class).createMatcher(src, dst);
        m.match();

        assertNotNull(m.getMappings());
        assertTrue(m.getMappingSet().size() > 0);
    }

    @Test
    public void insertDoesNotResultInMove() throws IOException {
        String srcString = "public class Foo {" +
                "public void testSendReceiveTransactedBatches() throws Exception {\n" +
                "\n" +
                "        TextMessage message = session.createTextMessage(\"Batch Message\");\n" +
                "\n" +
                "        for (int j = 0; j < batchCount; j++) {\n" +
                "            LOG.info(\"Producing bacth \" + j + \" of \" + batchSize + \" messages\");\n" +
                "\n" +
                "            for (int i = 0; i < batchSize; i++) {\n" +
                "                producer.send(message);\n" +
                "            }\n" +
                "            \n" +
                "            session.commit();\n" +
                "            LOG.info(\"Consuming bacth \" + j + \" of \" + batchSize + \" messages\");\n" +
                "\n" +
                "            for (int i = 0; i < batchSize; i++) {\n" +
                "                message = (TextMessage)consumer.receive(1000 * 5);\n" +
                "                assertNotNull(\"Received only \" + i + \" messages in batch \" + j, message);\n" +
                "                assertEquals(\"Batch Message\", message.getText());\n" +
                "            }\n" +
                "\n" +
                "            session.commit();\n" +
                "        }\n" +
                "    }" +
                "}";
        String dstString = "public class Foo {" +
                "public void testSendReceiveTransactedBatches() throws Exception {\n" +
                "\n" +
                "        TextMessage message = session.createTextMessage(\"Batch Message\");\n" +
                "\n" +
                "        for (int j = 0; j < batchCount; j++) {\n" +
                "            LOG.info(\"Producing bacth \" + j + \" of \" + batchSize + \" messages\");\n" +
                "\n" +
                "            for (int i = 0; i < batchSize; i++) {\n" +
                "                producer.send(message);\n" +
                "            }\n" +
                "           messageSent();\n" +
                "            session.commit();\n" +
                "            LOG.info(\"Consuming bacth \" + j + \" of \" + batchSize + \" messages\");\n" +
                "\n" +
                "            for (int i = 0; i < batchSize; i++) {\n" +
                "                message = (TextMessage)consumer.receive(1000 * 5);\n" +
                "                assertNotNull(\"Received only \" + i + \" messages in batch \" + j, message);\n" +
                "                assertEquals(\"Batch Message\", message.getText());\n" +
                "            }\n" +
                "\n" +
                "            session.commit();\n" +
                "        }\n" +
                "    }" +
                "}";
        ITree src = new OptimizedJdtTreeGenerator().generateFromString(srcString).getRoot();
        ITree dst = new OptimizedJdtTreeGenerator().generateFromString(dstString).getRoot();

        Matcher m = new MatcherFactory(JavaMatchers.IterativeJavaMatcher.class).createMatcher(src, dst);
        m.match();

        List<Action> actions = new ActionGenerator(src, dst, m.getMappings()).generate();
        List<Action> moves = actions.stream().filter(x -> x.getName() == "MOV").collect(Collectors.toList());

        assertEquals(0, moves.size());
    }

    @Test
    public void repeatingCodeIsMatchedCorrectly() throws IOException {
        String srcString = "public class Foo {" +
                " public void testSizeOfDirectory() throws Exception {\n" +
                "        File file = new File(getTestDirectory(), getName());\n" +
                "\n" +
                "        // Non-existent file\n" +
                "        try {\n" +
                "            FileUtils.sizeOfDirectory(file);\n" +
                "            fail(\"Exception expected.\");\n" +
                "        } catch (IllegalArgumentException ex) {}\n" +
                "\n" +
                "        // Creates file\n" +
                "        file.createNewFile();\n" +
                "        file.deleteOnExit();\n" +
                "\n" +
                "        // Existing file\n" +
                "        try {\n" +
                "            FileUtils.sizeOfDirectory(file);\n" +
                "            fail(\"Exception expected.\");\n" +
                "        } catch (IllegalArgumentException ex) {}\n" +
                "\n" +
                "        // Existing directory\n" +
                "        file.delete();\n" +
                "        file.mkdir();\n" +
                "\n" +
                "        // Create a cyclic symlink\n" +
                "        this.createCircularSymLink(file);\n" +
                "\n" +
                "        assertEquals(\n" +
                "            \"Unexpected directory size\",\n" +
                "            TEST_DIRECTORY_SIZE,\n" +
                "            FileUtils.sizeOfDirectory(file));\n" +
                "    }" +
                "}";
        String dstString = "public class Foo {" +
                "public void testSizeOfDirectory() throws Exception {\n" +
                "        final File file = new File(getTestDirectory(), getName());\n" +
                "\n" +
                "        // Non-existent file\n" +
                "        try {\n" +
                "            FileUtils.sizeOfDirectory(file);\n" +
                "            fail(\"Exception expected.\");\n" +
                "        } catch (final IllegalArgumentException ex) {}\n" +
                "\n" +
                "        // Creates file\n" +
                "        file.createNewFile();\n" +
                "        file.deleteOnExit();\n" +
                "\n" +
                "        // Existing file\n" +
                "        try {\n" +
                "            FileUtils.sizeOfDirectory(file);\n" +
                "            fail(\"Exception expected.\");\n" +
                "        } catch (final IllegalArgumentException ex) {}\n" +
                "\n" +
                "        // Existing directory\n" +
                "        file.delete();\n" +
                "        file.mkdir();\n" +
                "\n" +
                "        // Create a cyclic symlink\n" +
                "        this.createCircularSymLink(file);\n" +
                "\n" +
                "        assertEquals(\n" +
                "            \"Unexpected directory size\",\n" +
                "            TEST_DIRECTORY_SIZE,\n" +
                "            FileUtils.sizeOfDirectory(file));\n" +
                "    }" +
                "}";

        ITree src = new OptimizedJdtTreeGenerator().generateFromString(srcString).getRoot();
        ITree dst = new OptimizedJdtTreeGenerator().generateFromString(dstString).getRoot();

        Matcher m = new MatcherFactory(JavaMatchers.IterativeJavaMatcher.class).createMatcher(src, dst);
        m.match();

        List<Action> actions = new ActionGenerator(src, dst, m.getMappings()).generate();
        List<Action> moves = actions.stream().filter(x -> x.getName() == "MOV").collect(Collectors.toList());

        assertEquals(0, moves.size());
    }

    @Test
    public void renamedNodesAreMatched() throws IOException {
        String srcString = "public class Foo { private boolean IsDestroyed() { return destoryed;  } }";
        String dstString = "public class Foo { private boolean IsDestroyed() { return destroyed;  } }";

        ITree src = new OptimizedJdtTreeGenerator().generateFromString(srcString).getRoot();
        ITree dst = new OptimizedJdtTreeGenerator().generateFromString(dstString).getRoot();

        Matcher m = new MatcherFactory(JavaMatchers.IterativeJavaMatcher.class).createMatcher(src, dst);
        m.match();

        List<Action> actions = new ActionGenerator(src, dst, m.getMappings()).generate();
        List<Action> moves = actions.stream().filter(x -> x.getName() == "MOV").collect(Collectors.toList());
        List<Action> updates = actions.stream().filter(x -> x.getName() == "UPD").collect(Collectors.toList());

        assertEquals(1, m.getMappingSet().stream().filter(x -> x.first.getLabel().equals("destoryed")).collect(Collectors.toList()).size());
        assertEquals(0, moves.size());
        assertEquals(1, updates.size());
    }

    @Ignore("fix later")
    @Test
    public void statementInsertDoesNotCauseMove() throws IOException {
        ITree src = TestHelper.getTree("amq_1_src", new OptimizedJdtTreeGenerator());
        ITree dst = TestHelper.getTree("amq_1_dst", new OptimizedJdtTreeGenerator());

        Matcher m = new MatcherFactory(JavaMatchers.IterativeJavaMatcher.class).createMatcher(src, dst);
        m.match();

        List<Action> actions = new ActionGenerator(src, dst, m.getMappings()).generate();
        List<Action> moves = actions.stream().filter(x -> x.getName() == "MOV").collect(Collectors.toList());

        List<Mapping> sendMapping = m.getMappingSet().stream().filter(x -> x.first.getLabel().equals("send")).collect(Collectors.toList());

        assertEquals(0, moves.size());
    }

    @Test
    public void bug_eval_391_singleMethod() throws IOException {
        ITree src = TestHelper.getTree("eval_391_src", new OptimizedJdtTreeGenerator());
        ITree dst = TestHelper.getTree("eval_391_dst", new OptimizedJdtTreeGenerator());

        Matcher m = new MatcherFactory(JavaMatchers.IterativeJavaMatcher.class).createMatcher(src, dst);
        m.match();

        List<Action> actions = new ActionGenerator(src, dst, m.getMappings()).generate();
        List<Action> moves = actions.stream().filter(x -> x.getName() == "MOV").collect(Collectors.toList());
        List<Action> inserts = actions.stream().filter(x -> x.getName() == "INS").collect(Collectors.toList());

        assertEquals(0, moves.size());
        assertEquals(10, inserts.size());
    }

    @Test
    public void bug_eval_391_fullFile() throws IOException {
        ITree src = TestHelper.getTree("eval_391_full_src", new OptimizedJdtTreeGenerator());
        ITree dst = TestHelper.getTree("eval_391_full_dst", new OptimizedJdtTreeGenerator());

        Matcher m = new MatcherFactory(JavaMatchers.IterativeJavaMatcher.class).createMatcher(src, dst);
        m.match();

        List<Action> actions = new ActionGenerator(src, dst, m.getMappings()).generate();
        List<Action> moves = actions.stream().filter(x -> x.getName() == "MOV").collect(Collectors.toList());

        assertEquals(0, moves.size());
    }
}
