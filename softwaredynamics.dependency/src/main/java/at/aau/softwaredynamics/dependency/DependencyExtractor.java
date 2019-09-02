package at.aau.softwaredynamics.dependency;

import com.github.gumtreediff.actions.model.Action;
import com.github.gumtreediff.matchers.MappingStore;
import com.github.gumtreediff.tree.ITree;

import java.util.List;

public class DependencyExtractor {

    private DependencyFilter filter;
    private MappingStore mappings;
    private List<Action> actions;

    private ITree srcRoot;
    private ITree dstRoot;

    private String srcString;
    private String dstString;

    private DependencyChanges dependencyChanges;

    public DependencyExtractor(MappingStore mappings, List<Action> actions, ITree srcRoot, ITree dstRoot, String srcString, String dstString) {
        this.mappings = mappings;
        this.actions = actions;
        this.srcRoot = srcRoot;
        this.dstRoot = dstRoot;
        this.srcString = srcString;
        this.dstString = dstString;
        this.filter = new DependencyFilter();
    }

    public DependencyExtractor(DependencyFilter filter, MappingStore mappings, List<Action> actions, ITree srcRoot, ITree dstRoot, String srcString, String dstString) {
        this(mappings,actions,srcRoot,dstRoot,srcString,dstString);
        this.filter = filter;
    }

    public void extractDependencies() {
        dependencyChanges = new DependencyChanges(mappings, actions);
        dependencyChanges.extractDependencies(srcRoot, dstRoot, srcString, dstString, filter);
    }

    public DependencyChanges getDependencyChanges() {
        return dependencyChanges;
    }
}
