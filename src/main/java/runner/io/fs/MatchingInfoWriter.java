package runner.io.fs;


import com.github.gumtreediff.tree.ITree;
import differ.entities.FileChangeSummary;
import differ.entities.SourceCodeChange;
import differ.util.ITreeNodeHelper;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import runner.io.ChangeWriter;

import java.io.*;

/**
 * Created by thomas on 19.12.2016.
 */
public class MatchingInfoWriter implements ChangeWriter {
    private static final Logger logger = LogManager.getLogger(MatchingInfoWriter.class);

    private String SEPARATOR = ";";

    private String[] columns = new String[] {
            "Commit",
            "SrcFile",
            "DstFile",
            "Change",
            "NodeType",
            "SrcStart",
            "SrcEnd",
            "DstStart",
            "DstEnd",
            "SrcTreeSize",
            "DstTreeSize",
            "SrcSubTreeSize",
            "DstSubTreeSize"};

    private BufferedWriter writer;

    public MatchingInfoWriter(String filePath, String fileName) throws IOException {
        FileHandler fileHandler = new FileHandler(filePath, "csv");
        File outputFile = fileHandler.getFile(fileName, false);

        BufferedWriter bufferedWriter = new BufferedWriter(new OutputStreamWriter(
                new FileOutputStream(outputFile), "utf-8"));

        writer = bufferedWriter;

        this.writeHeader();
    }

    private void writeHeader() throws IOException {
        this.writer.write(String.join(SEPARATOR, columns));
        this.writer.newLine();
    }

    @Override
    public void write(FileChangeSummary changeSummary) {
        for(SourceCodeChange change : changeSummary.getChanges()) {
            int srcSubTreeSize, dstSubTreeSize, srcTreeSize, dstTreeSize;
            srcSubTreeSize = dstSubTreeSize = srcTreeSize = dstTreeSize = 0;

            if (change.getSrcInfo().getNode() != null) {
                ITree srcNode = change.getSrcInfo().getNode();
                srcSubTreeSize = srcNode.getSize();
                srcTreeSize = ITreeNodeHelper.getCompilationUnit(srcNode).getSize();
            }

            if (change.getDstInfo().getNode() != null) {
                ITree dstNode = change.getDstInfo().getNode();
                dstSubTreeSize = dstNode.getSize();
                dstTreeSize = ITreeNodeHelper.getCompilationUnit(dstNode).getSize();
            }

            String line = String.join(SEPARATOR,
                    new String[] {
                            changeSummary.getCommit(),
                            changeSummary.getSrcFileName(),
                            changeSummary.getDstFileName(),
                            change.getActionType(),
                            String.valueOf(change.getNodeType()),
                            String.valueOf(change.getSrcInfo().getStartLineNumber()),
                            String.valueOf(change.getSrcInfo().getEndLineNumber()),
                            String.valueOf(change.getDstInfo().getStartLineNumber()),
                            String.valueOf(change.getDstInfo().getEndLineNumber()),
                            String.valueOf(srcTreeSize),
                            String.valueOf(dstTreeSize),
                            String.valueOf(srcSubTreeSize),
                            String.valueOf(dstSubTreeSize)
                    }
            );

            try {
                this.writer.write(line);
                this.writer.newLine();
            } catch (IOException e) {
                e.printStackTrace();
                logger.error(e);
            }
        }
    }

    @Override
    public void close() throws IOException {
        this.writer.flush();
        this.writer.close();
    }
}
