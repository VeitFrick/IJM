package at.aau.softwaredynamics.classifier;

import com.github.gumtreediff.actions.model.Action;
import com.github.gumtreediff.gen.TreeGenerator;
import com.github.gumtreediff.matchers.Matcher;
import com.github.gumtreediff.tree.ITree;

import java.util.List;

/**
 * Created by thomas on 19.12.2016.
 */
public class NonClassifyingClassifier extends AbstractJavaChangeClassifier {
    public NonClassifyingClassifier(Class<? extends Matcher> matcherType, TreeGenerator generator) {
        super(matcherType, generator);
    }

    @Override
    protected void classify(List<Action> actions) {
        for (Action a : actions) {
            this.addCodeChange(this.getChangeFactory().create(a));
        }
    }

}
