package at.aau.softwaredynamics.classifier.entities;

import at.aau.softwaredynamics.classifier.util.LineNumberHelper;
import at.aau.softwaredynamics.classifier.util.LineNumberRange;
import at.aau.softwaredynamics.util.Meta;
import com.github.gumtreediff.actions.model.*;
import com.github.gumtreediff.matchers.MappingStore;
import com.github.gumtreediff.tree.ITree;

/**
 * Created by thomas on 19.12.2016.
 */
public class SourceCodeChangeFactory {
    private final LineNumberHelper lineNumberHelper;
    private MappingStore mappings;

//    public SourceCodeChangeFactory(String src, String dst, MappingStore mappings) {
//        this.mappings = mappings;
//        lineNumberHelper = new LineNumberHelper(src, dst, mappings);
//    }

    public SourceCodeChangeFactory(MappingStore mappings, LineNumberHelper lineNumberHelper) {
        this.mappings = mappings;
        this.lineNumberHelper = lineNumberHelper;
    }

    public SourceCodeChange create(Action action) {
        if (action instanceof Insert)
            return create((Insert)action);
        else if (action instanceof Delete)
            return create((Delete)action);
        else if (action instanceof Update)
            return create((Update) action);
        else if (action instanceof Move)
            return create((Move) action);
        else
            return create((Meta)action);
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

    public SourceCodeChange create(Meta meta) {
        NodeInfo srcInfo = NodeInfo.getDummyInfo();
        NodeInfo dstInfo = NodeInfo.getDummyInfo();

        if(meta.hasSrcNode()) {
            ITree srcNode = meta.getNode();
            if (mappings.getSrc(meta.getNode()) != null) srcNode = mappings.getSrc(meta.getNode());
            LineNumberRange srcLineNumbers = lineNumberHelper.getLineNumbers(srcNode, true, true);
            srcInfo = new NodeInfo(
                    srcNode,
                    srcLineNumbers);
        }

        if (meta.hasDstNode()) {
            ITree dstNode = meta.getNode();
            if (mappings.getDst(meta.getNode()) != null) dstNode = mappings.getDst(meta.getNode());
            LineNumberRange dstLineNumbers = lineNumberHelper.getLineNumbers(dstNode, false, true);
            dstInfo = new NodeInfo(
                    dstNode,
                    dstLineNumbers);
        }

        return new SourceCodeChange(meta, srcInfo, dstInfo);
    }
}
