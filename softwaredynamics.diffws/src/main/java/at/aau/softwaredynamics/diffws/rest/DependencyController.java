package at.aau.softwaredynamics.diffws.rest;

import at.aau.softwaredynamics.classifier.JChangeClassifier;
import at.aau.softwaredynamics.classifier.NonClassifyingClassifier;
import at.aau.softwaredynamics.dependency.meta.DependencyNetwork;
import at.aau.softwaredynamics.diffws.domain.ClassificationResult;
import at.aau.softwaredynamics.diffws.domain.ProjectInfo;
import at.aau.softwaredynamics.diffws.service.DependencyExtractionService;
import at.aau.softwaredynamics.diffws.util.MatcherRegistry;
import at.aau.softwaredynamics.diffws.util.TreeGeneratorFactory;
import at.aau.softwaredynamics.runner.git.FullProjectAnalyzer;
import at.aau.softwaredynamics.runner.git.SnapshotGenerator;
import at.aau.softwaredynamics.runner.output.WebserviceOutput;
import com.github.gumtreediff.matchers.Matcher;
import org.apache.commons.codec.binary.Base64;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.revwalk.RevCommit;
import org.springframework.boot.json.BasicJsonParser;
import org.springframework.web.bind.annotation.*;

import java.io.File;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

import static java.lang.Math.toIntExact;

@RestController
@RequestMapping("v1/dependencies")
public class DependencyController {
    private final MatcherRegistry matcherRegistry;
    private final TreeGeneratorFactory treeGeneratorFactory;

    public DependencyController() {
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

        DependencyExtractionService service =
                new DependencyExtractionService(src, dst, jClassifier);

        return service.classify();
    }

    /**
     * Classifies a whole Git project that is currently local to the server machine
     * Importing must be implemented!
     *
     * @param payload path to the git repo
     * @throws Exception
     */
    @CrossOrigin
    @RequestMapping(path = "project", method = RequestMethod.POST)
    public List<DependencyNetwork> classifyProject(@RequestBody String payload) throws Exception {
        HashMap<String, Object> inputMap = (HashMap<String, Object>) new BasicJsonParser().parseMap(payload);
        String gitPath = (String) inputMap.get("path");
        String singleCommit = (String) inputMap.get("commit");
        String module = inputMap.get("module") != null ? (String) inputMap.get("module") : "";
        if (singleCommit != null && singleCommit.equals("")) singleCommit = null;

        FullProjectAnalyzer fullProjectAnalyzer = new FullProjectAnalyzer(gitPath, module, "refs/heads/master", singleCommit, true);

        // only diff main project, no submodules TODO maybe change to preference
        fullProjectAnalyzer.setOutputWriter(new WebserviceOutput());
        fullProjectAnalyzer.analyzeLocalRepo();

        WebserviceOutput webserviceOutput = (WebserviceOutput) fullProjectAnalyzer.getOutputWriter();
        List<DependencyNetwork> outputNetworks = new ArrayList<>(webserviceOutput.getNetworks().values());
        outputNetworks.sort(Comparator.comparing(DependencyNetwork::getCommitTime)); // TODO insert commit time!
        return outputNetworks;
    }


    /**
     * Classifies a single commit of a project
     *
     * @param payload JSON input
     */
    @CrossOrigin
    @RequestMapping(path = "depnet", method = RequestMethod.POST)
    public List<DependencyNetwork> serveDependencyNetworksOfProjectAtCommit(@RequestBody String payload) throws Exception {
        HashMap<String, Object> inputMap = (HashMap<String, Object>) new BasicJsonParser().parseMap(payload);
        String gitPath = (String) inputMap.get("path");
        String commit = (String) inputMap.get("commit");
        String module = inputMap.get("module") != null ? (String) inputMap.get("module") : "";

        Git git = Git.open(new File(gitPath + File.separatorChar + ".git"));
        Repository repository = git.getRepository();
        SnapshotGenerator snapshotGenerator = new SnapshotGenerator(repository, "refs/head/master", module, new HashMap<>());

        // TODO implement

        WebserviceOutput webserviceOutput = (WebserviceOutput) null; //TODO fill this in
        List<DependencyNetwork> outputNetworks = new ArrayList<>(webserviceOutput.getNetworks().values());
        outputNetworks.sort(Comparator.comparing(DependencyNetwork::getCommitTime)); // TODO insert commit time!
        return outputNetworks;
    }

    @CrossOrigin
    @RequestMapping(path = "project/info", method = RequestMethod.POST)
    public ProjectInfo projectInformation(@RequestBody String payload) throws Exception {
        HashMap<String, Object> inputMap = (HashMap<String, Object>) new BasicJsonParser().parseMap(payload);
        String gitPath = (String) inputMap.get("path");
        ProjectInfo projectInfo = new ProjectInfo(gitPath);
//        String singleCommit = (String) inputMap.get("commit");
//        if (singleCommit != null && singleCommit.equals("")) singleCommit = null;

        FullProjectAnalyzer fullProjectAnalyzer = new FullProjectAnalyzer(gitPath, "", "refs/heads/master", null, true);

        projectInfo.setModules(fullProjectAnalyzer.getAllModulePaths());
        projectInfo.setCommitHashes(fullProjectAnalyzer.getCommits().stream().map(RevCommit::getName).collect(Collectors.toList()));
        return projectInfo;
    }

}
