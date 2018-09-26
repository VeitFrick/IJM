package runner;

import com.github.gumtreediff.gen.jdt.AbstractJdtTreeGenerator;
import com.github.gumtreediff.gen.jdt.JdtTreeGenerator;
import com.github.gumtreediff.matchers.CompositeMatchers;
import com.github.gumtreediff.matchers.Matcher;
import com.github.gumtreediff.matchers.OptimizedVersions;
import differ.AbstractJavaChangeDiffer;
import differ.NonClassifyingDiffer;
import matchers.JavaMatchers;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import runner.git.DiffFilter;
import runner.git.RepositoryAnalyzer;
import runner.io.ChangeWriter;
import runner.io.fs.MetricsWriter;
import runner.util.DifferFactory;

import java.io.File;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Observable;
import java.util.Observer;

/**
 * Created by Veit in 2018
 */
public class BadPractiseOneClickRunner implements Observer {
    private static final Logger logger = LogManager.getLogger(BadPractiseOneClickRunner.class);
    private static int timeOut = 180;
    private static int numThreads = 1;
    public static ArrayList<String> repoList = new ArrayList<>();

    public static void main(String[] args) {

        repoList.add("pathtorepo");

        for (String repo: repoList) {
            analyzeRepo(repo, JavaMatchers.IterativeJavaMatcher_V2.class);
            System.out.println(" " +repo + " IJM done");
        }


    }


    public static void analyzeRepo(String repo, Class<? extends Matcher> matcher){
            Class<? extends AbstractJavaChangeDiffer> differType = NonClassifyingDiffer.class;
            AbstractJdtTreeGenerator generator = new JdtTreeGenerator();
            File projectDir = new File(repo);
            String projectName = projectDir.getName();
            ChangeWriter changeWriter = createWirter(matcher.getSimpleName(), projectName);

            RepositoryAnalyzer analyzer = new RepositoryAnalyzer(
                    repo,
                    changeWriter,
                    new DifferFactory(differType, matcher, generator),
                    numThreads,
                    timeOut);

            new BadPractiseOneClickRunner().analyzeRepository(analyzer, null);
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


    private static ChangeWriter createWirter(String matcherName, String projectName) {
                String path = "./output";
                if (!new File(path).exists())
                    new File(path).mkdir();

                String fileName = String.format("%s_%s_%d", matcherName, projectName, System.currentTimeMillis());

                try {
                    return new MetricsWriter(path, fileName);
                } catch (IOException e) {
                    logger.error("Could not create metrics writer", e);
                }
                    return null;
    }
}
