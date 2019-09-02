package at.aau.softwaredynamics.runner.git;

import at.aau.softwaredynamics.classifier.JChangeClassifier;
import at.aau.softwaredynamics.dependency.DependencyChanges;
import at.aau.softwaredynamics.dependency.DependencyExtractor;
import at.aau.softwaredynamics.gen.SpoonTreeGenerator;
import at.aau.softwaredynamics.matchers.JavaMatchers;
import at.aau.softwaredynamics.runner.output.OutputWriter;
import at.aau.softwaredynamics.runner.util.GitHelper;
import org.eclipse.jgit.diff.DiffEntry;
import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.revwalk.RevCommit;

import java.util.ArrayList;
import java.util.List;

/**
 * Analyzes all files from a single commit from a repository
 * and extracts its dependencies using a JChangeClassifier instance.
 */
public class DependencyAnalyzer implements Runnable {

    private final Repository repository;
    private final RevCommit commit;
    private OutputWriter outputWriter;
    private JChangeClassifier classifier;
    private DependencyExtractor dependencyExtractor;

    List<DependencyChanges> dependencyChanges;
    private long totalTimeTaken;

    /**
     * Initializes a Dependency Analyzer
     *
     * @param repository   the git repository to work in
     * @param commit       the commit to analyze
     * @param classifier   the classifier to use
     * @param outputWriter the OutputWriter to write the output to, set to null if not wanted
     */
    public DependencyAnalyzer(Repository repository, RevCommit commit, JChangeClassifier classifier, OutputWriter outputWriter) {
        this.repository = repository;
        this.commit = commit;
        this.classifier = classifier;
        this.outputWriter = outputWriter;
        this.dependencyChanges = new ArrayList<>();
        RevCommit[] parents = commit.getParents();
        if (parents.length > 1) {
            throw new IllegalArgumentException("Supplied commit has more than 1 parent and is therefore not supported.");
        }
    }

    /**
     * Initializes a Dependency Analyzer
     *
     * @param repository   the git repository to work in
     * @param commit       the commit to analyze
     * @param classifier   the classifier to use
     * @param outputWriter the OutputWriter to write the output to, set to null if not wanted
     * @param repoName     the repository name of the GitHub repo in the format "account/repo"
     */
    public DependencyAnalyzer(Repository repository, RevCommit commit, JChangeClassifier classifier, OutputWriter outputWriter, String repoName) {
        this(repository,commit,classifier,outputWriter);
    }

    /**
     * Reads every change {@link DiffEntry} in a commit, extracts its contents and
     * dependencies. Results get saved into {@link DependencyChanges}
     * Writes it to the {@link OutputWriter} if available.
     */
    @Override
    public void run() {
        System.out.println(commit.getId().toObjectId() + " / MSG: " + commit.getShortMessage());
        if(commit.getParents().length>0) {
            List<DiffEntry> diffs = GitHelper.getDiffs(repository, commit, commit.getParents()[0]);
            for (DiffEntry diff : diffs) {

                DependencyChanges dependencyChanges = extractDependenciesForDiff(diff);
                if (dependencyChanges != null) this.dependencyChanges.add(dependencyChanges);

                if (outputWriter != null) {
                    String fileNameOld = diff.getNewPath().substring(diff.getNewPath().lastIndexOf("/") + 1);
                    String fileNameNew = diff.getNewPath().substring(diff.getOldPath().lastIndexOf("/") + 1);
                    if (diff.getNewPath().equals(diff.getOldPath())) {
                        outputWriter.writeToOutputIdentifier(commit + " " + fileNameOld, getOutputString());
                    } else {
                        outputWriter.writeToOutputIdentifier(commit + " " + fileNameOld + " " + fileNameNew, getOutputString());
                    }
                }
            }
        } else{
            // TODO Code for inital commit here (having no parent)
        }
    }


    private DependencyChanges extractDependenciesForDiff(DiffEntry diff) {
        try {

            String srcString = GitHelper.getFileContent(diff.getOldId(), repository);
            String dstString = GitHelper.getFileContent(diff.getNewId(), repository);

            long startTime = System.currentTimeMillis();
            classifier = new JChangeClassifier(JavaMatchers.IterativeJavaMatcher_Spoon.class, new SpoonTreeGenerator());
            classifier.classify(srcString,dstString);
            dependencyExtractor = new DependencyExtractor(classifier.getMappings(),classifier.getActions(),classifier.getSrcContext().getRoot(),classifier.getDstContext().getRoot(),srcString,dstString);
            dependencyExtractor.extractDependencies();

            DependencyChanges dependencyChanges = dependencyExtractor.getDependencyChanges();
//            extractor.setFileNames();
            this.totalTimeTaken = System.currentTimeMillis() - startTime;

            System.out.println("Took " + totalTimeTaken + "ms to extract Dependencies for " + diff.getNewPath());
            return dependencyChanges;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * Constructs output String for given {@link OutputWriter}
     *
     * @return
     */
    private String getOutputString() {
        long startTime = System.currentTimeMillis();
//        String s = "# Commit: " + commit.getName() + "\n" +
//                   "# Time: " + totalTimeTaken + "\n" +
//                   "# https://github.com/"+repoName+"/commit/"+commit.getName()+"\n" +
//                dependencyExtractor.getDependencyChanges().getDependencyChangeOverview(classifier.getChangedDependencyNodes(), outputWriter.getSeparator());
        this.totalTimeTaken = System.currentTimeMillis() - startTime;

        System.out.println("Took " + totalTimeTaken + "ms for getting the output.");
        return "This Method (getOutputString()) should currently not be used TODO "; //TODO: SourceCodeChanges not available anymore
    }

    /**
     * Returns the list of dependency changes that include changes and dependencies
     * for each file in this commit
     * Run {@link DependencyAnalyzer#run()} before to get extracted dependencies.
     *
     * @return
     */
    public List<DependencyChanges> getDependencyChanges() {
        return dependencyChanges;
    }

    public Repository getRepository() {
        return repository;
    }

    public RevCommit getCommit() {
        return commit;
    }

    public long getTotalTimeTaken() {
        return totalTimeTaken;
    }
}
