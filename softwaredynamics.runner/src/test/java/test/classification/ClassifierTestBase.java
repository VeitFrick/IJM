package test.classification;

import at.aau.softwaredynamics.classifier.AbstractJavaChangeClassifier;
import at.aau.softwaredynamics.classifier.JChangeClassifier;
import at.aau.softwaredynamics.classifier.entities.SourceCodeChange;
import at.aau.softwaredynamics.classifier.types.ChangeType;
import at.aau.softwaredynamics.gen.SpoonBuilder;
import at.aau.softwaredynamics.gen.SpoonTreeGenerator;
import at.aau.softwaredynamics.matchers.JavaMatchers;
import com.github.gumtreediff.matchers.Matcher;
import spoon.reflect.declaration.CtElement;

import java.io.IOException;
import java.util.Collection;
import java.util.Vector;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

/**
 * Created by thomas on 17.11.2016.
 */
public abstract class ClassifierTestBase {
    private boolean debug = true;

    protected Collection<SourceCodeChange> classify(String srcString, String dstString) throws IOException {
        return classify(srcString, dstString, false);
    }

    protected Collection<SourceCodeChange> classify(String srcString, String dstString, boolean includeUnclassified) throws IOException {
        return classify(
                srcString,
                dstString,
                new JChangeClassifier(includeUnclassified, JavaMatchers.IterativeJavaMatcher_Spoon.class, new SpoonTreeGenerator()));
    }

    protected Collection<SourceCodeChange> classify(String srcString, String dstString, boolean includeUnclassified, Class<? extends Matcher> matcherType) throws IOException {
        return classify(
                srcString,
                dstString,
                new JChangeClassifier(includeUnclassified, matcherType, new SpoonTreeGenerator()));
    }

    private Collection<SourceCodeChange> classify(String srcString, String dstString, AbstractJavaChangeClassifier classifier) throws IOException {
        try {
            classifier.classify(srcString, dstString);
            return classifier.getCodeChanges();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

    protected void assertChangeCountMin(Collection<SourceCodeChange> changes, Class changeClass, int actionID, int count) {
        assertTrue(count <= getChangesOfType(changes,changeClass,actionID).size());
    }

    protected void assertChangeCount(Collection<SourceCodeChange> changes, Class changeClass, int actionID, int count) {
        assertEquals(count, getChangesOfType(changes,changeClass,actionID).size());
    }

    protected void assertChangeCount(Collection<SourceCodeChange> changes, Class changeClass, Class actionClass, int count) {
        assertEquals(count, getChangesOfType(changes,changeClass, actionClass).size());
    }

    protected static Collection<SourceCodeChange> getChangesOfType(Collection<SourceCodeChange> changes, Class<?> changeClass, int actionID) {
        Collection<SourceCodeChange> retVal = new Vector<>();
        for(SourceCodeChange c : changes) {
            if(c.getChangeType().getActionType().isOfExactType(actionID)){
                if(changeClass.isInstance(c.getChangeType())) {
                    retVal.add(c);
                }
            }
        }
        return retVal;
    }

    protected static Collection<SourceCodeChange> getChangesOfType(Collection<SourceCodeChange> changes, Class<?> changeClass, Class actionTypeClass) {
        Collection<SourceCodeChange> retVal = new Vector<>();
        for(SourceCodeChange c : changes) {
            //if(c.getChangeType().getActionType().getClass().isInstance(actionTypeClass)){
            if(actionTypeClass.isInstance(c.getChangeType().getActionType())){
                if(changeClass.isInstance(c.getChangeType())) {
                    retVal.add(c);
                }
            }
        }
        return retVal;
    }


    protected void assertChangeCount(Collection<SourceCodeChange> changes, ChangeType type, int count) {
        assertEquals(count, getChangeCountByExactChangeType(changes, type));
    }

    protected static int getChangeCountByExactChangeType(Collection<SourceCodeChange> changes, ChangeType type) {
        return getChangesOfType(changes, type).size();
    }

//    protected static int getChangeCountByInheritedChangeType(Collection<SourceCodeChange> changes, Class changeClass) {
//        return getChangesOfType(changes, changeClass).size();
//    }

    protected static Collection<SourceCodeChange> getChangesOfType(Collection<SourceCodeChange> changes, ChangeType type) {
        Collection<SourceCodeChange> retVal = new Vector<>();
        for(SourceCodeChange c : changes) {
            if (c.getChangeType().getClass().equals(type.getClass()))
                retVal.add(c);
        }
        return retVal;
    }

    public void quickInfoDump(Collection<SourceCodeChange> changes) {
        if(!debug)
            return;

        System.out.println("Changes Size: " + changes.size());
        for (SourceCodeChange change : changes) {
            System.out.println("ActionTpye: " + change.getAction().getName() + " ChangeType: " + change.getChangeType().getName() + " NodeType: " + change.getNodeType());
        }
    }

    public void extendedInfoDump(Collection<SourceCodeChange> changes) {
        if(!debug)
            return;
        System.out.println('\n');
        System.out.println("Changes Size: " + changes.size());
        for (SourceCodeChange change : changes) {

            System.out.println("ActionType: " + change.getAction().getName() + " ChangeType: " + change.getChangeType().getName() + " NodeType: " + change.getNodeType() + " CtElement: " + ((CtElement) change.getNode().getMetadata(SpoonBuilder.SPOON_OBJECT)).getClass().getSimpleName() + " Hash: " + change.hashCode());
            System.out.println(change.getNode().getMetadata(SpoonBuilder.SPOON_OBJECT));
            if(change.getSrcParentChange()!=null)
                System.out.println(" (Parent(src) " + change.getSrcParentChange().hashCode());
            if(change.getDstParentChange()!=null)
                System.out.println(" (Parent(dst) " + change.getDstParentChange().hashCode());
            if(change.getContainingStatementSrc()!=null)
                System.out.println("Cont. Statement (src):  " + change.getContainingStatementSrc().toString());
            if(change.getContainingMethodSrc()!=null)
                System.out.println("Cont. Method (src):  " + change.getContainingMethodSrc().getSimpleName());
            if(change.getContainingClassSrc()!=null)
                System.out.println("Cont. Class (src):  " + (change.getContainingClassSrc()).getSimpleName());
            if(change.getContainingStatementDst()!=null)
                System.out.println("Cont. Statement (dst):  " + change.getContainingStatementDst().toString());
            if(change.getContainingMethodDst()!=null)
                System.out.println("Cont. Method (dst):  " + change.getContainingMethodDst().getSimpleName());
            if(change.getContainingClassDst()!=null)
                System.out.println("Cont. Class (dst):  " + change.getContainingClassDst().getSimpleName());

//            if(ITreeNodeHelper.isInMethod(change.getNode()))
//                System.out.print("   MethodName: "+ITreeNodeHelper.getMethodName(ITreeNodeHelper.getParentMethod(change.getNode())));
            System.out.print(" Src: "+change.getSrcInfo().getStartLineNumber()+"-"+change.getSrcInfo().getEndLineNumber());
            System.out.print(" Dst: "+change.getDstInfo().getStartLineNumber()+"-"+change.getDstInfo().getEndLineNumber());

            System.out.println('\n');
            System.out.println('\n');
        }
    }

    public void printSpecificLine(String s, int i){
        System.out.println("Print line: " + i);
        String[] cut = s.split("\\r?\\n");
        if(i<cut.length)
            System.out.println(cut[i-1]);
        else
            System.out.println("Array would be out of bound! Line Number non existing!");
    }
}
