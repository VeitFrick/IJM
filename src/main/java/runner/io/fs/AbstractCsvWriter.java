package runner.io.fs;


import differ.entities.FileChangeSummary;
import runner.io.ChangeWriter;

import java.io.*;

/**
 * Created by thomas on 30.07.2017.
 */
public abstract class AbstractCsvWriter implements ChangeWriter {
    private BufferedWriter writer;

    public AbstractCsvWriter(String filePath, String fileName) throws IOException {
        FileHandler fileHandler = new FileHandler(filePath, "csv");
        File outputFile = fileHandler.getFile(fileName, false);

        BufferedWriter bufferedWriter = new BufferedWriter(new OutputStreamWriter(
                new FileOutputStream(outputFile), "utf-8"));

        writer = bufferedWriter;
        this.writeLine(getHeader());
    }

    public void close() throws IOException {
        this.writer.flush();
        this.writer.close();
    }

    protected abstract String[] getHeader();

    protected synchronized void writeLine(String[] values) throws IOException {
        this.writer.write(String.join(";", values));
        this.writer.newLine();
        this.writer.flush();
    }

    @Override
    public abstract void write(FileChangeSummary change);
}