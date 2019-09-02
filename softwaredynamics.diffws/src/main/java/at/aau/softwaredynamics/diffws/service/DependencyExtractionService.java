package at.aau.softwaredynamics.diffws.service;

import at.aau.softwaredynamics.classifier.AbstractJavaChangeClassifier;
import at.aau.softwaredynamics.classifier.entities.ClassifierMetrics;
import at.aau.softwaredynamics.classifier.entities.SourceCodeChange;
import at.aau.softwaredynamics.classifier.entities.SourceCodeChangeFactory;
import at.aau.softwaredynamics.dependency.DependencyExtractor;
import at.aau.softwaredynamics.dependency.DependencyPairContainer;
import at.aau.softwaredynamics.dependency.NodeDependency;
import at.aau.softwaredynamics.dependency.meta.DependencySourceCodeNode;
import at.aau.softwaredynamics.diffws.domain.ClassificationResult;
import at.aau.softwaredynamics.diffws.domain.Metadata;
import at.aau.softwaredynamics.diffws.domain.Metrics;
import at.aau.softwaredynamics.diffws.domain.Result;
import at.aau.softwaredynamics.util.Meta;

import java.util.ArrayList;
import java.util.List;

public class DependencyExtractionService {

    private final String src;
    private final String dst;
    private AbstractJavaChangeClassifier classifier;
    private DependencyExtractor dependencyExtractor;

    public DependencyExtractionService(String src, String dst, AbstractJavaChangeClassifier classifier) {
        this.src = src;
        this.dst = dst;
        this.classifier = classifier;
    }

    public ClassificationResult classify() throws Exception {
        this.classifier.classify(src,dst);
        dependencyExtractor = new DependencyExtractor(classifier.getMappings(),classifier.getActions(),classifier.getSrcContext().getRoot(),classifier.getDstContext().getRoot(),src,dst);
        dependencyExtractor.extractDependencies();

        SourceCodeChangeFactory factory = classifier.getChangeFactory();
        List<DependencySourceCodeNode> metaDependencyNodes = new ArrayList<>();

        DependencyPairContainer depStruct = dependencyExtractor.getDependencyChanges().getDepStruct();

        /* Moved Code From Classifier */
        if (depStruct != null && depStruct.getRootStrucSrc() != null) { // we need to have a source file to show its dependencies (duh)
            for (NodeDependency dependency : depStruct.getRootStrucSrc().getDependencies()) {
                Meta meta = new Meta(dependency.getNode(), true, false);
                SourceCodeChange scc = factory.create(meta);
                DependencySourceCodeNode dep = new DependencySourceCodeNode(scc.getAction(), scc.getSrcInfo(), scc.getDstInfo());
                dep.addDependency(dependency);
                dependency.setLineNumbers(dep.getSrcInfo().getLineNumberRange());
                metaDependencyNodes.add(dep);
            }
        }

        if (depStruct != null && depStruct.getRootStrucDst() != null) { // we need to have a destination file to show its dependencies (duh)
            for (NodeDependency dependency : depStruct.getRootStrucDst().getDependencies()) {
                Meta meta = new Meta(dependency.getNode(), false, true);
                SourceCodeChange scc = factory.create(meta);
                DependencySourceCodeNode dep = new DependencySourceCodeNode(scc.getAction(), scc.getSrcInfo(), scc.getDstInfo());
                dep.addDependency(dependency);
                dependency.setLineNumbers(dep.getDstInfo().getLineNumberRange());
                metaDependencyNodes.add(dep);
            }
        }

        ClassifierMetrics metrics = classifier.getMetrics();

        return createResult(metaDependencyNodes, metrics);
    }

    private ClassificationResult createResult(List<DependencySourceCodeNode> deps, ClassifierMetrics metrics) {
        List<Result> resultList = new ArrayList<>();

        for (DependencySourceCodeNode dependency : deps) {
            String actionType = "DEPENDENCY";

            Result res = new Result(
                    actionType,
                    dependency.getSrcInfo().getId(),
                    dependency.getDstInfo().getId()
            );

            res.setSrcRelativePosition(
                    dependency.getSrcInfo().getStartLineNumber(),
                    dependency.getSrcInfo().getStartOffset(),
                    dependency.getSrcInfo().getEndLineNumber(),
                    dependency.getSrcInfo().getEndOffset()
            );

            res.setDstRelativePosition(
                    dependency.getDstInfo().getStartLineNumber(),
                    dependency.getDstInfo().getStartOffset(),
                    dependency.getDstInfo().getEndLineNumber(),
                    dependency.getDstInfo().getEndOffset()
            );
            Metadata meta = new Metadata();

            try {
                meta.setDependencies(dependency.getDependencies().toString()); //TODO FIX THIS IS BROKEN

            } catch (Exception e) {
                System.out.println("WARNING: Could not set dependency.");
            }

            res.setMetadata(meta);
            resultList.add(res);
        }


        Metrics metricsDto = new Metrics();
        metricsDto.setMatchingTime(metrics.getMatchingTime());
        metricsDto.setClassificationTime(metrics.getClassifyingTime());

        return new ClassificationResult(metricsDto, resultList);
    }
}
