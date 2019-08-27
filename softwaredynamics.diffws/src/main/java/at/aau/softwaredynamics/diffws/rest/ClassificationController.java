package at.aau.softwaredynamics.diffws.rest;

import at.aau.softwaredynamics.classifier.JChangeClassifier;
import at.aau.softwaredynamics.classifier.NonClassifyingClassifier;
import at.aau.softwaredynamics.diffws.domain.ClassificationResult;
import at.aau.softwaredynamics.diffws.service.ClassificationService;
import at.aau.softwaredynamics.diffws.service.CommitMsgGenService;
import at.aau.softwaredynamics.diffws.util.ChangePair;
import at.aau.softwaredynamics.diffws.util.MatcherRegistry;
import at.aau.softwaredynamics.diffws.util.TreeGeneratorFactory;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.gumtreediff.matchers.Matcher;
import org.apache.commons.codec.binary.Base64;
import org.springframework.boot.json.BasicJsonParser;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;

import static java.lang.Math.toIntExact;

@RestController
@RequestMapping("v1/changes")
public class ClassificationController {
    private final MatcherRegistry matcherRegistry;
    private final TreeGeneratorFactory treeGeneratorFactory;

    public ClassificationController() {
        this.matcherRegistry = new MatcherRegistry();
        this.treeGeneratorFactory = new TreeGeneratorFactory();
    }

    @CrossOrigin
    @RequestMapping(method = RequestMethod.POST)
    public ClassificationResult classify(@RequestBody String payload) throws Exception {
        HashMap<String, Object> inputMap = (HashMap<String, Object>) new BasicJsonParser().parseMap(payload);

        //Assuming src and dst objects are base64 encoded
        String src = new String(Base64.decodeBase64(inputMap.get("src").toString().getBytes()));
        String dst = new String(Base64.decodeBase64(inputMap.get("dst").toString().getBytes()));
        Integer matcherId = toIntExact((Long) inputMap.get("matcher"));

        // get matcher class
        Class<? extends Matcher> matcherClass = matcherRegistry.getMatcherTypeFor(matcherId);

        // TODO (Thomas): make classifier configurable
        NonClassifyingClassifier classifier = new NonClassifyingClassifier(
                matcherClass,
                treeGeneratorFactory.createDefaultGeneratorFor(matcherClass));

        JChangeClassifier jClassifier = new JChangeClassifier(true,
                matcherClass,
                treeGeneratorFactory.createDefaultGeneratorFor(matcherClass));
        jClassifier.setIncludeMetaChanges(true);

        ClassificationService service =
                new ClassificationService(src, dst, jClassifier);

        return service.classify();
    }

    /**
     * Generates a commit message for a list of java source code pairs
     *
     * @param payload
     * @return Commit message
     * @throws Exception
     */
    @CrossOrigin
    @RequestMapping(value = "msg", method = RequestMethod.POST)
    public String generateCommitMsg(@RequestBody String payload) throws Exception {

        ObjectMapper mapper = new ObjectMapper();
        //JSON from String to Object
        LinkedHashMap<String, Object> inputMap = (LinkedHashMap<String, Object>) mapper.readValue(payload, Object.class);
        Integer matcherId;
        try {
            matcherId = toIntExact((Integer) inputMap.get("matcher"));
        } catch (ClassCastException cce) {
            return "Invalid Matcher ID.";
        }
        Integer depth = inputMap.get("depth") != null ? toIntExact((Integer) inputMap.get("depth")) : 0;
        List<HashMap> pairsList = (List<HashMap>) inputMap.get("data");
        // get matcher class
        Class<? extends Matcher> matcherClass = matcherRegistry.getMatcherTypeFor(matcherId);

        JChangeClassifier jClassifier = new JChangeClassifier(true,
                matcherClass,
                treeGeneratorFactory.createDefaultGeneratorFor(matcherClass));
        jClassifier.setIncludeMetaChanges(true);

        CommitMsgGenService service = new CommitMsgGenService(jClassifier);
        for (HashMap pair : pairsList) {

            String src = new String(Base64.decodeBase64(pair.get("src").toString().getBytes()));
            String dst = new String(Base64.decodeBase64(pair.get("dst").toString().getBytes()));

            service.addChangePair(new ChangePair(
                    (String) pair.get("filename"),
                    src,
                    dst));
        }
        return service.generateMessage(depth);
    }


}
