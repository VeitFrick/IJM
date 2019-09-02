package at.aau.softwaredynamics.dependency.meta;

import at.aau.softwaredynamics.classifier.entities.NodeInfo;
import at.aau.softwaredynamics.classifier.entities.SourceCodeChange;
import at.aau.softwaredynamics.dependency.NodeDependency;
import com.github.gumtreediff.actions.model.Action;

import java.util.ArrayList;
import java.util.List;

/**
 * Used to display Dependencies in DiffViz
 * holds a List of Dependencies and contains
 * Action type and Src/Dst Positions
 */
public class DependencySourceCodeNode extends SourceCodeChange {

    List<NodeDependency> dependencies;

    public DependencySourceCodeNode(Action action, NodeInfo srcNodeInfo, NodeInfo dstNodeInfo) {
        super(action, srcNodeInfo, dstNodeInfo);
        dependencies = new ArrayList<>();
    }

    public List<NodeDependency> getDependencies() {
        return dependencies;
    }

    public void setDependencies(List<NodeDependency> dependencies) {
        this.dependencies = dependencies;
    }

    public void addDependency(NodeDependency dep) {
        dependencies.add(dep);
    }

}
