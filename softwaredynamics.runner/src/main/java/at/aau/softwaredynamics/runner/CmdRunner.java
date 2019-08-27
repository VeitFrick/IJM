package at.aau.softwaredynamics.runner;

import at.aau.softwaredynamics.classifier.AbstractJavaChangeClassifier;
import at.aau.softwaredynamics.classifier.JChangeClassifier;
import at.aau.softwaredynamics.classifier.NonClassifyingClassifier;
import at.aau.softwaredynamics.gen.DocIgnoringTreeGenerator;
import at.aau.softwaredynamics.gen.OptimizedJdtTreeGenerator;
import at.aau.softwaredynamics.gen.SpoonTreeGenerator;
import at.aau.softwaredynamics.matchers.JavaMatchers;
import at.aau.softwaredynamics.runner.git.DiffFilter;
import at.aau.softwaredynamics.runner.git.FilePairAnalyzer;
import at.aau.softwaredynamics.runner.git.RepositoryAnalyzer;
import at.aau.softwaredynamics.runner.io.ChangeWriter;
import at.aau.softwaredynamics.runner.io.NullChangeWriter;
import at.aau.softwaredynamics.runner.io.db.HibernateChangeWriter;
import at.aau.softwaredynamics.runner.io.fs.FileChangeSpoonWriter;
import at.aau.softwaredynamics.runner.io.fs.FileChangeWriter;
import at.aau.softwaredynamics.runner.util.ClassifierFactory;
import at.aau.softwaredynamics.runner.util.ConfigHelper;
import com.github.gumtreediff.gen.TreeGenerator;
import com.github.gumtreediff.gen.jdt.JdtTreeGenerator;
import com.github.gumtreediff.matchers.CompositeMatchers;
import com.github.gumtreediff.matchers.Matcher;
import com.github.gumtreediff.matchers.OptimizedVersions;
import org.apache.commons.cli.*;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.File;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Observable;
import java.util.Observer;

/**
 * Created by thomas on 09.02.2017.
 */
public class CmdRunner implements Observer {
    private static final Logger logger = LogManager.getLogger(CmdRunner.class);

    private static ConfigHelper configHelper = new ConfigHelper();

    public static void main(String[] args) {
        CommandLineParser parser = new DefaultParser();
        Options options = createOptions();

        try {
            // parse the command line arguments
            CommandLine line = parser.parse( options, args );

            if (line.hasOption("help")) {
                HelpFormatter formatter = new HelpFormatter();
                formatter.printHelp( "analyzer", options );
            } else if (line.hasOption("c")
                    && line.hasOption("m")
                    && line.hasOption("w")) {

                String repo = line.getOptionValue("r");
                Class<? extends AbstractJavaChangeClassifier> classifierType = getClassifierType(line.getOptionValue("c"));
                Class<? extends Matcher> matcher = getMatcherTypes(line.getOptionValue("m"));
                String writer = line.getOptionValue("w");
                TreeGenerator generator =
                        line.hasOption("g")
                                ? getTreeGenerator(line.getOptionValue("g"))
                                : getTreeGenerator("OPT");
                int numThreads =
                        line.hasOption("t")
                                ? Integer.valueOf(line.getOptionValue("t"))
                                : Runtime.getRuntime().availableProcessors();
                int timeOut =
                        line.hasOption("timeout")
                                ? Integer.valueOf(line.getOptionValue("timeout"))
                                : Integer.MAX_VALUE;

                DiffFilter filter = line.hasOption("f")
                        ? DiffFilter.create(line.getOptionValue("f"))
                        : null;

                boolean isDryRun = line.hasOption("dryRun");
                // Only spoon supports classification, so JChangeClassifier => SpoonOutput
                boolean isSpoon = classifierType.equals(JChangeClassifier.class);

                if (line.hasOption("repo")) {
                    File projectDir = new File(repo);
                    String projectName = projectDir.getName();

                    ChangeWriter changeWriter = createWriter(matcher.getSimpleName(), projectName, isDryRun, writer, isSpoon);

                    RepositoryAnalyzer analyzer = new RepositoryAnalyzer(
                            repo,
                            changeWriter,
                            new ClassifierFactory(classifierType, matcher, generator),
                            numThreads,
                            timeOut);
                    new CmdRunner().analyzeRepository(analyzer, filter);
                } else if (line.hasOption("src") && line.hasOption("dst")) {

                    String srcFilePath = line.getOptionValue("src");
                    String dstFilePath = line.getOptionValue("dst");
                    File srcFile = new File(srcFilePath);
                    File dstFile = new File(dstFilePath);

                    ChangeWriter changeWriter = createWriter(matcher.getSimpleName(), "FILE_DIFF_" + (dstFile.getName().replace('.', '-')), isDryRun, writer, isSpoon);

                    FilePairAnalyzer analyzer = new FilePairAnalyzer(
                            srcFile,
                            dstFile,
                            new ClassifierFactory(classifierType, matcher, generator),
                            timeOut,
                            changeWriter);
                    new CmdRunner().analyzeRepository(analyzer);
                } else {
                    System.out.println("repo or src/dst pair missing!");
                }
            } else {
                System.out.println("Options c,w or m missing! See help for more details.");
            }
        }
        catch( ParseException exp ) {
            System.out.println( "Unexpected exception:" + exp.getMessage() );
        }
    }

    @Override
    public void update(Observable o, Object arg) {
        RepositoryAnalyzer analyzer = (RepositoryAnalyzer) o;
        System.out.printf("\rProgress (%s): %d / %d",
                LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_TIME),
                analyzer.getCurrentCommit(),
                analyzer.getTotalCommits()
        );
    }

    private void analyzeRepository(RepositoryAnalyzer analyzer, DiffFilter filter) {
        analyzer.setFilter(filter);
        analyzer.addObserver(this);
        analyzer.analyzeRepository();
    }

    private void analyzeRepository(FilePairAnalyzer analyzer) {
        analyzer.run();
    }

    private static Class<? extends Matcher> getMatcherTypes(String option) {
        switch(option) {
            case "GT":
                return CompositeMatchers.ClassicGumtree.class;
            case "IJM":
                return JavaMatchers.IterativeJavaMatcher_V2.class;
            case "IJM_Spoon":
                return JavaMatchers.IterativeJavaMatcher_Spoon.class;
            case "MTDIFF":
                return OptimizedVersions.MtDiff.class;
        }

        return null;
    }

    private static Class<? extends AbstractJavaChangeClassifier> getClassifierType(String option) {
        switch (option)
        {
            case "Java": return JChangeClassifier.class;
            case "None": return NonClassifyingClassifier.class;
            default:
                return JChangeClassifier.class;
        }
    }

    private static TreeGenerator getTreeGenerator(String option) {
        switch (option)
        {
            case "OTG": return new OptimizedJdtTreeGenerator();
            case "JTG": return new JdtTreeGenerator();
            case "JTG1": return new DocIgnoringTreeGenerator();
            case "SPOON":
                return new SpoonTreeGenerator();
        }
        return null;
    }

    private static ChangeWriter createWriter(String matcherName, String projectName, boolean isDryrun, String writer, boolean isSpoon) {
        if (isDryrun)
            return new NullChangeWriter();

        switch (writer) {
            case "DB":
                String dbDriver = configHelper.getString("dbDriver");
                String dbUrl = configHelper.getString("dbUrl");
                String dbUser = configHelper.getString("dbUser");
                String dbPass = configHelper.getString("dbPass");

                return new HibernateChangeWriter(matcherName, projectName, dbDriver, dbUrl, dbUser, dbPass);
            case "FS":
                String path = "./output";
                if (!new File(path).exists())
                    new File(path).mkdir();

                String fileName = String.format("%s_%s_%d", matcherName, projectName, System.currentTimeMillis());

                try {
                    if (isSpoon) return new FileChangeSpoonWriter(path, fileName);
                    else return new FileChangeWriter(path, fileName);
                } catch (IOException e) {
                    logger.error("Could not create metrics writer", e);
                }
            default:
                    return null;
        }
    }

    private static Options createOptions() {
        Options options = new Options();
        options.addOption(Option.builder("r").hasArg().longOpt("repo").desc("the path to the repository").build());
        options.addOption(Option.builder("src").hasArg().longOpt("srcfile").desc("(if no repo option) the path to the source file").build());
        options.addOption(Option.builder("dst").hasArg().longOpt("dstfile").desc("(if no repo option) the path to the destination file").build());
        options.addOption(Option.builder("c").hasArg().longOpt("classifier").desc("classifier to be used ( Java | None )").build());
        options.addOption(Option.builder("m").hasArg().longOpt("matcher").desc("matcher to be used ( GT | IJM | MTDIFF )").build());
        options.addOption(Option.builder("w").hasArg().longOpt("writer").desc("writer to be used (DB | FS)").build());
        options.addOption(Option.builder("g").hasArg().longOpt("generator").desc("generator to be used ( OTG | JTG | JTG1 ) ").build());
        options.addOption(Option.builder("t").hasArg().longOpt("threads").desc("number of threads to be used").build());
        options.addOption(Option.builder("timeout").hasArg().desc("timeout for processing diffs in seconds").build());
        options.addOption(Option.builder("f").hasArg().longOpt("filter").desc("filter file").build());
        options.addOption(Option.builder("dryRun").desc("extracted changes are not written to the writer").build());
        options.addOption(Option.builder("help").desc("print help message").build());
        return options;
    }
}
