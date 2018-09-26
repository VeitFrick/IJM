package matchers;

import com.github.gumtreediff.matchers.Mapping;
import com.github.gumtreediff.matchers.MappingStore;
import com.github.gumtreediff.matchers.Matcher;
import com.github.gumtreediff.matchers.optimal.zs.ZsMatcher;
import com.github.gumtreediff.tree.ITree;
import com.github.gumtreediff.tree.TreeUtils;
import gen.NodeType;

import java.util.HashSet;

/**
 * Created by thomas on 27.02.2017. based on GreedyBottomUpMatcher in GumTreeDiff
 */
public class LabelAwareBottomUpMatcher extends AbstractGreedyBottomUpMatcher {
    private HashSet<Integer> labelMatchingNodes = new HashSet<>();

    private LabelSimilarityHelper labelSimHelper;
    private boolean removeMatched = true;

    public LabelAwareBottomUpMatcher(ITree src, ITree dst, MappingStore store, double labelSimThreshold){
        super(src, dst, store);
        this.labelSimHelper = new LabelSimilarityHelper(labelSimThreshold);

        // add node types tha should be compared with a min threshold
        this.labelMatchingNodes.add(NodeType.SIMPLE_NAME.getValue());
        this.labelMatchingNodes.add(NodeType.METHOD_DECLARATION.getValue());
        this.labelMatchingNodes.add(NodeType.ENUM_DECLARATION.getValue());
        this.labelMatchingNodes.add(NodeType.METHOD_INVOCATION.getValue());
        this.labelMatchingNodes.add(NodeType.IMPORT_DECLARATION.getValue());
        this.labelMatchingNodes.add(NodeType.ENUM_CONSTANT_DECLARATION.getValue());
    }

    public LabelAwareBottomUpMatcher(ITree src, ITree dst, MappingStore store) {
        this(src, dst, store, 0.3);
    }

    public void setMinLabelSimilarity(double simThreshold) {
        this.labelSimHelper = new LabelSimilarityHelper(simThreshold);
    }
    
    public void setRemoveMatched(boolean removeMatched) {
        this.removeMatched = removeMatched;
    }

    @Override
    protected void lastChanceMatch(ITree src, ITree dst) {
        ITree cSrc = src.deepCopy();
        ITree cDst = dst.deepCopy();

        if (this.removeMatched) {
            TreeUtils.removeMatched(cSrc);
            TreeUtils.removeMatched(cDst);
        }

        if (cSrc.getSize() < SIZE_THRESHOLD || cDst.getSize() < SIZE_THRESHOLD) {
            Matcher m = new ZsMatcher(cSrc, cDst, new MappingStore());
            m.match();
            for (Mapping candidate: m.getMappings()) {
                ITree left = srcIds.getTree(candidate.getFirst().getId());
                ITree right = dstIds.getTree(candidate.getSecond().getId());

                if (left.getId() == src.getId() || right.getId() == dst.getId()) {
                    continue;
                } else if (!left.isMatchable(right)) {
                    continue;
                } else if (left.getParent().getType() != right.getParent().getType()) {
                    continue;
                } else if (this.labelMatchingNodes.contains(left.getType())) {
                    if (this.labelSimHelper.getSimilarity(left, right) > 0D) {
                        addMapping(left, right);
                    }
                } else {
                    addMapping(left, right);
                }
            }
        }

        for (ITree t : src.getTrees())
            t.setMatched(true);
        for (ITree t : dst.getTrees())
            t.setMatched(true);
    }
}
