package at.aau.softwaredynamics.runner.io.fs;

import at.aau.softwaredynamics.classifier.entities.FileChangeSummary;
import at.aau.softwaredynamics.classifier.entities.SourceCodeChange;
import at.aau.softwaredynamics.gen.NodeType;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;

/**
 * Created by thomas on 12.12.2016.
 */
public class FileChangeWriter extends AbstractCsvWriter {
    private static final Logger logger = LogManager.getLogger(FileChangeWriter.class);

    public FileChangeWriter(String filePath, String fileName) throws IOException {
        super(filePath, fileName);
    }

    @Override
    public void write(FileChangeSummary changeSummary) {
        for(SourceCodeChange change : changeSummary.getChanges()) {

            String[] values = new String[] {
                    changeSummary.getCommit(),
                    change.getNode().getLabel(),
                    change.getAction().getName(),
                    String.valueOf(NodeType.getEnum(change.getNodeType())),
                    String.valueOf(change.getSrcInfo().getStartLineNumber()),
                    String.valueOf(change.getSrcInfo().getEndLineNumber()),
                    String.valueOf(change.getDstInfo().getStartLineNumber()),
                    String.valueOf(change.getDstInfo().getEndLineNumber()),
                    changeSummary.getDstFileName()
            };

            try {
                this.writeLine(values);
            } catch (IOException e) {
                logger.error(e);
            }
        }
    }

    @Override
    protected String[] getHeader() {
        return new String[]{"Commit", "Label", "Change", "NodeType", "SrcStart", "SrcEnd", "DstStart", "DstEnd", "DstFile"};
    }
}
