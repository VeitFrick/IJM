package at.aau.softwaredynamics.runner.output;

import at.aau.softwaredynamics.classifier.entities.SourceCodeChange;
import at.aau.softwaredynamics.dependency.ClassDependencyStrengthChange;
import at.aau.softwaredynamics.dependency.DependencyChanges;
import at.aau.softwaredynamics.dependency.NodeDependencyTree;
import at.aau.softwaredynamics.dependency.meta.DependencyLink;
import at.aau.softwaredynamics.dependency.meta.DependencyNetwork;
import at.aau.softwaredynamics.dependency.meta.Node;
import org.apache.commons.lang3.NotImplementedException;
import org.eclipse.jgit.revwalk.RevCommit;
import spoon.reflect.declaration.CtType;

import java.sql.SQLException;
import java.util.*;

public class WebserviceOutput implements OutputWriter {

    Map<RevCommit, DependencyNetwork> networks;

    public WebserviceOutput() {
        this.networks = new HashMap<>();
    }

    @Override
    public String getSeparator() {
        return null;
    }

    @Override
    public void writeToDefaultOutput(String content) {
        System.out.println("Not implemented");
    }

    @Override
    public void writeToOutputIdentifier(String identifier, String content) {
        System.out.println("Not implemented");

    }

    @Override
    public void writeDependencyInformation(Collection<DependencyChanges> dependencyChanges, RevCommit srcCommit,RevCommit dstCommit, String module, String project, long timeStamp) {
        DependencyNetwork network = new DependencyNetwork();
        Set<Node> classes = new HashSet<>();
        List<DependencyLink> links = new ArrayList<>();

        network.setCommit(dstCommit.getName());
        List<String> parentHashes = new ArrayList<>();
        for (RevCommit parent : dstCommit.getParents()) {
            parentHashes.add(parent.getName());
        }
        network.setParentCommits(parentHashes);
        network.setCommitTime(dstCommit.getCommitTime());
        dependencyChanges.removeIf(Objects::isNull);
        if (dependencyChanges.isEmpty()) {
            // same dependencies as in last commit
            //TODO maybe set flag?
        }
        for (DependencyChanges fileDependencyChange : dependencyChanges) {
            NodeDependencyTree rootStrucDst = fileDependencyChange.getDepStruct().getRootStrucDst();
            CtType containingType = rootStrucDst.getContainingType();
            // size was:  containingType.getAllMethods().size()
            Node node = new Node(containingType.getQualifiedName(), 0); // TODO maybe choose a more appropriate property for size
            classes.add(node);
            List<ClassDependencyStrengthChange> strengthChanges = rootStrucDst.calculateClassDependencyStrengthChange();
            for (ClassDependencyStrengthChange strengthChange : strengthChanges) {
                DependencyLink link = new DependencyLink(containingType.getQualifiedName(), strengthChange.getClassName(), strengthChange.getDstStrength(), strengthChange.getDeltaStrength());
                classes.add(new Node(strengthChange.getClassName()));
                links.add(link);
            }
        }

        for (DependencyLink link : links) {
            for (Node aClass : classes) {
                if (link.getTarget().equals(aClass.getName())) {
                    aClass.setSize(aClass.getSize() + link.getStrength());
                }
            }
        }


        network.setLinks(links);
        network.setNodes(new ArrayList<>(classes));
        networks.put(dstCommit, network);
    }

    @Override
    public void writeChangeInformation(Map<String, List<SourceCodeChange>> changes, RevCommit srcCommit, RevCommit dstCommit, String module, String project, long timeStamp) throws SQLException {
        throw new NotImplementedException("This will never work.");
    }

    public Map<RevCommit, DependencyNetwork> getNetworks() {
        return networks;
    }
}
