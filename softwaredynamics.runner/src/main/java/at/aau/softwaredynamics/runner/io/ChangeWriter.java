package at.aau.softwaredynamics.runner.io;

import at.aau.softwaredynamics.classifier.entities.FileChangeSummary;

import java.io.Closeable;

/**
 * Created by thomas on 12.12.2016.
 */
public interface ChangeWriter extends Closeable {
    void write(FileChangeSummary change);
}
