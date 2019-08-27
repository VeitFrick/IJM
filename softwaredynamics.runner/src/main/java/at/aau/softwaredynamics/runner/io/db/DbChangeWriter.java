package at.aau.softwaredynamics.runner.io.db;

import at.aau.softwaredynamics.runner.io.ChangeWriter;

/**
 * Created by thomas on 07.02.2017.
 */
public interface DbChangeWriter extends ChangeWriter {
     void setIdCache(IdCache cache);
}
