package at.aau.softwaredynamics.runner.git;

import org.eclipse.jgit.revwalk.RevCommit;

import java.util.List;
import java.util.concurrent.Callable;

public interface CancelableCallable extends Callable {

    void cancel();

    void setCommit(RevCommit commit);

    List<String> getPaths();

    void setPaths(List<String> paths);
}
