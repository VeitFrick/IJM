package at.aau.softwaredynamics.matchers.tests.util;

import com.github.gumtreediff.gen.jdt.AbstractJdtTreeGenerator;
import com.github.gumtreediff.tree.ITree;
import org.junit.Ignore;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

/**
 * Created by thomas on 13.03.2017.
 */
public class TestHelper {
    private static String filePath = ".\\src\\test\\resources";
    
    public static ITree getTree(String srcFile, AbstractJdtTreeGenerator gen) throws IOException {
        String src = TestHelper.loadFile(srcFile);
        return getTreeFromString(src, gen);
    }

    public static ITree getTreeFromString(String content, AbstractJdtTreeGenerator gen) throws IOException {
        return gen.generateFromString(content).getRoot();
    }

    public static String loadFile(String fileName){
        String path = TestHelper.filePath + File.separator + fileName+".test";
        try {
            return  new String(Files.readAllBytes(Paths.get(path)));
        } catch (IOException e) {
            e.printStackTrace();
            return e.toString();
        }
    }
}
