package runner.io;

import differ.entities.FileChangeSummary;

import java.io.IOException;

/**
 * Created by thomas on 30.03.2017.
 */
public class NullChangeWriter implements ChangeWriter {
    @Override
    public void write(FileChangeSummary change) {
        // do nothing
    }

    @Override
    public void close() throws IOException {
        // do nothing
    }
}
