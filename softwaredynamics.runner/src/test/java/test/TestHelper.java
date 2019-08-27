package test;

import at.aau.softwaredynamics.runner.io.fs.FileHandler;
import com.github.gumtreediff.gen.jdt.AbstractJdtTreeGenerator;
import com.github.gumtreediff.tree.ITree;

import java.io.IOException;

/**
 * Created by thomas on 13.03.2017.
 */
public class TestHelper {
    public static ITree getTree(String srcFile, AbstractJdtTreeGenerator gen) throws IOException {
        FileHandler fh = new FileHandler(".\\src\\test\\resources", "test");
        String src = fh.loadFile(srcFile);
        return getTreeFromString(src, gen);
    }

    public static ITree getTreeFromString(String content, AbstractJdtTreeGenerator gen) throws IOException {
        return gen.generateFromString(content).getRoot();
    }
}
