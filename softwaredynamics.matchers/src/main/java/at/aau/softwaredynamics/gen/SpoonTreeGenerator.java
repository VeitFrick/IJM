package at.aau.softwaredynamics.gen;

import com.github.gumtreediff.gen.TreeGenerator;
import com.github.gumtreediff.tree.ITree;
import com.github.gumtreediff.tree.Pair;
import com.github.gumtreediff.tree.TreeContext;
import org.apache.commons.io.IOUtils;
import spoon.SpoonModelBuilder;
import spoon.compiler.SpoonResourceHelper;
import spoon.reflect.CtModel;
import spoon.reflect.declaration.CtElement;
import spoon.reflect.declaration.CtPackage;
import spoon.reflect.factory.Factory;
import spoon.reflect.factory.FactoryImpl;
import spoon.support.DefaultCoreFactory;
import spoon.support.StandardEnvironment;
import spoon.support.compiler.VirtualFile;
import spoon.support.compiler.jdt.JDTBasedSpoonCompiler;

import java.io.File;
import java.io.IOException;
import java.io.Reader;

public class SpoonTreeGenerator extends TreeGenerator {
    private final Factory factory;

    public SpoonTreeGenerator() {
        this(new FactoryImpl(new DefaultCoreFactory(), new StandardEnvironment()));
    }

    public SpoonTreeGenerator(Factory factory) {
        this.factory = factory;
        factory.getEnvironment().setNoClasspath(true);
    }

    /**
     *  gets Tree for File
     */
    public Pair<TreeContext, ITree> getTree(File f1) throws Exception {
        return this.getTree(getCtType(f1));
    }

    /**
     * gets Tree for String
     */
    public Pair<TreeContext, ITree> getTree(String left) {
        return getTree(getCtType(left));
    }

    /**
     * gets Tree for String
     */
    public Pair<TreeContext, ITree> getTree(CtModel model) {
        return getTree(model.getRootPackage());
    }

    /**
     * gets Tree for CtElement
     */
    public Pair<TreeContext, ITree> getTree(CtElement left) {
        final SpoonBuilder scanner = new SpoonBuilder();
        scanner.getTreeContext();
        return new Pair<>(scanner.getTreeContext(), scanner.getTree(left));
    }

    private CtPackage getCtType(File file) throws Exception {
        SpoonModelBuilder compiler = new JDTBasedSpoonCompiler(factory);
        compiler.getFactory().getEnvironment().setLevel("OFF");
        compiler.addInputSource(SpoonResourceHelper.createResource(file));
        compiler.build();

        if (factory.Type().getAll().size() == 0) {
            return null;
        }

        return factory.getModel().getRootPackage();
        //return factory.Type().getAll().get(0);
    }

    private CtPackage getCtType(String content) {
        SpoonModelBuilder compiler = new JDTBasedSpoonCompiler(factory);
        compiler.addInputSource(new VirtualFile(content, "/test"));
        compiler.build();
        return factory.getModel().getRootPackage();
    }

    private TreeContext getTreeContext(String input) {

        Pair<TreeContext, ITree> treePair = new SpoonTreeGenerator().getTree(input);
        ITree tree = treePair.getSecond();
        TreeContext cxt = treePair.getFirst();
        cxt.setRoot(tree);
        return cxt;
    }

    @Override
    public TreeContext generate(Reader reader) throws IOException {
        String input = IOUtils.toString(reader);
        return this.getTreeContext(input);
    }

    @Override
    public TreeContext generateFromReader(Reader r) throws IOException {
        TreeContext ctx = this.generate(r);
        return ctx;
    }
}
