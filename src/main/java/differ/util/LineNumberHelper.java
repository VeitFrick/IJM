package differ.util;

import com.github.gumtreediff.actions.model.Action;
import com.github.gumtreediff.actions.model.Insert;
import com.github.gumtreediff.matchers.MappingStore;
import com.github.gumtreediff.tree.ITree;

/**
 * Created by thomas on 05.12.2016.
 */
public class LineNumberHelper {
    public static final String LINE_SEPARATOR = "\r\n";

    private String[] srcLines;
    private String[] dstLines;
    private MappingStore mappings;

    public LineNumberHelper(String src, String dst, MappingStore mappings) {
        srcLines = src.split(LINE_SEPARATOR);
        dstLines = dst.split(LINE_SEPARATOR);
        this.mappings = mappings;
    }

    public LineNumberRange getLineNumbers(ITree node, boolean isSrcNode) {
        if (node == null)
            return new LineNumberRange();

        String[] lines = isSrcNode ? this.srcLines : this.dstLines;

        LineNumberRange lnr = new LineNumberRange(
                this.getLineNumbers(lines, node.getPos()),
                this.getLineNumbers(lines, node.getPos() + node.getLength())
        );
        lnr.setOffset(
                this.getLineNumberOffset(lines, node.getPos()),
                this.getLineNumberOffset(lines, node.getPos() + node.getLength())
        );
        return lnr;
    }

    public LineNumberRange getSrcLineNumbers(Action action) {
        if (action instanceof Insert)
            return new LineNumberRange();

        ITree node = action.getNode();

        return getLineNumbers(node, true);
    }

    public LineNumberRange getDstLineNumbers(Action action) {
        ITree node = action.getNode();

        if (!(action instanceof Insert))
            node = mappings.getDst(node);

        if (node == null)
            return new LineNumberRange();

        return getLineNumbers(node, false);
    }

    private static int getLineNumbers(String[] lines, int position) {
        int currLineEnd = 0;
        int i = 0;
        for(; i < lines.length && currLineEnd <= position; i++) {
            currLineEnd += lines[i].length() + 2; // add chars in line
        }

        return i;
    }

    private static int getLineNumberOffset(String[] lines, int position) {
        int currLineEnd = 0;
        int i = 0;
        for(; i < lines.length && currLineEnd <= position; i++) {
            currLineEnd += lines[i].length() + 2; // add chars in line
        }

        int lineStart = currLineEnd - (lines[i-1].length() + 2);
        int linePosition = position - lineStart;

        return linePosition;
    }
}
