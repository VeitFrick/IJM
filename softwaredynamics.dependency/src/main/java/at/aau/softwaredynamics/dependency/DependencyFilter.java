package at.aau.softwaredynamics.dependency;

import java.util.ArrayList;
import java.util.List;

/**
 * Defines which elements should be ignored when creating a {@link NodeDependency}.
 */
public class DependencyFilter {
    private boolean ignoreSelfDependency;
    private List<String> ignoreRegex;

    public DependencyFilter() {
        this(false, new ArrayList<>());
    }

    /**
     * Creates a new DependencyFilter with given ignore rules.
     *
     * @param ignoreSelfDependency if true, all self dependencies will be ignored
     * @param ignoreRegex          A list of regular expressions that are used to filter the dependent classes
     *                             A dependency is ignored if at least one regex matches the dependent class FQN.
     */
    public DependencyFilter(boolean ignoreSelfDependency, List<String> ignoreRegex) {
        this.ignoreSelfDependency = ignoreSelfDependency;
        this.ignoreRegex = ignoreRegex;
    }

    public boolean accepts(Dependency dep) {
        if (ignoreSelfDependency && dep.getSelfDependency()) return false;
        for (String regex : ignoreRegex) {
            if (dep.getDependentOnClass().matches(regex))
                return false;
        }
        return true;
    }
}
