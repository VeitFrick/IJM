package at.aau.softwaredynamics.runner.git;

import at.aau.softwaredynamics.classifier.JChangeClassifier;
import at.aau.softwaredynamics.dependency.Commit;
import at.aau.softwaredynamics.dependency.CommitPair;
import at.aau.softwaredynamics.dependency.DependencyChanges;
import at.aau.softwaredynamics.dependency.DependencyExtractor;
import at.aau.softwaredynamics.gen.SpoonTreeGenerator;
import at.aau.softwaredynamics.matchers.JavaMatchers;
import at.aau.softwaredynamics.runner.output.AggregatedDBOutput;
import at.aau.softwaredynamics.runner.output.DBOutput;
import at.aau.softwaredynamics.runner.output.FileOutput;
import at.aau.softwaredynamics.runner.output.OutputWriter;
import at.aau.softwaredynamics.runner.util.VirtualSpoonPom;
import org.apache.commons.cli.*;
import org.apache.commons.io.filefilter.FileFilterUtils;
import org.codehaus.plexus.util.xml.pull.XmlPullParserException;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.lib.ObjectId;
import org.eclipse.jgit.lib.Ref;
import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.revwalk.RevCommit;
import org.eclipse.jgit.revwalk.RevSort;
import org.eclipse.jgit.revwalk.RevWalk;
import spoon.support.compiler.VirtualFile;

import java.io.File;
import java.io.FileFilter;
import java.io.FilenameFilter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.sql.SQLException;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import static at.aau.softwaredynamics.runner.git.DepCoordinator.sanitizeFilename;


/**
 * Analyzes all files from a single commit from a repository
 * and extracts its source code changes/dependencies or dependency changes using a JChangeClassifier instance.
 */
public class FullProjectAnalyzer {

    private final String projectName;
    private String treeName;
    private OutputWriter outputWriter;
    private SnapshotGenerator snapshotGenerator;
    private Repository repository;
    private Map<RevCommit, Map<String, VirtualSpoonPom>> pomMap = new HashMap<>();
    private Set<RevCommit> commits = new HashSet<>();

    private static DateTimeFormatter timeColonFormatter = DateTimeFormatter.ofPattern("HH:mm:ss SSS");
    private int numOfThreads = 4;

    private String subPath;

    private List<CommitPair> workList;
    private boolean aggregatedMode; // if set, only aggregated data will be saved to outputWriter
    private boolean sourceCodeChangeMode; // if set, only source code changes will be diffed, and NO dependencies
    Set<String> allModulePaths;
    String singleCommit; // TODO if set only this commit and its parent are considered
    static boolean verbose = false; // verbose output

    public static void main(String[] args) {
        CommandLineParser parser = new DefaultParser();
        Options options = createOptions();

        try {
            // parse the command line arguments
            CommandLine line = parser.parse(options, args);

            if (line.hasOption("help")) {
                HelpFormatter formatter = new HelpFormatter();
                formatter.printHelp("FullProjectAnalyzer", options);
            } else if (line.hasOption("p")) {

                logToConsole("FullProjectDependencyRunner started with following arguments:", "main", "INFO");

                for (Option option : line.getOptions()) {
                    logToConsole(option.getLongOpt() + (option.getValue() == null ? " is ON" : " = " + option.getValue()), "main", "INFO");
                }

                // set log level
                FullProjectAnalyzer.setVerbose(line.hasOption("verbose"));

                OutputWriter writer;
                if (line.hasOption("source") && line.hasOption("destination")) {
                    // TODO?
                    System.err.println("Not implemented");
                    return;
                }
                if (line.hasOption("output")) {
                    FileOutput fileOutput = new FileOutput(line.getOptionValue("output"), "csv");
                    writer = fileOutput;
                } else {
                    DBOutput dbOutput;
                    if (!line.hasOption("conn")) {
                        System.err.println("Database Connection Url missing!");
                        return;
                    }
                    if (!line.hasOption("usr")) {
                        System.err.println("Database User missing!");
                        return;
                    }
                    if (!line.hasOption("pw")) {
                        System.err.println("Database Password missing!");
                        return;
                    }

                    if (line.hasOption("agg")) {
                        dbOutput = new AggregatedDBOutput(line.getOptionValue("conn"),line.getOptionValue("usr"),line.getOptionValue("pw"));
                    } else {
                        dbOutput = new DBOutput(line.getOptionValue("conn"),line.getOptionValue("usr"),line.getOptionValue("pw"));
                    }
                    logToConsole("Current db schema name is: " + dbOutput.schemaname, "main", "INFO");
                    writer = dbOutput;
                }

                String path = line.getOptionValue("p");
                Boolean isRootMaven = line.hasOption("mvn");

                File[] dirs;
                if (line.hasOption("sub")) {
                    dirs = new File(path).listFiles((FileFilter) FileFilterUtils.directoryFileFilter());
                } else {
                    dirs = new File[1];
                    dirs[0] = new File(path);
                    if (!dirs[0].isDirectory()) throw new IllegalArgumentException("Path is not a directory!");
                }

                if (dirs != null) {
                    for (File dir : dirs) {
                        try {
                            FullProjectAnalyzer fullProjectAnalyzer = null;

                            if (line.hasOption("srcf") && line.hasOption("dstf")) {
                                logToConsole(("Working on " + dir.getAbsolutePath() + " with current src/dst files: " + line.getOptionValue("srcf") + " - " + line.getOptionValue("dstf")), "", "RUNNER");
                                logToConsole("THIS IS DEPRECATED AND WILL IGNORE MOST OF YOUR INPUT FLAGS, USE AT OWN RISK", "main", "WARNING");
                                //UNTESTED CODE BELOW
//                                fullProjectAnalyzer = new FullProjectAnalyzer(
//                                        dir.getAbsolutePath(),
//                                        "refs/heads/master",
//                                        isRootMaven);
//                                fullProjectAnalyzer.setAggregatedMode(line.hasOption("agg"));

                                String srcTxt = new String(Files.readAllBytes(Paths.get(dir.getAbsolutePath(), line.getOptionValue("srcf"))));
                                String dstTxt = new String(Files.readAllBytes(Paths.get(dir.getAbsolutePath(), line.getOptionValue("dstf"))));

                                JChangeClassifier classifier = new JChangeClassifier(false, JavaMatchers.IterativeJavaMatcher_Spoon.class, new SpoonTreeGenerator());
                                classifier.classify(srcTxt,dstTxt,false);
                                DependencyExtractor dependencyExtractor = new DependencyExtractor(classifier.getMappings(),classifier.getActions(),classifier.getSrcContext().getRoot(),classifier.getDstContext().getRoot(),srcTxt,dstTxt);
                                dependencyExtractor.extractDependencies();
                                DependencyChanges dependencyChanges = dependencyExtractor.getDependencyChanges();
                                System.out.println(dependencyChanges.getDependencyChangeOverview(classifier.getCodeChanges(),";"));
//                                fullProjectAnalyzer.writeDepDiff(Collections.singletonList(dependencyChanges), null, null);

                            } else {
                                // else do a module
                                String subPath = "";

                                if (line.hasOption("module")) {
                                    subPath = line.getOptionValue("module");
                                }

                                fullProjectAnalyzer = new FullProjectAnalyzer(
                                        dir.getAbsolutePath(),
                                        subPath,
                                        "refs/heads/master",
                                        true);
                                fullProjectAnalyzer.setAggregatedMode(line.hasOption("agg"));
                                fullProjectAnalyzer.setSourceCodeChangeMode(!line.hasOption("dep"));

                                Map<RevCommit, Map<String, VirtualSpoonPom>> pomMap = fullProjectAnalyzer.getPomMap();
                                Set<String> allModulePaths = fullProjectAnalyzer.getAllModulePaths();

                                if (!line.hasOption("module")) {
                                    // go through all modules if none is selected
                                    logToConsole("Going through all modules " + allModulePaths, "main", "INFO");
                                    for (String modulePath : allModulePaths) {
                                        // if we got defined submodules, we assume the top module is not interesting
                                        // this is because of performance, could obviously be a mistake
                                        if (allModulePaths.size() > 1 && modulePath.isEmpty()) continue;
                                        FullProjectAnalyzer moduleProjectAnalyzer = new FullProjectAnalyzer(
                                                dir.getAbsolutePath(),
                                                modulePath,
                                                "refs/heads/master",
                                                false);
                                        moduleProjectAnalyzer.setPomMap(pomMap);
                                        moduleProjectAnalyzer.setAggregatedMode(line.hasOption("agg"));
                                        moduleProjectAnalyzer.setSourceCodeChangeMode(!line.hasOption("dep"));

                                        if (line.hasOption("threads")) {
                                            moduleProjectAnalyzer.setNumOfThreads(Integer.parseInt(line.getOptionValue("threads")));
                                        }
                                        moduleProjectAnalyzer.setOutputWriter(writer);

                                        String progressFileName = null;
                                        try {
                                            progressFileName = chooseProgressFileBasedOnOptions(moduleProjectAnalyzer,
                                                    line.hasOption("autoprogress"),
                                                    line.getOptionValue("progress"));
                                        } catch (Exception e) {
                                            logToConsole("Could not choose progress file for " + moduleProjectAnalyzer.projectName + "due to an error: " + Arrays.toString(e.getStackTrace()), "main", "ERROR");
                                        }

                                        moduleProjectAnalyzer.analyzeLocalRepo(progressFileName);
                                    }
                                } else {
                                    if (line.hasOption("threads")) {
                                        fullProjectAnalyzer.setNumOfThreads(Integer.parseInt(line.getOptionValue("threads")));
                                    }
                                    fullProjectAnalyzer.setOutputWriter(writer);
                                    String progressFileName = null;
                                    try {
                                        progressFileName = chooseProgressFileBasedOnOptions(fullProjectAnalyzer,
                                                line.hasOption("autoprogress"),
                                                line.getOptionValue("progress"));
                                    } catch (Exception e) {
                                        logToConsole("Could not choose progress file due to an error: " + Arrays.toString(e.getStackTrace()), "main", "ERROR");
                                    }

                                    fullProjectAnalyzer.analyzeLocalRepo(progressFileName);
                                }

                                return;

                            }
                        } catch (Exception e) {
                            logToConsole(("Exception thrown when working on: " + dir.getAbsolutePath()), "", "ERROR");
                            e.printStackTrace();
                        }
                    }
                } else {
                    logToConsole("FATAL - No folder(s) found.", "main", "ERROR");
                }


            } else {
                logToConsole("Path missing! See help for more details.", "main", "ERROR");
            }
        } catch (ParseException exp) {
            logToConsole("Unexpected exception:" + exp.getMessage(), "main", "ERROR");
        }
    }

    /**
     * Choses a progress file based on a set of options
     *
     * @param analyzer   the analyzer to be used (only needed for autodetect)
     * @param autodetect if the most recent fitting file should be automatically loaded
     * @param manualPath (overrides autodetect) the path to the progress file to be used
     * @return a path to a progress file, null if none should be used
     */
    private static String chooseProgressFileBasedOnOptions(FullProjectAnalyzer analyzer, boolean autodetect, String manualPath) {
        if (manualPath != null) {
            logToConsole("Manual progress file will be used: " + manualPath, "progressFile", "INFO");
            return manualPath; // always prefer manual path
        } else if (autodetect) {
            String progressFileName = null;
            logToConsole("Progress file will be automatically guessed and loaded.", "progressFile", "INFO");
            String progressFilePrefix = sanitizeFilename(analyzer.getProjectName()) + "_" + sanitizeFilename(analyzer.getSubPath() + "_progress_");
            File[] foundFiles = new File(".").listFiles(new FilenameFilter() {
                public boolean accept(File dir, String name) {
                    return name.startsWith(progressFilePrefix);
                }
            });

            File toLoad = null;
            Long maxTimestamp = 0L;
            for (File foundFile : foundFiles) {
                String tempTime = foundFile.getName().substring(foundFile.getName().lastIndexOf("_") + 1, foundFile.getName().length() - 4);
                // - 4 removes the .txt
                Long timestamp = Long.parseLong(tempTime);
                if (timestamp > maxTimestamp) {
                    maxTimestamp = timestamp;
                    toLoad = foundFile;
                }
            }
            if (toLoad != null) {
                progressFileName = toLoad.getAbsolutePath();
                logToConsole("Automatically found most recent progress file: " + progressFileName, "progressFile", "INFO");
            } else {
                logToConsole("Found no progress file with prefix: " + progressFilePrefix, "progressFile", "WARNING");
            }
            return progressFileName;
        } else {
            logToConsole("No progress file will be used.", "progressFile", "INFO");
            return null;
        }
    }

    private static Options createOptions() {
        Options options = new Options();
        options.addOption(Option.builder("p").hasArg().longOpt("path").desc("The path of the project").build());
        options.addOption(Option.builder("prog").hasArg().longOpt("progress").desc("The path to the previous progress file (for continuing work from crash or similar)").build());
        options.addOption(Option.builder("autoprog").longOpt("autoprogress").desc("Enables searching for the most recent progress file in this directory and using it for every module").build());
        options.addOption(Option.builder("src").hasArg().longOpt("source").desc("The source commit to be diffed (omit to diff all commits)").build());
        options.addOption(Option.builder("dst").hasArg().longOpt("destination").desc("The destination commit to be diffed, must be child of source commit (omit to diff all commits)").build());
        options.addOption(Option.builder("srcf").hasArg().longOpt("srcfile").desc("DEPRECATED! Source file relative path (Overrides git building)").build());
        options.addOption(Option.builder("dstf").hasArg().longOpt("dstfile").desc("DEPRECATED! Destination file relative path (Overrides git building)").build());
        options.addOption(Option.builder("out").hasArg().longOpt("output").desc("Path to the file for file based output (will override db!)").build());

        //Database Options:
        options.addOption(Option.builder("conn").hasArg().longOpt("connection").desc("DB connection string eg. jdbc:postgresql://localhost:5432/postgres").build());
        options.addOption(Option.builder("usr").hasArg().longOpt("dbuser").desc("DB Username").build());
        options.addOption(Option.builder("pw").hasArg().longOpt("dbpassword").desc("DB Password").build());

        options.addOption(Option.builder("sub").longOpt("submode").desc("Run on all sub-folders of given path instead").build());
        options.addOption(Option.builder("mvn").longOpt("maven").desc("Set this to be the root of a maven project").build());
        options.addOption(Option.builder("agg").longOpt("aggregate").desc("Only write aggregated output to writer").build());
        options.addOption(Option.builder("dep").longOpt("dependencies").desc("Write dependencies to output instead of source code changes").build());
        options.addOption(Option.builder("mod").hasArg().longOpt("module").desc("Sets module of maven project that should be built").build());
        options.addOption(Option.builder("t").hasArg().longOpt("threads").desc("Sets number of threads").build());
        options.addOption(Option.builder("v").hasArg().longOpt("verbose").desc("Enables verbose logging").build());
        options.addOption(Option.builder("help").desc("print help message").build());
        return options;
    }

    public FullProjectAnalyzer(String repoPath, String treeName, boolean isRootMavenProject) throws IOException, GitAPIException {
        this(repoPath, "", treeName, null, isRootMavenProject);
    }

    public FullProjectAnalyzer(String repoPath, String subPath, String treeName, boolean isRootMavenProject) throws IOException, GitAPIException {
        this(repoPath, subPath, treeName, null, isRootMavenProject);
    }

    /**
     * @param repoPath The path to the repository (*containing* a .git folder)
     * @param subPath  Relative path to the actual Java sources, default empty
     * @param treeName Name of the branch that should be analysed
     * @throws IOException
     * @throws GitAPIException
     */
    public FullProjectAnalyzer(String repoPath, String subPath, String treeName, String singleCommit, boolean isRootMavenProject) throws IOException, GitAPIException {
        this.subPath = subPath;
        this.treeName = treeName;
        this.workList = new ArrayList<>();
        Map<ObjectId, AtomicInteger> commitUsages = new HashMap<>();

        Git git = Git.open(new File(repoPath + File.separatorChar + ".git"));
        repository = git.getRepository();

        this.projectName = repository.getWorkTree().getName();

        try (RevWalk revWalk = new RevWalk(repository)) {
            ObjectId commitId = repository.resolve(treeName);
            revWalk.markStart(revWalk.parseCommit(commitId));
            revWalk.sort(RevSort.TOPO);

            for (RevCommit commit : revWalk) {
                // TODO diff all parents? currently we only diff commits with 1 parent
                if (commit.getParentCount() == 1 && (singleCommit == null || commit.getName().equals(singleCommit))) {
                    workList.add(new CommitPair(commit.getParent(0), commit, subPath, repository.getWorkTree().getName()));
                    commits.add(commit);
                    commits.add(commit.getParent(0));
                    // add commit and it's parent to usage map
                    commitUsages.putIfAbsent(commit.getId(), new AtomicInteger(0));
                    commitUsages.get(commit.getId()).incrementAndGet();
                    commitUsages.putIfAbsent(commit.getParent(0).getId(), new AtomicInteger(0));
                    commitUsages.get(commit.getParent(0).getId()).incrementAndGet();
                }
            }
            logToConsole("Worklist generated for repo '" + repoPath + "' with module: '" + subPath + "'", "main", "INFO");
        }

        snapshotGenerator = new SnapshotGenerator(repository, treeName, subPath, commitUsages);
        if (isRootMavenProject) pomFileAnalysis();
    }

    /**
     * Analyzes all pom files of every commit and generates
     * a pomMap (Directory &gt; pomFile) and a list of all modules ie. Directories where
     * pom Files exist
     * <p>
     * This is very expensive to run, save this for submodules and apply with setter
     */
    public void pomFileAnalysis() throws IOException, GitAPIException {
        allModulePaths = this.generateModulePaths(true);// fills the pomMap
        logToConsole("All virtual POM files generated for repo '" + repository.getDirectory() + "' with module: '" + subPath + "'", "main", "INFO");

        snapshotGenerator.setPomMap(pomMap);
    }

    public void setOutputWriter(OutputWriter outputWriter) {
        this.outputWriter = outputWriter;
    }

    public void analyzeLocalRepo() {
        analyzeLocalRepo(null);
    }

    public void analyzeLocalRepo(String progressFilePath) {
        if (isAggregatedMode()) {
            analyzeLocalRepoAndAggregate(progressFilePath);
            return;
        }

        if (isSourceCodeChangeMode()) {
            analyzeLocalRepoChanges(progressFilePath);
            return;
        }

        long t = System.currentTimeMillis();

        if(getOutputWriter() instanceof DBOutput){
            if(sourceCodeChangeMode){
                ((DBOutput)getOutputWriter()).writeProjectInformation(repository.getWorkTree().getName(), subPath, System.currentTimeMillis(),"Only IJM_Spoon supported right now!");

            }
            else{
                ((DBOutput)getOutputWriter()).writeProjectInformation(repository.getWorkTree().getName(), subPath, System.currentTimeMillis());

            }
        }

        DiffCoordinator coordinator = createAndStartNewDiffCoordinator(progressFilePath);

        ExecutorService executor = Executors.newFixedThreadPool(numOfThreads);
        for (int i = 0; i < numOfThreads; i++) {
            try {
                executor.execute(new DepDiffWorker(coordinator, getOutputWriter()));
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        executor.shutdown();
        try {
            executor.awaitTermination(7, TimeUnit.DAYS);
            logToConsole(("All depdiffs done - building and diffing took " + (System.currentTimeMillis() - t) + "ms"), "", "DIFF");

        } catch (InterruptedException e) {
            e.printStackTrace();
        }

    }

    /**
     * Extracts the dependencies for all commits,
     * aggregates them per TYPE and writes them into the output writer
     *
     * @param progressFilePath existing commits progress file to skip
     */
    public void analyzeLocalRepoAndAggregate(String progressFilePath) {

        long t = System.currentTimeMillis();

        if (getOutputWriter() instanceof DBOutput) {
            ((DBOutput) getOutputWriter()).writeProjectInformation(repository.getWorkTree().getName(), subPath, System.currentTimeMillis());
        }

        // Construct a new work list of single commits for dependecy extraction and aggregation
        List<Commit> commitWorkList = new ArrayList<>();
        for (RevCommit commit : commits) {
            commitWorkList.add(new Commit(commit, subPath, repository.getWorkTree().getName()));
        }

        DepCoordinator coordinator = new DepCoordinator(snapshotGenerator, commitWorkList, numOfThreads);
        if (progressFilePath != null) {
            try {
                coordinator.applyProgress(progressFilePath);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        // start coordintator service so workers can connect
        coordinator.startCoordinator();

        ExecutorService executor = Executors.newFixedThreadPool(numOfThreads);
        for (int i = 0; i < numOfThreads; i++) {
            try {
                executor.execute(new DepWorker(coordinator, getOutputWriter()));
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        executor.shutdown();
        try {
            executor.awaitTermination(7, TimeUnit.DAYS);
            logToConsole(("All dependency extractions done - building and extracting took " + (System.currentTimeMillis() - t) + "ms"), "", "DEP");

        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    /**
     * Extracts the source code changes for all commits
     *
     * @param progressFilePath existing commits progress file to skip
     */
    public void analyzeLocalRepoChanges(String progressFilePath) {

        long t = System.currentTimeMillis();

        if (getOutputWriter() instanceof DBOutput) {
            ((DBOutput) getOutputWriter()).writeProjectInformation(repository.getWorkTree().getName(), subPath, System.currentTimeMillis());
        }

        DiffCoordinator coordinator = createAndStartNewDiffCoordinator(progressFilePath);

        ExecutorService executor = Executors.newFixedThreadPool(numOfThreads);
        for (int i = 0; i < numOfThreads; i++) {
            try {
                executor.execute(new DiffWorker(coordinator, getOutputWriter()));
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        executor.shutdown();
        try {
            executor.awaitTermination(7, TimeUnit.DAYS);
            logToConsole(("All diffs done - building and diffing took " + (System.currentTimeMillis() - t) + "ms"), "", "DIFF");

        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }


    private DiffCoordinator createAndStartNewDiffCoordinator(String progressFilePath) {
        DiffCoordinator coordinator =new DiffCoordinator(snapshotGenerator, workList, numOfThreads);
        if (progressFilePath != null) {
            try {
                coordinator.applyProgress(progressFilePath);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        // start coordintator service so workers can connect
        coordinator.startCoordinator();
        return coordinator;
    }

    /**
     * Writes the Changes to the given output Writer
     * @param changes - Changes to be written as list
     * @param srcCommit
     * @param dstCommit
     */
    private void writeDepDiff(List<DependencyChanges> changes, RevCommit srcCommit, RevCommit dstCommit) {
        if (outputWriter != null) {
            try {
                outputWriter.writeDependencyInformation(changes, srcCommit, dstCommit, subPath, repository.getWorkTree().getName(), System.currentTimeMillis());
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    public Set<RevCommit> getCommits() {
        return commits;
    }

    private VirtualSpoonPom getPOM(String path, RevCommit commit) throws IOException, XmlPullParserException {
        if (path.equals("/")) path = "";
        List<String> pomPath = Arrays.asList(Paths.get(path, "pom.xml").toString());
        Optional<VirtualFile> firstPom = snapshotGenerator.getSpoonResourcesForCommit(commit, pomPath, "pom.xml").stream().findFirst();
        if (firstPom.isPresent()) {
            VirtualFile virtualFile = firstPom.get();
            VirtualSpoonPom virtualSpoonPom = new VirtualSpoonPom(virtualFile);
            if (virtualSpoonPom.getModel() != null) {
                for (String module : virtualSpoonPom.getModel().getModules()) {
                    for (VirtualFile pomModule : snapshotGenerator.getSpoonResourcesForCommit(commit, Arrays.asList(module), "pom.xml")) {
                        virtualSpoonPom.addModule(new VirtualSpoonPom(pomModule, virtualSpoonPom));
                    }
                }
            }
            return virtualSpoonPom;
        } else {
            return null;
        }
    }


    private Set<String> generateModulePaths(boolean includeHistory) throws IOException, GitAPIException {
        Set<String> modulePaths = new HashSet<>();
        Git git = Git.open(new File(String.valueOf(Paths.get(repository.getDirectory().getAbsolutePath()))));
        Iterable<RevCommit> commits;
        if (includeHistory) {
            commits = git.log().add(repository.resolve(treeName)).call();
        } else {
            List<RevCommit> list = new ArrayList<>();
            Ref head = repository.exactRef("refs/heads/master");
            logToConsole("Ignoring history, found head commit " + head, "main", "INFO");

            // a RevWalk allows to walk over commits based on some filtering that is defined
            try (RevWalk walk = new RevWalk(repository)) {
                RevCommit commit = walk.parseCommit(head.getObjectId());
                System.out.println("Found Commit: " + commit);
                list.add(commit);
                walk.dispose();
            }
            commits = list; // set iterable to the single master commit
        }
        int i = 0;
        // go through all commits of tree
        for (RevCommit commit : commits) {
            i++;
            Map<String, VirtualSpoonPom> pomFilesInCommitDirectoryMap = new HashMap<>();
            try {
                VirtualSpoonPom pom = getPOM("", commit);
                if (pom != null) {

                    modulePaths.add(pom.getDirectory());

                    for (VirtualSpoonPom module : pom.getModules()) {
                        modulePaths.add(module.getDirectory());
                        pomFilesInCommitDirectoryMap.put(module.getDirectory(), module);
                    }
                    pomFilesInCommitDirectoryMap.put(pom.getDirectory(), pom);
                }
            } catch (IOException | XmlPullParserException e) {
                logToConsole("POM File Error in commit " + commit.getName() + " in " + repository.getWorkTree().getName() + "\n" + e.getMessage(), "POM", "ERROR");

            }
            pomMap.put(commit, pomFilesInCommitDirectoryMap);
            logToConsole("(" + i + ") " + pomFilesInCommitDirectoryMap.size() + " POM files for " + commit.getName() + " are ready.", "POM", "VERBOSE");
            if (i % 100 == 0) logToConsole("" + i + "th POM file was analyzed.", "POM", "INFO");
        }

        if (modulePaths.isEmpty()) modulePaths.add(subPath); // add myself if no modules are defined
        return modulePaths;
    }

    public Set<String> getAllModulePaths() {
        return allModulePaths;
    }

    public void setAllModulePaths(Set<String> allModulePaths) {
        this.allModulePaths = allModulePaths;
    }

    public Map<RevCommit, Map<String, VirtualSpoonPom>> getPomMap() {
        return pomMap;
    }

    public void setPomMap(Map<RevCommit, Map<String, VirtualSpoonPom>> pomMap) {
        this.pomMap = pomMap;
        this.snapshotGenerator.setPomMap(pomMap);
    }

    public OutputWriter getOutputWriter() {
        return outputWriter;
    }

    public static void logToConsole(String message, String threadName, String type) {
        StringBuilder s = new StringBuilder();
        if (threadName == null || threadName.equals("")) {
            s.append("{" + Thread.currentThread().getName() + "}\t");
        } else {
            s.append("{").append(threadName).append("}\t");
        }
        s.append(" [" + ZonedDateTime.now().format(timeColonFormatter) + "]\t");
        if (!type.equals("")) {
            s.append(" [").append(type).append("]\t");
        }
        s.append(" " + message + "");
        if(type.toLowerCase().equals("error")) {
            System.err.println(s.toString());
        } else {
            if (verbose) {
                System.out.println(s.toString());
            } else {
                if (!type.toLowerCase().equals("verbose")) {
                    System.out.println(s.toString());
                }
            }
        }
    }

    public void setNumOfThreads(int numOfThreads) {
        this.numOfThreads = numOfThreads;
    }

    public boolean isAggregatedMode() {
        return aggregatedMode;
    }

    public void setAggregatedMode(boolean aggregatedMode) {
        this.aggregatedMode = aggregatedMode;
    }

    public boolean isSourceCodeChangeMode() {
        return sourceCodeChangeMode;
    }

    public void setSourceCodeChangeMode(boolean sourceCodeChangeMode) {
        this.sourceCodeChangeMode = sourceCodeChangeMode;
    }

    public String getProjectName() {
        return projectName;
    }

    public String getSubPath() {
        return subPath;
    }

    public static boolean isVerbose() {
        return verbose;
    }

    public static void setVerbose(boolean verbose) {
        FullProjectAnalyzer.verbose = verbose;
    }

    public String getProjectAndModuleString() {
        return " { name: " + getProjectName() + " module: " + getSubPath() + "}";
    }
}
