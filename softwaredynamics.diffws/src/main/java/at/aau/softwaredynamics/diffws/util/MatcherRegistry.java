package at.aau.softwaredynamics.diffws.util;

import at.aau.softwaredynamics.matchers.JavaMatchers;
import com.github.gumtreediff.matchers.CompositeMatchers;
import com.github.gumtreediff.matchers.Matcher;
import com.github.gumtreediff.matchers.OptimizedVersions;

import java.util.HashMap;

public class MatcherRegistry {
    HashMap<Integer, Class<? extends  Matcher>> matcherMap;

    public MatcherRegistry() {
        this.matcherMap = new HashMap<>();

        // TODO (Thomas): init from config
        matcherMap.put(1, JavaMatchers.IterativeJavaMatcher_V2.class);
        matcherMap.put(2, CompositeMatchers.ClassicGumtree.class);
        matcherMap.put(3, OptimizedVersions.MtDiff.class);
        matcherMap.put(4, JavaMatchers.IterativeJavaMatcher_Spoon.class);
        /*matcherMap.put(3, JavaMatchers.IterativeJavaMatcher.class);
        matcherMap.put(4, JavaMatchers.IterativeJavaMatcher_V1.class);*/

    }

    public HashMap<Integer, Class<? extends Matcher>> getMatcherMap() {
        return matcherMap;
    }

    public Class<? extends Matcher> getMatcherTypeFor(int id) {
        return matcherMap.getOrDefault(id, null);
    }
}
