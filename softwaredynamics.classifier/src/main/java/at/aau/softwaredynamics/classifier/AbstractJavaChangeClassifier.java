package at.aau.softwaredynamics.classifier;

import at.aau.softwaredynamics.classifier.entities.ClassifierMetrics;
import at.aau.softwaredynamics.classifier.entities.SourceCodeChange;
import at.aau.softwaredynamics.classifier.entities.SourceCodeChangeFactory;
import at.aau.softwaredynamics.classifier.util.LineNumberHelper;
import at.aau.softwaredynamics.matchers.MatcherFactory;
import com.github.gumtreediff.actions.ActionGenerator;
import com.github.gumtreediff.actions.model.Action;
import com.github.gumtreediff.gen.TreeGenerator;
import com.github.gumtreediff.matchers.MappingStore;
import com.github.gumtreediff.matchers.Matcher;
import com.github.gumtreediff.tree.TreeContext;

import java.util.List;
import java.util.Vector;

/**
 * Created by thomas on 19.12.2016.
 */
public abstract class AbstractJavaChangeClassifier {
    private MatcherFactory matcherFactory;
    private LineNumberHelper lineNumberHelper;
    private List<SourceCodeChange> codeChanges;
    private SourceCodeChangeFactory changeFactory;
    private MappingStore mappings;
    private ClassifierMetrics metrics;
    private TreeGenerator generator;
    private Boolean doTreeGeneration;
    private TreeContext srcContext;
    private TreeContext dstContext;
    private List<Action> actions;

    public AbstractJavaChangeClassifier(
            Class<? extends Matcher> matcherType,
            TreeGenerator generator) {
        this.matcherFactory = new MatcherFactory(matcherType);
        this.generator = generator;
        this.doTreeGeneration = true;
    }

    public void classify(String src, String dst) throws Exception {
        classify(src, dst, true);
    }

    public void classify(String src, String dst, boolean shouldClassify) throws Exception {
        this.metrics = new ClassifierMetrics();

        long startMillis = System.currentTimeMillis();

        // replace any line separator with supported line breaks
        src = LineNumberHelper.unfiyLineEndings(src);
        dst = LineNumberHelper.unfiyLineEndings(dst);

        long refMillis = System.currentTimeMillis();
        // tree generation
        if (doTreeGeneration) {
            srcContext = this.generator.generateFromString(src);
            dstContext = this.generator.generateFromString(dst);
            this.metrics.setTreeGenerationTime(System.currentTimeMillis() - refMillis);
            long treeGenTime = System.currentTimeMillis() - refMillis;
            System.out.println(srcContext.getRoot().toShortString() + dstContext.getRoot().toShortString() + " > TreeGeneration took " + treeGenTime + "ms");
        }
        // matching
        refMillis = System.currentTimeMillis();
        Matcher m = matcherFactory.createMatcher(srcContext.getRoot(), dstContext.getRoot());
        m.match();
        mappings = m.getMappings();
        this.metrics.setMatchingTime(System.currentTimeMillis() - refMillis);


        // action generation
        refMillis = System.currentTimeMillis();
        ActionGenerator g = new ActionGenerator(srcContext.getRoot(), dstContext.getRoot(), mappings);
        g.generate();
        actions = g.getActions();
        this.metrics.setActionGenerationTime(System.currentTimeMillis() - refMillis);

        this.initLineNumberHelper(src, dst);
        this.changeFactory = new SourceCodeChangeFactory(mappings, lineNumberHelper);

        // change extraction
        refMillis = System.currentTimeMillis();
        codeChanges = new Vector<>();

        if (shouldClassify)
            classify(actions);


        long stopMillis = System.currentTimeMillis();

        this.metrics.setClassifyingTime(stopMillis - refMillis);
        this.metrics.setTotalTime(stopMillis - startMillis);

        this.metrics.setNumSrcNodes(srcContext.getRoot().getSize());
        this.metrics.setNumDstNodes(dstContext.getRoot().getSize());
        this.metrics.setNumActions(actions.size());
    }

    protected abstract void classify(List<Action> actions);

    //protected abstract void extractDependencies(ITree srcRoot, ITree dstRoot, List<Action> actions);

    public MappingStore getMappings() {
        return this.mappings;
    }

    public List<SourceCodeChange> getCodeChanges() {
        return this.codeChanges;
    }
    
    public ClassifierMetrics getMetrics() {
        return this.metrics;
    }

    public SourceCodeChangeFactory getChangeFactory() {
        return this.changeFactory;
    }

    public Boolean getDoTreeGeneration() {
        return doTreeGeneration;
    }

    public void setDoTreeGeneration(Boolean doTreeGeneration) {
        this.doTreeGeneration = doTreeGeneration;
    }

    public TreeContext getSrcContext() {
        return srcContext;
    }

    public void setSrcContext(TreeContext srcContext) {
        this.srcContext = srcContext;
    }

    public TreeContext getDstContext() {
        return dstContext;
    }

    public void setDstContext(TreeContext dstContext) {
        this.dstContext = dstContext;
    }

    protected void addCodeChange(SourceCodeChange change) {
        this.codeChanges.add(change);
    }

    public void initLineNumberHelper(String src, String dst) {
        this.lineNumberHelper = new LineNumberHelper(src,dst,mappings);
    }

    public LineNumberHelper getLineNumberHelper() {
        return lineNumberHelper;
    }

    public List<Action> getActions() {
        return actions;
    }
}
