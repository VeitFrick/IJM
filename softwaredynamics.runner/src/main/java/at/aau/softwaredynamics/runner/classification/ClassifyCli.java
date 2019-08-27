package at.aau.softwaredynamics.runner.classification;

import at.aau.softwaredynamics.classifier.JChangeClassifier;
import at.aau.softwaredynamics.classifier.entities.CodeChangeTree;
import at.aau.softwaredynamics.classifier.entities.SourceCodeChange;
import at.aau.softwaredynamics.gen.SpoonTreeGenerator;
import at.aau.softwaredynamics.matchers.JavaMatchers;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.commons.cli.*;
import org.eclipse.egit.github.core.*;
import org.eclipse.egit.github.core.client.GitHubClient;
import org.eclipse.egit.github.core.service.CommitService;
import org.eclipse.egit.github.core.service.RepositoryService;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ClassifyCli {

    private static final Logger log = Logger.getLogger(ClassifyCli.class.getName());
    RepositoryService service;
    CommitService commitService;
    private String[] args = null;
    private Options options = new Options();
    private List<DiffPair> filePairs;
    private List<ImportablePair> importablePairs;
    GitHubClient client;
    private boolean dependencyMode = false;

    public ClassifyCli(String[] args) {

        this.args = args;

        Option repo = Option.builder("r")
                .longOpt("repo")
                .hasArg()
                .desc("use given GitHub Repo")
                .argName("REPO NAME")
                .build();

        Option commit = Option.builder("c")
                .longOpt("commit")
                .hasArg()
                .desc("Set commit hash to be analyzed")
                .argName("HASH")
                .build();

        Option commitParent = Option.builder("p")
                .longOpt("parent")
                .hasArg()
                .desc("(optional) Set commit hash of parent, first parent when omitted")
                .argName("HASH")
                .build();

        Option fileExt = Option.builder("e")
                .longOpt("ext")
                .hasArg()
                .desc("(optional) Set allowed file extension. (Default: java)")
                .argName("EXTENSION")
                .build();

        Option outputFormat = Option.builder("o")
                .longOpt("output")
                .hasArg()
                .desc("(optional) Defines the output format. Possible options are: \nsimple (default, with classification), \njson (no classification) \nmsg (commit message generator)")
                .argName("simple, json, msg")
                .build();

        Option depth = Option.builder("d")
                .longOpt("depth")
                .hasArg()
                .desc("only works with msg output format, specifies search depth of tree")
                .argName("DEPTH")
                .build();


        Option githubUser = Option.builder("u")
                .longOpt("user")
                .hasArg()
                .desc("(optional) Sets the github user for a higher API rate limit (set with password!)")
                .argName("USERNAME")
                .build();

        Option githubToken = Option.builder("p")
                .longOpt("password")
                .hasArg()
                .desc("(optional) Sets the github password for a higher API rate limit")
                .argName("PASSWORD")
                .build();

        Option mode = Option.builder("m")
                .longOpt("mode")
                .hasArg()
                .desc("(optional) specifies the mode used (default: classification mode)")
                .argName("MODE")
                .build();

        options.addOption("h", "help", false, "Show help.");
        options.addOption("a", "all", false, "Show all changes including added and removed files.");
        options.addOption(repo);
        options.addOption(commit);
        options.addOption(commitParent);
        options.addOption(fileExt);
        options.addOption(outputFormat);
        options.addOption(githubUser);
        options.addOption(githubToken);
        options.addOption(depth);
        options.addOption(mode);

        //Basic authentication

        client = new GitHubClient();

        filePairs = new ArrayList<>();
        importablePairs = new ArrayList<>();
    }

    public void parse() {
        CommandLineParser parser = new DefaultParser();

        CommandLine cmd = null;
        try {
            cmd = parser.parse(options, args);




            System.out.println("\n" +
                    "   _____ _               _  __          _____ _      _____ \n" +
                    "  / ____| |             (_)/ _|        / ____| |    |_   _|\n" +
                    " | |    | | __ _ ___ ___ _| |_ _   _  | |    | |      | |  \n" +
                    " | |    | |/ _` / __/ __| |  _| | | | | |    | |      | |  \n" +
                    " | |____| | (_| \\__ \\__ \\ | | | |_| | | |____| |____ _| |_ \n" +
                    "  \\_____|_|\\__,_|___/___/_|_|  \\__, |  \\_____|______|_____|\n" +
                    "                                __/ |                      \n" +
                    "                               |___/                       \n");

            if (cmd.hasOption("h"))
                help();

            String extFilter = "java";
            if (cmd.hasOption("e"))
                extFilter = cmd.getOptionValue('e');

            if (cmd.hasOption("u") && cmd.hasOption("p")) {
                client.setCredentials(cmd.getOptionValue("user"), cmd.getOptionValue("password"));
                service = new RepositoryService(client);
                commitService = new CommitService(client);
            } else {
                service = new RepositoryService();
                commitService = new CommitService();
            }

            if(cmd.hasOption("m") && (cmd.getOptionValue('m').equals("dep")||cmd.getOptionValue('m').equals("d")||cmd.getOptionValue('m').equals("dependency"))) {
                dependencyMode = true;
            }



            if (cmd.hasOption("r") && cmd.hasOption("c")) {


                // Init repo
                String optionRepo = cmd.getOptionValue("r");
                String optionCommit = cmd.getOptionValue("c");
                String optionParent = cmd.getOptionValue("p");

                String user = optionRepo.split("/")[0];
                String repo = optionRepo.split("/")[1];

                Repository repository = service.getRepository(user, repo);
                RepositoryCommit commit = null;
                RepositoryCommit parent = null;
                System.out.println("Running on repo: " + repository.getName() + " by " + repository.getOwner().getLogin());
                System.out.println(repository.getCloneUrl());

                // Get requested commit
                commit = commitService.getCommit(repository, optionCommit);

                // Get parent
                if(optionParent != null) parent = commitService.getCommit(repository, optionParent);
                else parent = getRepositoryCommit(repository, commit.getParents().get(0));

                JChangeClassifier jClassifier = new JChangeClassifier(
                        true,
                        JavaMatchers.IterativeJavaMatcher_Spoon.class,
                        new SpoonTreeGenerator());
                jClassifier.setIncludeMetaChanges(true);


                // Download source code
                for (CommitFile file : commit.getFiles()) {
                    if(!file.getFilename().endsWith("." +  extFilter)) continue;
                    // Add a new filepair with SRC and DST
                    DiffPair dp = new DiffPair(file.getFilename(),
                            downloadFrom(rawURLOfParent(file, commit, parent)),
                            downloadFrom(file.getRawUrl()));
                    dp.setGitStatus(file.getStatus());

                    filePairs.add(dp);

                    if(cmd.hasOption("o") && cmd.getOptionValue('o').equals("json")) {
                        importablePairs.add(new ImportablePair(
                                dp.hashCode(),
                                "http://github.com/" + user + "/" + repo,
                                commit.getSha(),
                                parent.getSha(),
                                dp.getSrcFileName(),
                                dp.getDstFileName()
                        ));
                    }
                }
               if(dependencyMode) {

                   /**
                    * Do Dependency Stuff
                    */


//                        for (DiffPair pair : filePairs) {
//                            jClassifier.classify(pair.getSrcContent(), pair.getDstContent());
//                            if (pair.getGitStatus().equals("modified") || (!pair.getGitStatus().equals("renamed") && cmd.hasOption("a"))) {
//                                System.out.println("------------------------------------------");
//                                System.out.println("FILE: " + pair.getSrcFileName());
//                                System.out.println("STATUS: " + pair.getGitStatus());
//                                for (SourceCodeChange scc : jClassifier.getCodeChanges()) {
//                                    //if(scc.getActionTypeName() == "MET") continue;
//
//                                    if(scc.getParentChanges().isEmpty())
//                                    {
//                                        System.out.println(scc.toOrderedString(0,3));
//                                    }
//
//                                }
//                                System.out.println("------------------------------------------");
//
//                            } else if (pair.getGitStatus().equals("renamed")) {
//                                System.out.println("------------------------------------------");
//                                System.out.println("FILE: " + pair.getSrcFileName() + " ~> " + pair.getDstFileName());
//                                System.out.println("Renames not supported...");
//                                System.out.println("------------------------------------------");
//                            } else {
//                                System.out.println("------------------------------------------");
//                                System.out.println("FILE: " + pair.getSrcFileName());
//                                System.out.println("STATUS: " + pair.getGitStatus());
//                                System.out.println("Changes omitted because of status. Run with --all to see.");
//                                System.out.println("------------------------------------------");
//                            }
//                        }

                }
                else{
                    if(!cmd.hasOption("o") || cmd.getOptionValue('o').equals("simple") || cmd.getOptionValue('o') == null) {
                        for (DiffPair pair : filePairs) {
                            jClassifier.classify(pair.getSrcContent(), pair.getDstContent());
                            if (pair.getGitStatus().equals("modified") || (!pair.getGitStatus().equals("renamed") && cmd.hasOption("a"))) {
                                System.out.println("------------------------------------------");
                                System.out.println("FILE: " + pair.getSrcFileName());
                                System.out.println("STATUS: " + pair.getGitStatus());
                                for (SourceCodeChange scc : jClassifier.getCodeChanges()) {
                                    //if(scc.getActionTypeName() == "MET") continue;

                                    if(scc.getParentChanges().isEmpty())
                                    {
                                        System.out.println(scc.toOrderedString(0,3));
                                    }

                                }
                                System.out.println("------------------------------------------");

                            } else if (pair.getGitStatus().equals("renamed")) {
                                System.out.println("------------------------------------------");
                                System.out.println("FILE: " + pair.getSrcFileName() + " ~> " + pair.getDstFileName());
                                System.out.println("Renames not supported...");
                                System.out.println("------------------------------------------");
                            } else {
                                System.out.println("------------------------------------------");
                                System.out.println("FILE: " + pair.getSrcFileName());
                                System.out.println("STATUS: " + pair.getGitStatus());
                                System.out.println("Changes omitted because of status. Run with --all to see.");
                                System.out.println("------------------------------------------");
                            }
                        }
                    } else if (cmd.getOptionValue('o').equals("json")) {
                        System.out.println("JSON Output");

                        ObjectMapper mapper = new ObjectMapper();

                        //Object to JSON in String
                        String jsonInString = mapper.writeValueAsString(importablePairs);
                        System.out.println(jsonInString);
                    } else if (cmd.getOptionValue('o').equals("msg")) {
                        System.out.println("Commit msg output");
                        //jClassifier.setIncludeMetaChanges(false);

                        Integer depth = cmd.getOptionValue('d') != null ? Integer.valueOf(cmd.getOptionValue('d')) : 1;

                        StringBuilder msg = new StringBuilder();
                        for (DiffPair dp : filePairs) {
                            jClassifier.classify(dp.getSrcContent(), dp.getDstContent());

                            msg.append("File: ").append(dp.getSrcFileName()).append("\n");
                            for (SourceCodeChange scc : jClassifier.getCodeChanges()) {
                                if (scc.getParentChanges().isEmpty()) {
                                    CodeChangeTree significantChildrenChanges = scc.getSignificantChildrenChanges();
                                    significantChildrenChanges.expandSignificantNode(depth);
                                    significantChildrenChanges.mergeEqualNodes();
                                    msg.append(significantChildrenChanges.toTreeString());
                                    //System.out.println(scc.toOrderedString(0,3));
                                    msg.append("\n\n");
                                    break;
                                }
                            }


                        }
                        System.out.println(msg.toString());
                    }

                }





            } else {
                log.log(Level.SEVERE, "Missing --repo or --commit option!");
                help();
            }

        } catch (ParseException e) {
            log.log(Level.SEVERE, "Failed to parse comand line properties", e);
            help();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void help() {
        // This prints out some help
        HelpFormatter formatter = new HelpFormatter();

        System.out.println("ClassifyCommit CLI can classify all files affected in a commit using IJM.");
        formatter.printHelp("ClassifyCommit CLI", options);
        System.exit(0);
    }

    private void printCommit(RepositoryCommit commit) throws IOException {
        System.out.println("Author: " + commit.getCommitter().getEmail());
        System.out.println("SHA: " + commit.getSha());
        System.out.println("Parents:");
        for (Commit parent : commit.getParents()) {
            System.out.println(parent.getSha());
            System.out.println(parent.getMessage());
        }
        System.out.println("Files:");
        for (CommitFile file : commit.getFiles()) {
            System.out.println(file.getFilename());
            System.out.println(file.getBlobUrl());
            System.out.println(file.getRawUrl());

            System.out.println(downloadFrom(file.getRawUrl()));
        }
    }

    private RepositoryCommit getRepositoryCommit(IRepositoryIdProvider repo, Commit commit) throws IOException {
        return commitService.getCommit(repo, commit.getSha());
    }

    private String downloadFrom(String url) throws IOException {
        URL u = new URL(url);
        try (InputStream in = u.openStream()) {
            return new String(in.readAllBytes(), StandardCharsets.UTF_8);
        } catch (FileNotFoundException e) {
            return "";
        }
    }

    private String rawURLOfParent(CommitFile f, RepositoryCommit toSwap, RepositoryCommit parent) {
        return f.getRawUrl().replace(toSwap.getSha(), parent.getSha());
    }
}
