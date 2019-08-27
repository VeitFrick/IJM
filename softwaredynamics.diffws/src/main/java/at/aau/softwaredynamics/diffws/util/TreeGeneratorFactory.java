package at.aau.softwaredynamics.diffws.util;

import at.aau.softwaredynamics.gen.SpoonTreeGenerator;
import at.aau.softwaredynamics.matchers.JavaMatchers;
import com.github.gumtreediff.gen.TreeGenerator;
import com.github.gumtreediff.matchers.CompositeMatchers;
import com.github.gumtreediff.matchers.Matcher;
import com.github.gumtreediff.matchers.OptimizedVersions;

import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;

public class TreeGeneratorFactory {
    private HashMap<Class, Class> defaultGenerators = new HashMap<>();

    public TreeGeneratorFactory() {
        // TODO (Thomas): init from config
        defaultGenerators.put(CompositeMatchers.ClassicGumtree.class, SpoonTreeGenerator.class);
        defaultGenerators.put(OptimizedVersions.MtDiff.class, SpoonTreeGenerator.class);
        defaultGenerators.put(JavaMatchers.IterativeJavaMatcher.class, SpoonTreeGenerator.class);
        defaultGenerators.put(JavaMatchers.IterativeJavaMatcher_V1.class, SpoonTreeGenerator.class);
        defaultGenerators.put(JavaMatchers.IterativeJavaMatcher_V2.class, SpoonTreeGenerator.class);
        defaultGenerators.put(JavaMatchers.IterativeJavaMatcher_Spoon.class, SpoonTreeGenerator.class);
    }

    /**
     * Creates the default TreeGenerator for the given matcher type. If the type is not known null is returned
     *
     * @param matcherType
     * @return
     */
    public TreeGenerator createDefaultGeneratorFor(Class<? extends Matcher> matcherType) {
        TreeGenerator retVal = null;

        Class generatorType = defaultGenerators.getOrDefault(matcherType, null);

        if (generatorType != null) {
            try {
                retVal = (TreeGenerator) generatorType.getConstructor().newInstance();
            } catch (InstantiationException e) {
                e.printStackTrace();
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            } catch (InvocationTargetException e) {
                e.printStackTrace();
            } catch (NoSuchMethodException e) {
                e.printStackTrace();
            }
        }
        return retVal;
    }
}
