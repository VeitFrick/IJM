package at.aau.softwaredynamics.diffws.service;

import at.aau.softwaredynamics.classifier.AbstractJavaChangeClassifier;
import at.aau.softwaredynamics.classifier.entities.CodeChangeTree;
import at.aau.softwaredynamics.classifier.entities.SourceCodeChange;
import at.aau.softwaredynamics.diffws.util.ChangePair;
import spoon.reflect.declaration.ModifierKind;

import java.util.ArrayList;
import java.util.List;

public class CommitMsgGenService {

    private List<ChangePair> changePairs;
    private AbstractJavaChangeClassifier classifier;

    public CommitMsgGenService(AbstractJavaChangeClassifier classifier) {
        this(new ArrayList<>(), classifier);
    }

    public CommitMsgGenService(List<ChangePair> pairs, AbstractJavaChangeClassifier classifier) {
        this.changePairs = pairs;
        this.classifier = classifier;
    }

    public void addChangePair(String src, String dst) {
        changePairs.add(new ChangePair(src, dst));
    }

    public void addChangePair(ChangePair cp) {
        changePairs.add(cp);
    }

    public String generateMessage(int depth) throws Exception {
        String msg = "";

        for (ChangePair cp : changePairs) {
            this.classifier.classify(cp.getSrc(), cp.getDst());

            //msg += "File: " + cp.getFilename() + "\n";
            for (SourceCodeChange scc : classifier.getCodeChanges()) {
                if(scc.getParentChanges().isEmpty())
                {
                    CodeChangeTree significantChildrenChanges = scc.getSignificantChildrenChanges();
                    significantChildrenChanges.expandSignificantNode(depth);
                    significantChildrenChanges.mergeEqualNodes();
                    significantChildrenChanges.hideChildrenOfSameActions();
                    // this deletes invisible nodes from the tree
                    significantChildrenChanges.pruneInvisible();

                    List<ModifierKind> modifierKinds = new ArrayList<>();
                    modifierKinds.add(ModifierKind.PRIVATE);
                    significantChildrenChanges.filterModifiers(modifierKinds);
                    msg += significantChildrenChanges.toTreeString();

                    msg += "\n\n";
                    break;
                }
            }

        }

        return msg;
    }
}
