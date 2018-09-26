package differ;

import com.github.gumtreediff.actions.ActionGenerator;
import com.github.gumtreediff.actions.model.*;
import com.github.gumtreediff.gen.TreeGenerator;
import com.github.gumtreediff.matchers.MappingStore;
import com.github.gumtreediff.matchers.Matcher;
import com.github.gumtreediff.tree.TreeContext;
import differ.entities.DifferMetrics;
import differ.entities.SourceCodeChange;
import differ.entities.SourceCodeChangeFactory;
import differ.util.LineNumberHelper;
import matchers.MatcherFactory;

import java.util.List;
import java.util.Vector;

/**
 * Created by thomas on 19.12.2016.
 */
public abstract class AbstractJavaChangeDiffer {
    private MatcherFactory matcherFactory;
    private List<SourceCodeChange> result;
    private SourceCodeChangeFactory changeFactory;
    private MappingStore mappings;
    private DifferMetrics metrics;
    private TreeGenerator generator;

    public AbstractJavaChangeDiffer(
            Class<? extends Matcher> matcherType,
            TreeGenerator generator) {
        this.matcherFactory = new MatcherFactory(matcherType);
        this.generator = generator;
    }

    public void diff(String src, String dst) throws Exception {
        this.metrics = new DifferMetrics();
        long startMillis = System.currentTimeMillis();

        // replace any line separator with supported line breaks
        src = src.replaceAll("(\\r)?\\n", LineNumberHelper.LINE_SEPARATOR);
        dst = dst.replaceAll("(\\r)?\\n", LineNumberHelper.LINE_SEPARATOR);

        // tree generation
        long refMillis = System.currentTimeMillis();
        TreeContext srcCtx = this.generator.generateFromString(src);
        TreeContext dstCtx = this.generator.generateFromString(dst);
        this.metrics.setTreeGenerationTime(System.currentTimeMillis() - refMillis);

        // matching
        refMillis = System.currentTimeMillis();
        Matcher m = matcherFactory.createMatcher(srcCtx.getRoot(), dstCtx.getRoot());
        m.match();
        mappings = m.getMappings();
        this.metrics.setMatchingTime(System.currentTimeMillis() - refMillis);

        // action generation
        refMillis = System.currentTimeMillis();
        ActionGenerator g = new ActionGenerator(srcCtx.getRoot(), dstCtx.getRoot(), mappings);
        g.generate();
        List<Action> actions = g.getActions();
        this.metrics.setActionGenerationTime(System.currentTimeMillis() - refMillis);

        this.changeFactory = new SourceCodeChangeFactory(src, dst, mappings);

        // change extraction
        refMillis = System.currentTimeMillis();
        result = new Vector<>();
        for (Action a : actions) {
            if (a instanceof Insert)
                diff((Insert) a);
            else if (a instanceof Delete)
                diff((Delete) a);
            else if (a instanceof Update)
                diff((Update) a);
            else if (a instanceof Move)
                diff((Move) a);
        }

        long stopMillies = System.currentTimeMillis();

        this.metrics.setClassifyingTime(stopMillies - refMillis);
        this.metrics.setTotalTime(stopMillies - startMillis);

        this.metrics.setNumSrcNodes(srcCtx.getRoot().getSize());
        this.metrics.setNumDstNodes(dstCtx.getRoot().getSize());
        this.metrics.setNumActions(actions.size());    }

    public MappingStore getMappings() {
        return this.mappings;
    }
    
    public List<SourceCodeChange> getCodeChanges(){
        return this.result; 
    }
    
    public DifferMetrics getMetrics() {
        return this.metrics;
    }

    protected SourceCodeChangeFactory getChangeFactory() {
        return this.changeFactory;
    }

    protected void addResult(SourceCodeChange change) {
        this.result.add(change);
    }

    protected abstract void diff(Insert action);
    protected abstract void diff(Delete action);
    protected abstract void diff(Update action);
    protected abstract void diff(Move action);
}
