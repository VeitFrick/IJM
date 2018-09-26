package runner.io;

import differ.entities.FileChangeSummary;

import java.io.Closeable;

/**
 * Created by thomas on 12.12.2016.
 */
public interface ChangeWriter extends Closeable {
    void write(FileChangeSummary change);
}
