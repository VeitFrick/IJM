package test;

import at.aau.softwaredynamics.classifier.entities.SourceCodeChange;
import at.aau.softwaredynamics.runner.io.fs.FileHandler;
import org.junit.Ignore;
import org.junit.Test;
import test.classification.ClassifierTestBase;

import java.io.IOException;
import java.util.Collection;

import static org.junit.Assert.*;

/**
 * Created by Veit on 18.11.2016.
 */
public class FirstEvaluationTests extends ClassifierTestBase {

    @Ignore
    @Test
    public void debugErrorTest1() throws IOException {
        String src = "public class Foo{public void bar(){}}";
        String dst = "public class Foo{public void bar(){" +
                "walk.setRevFilter(new RevFilter() {\n" +
                "            @Override\n" +
                "            public boolean include(RevWalk walker, RevCommit cmit) throws StopWalkException, IOException {\n" +
                "                if (cmit.getParentCount() > 1)\n" +
                "                    // this is a merge commit\n" +
                "                    return false;\n" +
                "                return true;\n" +
                "            }\n" +
                "            @Override\n" +
                "            public RevFilter clone() {\n" +
                "                return null;\n" +
                "            }\n" +
                "        });\n" +
                "}}";

        Collection<SourceCodeChange> changes = classify(src, dst);

        assertEquals(11, changes);
        // assertChangeCount(changes, ChangeType.METHOD_INVOCATION_INSERT, 2);
        // assertChangeCount(changes, ChangeType.CONSTRUCTOR_INVOCATION_INSERT, 1);
        // assertChangeCount(changes, ChangeType.METHOD_INSERT,2);
        // assertChangeCount(changes, ChangeType.PARAMETER_INSERT,2);
        // assertChangeCount(changes, ChangeType.STATEMENT_INSERT,4);
     }

    @Test
    public void debugErrorTest3b() throws IOException {
        String src = "public class Foo{\n" +
                "    public void bar(){\n" +
                "        if(Rebellion.isReady()) {\n" +
                "            bar();\n" +
                "        }\n" +
                "        else if(Deathstar.hasWeakPoint()) {\n" +
                "            Deathstar.destroy();\n" +
                "        }\n" +
                "    }\n" +
                "}";
        String dst = "public class Foo{\n" +
                "    public void bar(){\n" +
                "        if(Rebellion.isReady()) {\n" +
                "            bar();\n" +
                "        }\n" +
                "        else if(ObiWan.isDead()) {\n" +
                "            Luke.grieve();\n" +
                "            Luke.grieve();\n" +
                "            Luke.grieve();\n" +
                "            Luke.grieve();\n" +
                "            Luke.grieve();\n" +
                "        }\n" +
                "        else if(Deathstar.hasWeakPoint()) {\n" +
                "            Deathstar.destroy();\n" +
                "        }\n" +
                "    }\n" +
                "}";

        Collection<SourceCodeChange> changes = classify(src, dst);

        // assertChangeCount(changes, ChangeType.STATEMENT_PARENT_CHANGE, 1);
    }

    //1fc61e1289587061533e5c7c6f621848fd890942	.JChangeClassifier	.JChangeClassifier.classifyInsert(Insert,MappingStore,HashSet<ITree>)	PARAMETER_DELETE	NOK	NOK		No parameter in method call	Debug, no method declaration	44	DEL	279	280	https://bitbucket.org/tgrassau/softwaredynamics/commits/1fc61e1289587061533e5c7c6f621848fd890942#chg-src/main/java/at/auu/softwaredynamics/classifier/JChangeClassifier.java	https://bitbucket.org/tgrassau/softwaredynamics/commits/1fc61e1289587061533e5c7c6f621848fd890942#chg-src/main/java/at/auu/softwaredynamics/classifier/JChangeClassifier.java
    @Test
    public void debugErrorTest6() throws IOException {
        FileHandler fh = new FileHandler("./src/test/resources", "test");
        String src = fh.loadFile("debug6source");
        String dst = fh.loadFile("debug6destination");

        Collection<SourceCodeChange> changes = classify(src, dst);

        // assertChangeCount(changes, ChangeType.LOOP_STATEMENT_DELETE, 1); //TODO Original Error fixed. But make this a complete Test Case
    }

    @Test
    public void debugErrorTest7() throws IOException {
        String src = "public class Foo{public void bar(GitRepositoryAnalyzer eh, Change change, Diff diff){}}";
        String dst = "public class Foo{public void bar(GitRepositoryAnalyzer eh, Change change, Diff diff){" +
                "eh.appendLine(change.getChangeType() + \";\"\n" +
                "+ change.getNodeType() + \";\"\n" +
                "+ change.getAction() + \";\"\n" +
                "+ change.getPosition() + \";\"\n" +
                "+ change.getLength() + \";\"\n" +
                "+ getFilePath(diff.getNewPath()));" +
                "}}";

        Collection<SourceCodeChange> changes = classify(src, dst);

        // assertChangeCount(changes, ChangeType.METHOD_INVOCATION_INSERT, 8);
    }

    // b86dc8cdb31850e407025936017c34245514eed5	FileHandler	FileHandler.writeToFile(String,String,List<String>)	Rb86dc8cdb31850e407025936017c34245514eed5	NOK	NOK		wrong line number??	Debug, parameter delete?	44	MOV	35	35	https://bitbucket.org/tgrassau/softwaredynamics/commits/b86dc8cdb31850e407025936017c34245514eed5#chg-src/main/java/files/FileHandler.java	https://bitbucket.org/tgrassau/softwaredynamics/commits/b86dc8cdb31850e407025936017c34245514eed5#chg-src/main/java/files/FileHandler.java
    @Test
    public void testLine179() throws IOException {
        FileHandler fh = new FileHandler("./src/test/resources", "test");
        String src = fh.loadFile("eval1Line179_src");
        String dst = fh.loadFile("eval1Line179_dst");

        Collection<SourceCodeChange> changes = classify(src, dst);

        // assertChangeCount(changes, ChangeType.PARAMETER_ORDERING_CHANGE, 0);
    }

    @Ignore("fix later")
    //325cdd6a1a8798652212863b3bcd4f899621f6e1	.ITreeNodeHelper	.ITreeNodeHelper.getConditionExpressionRoot(ITree)	STATEMENT_PARENT_CHANGE	NOKC	NOK	Check Line Numbers	wrong line number!!!	Debug, might be correct	41	MOV	142	142	https://bitbucket.org/tgrassau/softwaredynamics/commits/325cdd6a1a8798652212863b3bcd4f899621f6e1#chg-src/main/java/at/auu/softwaredynamics/classifier/util/ITreeNodeHelper.java	https://bitbucket.org/tgrassau/softwaredynamics/commits/325cdd6a1a8798652212863b3bcd4f899621f6e1#chg-src/main/java/at/auu/softwaredynamics/classifier/util/ITreeNodeHelper.java
    @Test
    public void testLine257() throws IOException {
        FileHandler fh = new FileHandler("./src/test/resources", "test");
        String src = fh.loadFile("eval1Line257_src");
        String dst = fh.loadFile("eval1Line257_dst");

        Collection<SourceCodeChange> changes = classify(src, dst);

        extendedInfoDump(changes);

        // assertChangeCount(changes, ChangeType.STATEMENT_PARENT_CHANGE, 1);
    }

    @Ignore("How to fix this?")
    //5b3e7844cb2d5bbfc0487af6b911ae42b1b0476f	.JChangeClassifier	.JChangeClassifier.classifyInsert(Insert,MappingStore,HashSet<ITree>)	STATEMENT_PARENT_CHANGE	NOK	NOK		should be DELETE, matched with duplicate	Debug, test case	32	MOV	219	219	https://bitbucket.org/tgrassau/softwaredynamics/commits/5b3e7844cb2d5bbfc0487af6b911ae42b1b0476f#chg-src/main/java/at/auu/softwaredynamics/classifier/JChangeClassifier.java	https://bitbucket.org/tgrassau/softwaredynamics/commits/5b3e7844cb2d5bbfc0487af6b911ae42b1b0476f#chg-src/main/java/at/auu/softwaredynamics/classifier/JChangeClassifier.java
    @Test
    public void testLine263() throws IOException {
        FileHandler fh = new FileHandler("./src/test/resources", "test");
        String src = fh.loadFile("eval1Line263_src");
        String dst = fh.loadFile("eval1Line263_dst");

        Collection<SourceCodeChange> changes = classify(src, dst);
        extendedInfoDump(changes);
        // assertChangeCount(changes, ChangeType.ALTERNATIVE_PART_DELETE, 1);
        // assertChangeCount(changes, ChangeType.METHOD_INVOCATION_DELETE, 1);
    }

    //5fedced310fb4ded5329a3759151c5340a4c414e	.ITreeNodeHelper	.ITreeNodeHelper.getFieldName(ITree)	METHOD_RENAMING	NOK	NOK		matched with wrong method, due to similar internal structure??	Debug, wrong matching (GT)	42	UPD	228	228	https://bitbucket.org/tgrassau/softwaredynamics/commits/5fedced310fb4ded5329a3759151c5340a4c414e#chg-src/main/java/at/auu/softwaredynamics/classifier/util/ITreeNodeHelper.java	https://bitbucket.org/tgrassau/softwaredynamics/commits/5fedced310fb4ded5329a3759151c5340a4c414e#chg-src/main/java/at/auu/softwaredynamics/classifier/util/ITreeNodeHelper.java
    @Test
    public void testLine284() throws IOException {
        FileHandler fh = new FileHandler("./src/test/resources", "test");
        String src = fh.loadFile("eval1Line284_src");
        String dst = fh.loadFile("eval1Line284_dst");

        Collection<SourceCodeChange> changes = classify(src, dst);

        // assertChangeCount(changes, ChangeType.METHOD_RENAMING,0); //Wrong matching.
    }

    // 7efc05febd5ff9063982a41bcca82d023c0dc7b0	.JChangeClassifier	.JChangeClassifier.classifyInsert(Insert,MappingStore,HashSet<ITree>)	STATEMENT_PARENT_CHANGE	NOKC	NOK		TG: OK in terms of AST (Infix Expression Insert)	Debug	32	MOV	260	260	https://bitbucket.org/tgrassau/softwaredynamics/commits/7efc05febd5ff9063982a41bcca82d023c0dc7b0#chg-src/main/java/at/auu/softwaredynamics/classifier/JChangeClassifier.java
    @Test
    public void testLine397() throws IOException {
        FileHandler fh = new FileHandler("./src/test/resources", "test");
        String src = fh.loadFile("eval1Line397_src");
        String dst = fh.loadFile("eval1Line397_dst");

        Collection<SourceCodeChange> changes = classify(src, dst);

        // assertChangeCount(changes, ChangeType.CONDITION_EXPRESSION_CHANGE, 1);
    }

    @Ignore("fix later")
    // ed7a28dad41f5161a247caea2a7cad35cb7d6983	.MatcherTests	.MatcherTests.methodDeclarationsAreMappedCorrectly_CD()	METHOD_INVOCATION_PARAMETER_ADD	NOK	NOK	No change according to Bitbucket	no change in this line	Debug, WTF	57	INS	74	74	https://bitbucket.org/tgrassau/softwaredynamics/commits/ed7a28dad41f5161a247caea2a7cad35cb7d6983#chg-src/test/java/MatcherTests.java
    @Test
    public void testLine378() throws IOException {
        FileHandler fh = new FileHandler("./src/test/resources", "test");
        String src = fh.loadFile("eval1Line378_src");
        String dst = fh.loadFile("eval1Line378_dst");

        Collection<SourceCodeChange> changes = classify(src, dst);

        // methodDeclarationsAreMappedCorrectly_CD, MTDIFF, RTED, getActions
        // assertChangeCount(changes, ChangeType.METHOD_INSERT, 5);
        // methodDeclarationsAreMappedCorrectly -> methodDeclarationsAreMappedCorrectly_GT
        // assertChangeCount(changes, ChangeType.METHOD_RENAMING, 1);
        // getMappings (getMappings) MappingStore -> List<Change>
        // assertChangeCount(changes, ChangeType.METHOD_REMOVE, 1);
    }

    // e6e3e3b6cc64b40cbc174de62cb8f04cac88d9ed	.ClassDeclarationsTests	.ClassDeclarationsTests.CanClassifyParentClassDelete()	STATEMENT_UPDATE	NOKC	NOK			Debug, test case	42	UPD	36	36	https://bitbucket.org/tgrassau/softwaredynamics/commits/e6e3e3b6cc64b40cbc174de62cb8f04cac88d9ed#chg-src/test/java/ClassDeclarationsTests.java
    @Ignore
    @Test
    public void testLine351() throws IOException {
        FileHandler fh = new FileHandler("./src/test/resources", "test");
        String src = fh.loadFile("eval1Line351_src");
        String dst = fh.loadFile("eval1Line351_dst");

        Collection<SourceCodeChange> changes = classify(src, dst);
        SourceCodeChange target = null;

//        for(SourceCodeChange c : changes)
//            if (c.getChangeType() == ChangeType.STATEMENT_DELETE
//                    && c.getSrcInfo().getStartLineNumber() == 22
//                    && c.getSrcInfo().getEndLineNumber() == 22)
//            {
//                target = c;
//                break;
//            }

        assertNotNull(target);
    }

    // b86dc8cdb31850e407025936017c34245514eed5	GitRepositoryAnalyzer	GitRepositoryAnalyzer.analyzeCommit(RevCommit,Repository,GitRepositoryAnalyzer)	STATEMENT_UPDATE	NOK	NOK			Debug, just WTF	42	UPD	76	76	https://bitbucket.org/tgrassau/softwaredynamics/commits/b86dc8cdb31850e407025936017c34245514eed5#chg-src/main/java/files/ExportHandler.java
    @Test
    public void testLine316() throws IOException {
        FileHandler fh = new FileHandler("./src/test/resources", "test");
        String src = fh.loadFile("eval1Line316_src");
        String dst = fh.loadFile("eval1Line316_dst");

        Collection<SourceCodeChange> changes = classify(src, dst);

        SourceCodeChange target = null;

//        for(SourceCodeChange c : changes)
//            if (c.getChangeType() == ChangeType.STATEMENT_UPDATE
//                    && c.getSrcInfo().getStartLineNumber() == 26
//                    && c.getSrcInfo().getEndLineNumber() == 26)
//            {
//                target = c;
//                break;
//            }

        assertNull(target);
    }
}
