package at.aau.softwaredynamics.runner.util;

import at.aau.softwaredynamics.classifier.AbstractJavaChangeClassifier;
import com.github.gumtreediff.gen.TreeGenerator;
import com.github.gumtreediff.matchers.Matcher;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.lang.reflect.InvocationTargetException;

/**
 * Created by thomas on 07.02.2017.
 */
public class ClassifierFactory {
    private static final Logger logger = LogManager.getLogger(ClassifierFactory.class);

    private Class<? extends AbstractJavaChangeClassifier> classifierType;
    private Class<? extends Matcher> matcherType;
    private TreeGenerator generator;

    public ClassifierFactory(
            Class<? extends AbstractJavaChangeClassifier> classifierType,
            Class<? extends Matcher> matcherType,
            TreeGenerator generator) {
        this.classifierType = classifierType;
        this.matcherType = matcherType;

        this.generator = generator;
    }

    public AbstractJavaChangeClassifier createClassifier() {
        try {
            return classifierType
                    .getConstructor(Class.class, TreeGenerator.class)
                    .newInstance(this.matcherType, this.generator);
        } catch (
                InstantiationException
                        | IllegalAccessException
                        | InvocationTargetException
                        | NoSuchMethodException e) {
            e.printStackTrace();
            logger.error("Cannot create classifier", e);
        }

        return null;
    }
}
