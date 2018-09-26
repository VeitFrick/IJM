package matchers;

import com.github.gumtreediff.tree.ITree;
import org.simmetrics.StringMetrics;

/**
 * Created by thomas on 30.01.2017.
 */
public class LabelSimilarityHelper {
    private final double threshold;

    public LabelSimilarityHelper(double similarityThreshold) {
        this.threshold = similarityThreshold;
    }

    /**
     * Returns a value between 0 and 1 describing the similarity of the labels of the given nodes.
     * Any value below LabelSimilarityHelper.threshold results in 0.
     *
     * @param node1
     * @param node2
     * @return
     */
    public double getSimilarity(ITree node1, ITree node2) {
        if (node1.getType() == node2.getType())
            return getSimilarity(node1.getLabel(), node2.getLabel());
        else
            return 0D;
    }

    /**
     * Returns a value between 0 and 1 describing the similarity of the labels of the given nodes.
     * Any value below LabelSimilarityHelper.threshold results in 0.
     *
     * @param label1
     * @param label2
     * @return
     */
    public double getSimilarity(String label1, String label2) {
        double simVal = StringMetrics.levenshtein()
                .compare(label1.toLowerCase(),label2.toLowerCase());

        if (simVal >= this.threshold)
            return simVal;
        else
            return 0D;
    }
}
