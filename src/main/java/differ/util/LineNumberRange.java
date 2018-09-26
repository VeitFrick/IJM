package differ.util;

/**
 * Created by thomas on 24.01.2017.
 */
public class LineNumberRange {
    private final int startLine;
    private final int endLine;

    private int startOffset;
    private int endOffset;

    public LineNumberRange() {
        this(0,0);
    }

    public LineNumberRange(int start, int end) {

        this.startLine = start;
        this.endLine = end;
        this.startOffset = 0;
        this.endOffset = 0;
    }

    public void setOffset(int start, int end) {

        this.startOffset = start;
        this.endOffset = end;
    }

    public int getStartLine(){ return this.startLine; }
    public int getEndLine() {return this.endLine; }

    public int getStartOffset() {
        return startOffset;
    }

    public int getEndOffset() {
        return endOffset;
    }
}
