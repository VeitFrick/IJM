package runner.io.fs;

import differ.entities.FileChangeSummary;
import differ.entities.SourceCodeChange;
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
                    change.getClassName(),
                    change.getMethodName(),
                    change.getChangeType().toString(),
                    String.valueOf(change.getNodeType()),
                    change.getActionType(),
                    String.valueOf(change.getSrcInfo().getStartLineNumber()),
                    String.valueOf(change.getSrcInfo().getEndLineNumber()),
                    String.valueOf(change.getDstInfo().getStartLineNumber()),
                    String.valueOf(change.getDstInfo().getStartLineNumber()),
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
        return new String[] {"Commit", "Type", "Method", "ChangeType", "NodeType", "Change", "SrcStart", "SrcEnd", "DstStart", "DstEnd", "DstFile"};
    }
}
