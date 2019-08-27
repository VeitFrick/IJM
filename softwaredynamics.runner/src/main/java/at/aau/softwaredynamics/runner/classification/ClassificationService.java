package at.aau.softwaredynamics.runner.classification;

import at.aau.softwaredynamics.classifier.AbstractJavaChangeClassifier;
import at.aau.softwaredynamics.classifier.entities.ClassifierMetrics;
import at.aau.softwaredynamics.classifier.entities.SourceCodeChange;

import java.util.List;

public class ClassificationService {

    private final String src;
    private final String dst;
    private AbstractJavaChangeClassifier classifier;

    public ClassificationService(String src, String dst, AbstractJavaChangeClassifier classifier) {
        this.src = src;
        this.dst = dst;
        this.classifier = classifier;
    }

    public List<SourceCodeChange> classify() throws Exception {
        this.classifier.classify(this.src, this.dst);

        List<SourceCodeChange> changes = classifier.getCodeChanges();
        ClassifierMetrics metrics = classifier.getMetrics();

        return changes;
    }

}
