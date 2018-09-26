package differ.entities;

import com.github.gumtreediff.actions.model.*;
import com.github.gumtreediff.matchers.MappingStore;
import differ.util.LineNumberHelper;

/**
 * Created by thomas on 19.12.2016.
 */
public class SourceCodeChangeFactory {
    private final LineNumberHelper lineNumberHelper;
    private MappingStore mappings;

    public SourceCodeChangeFactory(String src, String dst, MappingStore mappings) {
        this.mappings = mappings;
        lineNumberHelper = new LineNumberHelper(src, dst, mappings);
    }

    public SourceCodeChange create(Action action) {
        if (action instanceof Insert)
            return create((Insert)action);
        else if (action instanceof Delete)
            return create((Delete)action);
        else if (action instanceof Update)
            return create((Update) action);
        else
            return create((Move)action);
    }

    public SourceCodeChange create(Insert insert) {
        NodeInfo srcInfo = NodeInfo.getDummyInfo();

        NodeInfo dstInfo = new NodeInfo(
                insert.getNode(),
                lineNumberHelper.getDstLineNumbers(insert));

        return new SourceCodeChange(insert, srcInfo, dstInfo);
    }

    public SourceCodeChange create(Delete delete) {
        NodeInfo srcInfo = new NodeInfo(
                delete.getNode(),
                lineNumberHelper.getSrcLineNumbers(delete));
        NodeInfo dstInfo = NodeInfo.getDummyInfo();

        return new SourceCodeChange(delete, srcInfo, dstInfo);
    }

    public SourceCodeChange create(Update update) {
        NodeInfo srcInfo = new NodeInfo(
                update.getNode(),
                lineNumberHelper.getSrcLineNumbers(update));
        NodeInfo dstInfo = new NodeInfo(
                mappings.getDst(update.getNode()),
                lineNumberHelper.getDstLineNumbers(update));

        return new SourceCodeChange(update, srcInfo, dstInfo);
    }

    public SourceCodeChange create(Move move) {
        NodeInfo srcInfo = new NodeInfo(
                move.getNode(),
                lineNumberHelper.getSrcLineNumbers(move));
        NodeInfo dstInfo = new NodeInfo(
                mappings.getDst(move.getNode()),
                lineNumberHelper.getDstLineNumbers(move));

        return new SourceCodeChange(move, srcInfo, dstInfo);
    }
}
