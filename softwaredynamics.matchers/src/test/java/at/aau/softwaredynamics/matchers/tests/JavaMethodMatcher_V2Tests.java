package at.aau.softwaredynamics.matchers.tests;

import at.aau.softwaredynamics.gen.NodeType;
import at.aau.softwaredynamics.gen.OptimizedJdtTreeGenerator;
import at.aau.softwaredynamics.matchers.JavaMatchers;
import at.aau.softwaredynamics.matchers.MatcherFactory;
import at.aau.softwaredynamics.matchers.tests.util.TestHelper;
import com.github.gumtreediff.matchers.CompositeMatchers;
import com.github.gumtreediff.matchers.Mapping;
import com.github.gumtreediff.matchers.Matcher;
import com.github.gumtreediff.tree.ITree;
import org.junit.Ignore;
import org.junit.Test;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static org.junit.Assert.assertEquals;

@Ignore
public class JavaMethodMatcher_V2Tests extends JavaMethodMatcherTests
{
    @Override
    protected Class<? extends Matcher> getMatcherClass() {
        return JavaMatchers.IterativeJavaMatcher_V2.class;
    }

    @Test
    public void allMethodsAreMatched_1() throws IOException {
        ITree src = TestHelper.getTree("amq_2_src", new OptimizedJdtTreeGenerator());
        ITree dst = TestHelper.getTree("amq_2_dst", new OptimizedJdtTreeGenerator());

        Matcher matcher = this.createMatcher(src, dst);
        matcher.match();

        List<Mapping> matchedMethods = getMethodMatches(matcher.getMappingSet());

        assertMethodCount(matcher.getMappingSet(), 14);
        assertEquals(14, matchedMethods.size());
    }

    @Test
    public void allMethodsAreMatched_2() throws IOException {
        ITree src = TestHelper.getTree("jdt_1_src", new OptimizedJdtTreeGenerator());
        ITree dst = TestHelper.getTree("jdt_1_dst", new OptimizedJdtTreeGenerator());

        Matcher matcher = this.createMatcher(src, dst);
        matcher.match();

        List<Mapping> matchedMethods = getMethodMatches(matcher.getMappingSet());

        assertMethodCount(matcher.getMappingSet(), 33);
        assertMethodMatchNotExists(matchedMethods, "checkInheritedMethods", "findReplacedMethod");
    }

    @Test
    public void allMethodsAreMatched_3() throws IOException {
        ITree src = TestHelper.getTree("hbase_2_src", new OptimizedJdtTreeGenerator());
        ITree dst = TestHelper.getTree("hbase_2_dst", new OptimizedJdtTreeGenerator());

        Matcher matcher = this.createMatcher(src, dst);
        matcher.match();

        List<Mapping> matchedMethods = getMethodMatches(matcher.getMappingSet());

        assertMethodCount(matcher.getMappingSet(), 13);
        assertMethodMatchNotExists(matchedMethods, "testRegionMove", "testLocateAfter");
    }

    @Test
    public void allMethodsAreMatched_4() throws IOException {
        ITree src = TestHelper.getTree("hbase_3_src", new OptimizedJdtTreeGenerator());
        ITree dst = TestHelper.getTree("hbase_3_dst", new OptimizedJdtTreeGenerator());

        Matcher matcher = this.createMatcher(src, dst);
        matcher.match();

        List<Mapping> matchedMethods = getMethodMatches(matcher.getMappingSet());

        List<String[]> expectedMatches = new ArrayList<>();
        expectedMatches.add(new String[] { "setUpBeforeClass", "setUpBeforeClass"});
        expectedMatches.add(new String[] { "tearDownAfterClass", "tearDownAfterClass"});
        expectedMatches.add(new String[] { "setUp", "setUp"});
        expectedMatches.add(new String[] { "generateMobValue", "generateMobValue"});
        expectedMatches.add(new String[] { "setScan", "setScan"});
        expectedMatches.add(new String[] { "testMobStoreScanner", "testMobStoreScanner"});
        expectedMatches.add(new String[] { "testReversedMobStoreScanner", "testReversedMobStoreScanner"});
        expectedMatches.add(new String[] { "testGetMassive", "testGetMassive"});
        expectedMatches.add(new String[] { "testGetFromFiles", "testGetFromFiles"});
        expectedMatches.add(new String[] { "testGetFromMemStore", "testGetFromMemStore"});
        expectedMatches.add(new String[] { "testGetReferences", "testGetReferences"});
        expectedMatches.add(new String[] { "testMobThreshold", "testMobThreshold"});
        expectedMatches.add(new String[] { "testGetFromArchive", "testGetFromArchive"});
        expectedMatches.add(new String[] { "assertNotMobReference", "assertNotMobReference"});
        expectedMatches.add(new String[] { "assertIsMobReference", "assertIsMobReference"});

        assertMethodMatchExists(matchedMethods, expectedMatches);
        assertMethodMatchNotExists(matchedMethods, "testGetMassive", "testReadPt");
    }

    @Test
    public void methodOverloadsAreMatched() throws IOException {
        ITree src = TestHelper.getTree("cl_2_src", new OptimizedJdtTreeGenerator());
        ITree dst = TestHelper.getTree("cl_2_dst", new OptimizedJdtTreeGenerator());

        Matcher matcher = this.createMatcher(src, dst);
        matcher.match();

        List<Mapping> matchedMethods = getMethodMatches(matcher.getMappingSet());
        List<Mapping> indexOfMethods = matchedMethods
                .stream()
                .filter(
                        x -> x.first.getLabel().equals("indexOf")
                )
                .collect(Collectors.toList());

        assertMethodCount(indexOfMethods, 20);

        // 2 methods each
        for(String type : new String[] {"Object", "long", "int", "short", "char", "byte", "float", "boolean"}) {
            List<Mapping> indexOfType = matchedMethods
                    .stream()
                    .filter(
                            x -> x.first.getLabel().equals("indexOf")
                                    && x.first.getDescendants().stream()
                                        .filter(
                                                d -> d.getParents().stream().filter(p -> p.getType() == NodeType.SINGLE_VARIABLE_DECLARATION.getValue()).count() > 0
                                                && d.getLabel().equals(type)).count() > 1
                    )
                    .collect(Collectors.toList());

            assertMethodCount(indexOfType, 2);
        }

        // 4 methods for double
        List<Mapping> indexOfType = matchedMethods
                .stream()
                .filter(
                        x -> x.first.getLabel().equals("indexOf")
                                && x.first.getDescendants().stream()
                                .filter(
                                        d -> d.getParents().stream().filter(p -> p.getType() == NodeType.SINGLE_VARIABLE_DECLARATION.getValue()).count() > 0
                                                && d.getLabel().equals("double")).count() > 1
                )
                .collect(Collectors.toList());

        assertMethodCount(indexOfType, 4);


    }


    private void assertMethodCount(Collection<Mapping> mappings, int methodCount) {
        assertEquals(methodCount, getMethodMatches(mappings).size());
    }

    private void assertMethodMatchNotExists(Collection<Mapping> methodMappings, String srcLabel, String dstLabel) {
        List<Mapping> wrongMatch = methodMappings
                .stream()
                .filter(x -> x.first.getLabel().equals(srcLabel)
                        && x.second.getLabel().equals(dstLabel))
                .collect(Collectors.toList());

        assertEquals(0, wrongMatch.size());
    }


    private void assertMethodMatchExists(Collection<Mapping> methodMappings, Collection<String[]> expectedMatches) {
        for (String[] expected : expectedMatches) {
            List<Mapping> wrongMatch = methodMappings
                    .stream()
                    .filter(x -> x.first.getLabel().equals(expected[0])
                            && x.second.getLabel().equals(expected[1]))
                    .collect(Collectors.toList());

            assertEquals(String.format("%s <-> %s not found", expected[0], expected[1]), 1, wrongMatch.size());
        }
    }

    private List<Mapping> getMethodMatches(Collection<Mapping> mappings) {
        return mappings
                .stream()
                .filter(x -> x.first.getType() == 31)
                .collect(Collectors.toList());
    }
}
