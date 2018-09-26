package matchers;

import com.github.gumtreediff.matchers.Matcher;
import com.github.gumtreediff.tree.ITree;

import java.util.function.Function;

/**
 * Created by thomas on 31.01.2017.
 */
public class PartialMatcherConfiguration {
    private final Function<ITree, Boolean> cutOffCondition;
    private final Class<? extends Matcher> innerMatcherType;
    private final Class<? extends Matcher> subNodeMatcher;
    private final Function<ITree, Boolean> subNodeMatchCondition;

    public PartialMatcherConfiguration(
            Function<ITree, Boolean> cutOffCondition,
            Class<? extends Matcher> innerMatcherType,
            Class<? extends Matcher> subNodeMatcher,
            Function<ITree, Boolean> subNodeMatchCondition) {
        this.cutOffCondition = cutOffCondition;
        this.innerMatcherType = innerMatcherType;
        this.subNodeMatcher = subNodeMatcher;
        this.subNodeMatchCondition = subNodeMatchCondition;
    }

    public PartialMatcherConfiguration(
            Function<ITree, Boolean> cutOffCondition,
            Class<? extends Matcher> innerMatcherType) {
        this(cutOffCondition, innerMatcherType, null, null);
    }

    public Function<ITree, Boolean> getCutOffCondition() {
        return cutOffCondition;
    }

    public Class<? extends Matcher> getInnerMatcherType() {
        return this.innerMatcherType;
    }

    public Class<? extends Matcher> getSubNodeMatcherType() {
        return this.subNodeMatcher;
    }

    public boolean getMatchSubtrees() {
        return this.subNodeMatcher != null;
    }

    public boolean includeInResult(ITree node) {
        if (this.subNodeMatchCondition == null)
            return true;

        return subNodeMatchCondition.apply(node);
    }
}
