package test;

import at.aau.softwaredynamics.classifier.entities.SourceCodeChange;
import at.aau.softwaredynamics.runner.io.fs.FileHandler;
import org.junit.Test;
import test.classification.ClassifierTestBase;

import java.io.IOException;
import java.util.Collection;

/**
 * Created by thomas on 23.11.2016.
 */
public class MiscExamplesTests extends ClassifierTestBase {
    @Test
    public void ReturnTypeBug() throws IOException {
        FileHandler fh = new FileHandler("./src/test/resources", "test");
        String src = fh.loadFile("amq_2_src");
        String dst = fh.loadFile("amq_2_dst");

        Collection<SourceCodeChange> changes = classify(src, dst);
        extendedInfoDump(changes);
        //Collection<SourceCodeChange> added = getChangesOfType(changes, ChangeType.METHOD_INSERT);

        //assertEquals(1, added.size());
    }

    @Test
    public void Example1() throws IOException {
        String src = "public class Foo { }";
        String dst = "public class Foo {" +
                "private static Collection<RevCommit> getSnapshots(Repository repository, String startCommit) throws IOException {\n" +
                "        RevWalk walk = new RevWalk(repository);\n" +
                "\n" +
                "        // set start commit\n" +
                "        walk.markStart(walk.parseCommit(repository.resolve(startCommit)));\n" +
                "\n" +
                "        // set filter\n" +
                "        walk.setRevFilter(new RevFilter() {\n" +
                "            @Override\n" +
                "            public boolean include(RevWalk walker, RevCommit cmit) throws StopWalkException, IOException {\n" +
                "                if (cmit.getParentCount() > 1)\n" +
                "                    // this is a merge commit\n" +
                "                    return false;\n" +
                "\n" +
                "                return true;\n" +
                "            }\n" +
                "\n" +
                "            @Override\n" +
                "            public RevFilter clone() {\n" +
                "                return null;\n" +
                "            }\n" +
                "        });\n" +
                "\n" +
                "        // extract commits\n" +
                "        List<RevCommit> commits = new Vector<>();\n" +
                "        RevCommit commit;\n" +
                "        while ((commit = walk.next()) != null) {\n" +
                "            commits.add(commit);\n" +
                "        }\n" +
                "\n" +
                "        return commits;\n" +
                "    }" +
                "}";


        Collection<SourceCodeChange> changes = classify(src, dst);

        // assertChangeCount(changes, ChangeType.METHOD_INSERT, 3);
    }

    @Test
    public void Example2() throws IOException {
        FileHandler fh = new FileHandler("./src/test/resources", "test");
        String src = fh.loadFile("example2source");
        String dst = fh.loadFile("example2destination");

        Collection<SourceCodeChange> changes = classify(src, dst);
        //Collection<SourceCodeChange> added = getChangesOfType(changes, ChangeType.METHOD_INSERT);

        //assertEquals(7, added.size());
    }

    @Test
    public void Example3() throws IOException {
        FileHandler fh = new FileHandler("./src/test/resources", "test");
        String src = fh.loadFile("example3source");
        String dst = fh.loadFile("example3destination");

        Collection<SourceCodeChange> changes = classify(src, dst);

        // assertChangeCount(changes, ChangeType.METHOD_INSERT, 0);
        // assertChangeCount(changes, ChangeType.METHOD_REMOVE, 0);
    }

    @Test
    public void Example4() throws IOException {
        FileHandler fh = new FileHandler("./src/test/resources", "test");
        String src = fh.loadFile("example4source");
        String dst = fh.loadFile("example4destination");

        Collection<SourceCodeChange> changes = classify(src, dst);

        // assertChangeCount(changes, ChangeType.METHOD_INSERT, 1);
        // assertChangeCount(changes, ChangeType.METHOD_REMOVE, 2);
    }

    @Test
    public void CommonsMathExample1() throws IOException {
        // 762eb53f5cee901be190cd324dfde01977aeea60	FieldMatrixImplTest	testGetRowVector()	STATEMENT_PARENT_CHANGE	14	MOV	681	681	680	680	src/test/java/org/apache/commons/math4/linear/FieldMatrixImplTest.java	open

        FileHandler fh = new FileHandler("./src/test/resources", "test");
        String src = fh.loadFile("cm_1_src");
        String dst = fh.loadFile("cm_1_dst");

        Collection<SourceCodeChange> changes = classify(src, dst);

        //changes = filterChanges(changes, ChangeType.STATEMENT_PARENT_CHANGE, NodeType.CLASS_INSTANCE_CREATION, 681);

        //assertEquals(0, changes.size());
    }

    @Test
    public void CommonsMathExample1_1() throws IOException {
        // 762eb53f5cee901be190cd324dfde01977aeea60	FieldMatrixImplTest	testGetRowVector()	STATEMENT_PARENT_CHANGE	14	MOV	681	681	680	680	src/test/java/org/apache/commons/math4/linear/FieldMatrixImplTest.java	open

        String src = "public class Foo { " +
                " public void testGetRowVector() {\n" +
                "        FieldMatrix<Fraction> m = new Array2DRowFieldMatrix<Fraction>(subTestData);\n" +
                "        FieldVector<Fraction> mRow0 = new ArrayFieldVector<Fraction>(subRow0[0]);\n" +
                "        FieldVector<Fraction> mRow3 = new ArrayFieldVector<Fraction>(subRow3[0]);\n" +
                "        Assert.assertEquals(\"Row0\", mRow0, m.getRowVector(0));\n" +
                "        Assert.assertEquals(\"Row3\", mRow3, m.getRowVector(3));\n" +
                "        try {\n" +
                "            m.getRowVector(-1);\n" +
                "            Assert.fail(\"Expecting OutOfRangeException\");\n" +
                "        } catch (OutOfRangeException ex) {\n" +
                "            // expected\n" +
                "        }\n" +
                "        try {\n" +
                "            m.getRowVector(4);\n" +
                "            Assert.fail(\"Expecting OutOfRangeException\");\n" +
                "        } catch (OutOfRangeException ex) {\n" +
                "            // expected\n" +
                "        }\n" +
                "    }" +
                "}";
        String dst = "public class Foo { " +
                "public void testGetRowVector() {\n" +
                "        FieldMatrix<Fraction> m = new Array2DRowFieldMatrix<>(subTestData);\n" +
                "        FieldVector<Fraction> mRow0 = new ArrayFieldVector<>(subRow0[0]);\n" +
                "        FieldVector<Fraction> mRow3 = new ArrayFieldVector<>(subRow3[0]);\n" +
                "        Assert.assertEquals(\"Row0\", mRow0, m.getRowVector(0));\n" +
                "        Assert.assertEquals(\"Row3\", mRow3, m.getRowVector(3));\n" +
                "        try {\n" +
                "            m.getRowVector(-1);\n" +
                "            Assert.fail(\"Expecting OutOfRangeException\");\n" +
                "        } catch (OutOfRangeException ex) {\n" +
                "            // expected\n" +
                "        }\n" +
                "        try {\n" +
                "            m.getRowVector(4);\n" +
                "            Assert.fail(\"Expecting OutOfRangeException\");\n" +
                "        } catch (OutOfRangeException ex) {\n" +
                "            // expected\n" +
                "        }\n" +
                "    }" +
                "}";

        Collection<SourceCodeChange> changes = classify(src, dst);

        // assertChangeCount(changes, ChangeType.STATEMENT_PARENT_CHANGE, 0);
    }

    @Test
    public void CommonMathsExample2() throws IOException {
        //9867d9f2817fd6dd20d458022de3dda8c3b43b2f	EmpiricalDistributionTest	testNexFail()	STATEMENT_PARENT_CHANGE	32	MOV	165	165	164	164	src/test/java/org/apache/commons/math4/random/EmpiricalDistributionTest.java	open
        String src = "public class Foo { " +
                "   public void bar() { " +
                "   try {\n" +
                "            empiricalDistribution.getNextValue();\n" +
                "            empiricalDistribution2.getNextValue();\n" +
                "            Assert.fail(\"Expecting MathIllegalStateException\");\n" +
                "        } catch (MathIllegalStateException ex) {\n" +
                "            // expected\n" +
                "        }" +
                "   }" +
                "}";

        String dst = "public class Foo { " +
                "   public void bar() { " +
                "   try {\n" +
                "            empiricalDistribution.createSampler(RandomSource.create(RandomSource.JDK)).sample();\n" +
                "            Assert.fail(\"Expecting MathIllegalStateException\");\n" +
                "        } catch (MathIllegalStateException ex) {\n" +
                "            // expected\n" +
                "        }" +
                "   }" +
                "}";

        Collection<SourceCodeChange> changes = classify(src, dst);

        // assertChangeCount(changes, ChangeType.STATEMENT_PARENT_CHANGE, 0);
    }

    @Test
    public void CommonMathsExample2_1() throws IOException {
        //9867d9f2817fd6dd20d458022de3dda8c3b43b2f	EmpiricalDistributionTest	testNexFail()	STATEMENT_PARENT_CHANGE	32	MOV	165	165	164	164	src/test/java/org/apache/commons/math4/random/EmpiricalDistributionTest.java	open
        String src = "public class Foo { " +
                "   public void bar() { " +
                "       empiricalDistribution.getNextValue();" +
                "   }" +
                "}";

        String dst = "public class Foo { " +
                "   public void bar() { " +
                "       empiricalDistribution.createSampler(RandomSource.create(RandomSource.JDK)).sample();" +
                "   }" +
                "}";

        Collection<SourceCodeChange> changes = classify(src, dst);

        // assertChangeCount(changes, ChangeType.STATEMENT_PARENT_CHANGE, 0);
    }

    @Test
    public void CommonsMathExample3() throws IOException {
        // 221c843b8437e1c87a98a9a015b03b050dd08561	ArrayFieldVectorTest	end()	STATEMENT_PARENT_CHANGE	58	MOV	1086	1086	1152	1152	src/test/java/org/apache/commons/math4/linear/ArrayFieldVectorTest.java	open

        String src = "public class Foo {" +
                "public void testWalkInOptimizedOrderChangingVisitor1() {\n" +
                "        final Fraction[] data = new Fraction[] {\n" +
                "            Fraction.ZERO, Fraction.ONE, Fraction.ZERO,\n" +
                "            Fraction.ZERO, Fraction.TWO, Fraction.ZERO,\n" +
                "            Fraction.ZERO, Fraction.ZERO, new Fraction(3)\n" +
                "        };\n" +
                "        final ArrayFieldVector<Fraction> v = new ArrayFieldVector<>(data);\n" +
                "        final FieldVectorChangingVisitor<Fraction> visitor;\n" +
                "        visitor = new FieldVectorChangingVisitor<Fraction>() {\n" +
                "            private final boolean[] visited = new boolean[data.length];\n" +
                "\n" +
                "            public Fraction visit(final int actualIndex, final Fraction actualValue) {\n" +
                "                visited[actualIndex] = true;\n" +
                "                Assert.assertEquals(Integer.toString(actualIndex),\n" +
                "                                    data[actualIndex], actualValue);\n" +
                "                return actualValue.add(actualIndex);\n" +
                "            }\n" +
                "\n" +
                "            public void start(final int actualSize, final int actualStart,\n" +
                "                              final int actualEnd) {\n" +
                "                Assert.assertEquals(data.length, actualSize);\n" +
                "                Assert.assertEquals(0, actualStart);\n" +
                "                Assert.assertEquals(data.length - 1, actualEnd);\n" +
                "                Arrays.fill(visited, false);\n" +
                "            }\n" +
                "\n" +
                "            public Fraction end() {\n" +
                "                for (int i = 0; i < data.length; i++) {\n" +
                "                    Assert.assertTrue(\"entry \" + i + \"has not been visited\",\n" +
                "                                      visited[i]);\n" +
                "                }\n" +
                "                return Fraction.ZERO;\n" +
                "            }\n" +
                "        };\n" +
                "        v.walkInOptimizedOrder(visitor);\n" +
                "        for (int i = 0; i < data.length; i++) {\n" +
                "            Assert.assertEquals(\"entry \" + i, data[i].add(i), v.getEntry(i));\n" +
                "        }\n" +
                "    }" +
                "}";

        String dst = "public class Foo {" +
                "public void testWalkInOptimizedOrderChangingVisitor1() {\n" +
                "        final Fraction[] data = new Fraction[] {\n" +
                "            Fraction.ZERO, Fraction.ONE, Fraction.ZERO,\n" +
                "            Fraction.ZERO, Fraction.TWO, Fraction.ZERO,\n" +
                "            Fraction.ZERO, Fraction.ZERO, new Fraction(3)\n" +
                "        };\n" +
                "        final ArrayFieldVector<Fraction> v = new ArrayFieldVector<>(data);\n" +
                "        final FieldVectorChangingVisitor<Fraction> visitor;\n" +
                "        visitor = new FieldVectorChangingVisitor<Fraction>() {\n" +
                "            private final boolean[] visited = new boolean[data.length];\n" +
                "\n" +
                "            @Override\n" +
                "            public Fraction visit(final int actualIndex, final Fraction actualValue) {\n" +
                "                visited[actualIndex] = true;\n" +
                "                Assert.assertEquals(Integer.toString(actualIndex),\n" +
                "                                    data[actualIndex], actualValue);\n" +
                "                return actualValue.add(actualIndex);\n" +
                "            }\n" +
                "\n" +
                "            @Override\n" +
                "            public void start(final int actualSize, final int actualStart,\n" +
                "                              final int actualEnd) {\n" +
                "                Assert.assertEquals(data.length, actualSize);\n" +
                "                Assert.assertEquals(0, actualStart);\n" +
                "                Assert.assertEquals(data.length - 1, actualEnd);\n" +
                "                Arrays.fill(visited, false);\n" +
                "            }\n" +
                "\n" +
                "            @Override\n" +
                "            public Fraction end() {\n" +
                "                for (int i = 0; i < data.length; i++) {\n" +
                "                    Assert.assertTrue(\"entry \" + i + \"has not been visited\",\n" +
                "                                      visited[i]);\n" +
                "                }\n" +
                "                return Fraction.ZERO;\n" +
                "            }\n" +
                "        };\n" +
                "        v.walkInOptimizedOrder(visitor);\n" +
                "        for (int i = 0; i < data.length; i++) {\n" +
                "            Assert.assertEquals(\"entry \" + i, data[i].add(i), v.getEntry(i));\n" +
                "        }\n" +
                "    }" +
                "}";

        // 221c843b8437e1c87a98a9a015b03b050dd08561	ArrayFieldVectorTest	end()	STATEMENT_PARENT_CHANGE	58	MOV	1086	1086	1152	1152	src/test/java/org/apache/commons/math4/linear/ArrayFieldVectorTest.java	open

        Collection<SourceCodeChange> changes = classify(src, dst);

        // assertChangeCount(changes, ChangeType.STATEMENT_PARENT_CHANGE, 0);
    }

    @Test
    public void CommonsMathExample3_1() throws IOException {
        FileHandler fh = new FileHandler("./src/test/resources", "test");
        String src = fh.loadFile("cm_3_src");
        String dst = fh.loadFile("cm_3_dst");

        Collection<SourceCodeChange> changes = classify(src, dst);

        // changes = filterChanges(changes, ChangeType.STATEMENT_PARENT_CHANGE, NodeType.VARIABLE_DECLARATION_EXPRESSION, 1152);

        // assertChangeCount(changes, ChangeType.STATEMENT_PARENT_CHANGE, 0);
    }

    @Test
    public void CommonMathsExample4() throws IOException {
        //	96c2ce398318fdc1c4ebb865ef06061e72c6e5fd	BigFractionTest	testAdd()	STATEMENT_ORDERING_CHANGE	21	MOV	368	368	370	370	src/test/java/org/apache/commons/math/fraction/BigFractionTest.java	open
        String src = "public class Foo { " +
                "   public void bar() { " +
                "       assertEquals(Integer.MAX_VALUE, f.getNumeratorAsInt());" +
                "   }" +
                "}";

        String dst = "public class Foo { " +
                "   public void bar() { " +
                "       Assert.assertEquals(Integer.MAX_VALUE, f.getNumeratorAsInt());" +
                "   }" +
                "}";

        Collection<SourceCodeChange> changes = classify(src, dst);

        // assertChangeCount(changes, ChangeType.STATEMENT_PARENT_CHANGE, 0);
        // assertChangeCount(changes, ChangeType.STATEMENT_UPDATE, 1);
    }

    @Test
    public void CommonsMathExample5() throws IOException {
        // 8b4597937e00ecb269318355cc3e092030d64a9b	SymmLQ	checkSymmetry(final,RealLinearOperator,final,RealVector,final,RealVector,final,RealVector)	PARAMETER_ORDERING_CHANGE	44	MOV	809	809	388	388	src/main/java/org/apache/commons/math3/linear/SymmLQ.java
        FileHandler fh = new FileHandler("./src/test/resources", "test");
        String src = fh.loadFile("cm_5_src");
        String dst = fh.loadFile("cm_5_dst");

        Collection<SourceCodeChange> changes = classify(src, dst);

        // changes = filterChanges(changes, ChangeType.PARAMETER_ORDERING_CHANGE, NodeType.SINGLE_VARIABLE_DECLARATION, 388);


        // wrong at.aau.softwardynamics.test.classification
        // assertChangeCount(changes, ChangeType.PARAMETER_ORDERING_CHANGE, 0);
    }

    @Test
    public void CommonsMathExample6() throws IOException {
        // 8b4597937e00ecb269318355cc3e092030d64a9b	SymmLQ	checkSymmetry(final,RealLinearOperator,final,RealVector,final,RealVector,final,RealVector)	PARAMETER_ORDERING_CHANGE	44	MOV	809	809	388	388	src/main/java/org/apache/commons/math3/linear/SymmLQ.java
        FileHandler fh = new FileHandler("./src/test/resources", "test");
        String src = fh.loadFile("cm_6_src");
        String dst = fh.loadFile("cm_6_dst");

        Collection<SourceCodeChange> changes = classify(src, dst);

        System.setProperty("gumtree.match.gt.minh", "2");

        // changes = filterChanges(changes, ChangeType.STATEMENT_PARENT_CHANGE, NodeType.EXPRESSION_STATEMENT, 134);

        // assertChangeCount(changes, ChangeType.STATEMENT_PARENT_CHANGE, 0);
    }

//    private Collection<SourceCodeChange> filterChanges(Collection<SourceCodeChange> changes, ChangeType type, NodeType nodeType, int dstStartLine) {
//        Vector<SourceCodeChange> retVal = new Vector<>();
//
//        for(SourceCodeChange change : changes) {
//            if (change.getChangeType().equals(type)
//                    && change.getNodeType() == nodeType.getValue()
//                    && (dstStartLine == 0 || change.getDstInfo().getStartLineNumber() == dstStartLine))
//                retVal.add(change);
//        }
//
//        return retVal;
//    }
}
