package at.aau.softwaredynamics.runner.output;

import at.aau.softwaredynamics.classifier.entities.SourceCodeChange;
import at.aau.softwaredynamics.dependency.DependencyChanges;
import org.eclipse.jgit.revwalk.RevCommit;

import java.sql.SQLException;
import java.util.Collection;
import java.util.List;
import java.util.Map;


public interface OutputWriter {


    String getSeparator();
    void writeToDefaultOutput(String content);
    void writeToOutputIdentifier(String identifier, String content);

    void writeDependencyInformation(Collection<DependencyChanges> dependencyChanges, RevCommit srcCommit, RevCommit dstCommit, String module, String project, long timeStamp) throws SQLException;

    void writeChangeInformation(Map<String, List<SourceCodeChange>> changes, RevCommit srcCommit, RevCommit dstCommit, String module, String project, long timeStamp) throws SQLException;
}
