package differ;

import com.github.gumtreediff.actions.model.Delete;
import com.github.gumtreediff.actions.model.Insert;
import com.github.gumtreediff.actions.model.Move;
import com.github.gumtreediff.actions.model.Update;
import com.github.gumtreediff.gen.TreeGenerator;
import com.github.gumtreediff.matchers.Matcher;

/**
 * Created by thomas on 19.12.2016.
 */
public class NonClassifyingDiffer extends AbstractJavaChangeDiffer {
    public NonClassifyingDiffer(Class<? extends Matcher> matcherType, TreeGenerator generator) {
        super(matcherType, generator);
    }

    @Override
    protected void diff(Insert action) {
        this.addResult(this.getChangeFactory().create(action));
    }

    @Override
    protected void diff(Delete action) {
        this.addResult(this.getChangeFactory().create(action));
    }

    @Override
    protected void diff(Update action) {
        this.addResult(this.getChangeFactory().create(action));
    }

    @Override
    protected void diff(Move action) {
        this.addResult(this.getChangeFactory().create(action));
    }
}
