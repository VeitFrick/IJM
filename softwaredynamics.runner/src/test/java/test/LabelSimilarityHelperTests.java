package test;

import at.aau.softwaredynamics.matchers.LabelSimilarityHelper;
import org.junit.Test;

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.assertEquals;

/**
 * Created by thomas on 31.01.2017.
 */
public class LabelSimilarityHelperTests {

    private static final double targetSim = 0.3d;

    @Test
    public void comparisonIsCasInsensitive() {
        String label1 = "Connection";
        String label2 = "connection";

        double target = new LabelSimilarityHelper(targetSim).getSimilarity(label1, label2);

        assertEquals(1, target, 0);
    }

    @Test
    public void lowerEqualMatcheslower() {
        String label1 = "<=";
        String label2 = "<";

        double target = new LabelSimilarityHelper(targetSim).getSimilarity(label1, label2);

        assertTrue(0 < target);
    }

    @Test
    public void sameLabelsResultIn1D() {
        String label1 = "asdf";
        String label2 = "asdf";

        assertEquals(1D, new LabelSimilarityHelper(targetSim).getSimilarity(label1, label2), 0);
    }

    @Test
    public void differentLabelsResultIn0D() {
        String label1 = "asdf";
        String label2 = "qwer";

        assertEquals(0D, new LabelSimilarityHelper(targetSim).getSimilarity(label1, label2), 0);
    }

    @Test
    public void simialrLabelsResultInSimGreater0_5() {
        String label1 = "assertEquals";
        String label2 = "assertFalse";

        assertTrue(0D < new LabelSimilarityHelper(targetSim).getSimilarity(label1, label2));
    }

    @Test
    public void emptyLabelsResultIn1D() {
        String label1 = "";
        String label2 = "";

        assertEquals(1D, new LabelSimilarityHelper(targetSim).getSimilarity(label1, label2), 0);
    }

    @Test
    public void modifiersResultsIn0D() {
        String label1 = "public";
        String label2 = "private";

        assertEquals(0D, new LabelSimilarityHelper(targetSim).getSimilarity(label1, label2), 0);
    }
}
