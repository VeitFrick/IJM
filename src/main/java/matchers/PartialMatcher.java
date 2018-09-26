package matchers;

import com.github.gumtreediff.matchers.Mapping;
import com.github.gumtreediff.matchers.MappingStore;
import com.github.gumtreediff.matchers.Matcher;
import com.github.gumtreediff.tree.ITree;
import com.github.gumtreediff.tree.TreeMap;
import com.github.gumtreediff.tree.TreeUtils;
import util.CuttingCopyTreeHelper;

/**
 * Created by thomas on 31.01.2017.
 */
public class PartialMatcher extends Matcher {
    private final PartialMatcherConfiguration config;
    private final CuttingCopyTreeHelper treeCutter;
    private final TreeMap srcIds;
    private final TreeMap dstIds;

    public PartialMatcher(ITree src, ITree dst, MappingStore mappings, PartialMatcherConfiguration config) {
        super(src, dst, mappings);
        this.config = config;
        treeCutter = new CuttingCopyTreeHelper(config.getCutOffCondition(), false);
        srcIds = new TreeMap(src);
        dstIds = new TreeMap(dst);
    }

    @Override
    public void match() {
        ITree cutSrc = TreeUtils.removeMatched(treeCutter.cutTree(this.src));
        ITree cutDst = TreeUtils.removeMatched(treeCutter.cutTree(this.dst));

        // init inner matcher with cut trees
        Matcher innerMatcher = new MatcherFactory(config.getInnerMatcherType()).createMatcher(cutSrc, cutDst);
        innerMatcher.match();

        // add mappings
        for(Mapping m : innerMatcher.getMappings()) {
            if (!this.config.includeInResult(m.first))
                continue;

            // map tmp mapping back to original mappings
            ITree originalSrc = this.srcIds.getTree(m.first.getId());
            ITree originalDst = this.dstIds.getTree(m.second.getId());

            // add sub mappings if configured
            matchSubTrees(originalSrc, originalDst);

            // add mapping
            this.addMapping(originalSrc, originalDst);
        }
    }

    private void matchSubTrees(ITree src, ITree dst) {
        CuttingCopyTreeHelper copyTreeHelper = new CuttingCopyTreeHelper(t -> false, true);

        if (this.config.getMatchSubtrees()) {
            ITree srcCopy = copyTreeHelper.cutTree(src);
            ITree dstCopy = copyTreeHelper.cutTree(dst);

            Matcher subMatcher =
                    new MatcherFactory(config.getSubNodeMatcherType())
                            .createMatcher(srcCopy, dstCopy);
            subMatcher.match();

            for(Mapping m : subMatcher.getMappings())
                this.addMapping(
                        this.srcIds.getTree(m.first.getId()),
                        this.dstIds.getTree(m.second.getId()));
        }
    }

    @Override
    protected void addMapping(ITree src, ITree dst) {
        if (src.isMatched() || dst.isMatched())
            return;

        super.addMapping(src,dst);
    }
}
