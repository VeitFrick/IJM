package test;

import at.aau.softwaredynamics.classifier.JChangeClassifier;
import at.aau.softwaredynamics.classifier.entities.SourceCodeChange;
import at.aau.softwaredynamics.classifier.util.LineNumberHelper;
import at.aau.softwaredynamics.classifier.util.LineNumberRange;
import at.aau.softwaredynamics.gen.SpoonTreeGenerator;
import at.aau.softwaredynamics.matchers.JavaMatchers;
import com.github.gumtreediff.matchers.MappingStore;
import org.junit.Ignore;
import org.junit.Test;
import java.io.IOException;
import java.util.Collection;

import static org.junit.Assert.*;

/**
 * Created by Veit on 24.04.2019.
 */
public class LineNumberHelperTest {

    @Test
    public void lineNumberSanityCheck() throws Exception {
        String src = "public class Dog {\n" +
                "   String breed = \"\";\n" +
                "   int age = 1;\n" +
                "   String color = \"Red\";\n" +
                "\n" +
                "   void barking() {\n" +
                "this.age = 2;\n" +
                "   }\n" +
                "}";
        String dst = "public class Dog {\n" +
                "   String breed = \"\";\n" +
                "   int age = 2;\n" +
                "   String color = \"Red\";\n" +
                "\n" +
                "   void barking() {\n" +
                "this.age = 4000;\n" +
                "   }\n" +
                "}";
        JChangeClassifier jChangeClassifier = new JChangeClassifier(JavaMatchers.IterativeJavaMatcher_Spoon.class, new SpoonTreeGenerator());
        jChangeClassifier.classify(src,dst);
        LineNumberHelper lineNumberHelper = new LineNumberHelper(src,src,jChangeClassifier.getMappings());
        LineNumberRange lnr = lineNumberHelper.getLineNumbers(jChangeClassifier.getActions().get(0).getNode(),true);
        assertEquals(3,lnr.getStartLine());
        assertEquals(13,lnr.getStartOffset());
        assertEquals(3,lnr.getEndLine());
        assertEquals(14,lnr.getEndOffset());
        lnr = lineNumberHelper.getLineNumbers(jChangeClassifier.getActions().get(1).getNode(),true);
        assertEquals(7,lnr.getStartLine());
        assertEquals(11,lnr.getStartOffset());
        assertEquals(7,lnr.getEndLine());
        assertEquals(12,lnr.getEndOffset());
     }

}
