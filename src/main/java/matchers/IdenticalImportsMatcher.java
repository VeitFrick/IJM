package matchers;


import com.github.gumtreediff.matchers.MappingStore;
import com.github.gumtreediff.matchers.Matcher;
import com.github.gumtreediff.tree.ITree;
import com.github.gumtreediff.tree.TreeMap;
import com.github.gumtreediff.tree.TreeUtils;
import gen.NodeType;
import util.CuttingCopyTreeHelper;

import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Created by thomas on 27.03.2017.
 */
public class IdenticalImportsMatcher extends Matcher {
    private final TreeMap srcIds;
    private final TreeMap dstIds;
    private final CuttingCopyTreeHelper treeCutter;

    public IdenticalImportsMatcher(ITree src, ITree dst, MappingStore store) {
        super(src, dst, store);

        srcIds = new TreeMap(src);
        dstIds = new TreeMap(dst);

        treeCutter = new CuttingCopyTreeHelper(
                t -> !t.isRoot()
                        && t.getParent().getType() == NodeType.TYPE_DECLARATION.getValue()
                        && t.getType() !=  NodeType.IMPORT_DECLARATION.getValue(),
                false);
    }

    @Override
    public void match() {
        List<ITree> srcImports = getImports(src);
        List<ITree> dstImports = getImports(dst);

        Iterator<ITree> srcIterator = srcImports.iterator();

        while (srcIterator.hasNext() && dstImports.size() > 0) {
            ITree currentSrc = srcIterator.next();

            Iterator<ITree> dstIterator = dstImports.iterator();

            while (dstIterator.hasNext()) {
                ITree currentDst = dstIterator.next();

                int compValue = currentSrc.getLabel().compareTo(currentDst.getLabel());

                if (compValue > 1)
                    break;

                if (compValue == 0) {
                    ITree originalSrc = srcIds.getTree(currentSrc.getId());
                    ITree originalDst = dstIds.getTree(currentDst.getId());

                    addMapping(originalSrc, originalDst);

                    srcIterator.remove();
                    dstIterator.remove();

                    break;
                }
            }
        }
    }

    private List<ITree> getImports(ITree root) {
        ITree cutTree = TreeUtils.removeMatched(treeCutter.cutTree(root));

        return cutTree.getDescendants()
                .stream()
                .filter(x -> x.getType() == NodeType.IMPORT_DECLARATION.getValue())
                .sorted(Comparator.comparing(ITree::getLabel))
                .collect(Collectors.toList());
    }
}
