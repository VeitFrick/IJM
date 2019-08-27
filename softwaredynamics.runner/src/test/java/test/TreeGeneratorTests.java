package test;

import at.aau.softwaredynamics.gen.AstNodePopulatingJdtTreeGenerator;
import at.aau.softwaredynamics.gen.OptimizedJdtTreeGenerator;
import com.github.gumtreediff.gen.jdt.JdtTreeGenerator;
import com.github.gumtreediff.tree.TreeContext;
import org.junit.Ignore;
import org.junit.Test;

import static org.junit.Assert.assertNotNull;

import java.io.IOException;

/**
 * Created by thomas on 24.11.2016.
 */
@Ignore
public class TreeGeneratorTests {
    @Test
    public void canCreateTreeWithJdtTreeGenerator() throws IOException {
        String text = "package at.aau.test; public class Foo { public int bar() { prod.send(createMessage(session, messageSize, publishedMessageSize)); } }";

        TreeContext context = new JdtTreeGenerator().generateFromString(text);

        assertNotNull(context);
        assertNotNull(context.getRoot());
    }

    @Test
    public void canCreateTreeWithAstNodePopulatingTreeGenerator() throws IOException {
        String text = "package at.aau.test; public class Foo { public int bar() { prod.send(createMessage(session, messageSize, publishedMessageSize)); } }";

        TreeContext context = new AstNodePopulatingJdtTreeGenerator().generateFromString(text);

        assertNotNull(context);
        assertNotNull(context.getRoot());
    }

    @Test
    public void canCreateTreeWithOptimizedJdtTreeGenerator() throws IOException {
        String text = "package at.aau.test; public class Foo { public int bar() { prod.send(createMessage(session, messageSize, publishedMessageSize)); } }";

        TreeContext context = new OptimizedJdtTreeGenerator().generateFromString(text);

        assertNotNull(context);
        assertNotNull(context.getRoot());
    }
}
