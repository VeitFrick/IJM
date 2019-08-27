package at.aau.softwaredynamics.matchers;

import com.github.gumtreediff.matchers.MappingStore;
import com.github.gumtreediff.matchers.Matcher;
import com.github.gumtreediff.tree.ITree;
import com.github.gumtreediff.tree.TreeMap;

import java.util.*;

/**
 * Created by thomas on 27.02.2017. based on GreedyBottomUpMatcher in GumTreeDiff
 */
public abstract class AbstractGreedyBottomUpMatcher extends Matcher {
    protected static final double SIM_THRESHOLD = Double.parseDouble(System.getProperty("gumtree.match.bu.sim", "0.3"));
    protected static final int SIZE_THRESHOLD = Integer.parseInt(System.getProperty("gumtree.match.bu.size", "1000"));

    protected TreeMap srcIds;
    protected TreeMap dstIds;

    public AbstractGreedyBottomUpMatcher(ITree src, ITree dst, MappingStore store) {
        super(src, dst, store);
    }

    public void match() {
        srcIds = new TreeMap(src);
        dstIds = new TreeMap(dst);

        for (ITree t: src.postOrder())  {
            if (t.isRoot()) {
                addMapping(t, this.dst);
                lastChanceMatch(t, this.dst);
                break;
            } else if (!(t.isMatched() || t.isLeaf())) {
                List<ITree> candidates = getDstCandidates(t);
                ITree best = null;
                double max = -1D;

                for (ITree cand: candidates) {
                    double sim = jaccardSimilarity(t, cand);
                    if (sim > max && sim >= SIM_THRESHOLD) {
                        max = sim;
                        best = cand;
                    }
                }

                if (best != null) {
                    lastChanceMatch(t, best);
                    addMapping(t, best);
                }
            }
        }
        clean();
    }

    private List<ITree> getDstCandidates(ITree src) {
        List<ITree> seeds = new ArrayList<>();
        for (ITree c: src.getDescendants()) {
            ITree m = mappings.getDst(c);
            if (m != null) seeds.add(m);
        }
        List<ITree> candidates = new ArrayList<>();
        Set<ITree> visited = new HashSet<>();
        for (ITree seed: seeds) {
            while (seed.getParent() != null) {
                ITree parent = seed.getParent();
                if (visited.contains(parent))
                    break;
                visited.add(parent);
                if (parent.getType() == src.getType() && !parent.isMatched() && !parent.isRoot())
                    candidates.add(parent);
                seed = parent;
            }
        }

        return candidates;
    }

    protected abstract void lastChanceMatch(ITree src, ITree dst);
}
