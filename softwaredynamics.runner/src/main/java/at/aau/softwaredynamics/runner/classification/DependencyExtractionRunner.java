package at.aau.softwaredynamics.runner.classification;


import at.aau.softwaredynamics.classifier.JChangeClassifier;
import at.aau.softwaredynamics.dependency.DependencyExtractor;
import at.aau.softwaredynamics.gen.SpoonTreeGenerator;
import at.aau.softwaredynamics.matchers.JavaMatchers;
import at.aau.softwaredynamics.runner.git.DependencyAnalyzer;
import at.aau.softwaredynamics.runner.output.FileOutput;
import at.aau.softwaredynamics.runner.util.GitHelper;
import org.eclipse.jgit.diff.DiffEntry;
import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.revwalk.RevCommit;

import java.io.IOException;
import java.io.SyncFailedException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Random;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;


/**
 * EXPERIMENTAL CLASS - NOT FOR PRODUCTION CODE
 * Imports and extracts changes for a whole project or commit from GitHub at a specific commit
 * This class can be used (and adapted) for special use cases. Such as only N random commits for a project.
 */
public class DependencyExtractionRunner {

    private Project project;
    private int numThreads = 4; //Change this, to change the number of Threads!
    private int currentCommit;
    private long totalRunTimeSoFar;
    private long startTime;
    private int totalCommits;
    private static FileOutput fileOutput;

    private static ArrayList<Project> projectList;

    private List<DependencyAnalyzer> analyzedCommits;

    public DependencyExtractionRunner(Project project) {
        this.project = project;
        this.startTime = System.currentTimeMillis();
        this.analyzedCommits = new ArrayList<>();
    }

    public static void main(String[] args) {
        System.out.println("THIS IS EXPERIMENTAL CODE, DO USE WITH CAUTION!");
        String pathToProjects = args[0];
        //String pathToProjects = "/HARD/CODED/PATH/";
        fileOutput = new FileOutput(args[1], "csv");
        //fileOutput = new FileOutput("/HARD/CODED/PATH/", "csv");

        projectList = new ArrayList<>();
        projectList.add(new Project(pathToProjects,"apache/commons-io","commons-io"));
        DependencyExtractionRunner importer;

        //30 random Files for each project
        for (Project project: projectList) {
            importer = new DependencyExtractionRunner(project);
            importer.analyzeRandomFilesFromRepository(20);
        }

    }

    public void analyzeCommit(String commitHash){
        Repository repository = null;
        try {
            repository = GitHelper.openRepository(this.project.getFullPath());
            RevCommit commit = GitHelper.getCommit(repository,commitHash);
            processCommit(repository, commit);
            repository.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void analyzeRepository() {
        try {
            Repository repository = GitHelper.openRepository(this.project.getFullPath());
            Collection<RevCommit> commits = GitHelper.getCommits(repository, "HEAD");

            totalCommits = commits.size();
            System.out.println("Analyzing " + totalCommits +" commits.");
            System.out.println("AverageTimePer Commit " + (totalRunTimeSoFar / (currentCommit + 1)));

            ExecutorService executor = Executors.newFixedThreadPool(numThreads);

            for (RevCommit commit : commits) {
                executor.execute(() -> this.processCommit(repository, commit));
            }
            executor.shutdown();

            try {
                executor.awaitTermination(Long.MAX_VALUE, TimeUnit.NANOSECONDS);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            repository.close();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    public void analyzeRandomCommitsFromRepository(int i ){
        try {
            Repository repository = GitHelper.openRepository(this.project.getFullPath());
            Collection<RevCommit> commits = GitHelper.getCommits(repository, "HEAD");

            for (int j = 0; j < i; j++) {
                int k = new Random().nextInt(commits.size());
                processCommit(repository,(RevCommit)commits.toArray()[k]);
            }

            repository.close();

        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    /**
     * Analyzes the Dependencies for one Files of i Random Commits
     * @param i number of commits
     */
    private void analyzeRandomFilesFromRepository(int i) {
        try {
            Repository repository = GitHelper.openRepository(this.project.getFullPath());
            Collection<RevCommit> commits = GitHelper.getCommits(repository, "HEAD");

            for (int j = 0; j < i; j++) {
                int k = 0;
                do{k = new Random().nextInt(commits.size());}
                while(!processCommitOneRandomFile(repository,(RevCommit)commits.toArray()[k]));
            }
            repository.close();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    private void processCommit(Repository repository, RevCommit commit) {
        System.out.println("Commit: " + this.currentCommit +"/" + this.totalCommits);
        System.out.println("Time remaining: " + this.getRemainingTime());
        DependencyAnalyzer analyzer = new DependencyAnalyzer(repository, commit, new JChangeClassifier(JavaMatchers.IterativeJavaMatcher_Spoon.class,new SpoonTreeGenerator()), fileOutput, this.project.getGitRepoName());
        analyzer.run();
        analyzedCommits.add(analyzer);
        increaseCurrentCommit();
        increaseTotalRuntime(System.currentTimeMillis());
        System.gc();
    }


    /**
     * Processes on random File of a commit.
     * Does not use DependencyAnalyzer.
     * @param repository
     * @param commit
     * @return returns true if proper results have been obtained.False if something went wrong or if there are no changes at all in the random commit file
     */
    private boolean processCommitOneRandomFile(Repository repository, RevCommit commit) {
        System.out.println("Commit: " + this.currentCommit + "/" + this.totalCommits);
        System.out.println("Time remaining: " + this.getRemainingTime());
        System.out.println(commit.getId().toObjectId() + " / MSG: " + commit.getShortMessage());
        long totalTimeTaken = -1;
        boolean producedFeasableResults = false;
        if (commit.getParents().length > 0) {
            List<DiffEntry> diffs = GitHelper.getDiffs(repository, commit, commit.getParents()[0]);
            if (diffs.size() > 0) {
                int k = new Random().nextInt(diffs.size());
                DiffEntry diff = (DiffEntry) diffs.toArray()[k];
                String oldFileName = diff.getOldPath().substring(diff.getOldPath().lastIndexOf("/") + 1);
                String newFileName = diff.getNewPath().substring(diff.getNewPath().lastIndexOf("/") + 1);
                JChangeClassifier classifier = new JChangeClassifier(JavaMatchers.IterativeJavaMatcher_Spoon.class, new SpoonTreeGenerator());
                DependencyExtractor dependencyExtractor = null;
                try {
                    String srcString = GitHelper.getFileContent(diff.getOldId(), repository);
                    String dstString = GitHelper.getFileContent(diff.getNewId(), repository);

                    long startTime = System.currentTimeMillis();
                    classifier.classify(srcString,dstString,false);
                    dependencyExtractor = new DependencyExtractor(classifier.getMappings(),classifier.getActions(),classifier.getSrcContext().getRoot(),classifier.getDstContext().getRoot(),srcString,dstString);
                    dependencyExtractor.extractDependencies();
                    totalTimeTaken = System.currentTimeMillis() - startTime;

                    System.out.println("Took " + totalTimeTaken + "ms to extract Dependencies for " + diff.getNewPath());
                } catch (Exception e) {
                    e.printStackTrace();
                }
                if (fileOutput != null) {
                    String fileName = diff.getNewPath().substring(diff.getNewPath().lastIndexOf("/") + 1);
                    String outputString = "# Commit: " + commit.getName() + "\n" +
                            "# Time: " + totalTimeTaken + "\n" +
                            "# File(s) " + oldFileName + " -> " + newFileName + "\n" +
                            "# https://github.com/" + this.project.getGitRepoName() + "/commit/" + commit.getName() + "\n";
                    try {


                        if (dependencyExtractor.getDependencyChanges() != null) {
                            //Can be Null if no actions (changes in the source code) detected in the file.
//                            outputString += dependencyExtractor.getDependencyChanges().getChangedDependenciesAsString(fileOutput.getSeparator()); //Change This line for Overview/Or Change List
                            producedFeasableResults = true;
                        }
                        else{
                            outputString+= "No Actions (Source Code Changes) detected in this Diff!";
                        }

                        fileOutput.writeToOutputIdentifier(commit + " " + fileName, outputString);

                    } catch (Exception e) {
                        System.out.println("This should not be happening!");
                        e.printStackTrace();
                    }
                }

            }


    }
        increaseCurrentCommit();
        increaseTotalRuntime(System.currentTimeMillis());
        System.gc();
        return producedFeasableResults;
    }



    private String getRemainingTime() {
        if(currentCommit==0)
            return "";
        long timeToFinish = (totalRunTimeSoFar/currentCommit)*(totalCommits-currentCommit);
        return String.format("%02d:%02d:%02d",
                TimeUnit.MILLISECONDS.toHours(timeToFinish),
                TimeUnit.MILLISECONDS.toMinutes(timeToFinish) -
                        TimeUnit.HOURS.toMinutes(TimeUnit.MILLISECONDS.toHours(timeToFinish)),
                TimeUnit.MILLISECONDS.toSeconds(timeToFinish) -
                        TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(timeToFinish)));
    }

    private synchronized void increaseCurrentCommit() {
        this.currentCommit++;
    }

    private synchronized void increaseTotalRuntime(long finishTime) {
        this.totalRunTimeSoFar = finishTime - startTime;
    }

    public synchronized boolean isDone() {
        return analyzedCommits.size() == totalCommits;
    }

    public List<DependencyAnalyzer> getAnalyzedCommits() throws SyncFailedException {
        if (isDone())
            return analyzedCommits;
        else
            throw new SyncFailedException("Commits are not analyzed yet.");
    }


}