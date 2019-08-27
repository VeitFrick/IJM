package at.aau.softwaredynamics.matchers.tests;

import at.aau.softwaredynamics.gen.OptimizedJdtTreeGenerator;
import at.aau.softwaredynamics.matchers.JavaMatchers;
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

import static org.junit.Assert.assertEquals;

/**
 * Created by thomas on 13.03.2017.
 */
@Ignore
public class JavaMethodMatcherTests extends AbstractMatcherTests  {

    @Override
    protected Class<? extends Matcher> getMatcherClass() {
        return JavaMatchers.IterativeJavaMatcher.class;
    }

    @Test
    public void allMethodsAreMatched() throws IOException {
        //https://github.com/apache/commons-lang/commit/98817e88f04cc6757c42ae52d653b63cb85af486
        ITree src = TestHelper.getTree("cl_1_src", new OptimizedJdtTreeGenerator());
        ITree dst = TestHelper.getTree("cl_1_dst", new OptimizedJdtTreeGenerator());

        List<ITree> srcMethods = src.getDescendants().stream().filter(x -> x.getType() == 31).collect(Collectors.toList());

        Matcher matcher = this.createMatcher(src, dst);
        matcher.match();
        List<Mapping> matchedMethods = matcher
                .getMappingSet()
                .stream()
                .filter(x -> x.first.getType() == 31)
                .collect(Collectors.toList());

        assertEquals(srcMethods.size(), matchedMethods.size());
    }

    @Test
    public void forLoopIsMatchedCorrectly() throws IOException {
        String srcString = "public class Foo { " +
                "   public void bar() {" +
                "       for (int j = 1; j <= n; j++) {" +
                "           xopt.setEntry(j, xnew.getEntry(j));" +
                "           final double d1 = xopt.getEntry(j);" +
                "           xoptsq += d1 * d1;" +
                "       }" +
                "   }" +
                "}";
        String dstString = "public class Foo { " +
                "   public void bar() {" +
                "       for (int j = 0; j < n; j++) {" +
                "           xopt.setEntry(j, xnew.getEntry(j));" +
                "           final double d1 = xopt.getEntry(j);" +
                "           xoptsq += d1 * d1;" +
                "       }" +
                "   }" +
                "}";

        ITree src = TestHelper.getTreeFromString(srcString, new OptimizedJdtTreeGenerator());
        ITree dst = TestHelper.getTreeFromString(dstString, new OptimizedJdtTreeGenerator());

        Matcher matcher = this.createMatcher(src, dst);
        matcher.match();

        List<Action> actions = new ActionGenerator(src, dst, matcher.getMappings()).generate();

        assertEquals(0, actions.stream().filter(x -> x.getName().equals("MOV")).collect(Collectors.toList()).size());
    }

    @Test
    public void forLoopIsMatchedCorrectly_1() throws IOException {
        String srcString = "public class Foo { " +
                "   public void bar() {" +
                "       for (int j = 1; j <= n; j++) {" +
                "           xopt.setEntry(j, xnew.getEntry(j));" +
                "           final double d1 = xopt.getEntry(j);" +
                "           xoptsq += d1 * d1;" +
                "       }" +
                "   }" +
                "}";
        String dstString = "public class Foo { " +
                "   public void bar() {" +
                "       for (int i = 0; i < n; i++) {" +
                "           xopt.setEntry(j, xnew.getEntry(j));" +
                "           final double d1 = xopt.getEntry(j);" +
                "           xoptsq += d1 * d1;" +
                "       }" +
                "   }" +
                "}";

        ITree src = TestHelper.getTreeFromString(srcString, new OptimizedJdtTreeGenerator());
        ITree dst = TestHelper.getTreeFromString(dstString, new OptimizedJdtTreeGenerator());

        Matcher matcher = this.createMatcher(src, dst);
        matcher.match();

        List<Action> actions = new ActionGenerator(src, dst, matcher.getMappings()).generate();
        List<Action> moves = actions.stream().filter(x -> x.getName().equals("MOV")).collect(Collectors.toList());

        assertEquals(0, moves.size());

    }

    @Ignore("Can this be fixed with LCS matching??")
    @Test
    public void statementOrderIsMaintained() throws IOException {
        //9867d9f2817fd6dd20d458022de3dda8c3b43b2f	EmpiricalDistributionTest	testNexFail()	STATEMENT_PARENT_CHANGE	32	MOV	165	165	164	164	src/test/java/org/apache/commons/math4/random/EmpiricalDistributionTest.java	open
        String srcString = "public class Foo { " +
                "private void mix() {\r\n" +
                "   a -= c;  a ^= Integer.rotateLeft(c, 4);  c += b;\r\n" + //2
                "   b -= a;  b ^= Integer.rotateLeft(a, 6);  a += c;\r\n" + //3
                "   c -= b;  c ^= Integer.rotateLeft(b, 8);  b += a;\r\n" + //4
                "   a -= c;  a ^= Integer.rotateLeft(c, 16); c += b;\r\n" + //5
                "   b -= a;  b ^= Integer.rotateLeft(a, 19); a += c;\r\n" + //6
                "   c -= b;  c ^= Integer.rotateLeft(b, 4);  b += a;\r\n" + //7
                "}" +
                "}";

        String dstString = "public class Foo { " +
                "private void mix() {\r\n" +
                "   this.a -= this.c;  this.a ^= Integer.rotateLeft(this.c, 4);  this.c += this.b;\r\n" + //2
                "   this.b -= this.a;  this.b ^= Integer.rotateLeft(this.a, 6);  this.a += this.c;\r\n" + //3
                "   this.c -= this.b;  this.c ^= Integer.rotateLeft(this.b, 8);  this.b += this.a;\r\n" + //4
                "   this.a -= this.c;  this.a ^= Integer.rotateLeft(this.c, 16); this.c += this.b;\r\n" + //5
                "   this.b -= this.a;  this.b ^= Integer.rotateLeft(this.a, 19); this.a += this.c;\r\n" + //6
                "   this.c -= this.b;  this.c ^= Integer.rotateLeft(this.b, 4);  this.b += this.a;\r\n" + //7
                "}" +
                "}";

        ITree src = TestHelper.getTreeFromString(srcString, new OptimizedJdtTreeGenerator());
        ITree dst = TestHelper.getTreeFromString(dstString, new OptimizedJdtTreeGenerator());

        Matcher matcher = this.createMatcher(src, dst);
        matcher.match();

        List<Action> actions = new ActionGenerator(src, dst, matcher.getMappings()).generate();
        List<Action> moves = actions.stream().filter(x -> x.getName().equals("MOV")).collect(Collectors.toList());

        assertEquals(36, moves.size());
    }
}
