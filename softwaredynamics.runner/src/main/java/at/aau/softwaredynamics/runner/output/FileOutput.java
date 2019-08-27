package at.aau.softwaredynamics.runner.output;

import at.aau.softwaredynamics.classifier.entities.SourceCodeChange;
import org.apache.commons.lang3.NotImplementedException;
import org.eclipse.jgit.revwalk.RevCommit;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.charset.Charset;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;

public class FileOutput implements OutputWriter {

    private File filePath;
    private String extension;
    private String defaultFileName = "DefaultFileNameFile";

    public FileOutput(String filePath, String extension){
        this.filePath = new File(filePath);
        this.extension = extension;
    }

    @Override
    public String getSeparator() {
        return ";";
    }

    @Override
    public void writeToDefaultOutput(String content) {
        writeToOutputIdentifier(defaultFileName,content);
    }

    @Override
    public void writeToOutputIdentifier(String fileName, String content) {
        File file = getFile(fileName,false);

        try {
            FileOutputStream fileOutputStream = new FileOutputStream(file, false);
            FileChannel fileChannel = fileOutputStream.getChannel();
            ByteBuffer byteBuffer = ByteBuffer.wrap(content.getBytes(Charset.forName("ISO-8859-1")));
                fileChannel.write(byteBuffer);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void writeChangeInformation(Map<String, List<SourceCodeChange>> changes, RevCommit srcCommit, RevCommit dstCommit, String module, String project, long timeStamp) throws SQLException {
        throw new NotImplementedException("TODO write to file");
    }


    public boolean fileExists(File file){
        return file.exists() && !file.isDirectory();
    }

    public File getFile(String fileName, boolean override) {
        File file = new File(this.filePath + File.separator + fileName+"."+extension);

        if (fileExists(file) && !override) {
            file = new File(this.filePath + File.separator + fileName+ System.currentTimeMillis()+"."+extension);
        }

        return file;
    }
}
