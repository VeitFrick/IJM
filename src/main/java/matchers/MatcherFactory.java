package matchers;

import com.github.gumtreediff.matchers.MappingStore;
import com.github.gumtreediff.matchers.Matcher;
import com.github.gumtreediff.tree.ITree;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

/**
 * Created by thomas on 12.12.2016.
 */
public class MatcherFactory  {
    private static final Logger logger = LogManager.getLogger(MatcherFactory.class);

    public Class<? extends Matcher> defaultMatcherType;

    public MatcherFactory(Class<? extends Matcher> defaultMatcherType) {
        this.defaultMatcherType = defaultMatcherType;
    }

    public Matcher createMatcher(ITree srcTree, ITree dstTree) {
        return createMatcher(defaultMatcherType, srcTree, dstTree, new MappingStore());
    }

    public Matcher createMatcher(ITree srcTree, ITree dstTree, MappingStore store) {
        return createMatcher(defaultMatcherType, srcTree, dstTree, store);
    }

    public Matcher createMatcher(Class<? extends Matcher> type, ITree srcTree, ITree dstTree, MappingStore store)  {
        try {
            Constructor constructor = type.getConstructor(ITree.class, ITree.class, MappingStore.class);
            return (Matcher) constructor.newInstance(srcTree, dstTree, store);
        } catch (
                InstantiationException
                        | IllegalAccessException
                        | InvocationTargetException
                        | NoSuchMethodException e) {
            e.printStackTrace();
            logger.error("Cannot create matcher", e);
        }

        return null;
    }
}
