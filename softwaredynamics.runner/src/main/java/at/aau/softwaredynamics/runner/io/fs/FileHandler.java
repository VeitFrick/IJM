package at.aau.softwaredynamics.runner.io.fs;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

/**
 * Created by veit on 22.11.2016.
 */
public class FileHandler {
    private static final Logger logger = LogManager.getLogger(FileHandler.class);

    private File filePath;
    private String extension;

    public FileHandler(String filePath, String extension){
        this.filePath = new File(filePath);
        this.extension = extension;
    }

    public void writeToFile(String fileName, List<String> lines){
        Path file = Paths.get(fileName);
        try {
            Files.write(file, lines, Charset.forName("UTF-8"));
        } catch (IOException e) {
            e.printStackTrace();
            logger.error(e);
        }
    }

    /**
     * Writes to a File. Does not override existing Files.
     */
    public void writeToFileNoOverride(String fileName, List<String> lines){
        writeToFile(getFile(fileName,false).toString(), lines);
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

    public String loadFile(String fileName){
        String path = filePath+File.separator+fileName+"."+extension;
        try {
            return  new String(Files.readAllBytes(Paths.get(path)));
        } catch (IOException e) {
            e.printStackTrace();
            logger.error(e);
            return e.toString();
        }
    }


    public String getFilePath() {
        return filePath.toString();
    }

    public void setFilePath(String filePath) {
        this.filePath = new File(filePath);
    }

    public String getExtension() {
        return extension;
    }

    public void setExtension(String extension) {
        this.extension = extension;
    }

}
