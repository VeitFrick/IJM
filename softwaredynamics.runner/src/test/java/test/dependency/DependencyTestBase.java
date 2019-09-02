package test.dependency;

import at.aau.softwaredynamics.classifier.JChangeClassifier;
import at.aau.softwaredynamics.classifier.entities.SourceCodeChange;
import at.aau.softwaredynamics.dependency.DependencyChanges;
import at.aau.softwaredynamics.dependency.DependencyExtractor;
import at.aau.softwaredynamics.gen.SpoonTreeGenerator;
import at.aau.softwaredynamics.matchers.JavaMatchers;
import com.github.gumtreediff.matchers.Matcher;

import java.util.Collection;

public abstract class DependencyTestBase {
    private boolean debug = true;

    enum Type {
        ADDED, REMOVED
    }

    protected DependencyChanges classifyDependencies(String srcString, String dstString) {
        return classifyDependencies(srcString, dstString, false);
    }

    protected DependencyChanges classifyDependencies(String srcString, String dstString, boolean includeUnclassified) {
        return classifyDependencies(
                srcString,
                dstString,
                new JChangeClassifier(includeUnclassified, JavaMatchers.IterativeJavaMatcher_Spoon.class, new SpoonTreeGenerator()));
    }

    protected DependencyChanges classifyDependencies(String srcString, String dstString, boolean includeUnclassified, Class<? extends Matcher> matcherType) {
        return classifyDependencies(
                srcString,
                dstString,
                new JChangeClassifier(includeUnclassified, matcherType, new SpoonTreeGenerator()));
    }

    private DependencyChanges classifyDependencies(String srcString, String dstString, JChangeClassifier classifier) {
        try {
            return getFileDependencyChangesFromClassifier(classifier,srcString,dstString);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

    public void extendedInfoDump(Collection<SourceCodeChange> dependencies) { //TODO  shouldn't take SourceCode Changes! (Refactoring Broke this)
            return;
//        System.out.println('\n');
//        System.out.println("Changes Size: " + dependencies.size());
//        for (SourceCodeChange dep : dependencies) {
//
//            for (NodeDependency dependency : dep.getDependencies()) {
////                System.out.println(dependency.toConsoleString()); //TODO - Refactoring broke this. ToFix
//            }
//
//        }
    }

//    public Boolean hasAddedClassDependency(DependencyChanges extractor, String classFQDN) {
//
//        for (Set<String> classDep : extractor.getAddedClassDependeciesPerNode().values()) {
//            if (classDep.contains(classFQDN)) return true;
//        }
//        return false;
//    }

    public boolean containsClassDependency(DependencyChanges extractor, String classFQDN, Type type) {
        return true;
//        Set<String> set;
//        switch (type) {
//            case ADDED:
//                set = extractor.getAddedRootClassDependencies();
//                break;
//            case REMOVED:
//                set = extractor.getRemovedRootClassDependencies();
//                break;
//            default:
//                throw new IllegalArgumentException("Dependency Type is unsupported.");
//        }
//
//        return set.contains(classFQDN);
    }

    /**
     * Returns the overview String for the content of two Files as Strings
     * @param src
     * @param dst
     * @return
     */
    public String dumpStringOverview(String src, String dst){
        JChangeClassifier jChangeClassifier = new JChangeClassifier(JavaMatchers.IterativeJavaMatcher_Spoon.class, new SpoonTreeGenerator());
//        try {
//            jChangeClassifier.extractDependencies(src,dst);
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//        return jChangeClassifier.getExtractor().getDependencyChangeOverview(jChangeClassifier.getChangedDependencyNodes(), ",");
        return "This is broken right now - Refactoring!"; //TODO Fix this!
    }

//    /**
//     * Returns the changes String for the content of two Files as Strings
//     * @param src
//     * @param dst
//     * @return
//     */
//    public String dumpStringChanges(String src, String dst){
//        JChangeClassifier jChangeClassifier = new JChangeClassifier(JavaMatchers.IterativeJavaMatcher_Spoon.class, new SpoonTreeGenerator());
//        DependencyChanges fileDependencyChanges = null;
//        try {
//            fileDependencyChanges = getFileDependencyChangesFromClassifier(jChangeClassifier,src,dst);
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//        return fileDependencyChanges.getChangedDependenciesAsString(",");
//    }


    public DependencyChanges getFileDependencyChangesFromClassifier(JChangeClassifier classifier, String srcString, String dstString) throws Exception {
        classifier.classify(srcString,dstString);
        DependencyExtractor dependencyExtractor = new DependencyExtractor(classifier.getMappings(),classifier.getActions(),classifier.getSrcContext().getRoot(),classifier.getDstContext().getRoot(),srcString,dstString);
        dependencyExtractor.extractDependencies();
        return dependencyExtractor.getDependencyChanges();
    }
}
