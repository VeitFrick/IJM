package runner.git;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by thomas on 21.03.2017.
 */
public class DiffFilter {
    private static final Logger logger = LogManager.getLogger(DiffFilter.class);

    private HashMap<String, List<DiffFilterEntry>> filterEntries = new HashMap<>();

    private DiffFilter() {
    }

    public boolean containsCommit(String commit) {
        return getFilterEntries(commit).size() != 0;
    }

    public boolean containsDiff(String commit, String srcFile, String dstFile) {
        String finalSrcFile = srcFile == null ? "" : srcFile;
        String finalDstFile = dstFile == null ? "" : dstFile;

        return getFilterEntries(commit)
                .stream()
                .filter(x ->
                        x.getSrcFile().equals(finalSrcFile)
                                && x.getDstFile().equals(finalDstFile))
                .count() > 0;
    }

    private List<DiffFilterEntry> getFilterEntries(String commit) {
        return filterEntries.getOrDefault(commit, new ArrayList<>());
    }

    public static DiffFilter create(String filterFilePath) {
        DiffFilter retVal = new DiffFilter();

        try {
            List<String> lines = Files.readAllLines(Paths.get(filterFilePath));
            for(String line : lines) {
                String[] lineParts = line.split(";");
                String commit = lineParts[0];
                String srcFile = lineParts[1];
                String dstFile = lineParts.length == 3 ? lineParts[2] : "";

                List<DiffFilterEntry> entries = retVal.getFilterEntries(commit);
                entries.add(new DiffFilterEntry(commit, srcFile, dstFile));

                if (!retVal.containsCommit(lineParts[0]))
                    retVal.filterEntries.put(commit, entries);

            }

        } catch (IOException e) {
            e.printStackTrace();
            logger.error("Could not load filter File", e);
        }

        return retVal;
    }

}
