package at.aau.softwaredynamics.diffws;

import at.aau.softwaredynamics.classifier.AbstractJavaChangeClassifier;
import at.aau.softwaredynamics.classifier.JChangeClassifier;
import at.aau.softwaredynamics.classifier.NonClassifyingClassifier;
import at.aau.softwaredynamics.diffws.service.CommitMsgGenService;
import at.aau.softwaredynamics.diffws.util.ChangePair;
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
import org.apache.commons.io.FileUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Observable;
import java.util.Observer;


public class CommitMSGrunner implements Observer {

    public static void main(String[] args) {
        CommandLineParser parser = new DefaultParser();
        Options options = createOptions();

        try {
            // parse the command line arguments
            CommandLine line = parser.parse( options, args );

            if (line.hasOption("help")) {
                HelpFormatter formatter = new HelpFormatter();
                formatter.printHelp( "analyzer", options );
            } else {

                if (line.hasOption("src") && line.hasOption("dst")) {
                    int depth = 4;
                    if(line.hasOption("d")) {
                        depth = Integer.parseInt(line.getOptionValue("d"));
                    }
                    String srcFilePath = line.getOptionValue("src");
                    String dstFilePath = line.getOptionValue("dst");
                    File srcFile = new File(srcFilePath);
                    File dstFile = new File(dstFilePath);
                    String src = FileUtils.readFileToString(srcFile, StandardCharsets.UTF_8);
                    String dst = FileUtils.readFileToString(dstFile, StandardCharsets.UTF_8);
                    System.out.println(commitMsgGen(dstFile.getName(), src, dst,depth));

                } else {
                    System.out.println("repo or src/dst pair missing!");
                }
            }
        }
        catch( ParseException exp ) {
            System.out.println( "Unexpected exception:" + exp.getMessage() );
        } catch (IOException e) {
            e.printStackTrace();
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


    private static String commitMsgGen(String fn, String src, String dst, int depth){
        JChangeClassifier classifier = new JChangeClassifier(false, JavaMatchers.IterativeJavaMatcher_Spoon.class, new SpoonTreeGenerator());
        classifier.setIncludeMetaChanges(true);
        CommitMsgGenService service = new CommitMsgGenService(classifier);

            service.addChangePair(new ChangePair(
                    fn,
                    src,
                    dst));

        try {
            return service.generateMessage(depth);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "Error";
    }


    private static Options createOptions() {
        Options options = new Options();
        options.addOption(Option.builder("src").hasArg().longOpt("srcfile").desc("(if no repo option) the path to the source file").build());
        options.addOption(Option.builder("dst").hasArg().longOpt("dstfile").desc("(if no repo option) the path to the destination file").build());
        options.addOption(Option.builder("d").hasArg().longOpt("depth").desc("How deep should the tree be?").build());
        options.addOption(Option.builder("help").desc("print help message").build());
        return options;
    }
}
