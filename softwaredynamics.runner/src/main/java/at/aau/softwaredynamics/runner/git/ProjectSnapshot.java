package at.aau.softwaredynamics.runner.git;

import at.aau.softwaredynamics.classifier.JChangeClassifier;
import at.aau.softwaredynamics.classifier.entities.SourceCodeChange;
import at.aau.softwaredynamics.gen.SpoonTreeGenerator;
import at.aau.softwaredynamics.matchers.JavaMatchers;
import com.github.gumtreediff.tree.ITree;
import com.github.gumtreediff.tree.TreeContext;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;
import org.eclipse.jgit.revwalk.RevCommit;
import spoon.reflect.cu.CompilationUnit;
import spoon.reflect.declaration.CtType;
import spoon.reflect.factory.Factory;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

class ProjectSnapshot {
    String projectPath;
    RevCommit commit;
    Factory spoonFactory;
    Map<String, String> fileToContentMap;
    Map<String, List<CtType<?>>> fileToTypesMap;
    List<Pair<String, String>> fileRenames;

    public ProjectSnapshot(RevCommit commit, Factory spoonFactory) {
        this.commit = commit;
        this.spoonFactory = spoonFactory;
        this.fileToTypesMap = new HashMap<>();
        this.fileRenames = new ArrayList<>();
        this.fileToContentMap = new HashMap<>();

        if (spoonFactory != null) {
            Map<String, CompilationUnit> compilationUnitMap = spoonFactory.CompilationUnit().getMap();

            for (Map.Entry<String, CompilationUnit> entry : compilationUnitMap.entrySet()) {

                String filePath = entry.getKey().replace("\\", "/");
                CompilationUnit cu = entry.getValue();
                fileToTypesMap.put(filePath, cu.getDeclaredTypes());
            }
        }
    }

    public String getProjectPath() {
        return projectPath;
    }

    public void setProjectPath(String projectPath) {
        this.projectPath = projectPath;
    }

    public RevCommit getCommit() {
        return commit;
    }

    public RevCommit getParent() {
        if (commit.getParents().length == 0) return null;
        return commit.getParent(0);
    }

    public boolean isRoot() {
        return getParent() == null;
    }

    private List<CtType<?>> getTypesForFilename(String filename) {
        return fileToTypesMap.getOrDefault(filename, new ArrayList<>());
    }

    public Map<String, String> getFileToContentMap() {
        return fileToContentMap;
    }

    @Override
    public String toString() {
        String fileMapping = "";
        for (Map.Entry<String, List<CtType<?>>> entry : fileToTypesMap.entrySet()) {
            String file = entry.getKey();
            List<CtType<?>> types = entry.getValue();
            String typeString = "";
            for (CtType<?> type : types) {
                typeString += type.getSimpleName() + ", ";
            }

            fileMapping += file + " => " + typeString + "\n";
        }
        String renamesString = "";
        if (fileRenames.size() > 0) {
            renamesString = "Renames: ";
            for (Pair<String, String> rename : fileRenames) {
                renamesString += rename.toString() + ", ";
            }
        }
        return commit.getId().getName() + "\n" + fileMapping + renamesString + "\n";
    }

    public Map<String, List<SourceCodeChange>> calculateSourceCodeChangesBetweenSnapshots(ProjectSnapshot parent) {

        Map<String, List<SourceCodeChange>> sourceCodeChangesPerFile = new HashMap<>();
        if (parent == null) return sourceCodeChangesPerFile; // TODO maybe consider all added?

        if (!isRoot()) { // has a parent
            fileToTypesMap.forEach((filename, types) -> {
                Pair<String, String> fileTuple = fileRenames.stream()
                        .filter(f -> f.getRight().equals(filename))
                        .findFirst().orElseGet(() -> new ImmutablePair<>(filename, filename));

                CtType newTopType = null;
                CtType oldTopType = null;

                // get new top type
                for (CtType<?> type : types) {
                    if (type.isTopLevel()) {
                        newTopType = type;
                        break;
                    }
                }

                // get the old top type
                for (CtType<?> type : parent.getTypesForFilename(fileTuple.getLeft())) {
                    if (type.isTopLevel()) {
                        oldTopType = type;
                        break;
                    }
                }

                if (oldTopType == null && newTopType == null) {
                    return;
                }

                SpoonTreeGenerator spoonTreeGenerator = new SpoonTreeGenerator();
                com.github.gumtreediff.tree.Pair<TreeContext, ITree> pair = spoonTreeGenerator.getTree(newTopType);
                TreeContext newContext = pair.first;
                newContext.setRoot(pair.second);

                com.github.gumtreediff.tree.Pair<TreeContext, ITree> oldPair;
                if (oldTopType != null)
                    oldPair = spoonTreeGenerator.getTree(oldTopType);
                else
                    oldPair = spoonTreeGenerator.getTree("");
                TreeContext oldContext = oldPair.first;
                oldContext.setRoot(oldPair.second);

                JChangeClassifier classifier = new JChangeClassifier(JavaMatchers.IterativeJavaMatcher_Spoon.class, null);
                classifier.setDoTreeGeneration(false);

                classifier.setSrcContext(oldContext);
                classifier.setDstContext(newContext);

                String src, dst;
                src = parent.getFileToContentMap().get(fileTuple.getLeft());
                if (src == null) src = "";
                dst = fileToContentMap.get(filename);

                try {
                    classifier.classify(src, dst, true);
                    if (!classifier.getCodeChanges().isEmpty())
                        sourceCodeChangesPerFile.put(filename, classifier.getCodeChanges());
                } catch (Exception e) {
                    e.printStackTrace();
                }

            });

        }
        return sourceCodeChangesPerFile;
    }
}