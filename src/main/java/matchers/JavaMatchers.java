package matchers;

import com.github.gumtreediff.matchers.CompositeMatcher;
import com.github.gumtreediff.matchers.MappingStore;
import com.github.gumtreediff.matchers.Matcher;
import com.github.gumtreediff.matchers.heuristic.gt.GreedySubtreeMatcher;
import com.github.gumtreediff.matchers.optimizations.IdenticalSubtreeMatcher;
import com.github.gumtreediff.tree.ITree;
import gen.NodeType;

/**
 * Created by thomas on 28.02.2017.
 */
public class JavaMatchers {

    @Deprecated
    public static class IterativeJavaMatcher extends CompositeMatcher {
        public IterativeJavaMatcher(ITree src, ITree dst, MappingStore mappings) {
            super(src, dst, mappings, new Matcher[] {
                    new IdenticalSubtreeMatcher(src, dst, mappings),
                    new IdenticalImportsMatcher(src, dst, mappings),
                    new JavaInnerTypeDeclarationMatcher(src, dst, mappings),
                    new JavaInnerEnumDeclarationMatcher(src, dst, mappings),
                    new JavaMethodMatcher(src, dst, mappings),
                    new JavaFieldDeclarationMatcher(src, dst, mappings),
                    new JavaClassDeclarationMatcher(src, dst, mappings)
            });
        }
    }

    @Deprecated
    public static class IterativeJavaMatcher_V1 extends CompositeMatcher {
        public IterativeJavaMatcher_V1(ITree src, ITree dst, MappingStore mappings) {
            super(src, dst, mappings, new Matcher[] {
                    new IdenticalSubtreeMatcher(src, dst, mappings),
                    new IdenticalImportsMatcher(src, dst, mappings),
                    new JavaInnerTypeDeclarationMatcher_V1(src, dst, mappings),
                    new JavaInnerEnumDeclarationMatcher(src, dst, mappings),
                    new JavaMethodMatcher_V1(src, dst, mappings),
                    new JavaFieldDeclarationMatcher(src, dst, mappings),
                    new JavaClassDeclarationMatcher(src, dst, mappings)
            });
        }
    }

    public static class IterativeJavaMatcher_V2 extends CompositeMatcher {
        public IterativeJavaMatcher_V2(ITree src, ITree dst, MappingStore mappings) {
            super(src, dst, mappings, new Matcher[] {
                    new IdenticalSubtreeMatcher(src, dst, mappings),
                    new IdenticalImportsMatcher(src, dst, mappings),
                    new JavaInnerTypeDeclarationMatcher_V2(src, dst, mappings),
                    new JavaInnerEnumDeclarationMatcher(src, dst, mappings),
                    new JavaMethodMatcher_V2(src, dst, mappings),
                    new JavaFieldDeclarationMatcher(src, dst, mappings),
                    new JavaClassDeclarationMatcher(src, dst, mappings)
            });
        }
    }

    public static class LabelAwareClassicGumTree extends CompositeMatcher {
        public LabelAwareClassicGumTree(ITree src, ITree dst, MappingStore mappings) {
            super(src, dst, mappings, new Matcher[] {
                    new IdenticalSubtreeMatcher(src, dst, mappings),
                    new GreedySubtreeMatcher(src, dst, mappings),
                    new LabelAwareBottomUpMatcher(src, dst, mappings),
            });
        }
    }

    public static class PartialInnerMatcher extends CompositeMatcher {
        public PartialInnerMatcher(ITree src, ITree dst, MappingStore mappings) {
            super(src, dst, mappings, new Matcher[] {
                    new IdenticalSubtreeMatcher(src, dst, mappings),
                    new LabelAwareBottomUpMatcher(src, dst, mappings),
            });
        }
    }

    public static class JavaClassDeclarationMatcher extends PartialMatcher {
        public JavaClassDeclarationMatcher(ITree src, ITree dst, MappingStore mappings) {
            super(src, dst, mappings, new PartialMatcherConfiguration(
                    t -> t.getDepth() > 1 &&
                            (
                                    t.getType() == NodeType.FIELD_DECLARATION.getValue()
                                        || t.getType() == NodeType.METHOD_DECLARATION.getValue()
                                        || t.getType() == NodeType.TYPE_DECLARATION.getValue()
                                        || t.getType() == NodeType.ENUM_DECLARATION.getValue()
                            ),
                    PartialInnerMatcher.class
            ));
        }
    }

    public static class JavaFieldDeclarationMatcher extends PartialMatcher {
        public JavaFieldDeclarationMatcher(ITree src, ITree dst, MappingStore mappings) {
            super(src, dst, mappings, new PartialMatcherConfiguration(
                    t -> !t.isRoot()
                            && t.getParent().getType() == NodeType.TYPE_DECLARATION.getValue()
                            && t.getType() !=  NodeType.FIELD_DECLARATION.getValue(),
                    PartialInnerMatcher.class,
                    LabelAwareClassicGumTree.class,
                    t -> t.getType() == NodeType.FIELD_DECLARATION.getValue()
            ));
        }
    }

    @Deprecated
    public static class JavaMethodMatcher extends PartialMatcher {
        public JavaMethodMatcher(ITree src, ITree dst, MappingStore mappings) {
            super(src, dst, mappings, new PartialMatcherConfiguration(
                    t ->    // sub nodes of types that are no methods
                            !t.isRoot()
                                    && t.getParent().getType() == NodeType.TYPE_DECLARATION.getValue()
                                    && t.getType() != NodeType.METHOD_DECLARATION.getValue()
                                    // method bodies
                                    || !t.isRoot()
                                    && t.getParent().getType() == NodeType.METHOD_DECLARATION.getValue()
                                    && t.getType() == NodeType.BLOCK.getValue()
                    ,
                    LabelAwareClassicGumTree.class,
                    LabelAwareClassicGumTree.class,
                    t -> t.getType() == NodeType.METHOD_DECLARATION.getValue()
            ));
        }
    }

    @Deprecated
    public static class SignatureBasedJavaMethodMatcher extends PartialMatcher {
        public SignatureBasedJavaMethodMatcher(ITree src, ITree dst, MappingStore mappings) {
            super(src, dst, mappings, new PartialMatcherConfiguration(
                    t ->    // sub nodes of types that are no methods
                            !t.isRoot()
                                && t.getParent().getType() == NodeType.TYPE_DECLARATION.getValue()
                                && t.getType() != NodeType.METHOD_DECLARATION.getValue()
                            // method bodies
                            || !t.isRoot()
                                && t.getParent().getType() == NodeType.METHOD_DECLARATION.getValue()
                                && t.getType() == NodeType.BLOCK.getValue()
                    ,
                    LabelAwareClassicGumTree.class,
                    LabelAwareClassicGumTree.class,
                    t -> t.getType() == NodeType.METHOD_DECLARATION.getValue()
            ));
        }
    }

    public static class SignatureBasedJavaMethodMatcher_V1 extends PartialMatcher {
        public SignatureBasedJavaMethodMatcher_V1(ITree src, ITree dst, MappingStore mappings) {
            super(src, dst, mappings, new PartialMatcherConfiguration(
                    t ->    // sub nodes of types that are no methods
                            !t.isRoot()
                                    && t.getParent().getType() == NodeType.TYPE_DECLARATION.getValue()
                                    && t.getType() != NodeType.METHOD_DECLARATION.getValue()
                                    // modifiers in methods
                                    || !t.isRoot()
                                    && t.getType() == NodeType.MODIFIER.getValue()
                                    && t.getParents().stream().filter(x -> x.getType() ==  NodeType.METHOD_DECLARATION.getValue()).count() > 0
                                    // method bodies
                                    || !t.isRoot()
                                    && t.getParent().getType() == NodeType.METHOD_DECLARATION.getValue()
                                    && t.getType() == NodeType.BLOCK.getValue()
                    ,
                    LabelAwareClassicGumTree.class,
                    LabelAwareClassicGumTree.class,
                    t -> t.getType() == NodeType.METHOD_DECLARATION.getValue()
            ));
        }
    }

    @Deprecated
    public static class JavaMethodMatcher_V1 extends CompositeMatcher {

        public JavaMethodMatcher_V1(ITree src, ITree dst, MappingStore store) {
            super(src, dst, store, new Matcher[]{
                    new StructureBasedJavaMethodMatcher(src, dst, store),
                    new SignatureBasedJavaMethodMatcher(src, dst, store),
            });
        }
    }

    public static class JavaMethodMatcher_V2 extends CompositeMatcher {
        public JavaMethodMatcher_V2(ITree src, ITree dst, MappingStore store) {
            super(src, dst, store, new Matcher[]{
                    new SignatureBasedJavaMethodMatcher_V1(src, dst, store),
                    new StructureBasedJavaMethodMatcher(src, dst, store),

            });
        }
    }

    public static class StructureBasedJavaMethodMatcher extends PartialMatcher {
        public StructureBasedJavaMethodMatcher(ITree src, ITree dst, MappingStore mappings) {
            super(src, dst, mappings, new PartialMatcherConfiguration(
                    t ->    // sub nodes of types that are no methods
                            !t.isRoot()
                                    && t.getParent().getType() == NodeType.TYPE_DECLARATION.getValue()
                                    && t.getType() != NodeType.METHOD_DECLARATION.getValue()
                    ,
                    LabelAwareClassicGumTree.class,
                    LabelAwareClassicGumTree.class,
                    t -> t.getType() == NodeType.METHOD_DECLARATION.getValue()
            ));
        }
    }

    @Deprecated
    public static class JavaInnerTypeDeclarationMatcher extends PartialMatcher {
        public JavaInnerTypeDeclarationMatcher(ITree src, ITree dst, MappingStore mappings) {
            super(src, dst, mappings, new PartialMatcherConfiguration(
                    t ->  !t.isRoot()
                            && t.getParent().getType() == NodeType.TYPE_DECLARATION.getValue()
                            && t.getType() !=  NodeType.TYPE_DECLARATION.getValue(),
                    PartialInnerMatcher.class,
                    IterativeJavaMatcher.class,
                    t -> t.getType() == NodeType.TYPE_DECLARATION.getValue()
                            && t.getParent() != null
                            && t.getParent().getType() == NodeType.TYPE_DECLARATION.getValue()
            ));
        }
    }

    @Deprecated
    public static class JavaInnerTypeDeclarationMatcher_V1 extends PartialMatcher {
        public JavaInnerTypeDeclarationMatcher_V1(ITree src, ITree dst, MappingStore mappings) {
            super(src, dst, mappings, new PartialMatcherConfiguration(
                    t -> {
                        if (t.isRoot())
                            return false;

                        ITree grandParent = t.getParent().getParent(); // could be null

                        return grandParent != null
                                && grandParent.getType() == NodeType.TYPE_DECLARATION.getValue()
                                && grandParent.getParent() != null
                                && grandParent.getParent().getType() == NodeType.TYPE_DECLARATION.getValue()
                                ||
                                t.getType() != NodeType.TYPE_DECLARATION.getValue()
                                && (grandParent == null || grandParent.getType() !=  NodeType.TYPE_DECLARATION.getValue());
                    }
                    ,
                    PartialInnerMatcher.class,
                    IterativeJavaMatcher_V1.class,
                    t -> t.getType() == NodeType.TYPE_DECLARATION.getValue()
                            && t.getParent() != null
                            && t.getParent().getType() == NodeType.TYPE_DECLARATION.getValue()
            ));
        }
    }

    public static class JavaInnerTypeDeclarationMatcher_V2 extends PartialMatcher {
        public JavaInnerTypeDeclarationMatcher_V2(ITree src, ITree dst, MappingStore mappings) {
            super(src, dst, mappings, new PartialMatcherConfiguration(
                    t -> {
                        if (t.isRoot())
                            return false;

                        ITree grandParent = t.getParent().getParent(); // could be null

                        return grandParent != null
                                && grandParent.getType() == NodeType.TYPE_DECLARATION.getValue()
                                && grandParent.getParent() != null
                                && grandParent.getParent().getType() == NodeType.TYPE_DECLARATION.getValue()
                                ||
                                t.getType() != NodeType.TYPE_DECLARATION.getValue()
                                        && (grandParent == null || grandParent.getType() !=  NodeType.TYPE_DECLARATION.getValue());
                    }
                    ,
                    PartialInnerMatcher.class,
                    IterativeJavaMatcher_V2.class,
                    t -> t.getType() == NodeType.TYPE_DECLARATION.getValue()
                            && t.getParent() != null
                            && t.getParent().getType() == NodeType.TYPE_DECLARATION.getValue()
            ));
        }
    }

    public static class JavaInnerEnumDeclarationMatcher extends PartialMatcher {
        public JavaInnerEnumDeclarationMatcher(ITree src, ITree dst, MappingStore mappings) {
            super(src, dst, mappings, new PartialMatcherConfiguration(
                    t ->  !t.isRoot()
                            && t.getParent().getType() == NodeType.TYPE_DECLARATION.getValue()
                            && t.getType() !=  NodeType.ENUM_DECLARATION.getValue(),
                    PartialInnerMatcher.class,
                    LabelAwareClassicGumTree.class,
                    t -> t.getType() == NodeType.ENUM_DECLARATION.getValue()
                            && t.getParent() != null
                            && t.getParent().getType() == NodeType.TYPE_DECLARATION.getValue()

            ));
        }
    }

}
