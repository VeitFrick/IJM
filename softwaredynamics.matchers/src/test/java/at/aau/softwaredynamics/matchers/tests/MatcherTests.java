package at.aau.softwaredynamics.matchers.tests;

import at.aau.softwaredynamics.gen.OptimizedJdtTreeGenerator;
import at.aau.softwaredynamics.gen.SpoonTreeGenerator;
import at.aau.softwaredynamics.matchers.LabelAwareBottomUpMatcher;
import at.aau.softwaredynamics.matchers.tests.util.TestHelper;
import com.github.gumtreediff.matchers.MappingStore;
import com.github.gumtreediff.tree.ITree;
import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;

import java.io.IOException;

/**
 * Created by thomas on 24.11.2016.
 */
@Ignore
public class MatcherTests {

    @Test
    public void resultsAreEqual() throws IOException {
        ITree src = TestHelper.getTree("example2source", new OptimizedJdtTreeGenerator());
        ITree dst = TestHelper.getTree("example2destination", new OptimizedJdtTreeGenerator());

        LabelAwareBottomUpMatcher m1 = new LabelAwareBottomUpMatcher(src.deepCopy(), dst.deepCopy(), new MappingStore());
        LabelAwareBottomUpMatcher m2 = new LabelAwareBottomUpMatcher(src.deepCopy(), dst.deepCopy(), new MappingStore());
        m2.setRemoveMatched(false);

        m1.match();
        m2.match();

        Assert.assertEquals(m1.getMappingSet().size(), m2.getMappingSet().size());
    }

    @Test
    public void resultsAreEqual1() throws IOException {
        ITree src = TestHelper.getTree("eval1Line397_src", new OptimizedJdtTreeGenerator());
        ITree dst = TestHelper.getTree("eval1Line397_dst", new OptimizedJdtTreeGenerator());

        LabelAwareBottomUpMatcher m1 = new LabelAwareBottomUpMatcher(src.deepCopy(), dst.deepCopy(), new MappingStore());
        LabelAwareBottomUpMatcher m2 = new LabelAwareBottomUpMatcher(src.deepCopy(), dst.deepCopy(), new MappingStore());
        m2.setRemoveMatched(false);

        m1.match();
        m2.match();

        Assert.assertEquals(m1.getMappingSet().size(), m2.getMappingSet().size());
    }

    @Test
    public void resultsAreEqual2() throws IOException {
        ITree src = TestHelper.getTree("cm_3_src", new OptimizedJdtTreeGenerator());
        ITree dst = TestHelper.getTree("cm_3_dst", new OptimizedJdtTreeGenerator());

        LabelAwareBottomUpMatcher m1 = new LabelAwareBottomUpMatcher(src.deepCopy(), dst.deepCopy(), new MappingStore());
        LabelAwareBottomUpMatcher m2 = new LabelAwareBottomUpMatcher(src.deepCopy(), dst.deepCopy(), new MappingStore());
        m2.setRemoveMatched(false);

        m1.match();
        m2.match();

        Assert.assertEquals(m1.getMappingSet().size(), m2.getMappingSet().size());
    }

    @Test
    public void resultsAreEqual3() throws IOException {
        ITree src = TestHelper.getTree("cm_6_src", new OptimizedJdtTreeGenerator());
        ITree dst = TestHelper.getTree("cm_6_dst", new OptimizedJdtTreeGenerator());

        LabelAwareBottomUpMatcher m1 = new LabelAwareBottomUpMatcher(src.deepCopy(), dst.deepCopy(), new MappingStore());
        LabelAwareBottomUpMatcher m2 = new LabelAwareBottomUpMatcher(src.deepCopy(), dst.deepCopy(), new MappingStore());
        m2.setRemoveMatched(false);

        m1.match();
        m2.match();

        Assert.assertEquals(m1.getMappingSet().size(), m2.getMappingSet().size());
    }

    @Test
    public void resultsAreEqual4() throws IOException {
        ITree src = TestHelper.getTree("cm_1_src", new OptimizedJdtTreeGenerator());
        ITree dst = TestHelper.getTree("cm_1_dst", new OptimizedJdtTreeGenerator());

        LabelAwareBottomUpMatcher m1 = new LabelAwareBottomUpMatcher(src.deepCopy(), dst.deepCopy(), new MappingStore());
        LabelAwareBottomUpMatcher m2 = new LabelAwareBottomUpMatcher(src.deepCopy(), dst.deepCopy(), new MappingStore());
        m2.setRemoveMatched(false);

        m1.match();
        m2.match();

        Assert.assertEquals(m1.getMappingSet().size(), m2.getMappingSet().size());
    }
}
