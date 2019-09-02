package at.aau.softwaredynamics.classifier.util;

import at.aau.softwaredynamics.util.Meta;
import com.github.gumtreediff.actions.model.Action;
import com.github.gumtreediff.actions.model.Insert;
import com.github.gumtreediff.matchers.MappingStore;
import com.github.gumtreediff.tree.ITree;
import spoon.reflect.cu.position.DeclarationSourcePosition;
import spoon.reflect.cu.position.NoSourcePosition;
import spoon.reflect.declaration.CtElement;

/**
 * Created by thomas on 05.12.2016.
 */
public class LineNumberHelper {
    private static final String LINE_SEPARATOR = "\r\n";

    private String[] srcLines;
    private String[] dstLines;
    private MappingStore mappings;

    public LineNumberHelper(String src, String dst, MappingStore mappings) {
        if(src == null) src= "";
        if(dst == null) dst= "";
        src = unfiyLineEndings(src);
        dst = unfiyLineEndings(dst);
        srcLines = src.split(LINE_SEPARATOR);
        dstLines = dst.split(LINE_SEPARATOR);
        this.mappings = mappings;
    }

    public LineNumberRange getLineNumbers(ITree node, boolean isSrcNode) {
        return getLineNumbers(node, isSrcNode, false);
    }

    public LineNumberRange getLineNumbers(ITree node, boolean isSrcNode, boolean compact) {
        if (node == null)
            return new LineNumberRange();

        String[] lines = isSrcNode ? this.srcLines : this.dstLines;

        CtElement spoonObject = (CtElement) node.getMetadata("spoon_object");
        Integer startPosition;
        Integer endPosition;

        if (spoonObject != null) {
            // spoon specific code of getting positions
            if (spoonObject.getPosition() instanceof NoSourcePosition)
                return getLineNumbers(node.getParent(), isSrcNode, compact);
            else if (spoonObject.getPosition() instanceof DeclarationSourcePosition && compact) {
                startPosition = ((DeclarationSourcePosition) spoonObject.getPosition()).getNameStart();
                endPosition = ((DeclarationSourcePosition) spoonObject.getPosition()).getNameEnd();
            } else {
                startPosition = spoonObject.getPosition().getSourceStart();
                endPosition = spoonObject.getPosition().getSourceEnd();
            }
        } else {
            if (node.getPos() < 0) return new LineNumberRange();
            startPosition = node.getPos();
            endPosition = node.getPos() + node.getLength();
        }

        LineNumberRange lnr = new LineNumberRange(
                getLineNumbers(lines, startPosition),
                getLineNumbers(lines, endPosition)
        );
        lnr.setOffset(
                getLineNumberOffset(lines, startPosition),
                getLineNumberOffset(lines, endPosition + 1) //TODO find out why +1 offset is necessary
        );

        return lnr;
    }

    /**
     * Static method for simply getting a LineNumberRange from source and node
     *
     * @param sourceCode the source code of the file containing the node
     * @param node       ITree node for which the LineNumberRange should be calculated
     * @param compact    true if only declaration position should be included
     * @return LineNumberRange containing startLine, endLine, startOffset and endOffset
     */
    public static LineNumberRange getLineNumberRange(String sourceCode, ITree node, boolean compact) {
        LineNumberHelper lineNumberHelper = new LineNumberHelper(sourceCode, sourceCode, null);
        return lineNumberHelper.getLineNumbers(node, true, compact);
    }

    public LineNumberRange getSrcLineNumbers(Action action) {
        if (action instanceof Insert)
            return new LineNumberRange();

        ITree node = action.getNode();

        return getLineNumbers(node, true, (action instanceof Meta));
    }

    public LineNumberRange getDstLineNumbers(Action action) {
        ITree node = action.getNode();

        if (!(action instanceof Insert) && !(action instanceof Meta)) {
            node = mappings.getDst(node);
        }

        if (node == null)
            return new LineNumberRange();

        return getLineNumbers(node, false, (action instanceof Meta));
    }

    private static int getLineNumbers(String[] lines, int position) {
        //if(String.join("\n\r", lines).length() + 2 < position || position < 0) throw new IllegalArgumentException("Position "+position+"is out of bounds!");

        int currLineEnd = 0;
        int i = 0;
        for(; i < lines.length && currLineEnd <= position; i++) {
            currLineEnd += lines[i].length() + 2; // add chars in line
        }

        return i;
    }

    private static int getLineNumberOffset(String[] lines, int position) {
        //if(String.join("\n\r", lines).length() + 2 < position || position < 0) throw new IllegalArgumentException("Position "+position+"is out of bounds!");

        int currLineEnd = 0;
        int linePosition;
        int i = 0;
        try {
            for (; i < lines.length && currLineEnd <= position; i++) {
                currLineEnd += lines[i].length() + 2; // add chars in line
            }

            int lineStart = currLineEnd - (lines[i - 1].length() + 2);
            linePosition = position - lineStart;
        }
        catch (Exception ex) {
            linePosition = -1;
            System.out.println("Invalid line number, cannot calc offset: " + position);
        }
        return linePosition;
    }

    /**
     * Replace any line separator with supported line breaks
     *
     * @param sourceCode the string to be unified
     * @return the unified string
     */
    public static String unfiyLineEndings(String sourceCode) {
        if(sourceCode == null) return null;
        return sourceCode.replaceAll("(\\r)?\\n", LINE_SEPARATOR);
    }
}
