package at.aau.softwaredynamics.matchers.tests;

import at.aau.softwaredynamics.matchers.MatcherFactory;
import com.github.gumtreediff.matchers.Matcher;
import com.github.gumtreediff.tree.ITree;

public abstract class AbstractMatcherTests {

    protected abstract Class<? extends Matcher> getMatcherClass();

    protected Matcher createMatcher(ITree src, ITree dst) {
        return new MatcherFactory(getMatcherClass()).createMatcher(src, dst);
    }
}
