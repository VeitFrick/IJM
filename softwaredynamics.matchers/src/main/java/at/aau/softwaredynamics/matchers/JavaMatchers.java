package at.aau.softwaredynamics.matchers;

import at.aau.softwaredynamics.gen.NodeType;
import at.aau.softwaredynamics.gen.SpoonBuilder;
import at.aau.softwaredynamics.util.SpoonType;
import com.github.gumtreediff.matchers.CompositeMatcher;
import com.github.gumtreediff.matchers.MappingStore;
import com.github.gumtreediff.matchers.Matcher;
import com.github.gumtreediff.matchers.heuristic.gt.GreedySubtreeMatcher;
import com.github.gumtreediff.matchers.optimizations.IdenticalSubtreeMatcher;
import com.github.gumtreediff.tree.ITree;
import spoon.reflect.declaration.CtElement;

/**
 * Created by thomas on 28.02.2017.
 */
public class JavaMatchers {

    @Deprecated
    public static class IterativeJavaMatcher extends CompositeMatcher {
        public IterativeJavaMatcher(ITree src, ITree dst, MappingStore mappings) {
            super(src, dst, mappings, new Matcher[]{
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
            super(src, dst, mappings, new Matcher[]{
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
            super(src, dst, mappings, new Matcher[]{
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

    public static class IterativeJavaMatcher_Spoon extends CompositeMatcher {
        public IterativeJavaMatcher_Spoon(ITree src, ITree dst, MappingStore mappings) {
            super(src, dst, mappings, new Matcher[]{
                    new IdenticalSubtreeMatcher(src, dst, mappings),
//                    new IdenticalImportsMatcher(src, dst, mappings), //Not implemented in Spoon
                    new JavaInnerTypeDeclarationMatcher_Spoon(src, dst, mappings), //SPOON!
                    new JavaInnerEnumDeclarationMatcher_Spoon(src, dst, mappings), //Spoon! But hey TODO check if okay
                    new JavaMethodMatcher_Spoon(src, dst, mappings), //SPOON!
                    new JavaFieldDeclarationMatcher_Spoon(src, dst, mappings), //SPOON!
                    new JavaClassDeclarationMatcher(src, dst, mappings) //TODO - Can't just delete (cleanup matcher?)
            });
        }
    }

    public static class LabelAwareClassicGumTree extends CompositeMatcher {
        public LabelAwareClassicGumTree(ITree src, ITree dst, MappingStore mappings) {
            super(src, dst, mappings, new Matcher[]{
                    new IdenticalSubtreeMatcher(src, dst, mappings),
                    new GreedySubtreeMatcher(src, dst, mappings),
                    new LabelAwareBottomUpMatcher(src, dst, mappings),
            });
        }
    }

    public static class PartialInnerMatcher extends CompositeMatcher {
        public PartialInnerMatcher(ITree src, ITree dst, MappingStore mappings) {
            super(src, dst, mappings, new Matcher[]{
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

    public static class JavaClassDeclarationMatcher_Spoon extends PartialMatcher {
        public JavaClassDeclarationMatcher_Spoon(ITree src, ITree dst, MappingStore mappings) {
            super(src, dst, mappings, new PartialMatcherConfiguration(
                    t -> {
                        CtElement ctMe = (CtElement) t.getMetadata(SpoonBuilder.SPOON_OBJECT);
                        return !SpoonType.CLASS.isTypeOf(ctMe) &&
                                (
                                        SpoonType.FIELD.isTypeOf(ctMe) ||
                                                SpoonType.METHOD.isTypeOf(ctMe) ||
                                                SpoonType.TYPE.isTypeOf(ctMe) ||
                                                SpoonType.ENUM.isTypeOf(ctMe)
                                );
                    },
                    PartialInnerMatcher.class
            ));
        }
    }

    public static class JavaFieldDeclarationMatcher extends PartialMatcher {
        public JavaFieldDeclarationMatcher(ITree src, ITree dst, MappingStore mappings) {
            super(src, dst, mappings, new PartialMatcherConfiguration(
                    t -> !t.isRoot()
                            && t.getParent().getType() == NodeType.TYPE_DECLARATION.getValue()
                            && t.getType() != NodeType.FIELD_DECLARATION.getValue(),
                    PartialInnerMatcher.class,
                    LabelAwareClassicGumTree.class,
                    t -> t.getType() == NodeType.FIELD_DECLARATION.getValue()
            ));
        }
    }

    public static class JavaFieldDeclarationMatcher_Spoon extends PartialMatcher {
        public JavaFieldDeclarationMatcher_Spoon(ITree src, ITree dst, MappingStore mappings) {
            super(src, dst, mappings, new PartialMatcherConfiguration(
                    t -> {    // sub nodes of types that are no methods
                        if (t.isRoot())
                            return false;

                        CtElement ctMe = (CtElement) t.getMetadata(SpoonBuilder.SPOON_OBJECT);
                        CtElement ctParent = null;
                        if (t.getParent() != null) {
                            ctParent = (CtElement) t.getParent().getMetadata(SpoonBuilder.SPOON_OBJECT);
                        }
                        return SpoonType.TYPE.isTypeOf(ctParent)
                                && SpoonType.FIELD.isTypeOf(ctMe);
                    },
                    PartialInnerMatcher.class,
                    LabelAwareClassicGumTree.class,
                    t -> SpoonType.FIELD.isTypeOf((CtElement) t.getMetadata(SpoonBuilder.SPOON_OBJECT))
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
                                    && t.getParents().stream().filter(x -> x.getType() == NodeType.METHOD_DECLARATION.getValue()).count() > 0
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

    public static class SignatureBasedJavaMethodMatcher_Spoon extends PartialMatcher {
        public SignatureBasedJavaMethodMatcher_Spoon(ITree src, ITree dst, MappingStore mappings) {
            super(src, dst, mappings, new PartialMatcherConfiguration(
                    t -> {    // sub nodes of types that are no methods
                        if (t.isRoot())
                            return false;

                        CtElement ctMe = (CtElement) t.getMetadata(SpoonBuilder.SPOON_OBJECT);
                        CtElement ctParent = null;
                        if (t.getParent() != null) {
                            ctParent = (CtElement) t.getParent().getMetadata(SpoonBuilder.SPOON_OBJECT);
                        }

                        //System.out.println(ctMe.getShortRepresentation() + " " + t);
                        return SpoonType.TYPE.isTypeOf(ctParent)
                                && !SpoonType.METHOD.isTypeOf(ctMe)
                                // remove method bodies (parent is method, keep parameters)
                                || SpoonType.METHOD.isTypeOf(ctParent)
                                && !SpoonType.PARAMETER.isTypeOf(ctMe);
                    },
                    LabelAwareClassicGumTree.class,
                    LabelAwareClassicGumTree.class,
                    t -> SpoonType.METHOD.isTypeOf((CtElement) t.getMetadata(SpoonBuilder.SPOON_OBJECT)) //TODO experimental
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

    public static class JavaMethodMatcher_Spoon extends CompositeMatcher {
        public JavaMethodMatcher_Spoon(ITree src, ITree dst, MappingStore store) {
            super(src, dst, store, new Matcher[]{
                    new SignatureBasedJavaMethodMatcher_Spoon(src, dst, store), //SPOONIFIED
                    new StructureBasedJavaMethodMatcher_Spoon(src, dst, store), //SPOONIFIED

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

    public static class StructureBasedJavaMethodMatcher_Spoon extends PartialMatcher {
        public StructureBasedJavaMethodMatcher_Spoon(ITree src, ITree dst, MappingStore mappings) {
            super(src, dst, mappings, new PartialMatcherConfiguration(
                    t -> {    // sub nodes of types that are no methods
                        if (t.isRoot())
                            return false;

                        CtElement ctMe = (CtElement) t.getMetadata(SpoonBuilder.SPOON_OBJECT);
                        CtElement ctParent = null;
                        if (t.getParent() != null) {
                            ctParent = (CtElement) t.getParent().getMetadata(SpoonBuilder.SPOON_OBJECT);
                        }
                        return SpoonType.TYPE.isTypeOf(ctParent)
                                && SpoonType.METHOD.isTypeOf(ctMe);
                    },
                    LabelAwareClassicGumTree.class,
                    LabelAwareClassicGumTree.class,
                    t -> SpoonType.METHOD.isTypeOf((CtElement) t.getMetadata(SpoonBuilder.SPOON_OBJECT))
            ));
        }
    }

    @Deprecated
    public static class JavaInnerTypeDeclarationMatcher extends PartialMatcher {
        public JavaInnerTypeDeclarationMatcher(ITree src, ITree dst, MappingStore mappings) {
            super(src, dst, mappings, new PartialMatcherConfiguration(
                    t -> !t.isRoot()
                            && t.getParent().getType() == NodeType.TYPE_DECLARATION.getValue()
                            && t.getType() != NodeType.TYPE_DECLARATION.getValue(),
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
                                        && (grandParent == null || grandParent.getType() != NodeType.TYPE_DECLARATION.getValue());
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
                                        && (grandParent == null || grandParent.getType() != NodeType.TYPE_DECLARATION.getValue());
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

    public static class JavaInnerTypeDeclarationMatcher_Spoon extends PartialMatcher {
        public JavaInnerTypeDeclarationMatcher_Spoon(ITree src, ITree dst, MappingStore mappings) {
            super(src, dst, mappings, new PartialMatcherConfiguration(
                    t -> {
                        if (t.isRoot())
                            return false;

                        ITree grandParent = t.getParent().getParent(); // could be null
                        CtElement ctMe = (CtElement) t.getMetadata(SpoonBuilder.SPOON_OBJECT);
                        CtElement ctGrandParent = null;
                        CtElement ctGreatGrandParent = null;
                        if (grandParent != null) {
                            ctGrandParent = (CtElement) grandParent.getMetadata(SpoonBuilder.SPOON_OBJECT);
                            if (grandParent.getParent() != null) {
                                ctGreatGrandParent = (CtElement) grandParent.getParent().getMetadata(SpoonBuilder.SPOON_OBJECT);
                            }
                        }

                        return grandParent != null
                                && SpoonType.TYPE.isTypeOf(ctGrandParent)
                                && ctGreatGrandParent != null
                                && SpoonType.TYPE.isTypeOf(ctGreatGrandParent)
                                ||
                                !SpoonType.TYPE.isTypeOf(ctMe)
                                        && (ctGrandParent == null || !SpoonType.TYPE.isTypeOf(ctGrandParent));
                    }
                    ,
                    PartialInnerMatcher.class, // TODO change
                    IterativeJavaMatcher_Spoon.class,
                    t -> {
                        CtElement ctMe = (CtElement) t.getMetadata(SpoonBuilder.SPOON_OBJECT);
                        CtElement ctParent = null;
                        if (t.getParent() != null)
                            ctParent = (CtElement) t.getParent().getMetadata(SpoonBuilder.SPOON_OBJECT);
                        return SpoonType.TYPE.isTypeOf(ctMe)
                                && SpoonType.TYPE.isTypeOf(ctParent);
                    }
            ));
        }
    }

    public static class JavaInnerEnumDeclarationMatcher extends PartialMatcher {
        public JavaInnerEnumDeclarationMatcher(ITree src, ITree dst, MappingStore mappings) {
            super(src, dst, mappings, new PartialMatcherConfiguration(
                    t -> !t.isRoot()
                            && t.getParent().getType() == NodeType.TYPE_DECLARATION.getValue()
                            && t.getType() != NodeType.ENUM_DECLARATION.getValue(),

                    PartialInnerMatcher.class,
                    LabelAwareClassicGumTree.class,
                    t -> t.getType() == NodeType.ENUM_DECLARATION.getValue()
                            && t.getParent() != null
                            && t.getParent().getType() == NodeType.TYPE_DECLARATION.getValue()

            ));
        }
    }

    public static class JavaInnerEnumDeclarationMatcher_Spoon extends PartialMatcher {
        public JavaInnerEnumDeclarationMatcher_Spoon(ITree src, ITree dst, MappingStore mappings) {
            super(src, dst, mappings, new PartialMatcherConfiguration(
                    t -> {
                        if (t.isRoot())
                            return false;

                        CtElement ctMe = (CtElement) t.getMetadata(SpoonBuilder.SPOON_OBJECT);
                        CtElement ctParent = null;
                        if (t.getParent() != null) {
                            ctParent = (CtElement) t.getParent().getMetadata(SpoonBuilder.SPOON_OBJECT);
                        }
                        return SpoonType.TYPE.isTypeOf(ctParent)
                                && SpoonType.ENUM.isTypeOf(ctMe);
                    },
                    PartialInnerMatcher.class,
                    LabelAwareClassicGumTree.class,
                    t -> SpoonType.ENUM.isTypeOf((CtElement) t.getMetadata(SpoonBuilder.SPOON_OBJECT))
                            && t.getParent() != null
                            && SpoonType.TYPE.isTypeOf((CtElement) t.getParent().getMetadata(SpoonBuilder.SPOON_OBJECT))
            ));
        }
    }

}
