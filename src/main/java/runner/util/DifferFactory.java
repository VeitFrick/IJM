package runner.util;

import com.github.gumtreediff.gen.TreeGenerator;
import com.github.gumtreediff.gen.jdt.AbstractJdtTreeGenerator;
import com.github.gumtreediff.matchers.Matcher;
import differ.AbstractJavaChangeDiffer;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.lang.reflect.InvocationTargetException;

/**
 * Created by thomas on 07.02.2017.
 */
public class DifferFactory {
    private static final Logger logger = LogManager.getLogger(DifferFactory.class);

    private Class<? extends AbstractJavaChangeDiffer> differType;
    private Class<? extends Matcher> matcherType;
    private AbstractJdtTreeGenerator generator;

    public DifferFactory(
            Class<? extends AbstractJavaChangeDiffer> differType,
            Class<? extends Matcher> matcherType,
            AbstractJdtTreeGenerator generator) {
        this.differType = differType;
        this.matcherType = matcherType;

        this.generator = generator;
    }

    public AbstractJavaChangeDiffer createDiffer() {
        try {
            return differType
                    .getConstructor(Class.class, TreeGenerator.class)
                    .newInstance(this.matcherType, this.generator);
        } catch (
                InstantiationException
                        | IllegalAccessException
                        | InvocationTargetException
                        | NoSuchMethodException e) {
            e.printStackTrace();
            logger.error("Cannot create differ", e);
        }

        return null;
    }
}
