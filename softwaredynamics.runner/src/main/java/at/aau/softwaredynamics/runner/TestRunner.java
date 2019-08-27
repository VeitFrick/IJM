package at.aau.softwaredynamics.runner;

import at.aau.softwaredynamics.classifier.entities.NodeInfo;
import at.aau.softwaredynamics.classifier.entities.SourceCodeChange;
import at.aau.softwaredynamics.classifier.entities.SourceCodeChangeFactory;
import at.aau.softwaredynamics.classifier.util.LineNumberHelper;
import at.aau.softwaredynamics.gen.DocIgnoringTreeGenerator;
import at.aau.softwaredynamics.matchers.JavaMatchers;
import at.aau.softwaredynamics.runner.io.fs.FileHandler;
import com.github.gumtreediff.actions.ActionGenerator;
import com.github.gumtreediff.actions.model.Action;
import com.github.gumtreediff.gen.TreeGenerator;
import com.github.gumtreediff.matchers.Mapping;
import com.github.gumtreediff.matchers.MappingStore;
import com.github.gumtreediff.matchers.Matcher;
import com.github.gumtreediff.tree.ITree;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.List;

/**
 * Created by thomas on 23.01.2017.
 */
public class TestRunner {
    public static void main(String[] args) throws IOException {
        String folder = ".\\src\\test\\resources";

        String baseFile = "cl_1";

        String srcFile = baseFile + "_src";
        String dstFile = baseFile + "_dst";
        String outFileMappings = folder + "\\" + baseFile + "_out_mappings.csv";
        String outFileActions = folder + "\\" + baseFile + "_out_actions.csv";

        // read files
        FileHandler fh = new FileHandler(folder, "test");
        String srcString = fh.loadFile(srcFile);
        String dstString = fh.loadFile(dstFile);

        // get matcher
        Matcher matcher = getMatcher(srcString, dstString);

        // write mappings
        writeMappings(outFileMappings, srcString, dstString, matcher);
        // write actions
        writeActions(outFileActions, srcString, dstString, matcher);
    }

    private static void writeMappings(String outFileMappings, String srcString, String dstString, Matcher matcher) throws IOException {
        LineNumberHelper numberHelper = new LineNumberHelper(srcString, dstString, matcher.getMappings());

        // write mappings
        Writer writer = new BufferedWriter(
                new OutputStreamWriter(
                        new FileOutputStream(new File(outFileMappings))
                        , StandardCharsets.UTF_8));
        // write
        String header = String.join(";",
                new String[] {
                        "NodeType",
                        "SrcId",
                        "SrcParentId",
                        "SrcLabel",
                        "SrcStart",
                        "SrcEnd",
                        "DstId",
                        "DstParentId",
                        "DstLabel",
                        "DstStart",
                        "DstEnd"
                }
        );
        writer.write(header + "\r\n");

        // write mappings
        for(Mapping mapping : matcher.getMappings()) {
            NodeInfo srcInfo = new NodeInfo(mapping.first, numberHelper.getLineNumbers(mapping.first, true));
            NodeInfo dstInfo = new NodeInfo(mapping.second, numberHelper.getLineNumbers(mapping.second, false));

            String line = String.join(";",
                    new String[] {
                            String.valueOf(srcInfo.getNodeType()),
                            String.valueOf(srcInfo.getId()),
                            String.valueOf(srcInfo.getParentId()),
                            srcInfo.getLabel(),
                            String.valueOf(srcInfo.getStartLineNumber()),
                            String.valueOf(srcInfo.getEndLineNumber()),
                            String.valueOf(dstInfo.getId()),
                            String.valueOf(dstInfo.getParentId()),
                            dstInfo.getLabel(),
                            String.valueOf(dstInfo.getStartLineNumber()),
                            String.valueOf(dstInfo.getEndLineNumber())
                    }
            );

            writer.write(line + "\r\n");
        }

        writer.close();
    }

    private static void writeActions(String outFileActions, String srcString, String dstString, Matcher matcher) throws IOException {
        List<Action> actions = new ActionGenerator(matcher.getSrc(), matcher.getDst(), matcher.getMappings()).generate();
        SourceCodeChangeFactory changeFactory = new SourceCodeChangeFactory(matcher.getMappings(), new LineNumberHelper(srcString,dstString, matcher.getMappings()));
        // write mappings
        Writer writer = new BufferedWriter(
                new OutputStreamWriter(
                        new FileOutputStream(new File(outFileActions))
                        , StandardCharsets.UTF_8));
        // write
        String header = String.join(";",
                new String[] {
                        "Change",
                        "NodeType",
                        "SrcId",
                        "SrcLabel",
                        "SrcStart",
                        "SrcEnd",
                        "DstId",
                        "DstLabel",
                        "DstStart",
                        "DstEnd"
                }
        );
        writer.write(header + "\r\n");

        for(Action action : actions) {
            SourceCodeChange change = changeFactory.create(action);

            String line = String.join(";",
                    new String[] {
                            change.getAction().getName(),
                            String.valueOf(change.getNodeType()),
                            String.valueOf(change.getSrcInfo().getId()),
                            change.getSrcInfo().getLabel(),
                            String.valueOf(change.getSrcInfo().getStartLineNumber()),
                            String.valueOf(change.getSrcInfo().getEndLineNumber()),
                            String.valueOf(change.getDstInfo().getId()),
                            change.getDstInfo().getLabel(),
                            String.valueOf(change.getDstInfo().getStartLineNumber()),
                            String.valueOf(change.getDstInfo().getEndLineNumber())
                    }
            );

            writer.write(line + "\r\n");
        }
        writer.flush();
        writer.close();
    }


    private static Matcher getMatcher(String srcString, String dstString) throws IOException {
        // get trees
//        TreeGenerator gen = new OptimizedJdtTreeGenerator();
        TreeGenerator gen = new DocIgnoringTreeGenerator();

        ITree src = gen.generateFromString(srcString).getRoot();
        ITree dst = gen.generateFromString(dstString).getRoot();

        MappingStore mappings = new MappingStore();

        Matcher matcher = new JavaMatchers.IterativeJavaMatcher_V2(src, dst, mappings);

        matcher.match();

        return matcher;
    }

}
