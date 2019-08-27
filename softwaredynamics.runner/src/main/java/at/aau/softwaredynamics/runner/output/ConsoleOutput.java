package at.aau.softwaredynamics.runner.output;

import at.aau.softwaredynamics.classifier.entities.SourceCodeChange;
import org.apache.commons.lang3.NotImplementedException;
import org.eclipse.jgit.revwalk.RevCommit;

import java.sql.SQLException;
import java.util.List;
import java.util.Map;

public class ConsoleOutput implements OutputWriter {

    @Override
    public String getSeparator() {
        return null;
    }

    @Override
    public void writeToDefaultOutput(String content) {
        System.out.println(content);
    }

    @Override
    public void writeToOutputIdentifier(String identifier, String content) {
        System.out.println(content);
    }

    @Override
    public void writeChangeInformation(Map<String, List<SourceCodeChange>> changes, RevCommit srcCommit, RevCommit dstCommit, String module, String project, long timeStamp) throws SQLException {
        throw new NotImplementedException("TODO write to console");
    }

}
