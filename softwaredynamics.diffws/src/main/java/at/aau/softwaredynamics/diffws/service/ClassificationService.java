package at.aau.softwaredynamics.diffws.service;

import at.aau.softwaredynamics.classifier.AbstractJavaChangeClassifier;
import at.aau.softwaredynamics.classifier.entities.ClassifierMetrics;
import at.aau.softwaredynamics.classifier.entities.SourceCodeChange;
import at.aau.softwaredynamics.diffws.domain.ClassificationResult;
import at.aau.softwaredynamics.diffws.domain.Metadata;
import at.aau.softwaredynamics.diffws.domain.Metrics;
import at.aau.softwaredynamics.diffws.domain.Result;
import at.aau.softwaredynamics.diffws.util.SpoonMetadataScraper;
import at.aau.softwaredynamics.util.Meta;

import java.util.ArrayList;
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

    public ClassificationResult classify() throws Exception {
        this.classifier.classify(this.src, this.dst);

        List<SourceCodeChange> changes = classifier.getCodeChanges();
        ClassifierMetrics metrics = classifier.getMetrics();

        return createResult(changes, metrics);
    }

    private ClassificationResult createResult(List<SourceCodeChange> changes, ClassifierMetrics metrics) {
        List<Result> resultList = new ArrayList<>();
        for (SourceCodeChange change : changes) {
            String actionType = ClassificationService.getActionName(change);

            Result res = new Result(
                    actionType,
                    change.getSrcInfo().getId(),
                    change.getDstInfo().getId()
            );

            res.setSrcRelativePosition(
                    change.getSrcInfo().getStartLineNumber(),
                    change.getSrcInfo().getStartOffset(),
                    change.getSrcInfo().getEndLineNumber(),
                    change.getSrcInfo().getEndOffset()
            );

            res.setDstRelativePosition(
                    change.getDstInfo().getStartLineNumber(),
                    change.getDstInfo().getStartOffset(),
                    change.getDstInfo().getEndLineNumber(),
                    change.getDstInfo().getEndOffset()
            );

            //Building and adding metadata
            Metadata meta = new Metadata();
            try {
                //meta.setNodeType(new NodeTypeInfo(NodeType.getEnum(change.getNodeType())));
                meta.setChangeType(change.getChangeType().toString());

            } catch (Exception e) {
                System.out.println("WARNING: Could not set change type...");
            }

            SpoonMetadataScraper smsSrc = new SpoonMetadataScraper(change.getSrcInfo().getSpoonObject());
            SpoonMetadataScraper smsDst = new SpoonMetadataScraper(change.getDstInfo().getSpoonObject());

            meta.setSpoonSrc(smsSrc.generateMetadataMap());
            meta.setSpoonDst(smsDst.generateMetadataMap());

            if(change.getSrcInfo().getNode() != null) meta.setLabelSrc(change.getSrcInfo().getNode().getLabel());
            if(change.getDstInfo().getNode() != null) meta.setLabelDst(change.getDstInfo().getNode().getLabel());

            if(change.getAction() instanceof Meta) {
                meta.setChildrenChangeInfo(SourceCodeChange.changeTypeDump(change.getChildrenChanges()));
                meta.setParentChangeInfo(SourceCodeChange.changeTypeDump(change.getParentChanges()));
            }

            res.setMetadata(meta);


            //TODO (Christoph) check for invalid results (-1 as position) and filter them!
            resultList.add(res);
        }

        Metrics metricsDto = new Metrics();
        metricsDto.setMatchingTime(metrics.getMatchingTime());
        metricsDto.setClassificationTime(metrics.getClassifyingTime());

        return new ClassificationResult(metricsDto, resultList);
    }

    private static String getActionName(SourceCodeChange change) {
        switch (change.getAction().getName()) {
            case "INS": return "INSERT";
            case "DEL": return "DELETE";
            case "UPD": return "UPDATE";
            case "MOV": return "MOVE";
            case "MET": return "META";
            default: return null;
        }
    }

}
