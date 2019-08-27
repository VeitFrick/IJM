package at.aau.softwaredynamics.classifier;

import at.aau.softwaredynamics.classifier.entities.SourceCodeChange;
import at.aau.softwaredynamics.classifier.entities.SourceCodeChangeFactory;
import at.aau.softwaredynamics.classifier.types.*;
import at.aau.softwaredynamics.gen.SpoonBuilder;
import at.aau.softwaredynamics.util.Meta;
import at.aau.softwaredynamics.util.SpoonType;
import com.github.gumtreediff.actions.model.*;
import com.github.gumtreediff.gen.TreeGenerator;
import com.github.gumtreediff.matchers.Matcher;
import com.github.gumtreediff.tree.ITree;
import spoon.reflect.code.CtStatement;
import spoon.reflect.declaration.*;
import spoon.support.reflect.declaration.CtClassImpl;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Created by Veit on 16.11.2016.
 */
public class JChangeClassifier extends AbstractJavaChangeClassifier{
    private boolean includeUnclassified;
    private boolean includeMetaChanges;
//    DependencyFilter filter;

//    private FileDependencyChanges extractor;

//    public FileDependencyChanges extractDependencies(String src, String dst) throws Exception {
//        classify(src, dst, true, false);
//        return extractor;
//    }

    public JChangeClassifier(
            boolean includeUnclassified, Class<? extends Matcher> matcherType, TreeGenerator generator) {
        super(matcherType, generator);
        this.includeUnclassified = includeUnclassified;
        this.includeMetaChanges = false;
//        this.filter = new DependencyFilter();
    }

    public JChangeClassifier(Class<? extends Matcher> matcherType, TreeGenerator generator) {
        this(false, matcherType, generator);
    }

//    public DependencyFilter getFilter() {
//        return filter;
//    }
//
//    public void setFilter(DependencyFilter filter) {
//        this.filter = filter;
//    }

    public void setIncludeMetaChanges(boolean includeMetaChanges) {
        this.includeMetaChanges = includeMetaChanges;
    }

    @Override
    protected void classify(List<Action> actions) {
        for (Action a : actions) {
            ITree srcNode = a.getNode();
            ITree dstNode = this.getMappings().getDst(srcNode);

            if (a instanceof Insert)
                dstNode = a.getNode();

            classify(a, srcNode, dstNode);
        }

        if (includeMetaChanges) {
            Set<SourceCodeChange> newCodeChanges = new HashSet<>();
            for (SourceCodeChange scc : getCodeChanges()) {
                newCodeChanges.addAll(bottomUpFindMajorElement(scc));
            }
            Set<ITree> nodesEffected = new HashSet<>();
            for (SourceCodeChange codeChange : newCodeChanges) {
                boolean added = nodesEffected.add(codeChange.getNode());
                if (added) this.addToCodeChanges(codeChange);
            }
        }
    }

//    @Override
//    protected void extractDependencies(ITree srcRoot, ITree dstRoot, List<Action> actions) {
//        extractor = new FileDependencyChanges(getMappings(), actions, this.getChangeFactory());
//        extractor.extractDependencies(srcRoot, dstRoot, filter);
//    }

//    /**
//     * Used for getting ALL the nodes, SRC and DST alike
//     *
//     * @return a List of Dependency SourceCodeChanges with Positions set
//     */
//    public List<SourceCodeChange> getChangedDependencyNodes() {
//        return this.extractor.getChangedDependencyNodes();
//    }

    protected void classify(Action action, ITree srcNode, ITree dstNode) {
        CtElement ctElement = (CtElement) srcNode.getMetadata(SpoonBuilder.SPOON_OBJECT);

        SourceCodeChange change = this.getChangeFactory().create(action);

        ChangeType changeType = new ChangeType(srcNode, this.getMappings());

        // STATEMENT
        if (SpoonType.STATEMENT.isTypeOf(ctElement)) {
            // INVOCATION
            if (SpoonType.INVOCATION.isTypeOf(ctElement)) {
                if ((ctElement).toString().length() >= 4 && (ctElement).toString().substring(0, 4).equals("this")) {
                    changeType = new ThisInvocationChange(srcNode, dstNode, this.getMappings());
                } else {
                    changeType = new InvocationChange(srcNode, dstNode, this.getMappings());
                }

            }
            // CONSTRUCTOR
            else if (SpoonType.CONSTRUCTOR_CALL.isTypeOf(ctElement)) {
                changeType = new ConstructorInvocationChange(srcNode, dstNode, this.getMappings());
            }
            // LOOP
            else if (SpoonType.WHILE.isTypeOf(ctElement) || SpoonType.FOR_EACH.isTypeOf(ctElement) || SpoonType.FOR.isTypeOf(ctElement)) {
                changeType = new LoopChange(srcNode, dstNode, this.getMappings());
            }
            // RETURN STATEMENT
            else if (SpoonType.RETURN.isTypeOf(ctElement)) {
                changeType = new ReturnStatementChange(srcNode, dstNode, this.getMappings());
            }
            //TYPE DECLARATIONS
            else if (SpoonType.TYPE.isTypeOf(ctElement)) {
                // CLASS
                if (SpoonType.CLASS.isTypeOf(ctElement)) { //I deleted new_class here. new class is part of constructor according to ctmodel
                    //ENUM
                    if (SpoonType.ENUM.isTypeOf(ctElement)) {
                        changeType = new EnumChange(srcNode, dstNode, this.getMappings());
                    }
                    //GENERIC CLASS
                    else {
                        changeType = new ClassChange(srcNode, dstNode, this.getMappings());
                    }
                }
            }
            // Variable Declaration
            else if (SpoonType.LOCAL_VARIABLE.isTypeOf(ctElement)) {
                changeType = new VariableDeclarationChange(srcNode, dstNode, this.getMappings());
            }
            // IF
            else if (SpoonType.IF.isTypeOf(ctElement)) {
                changeType = new IfBranchChange(srcNode, dstNode, this.getMappings());
            }
            // ELSE
            else if (SpoonType.BRANCH.isTypeOf(ctElement)) {
                changeType = new ElseBranchChange(srcNode, dstNode, this.getMappings());
            }

            // GENERIC STATEMENT
            else {
                changeType = new StatementChange(srcNode, dstNode, this.getMappings());
            }

            // Closer look at some elements who can have a type already:
            // Else - Branch
//            if (srcNode.getParent() != null && SpoonType.IF.isTypeOf(srcNode.getParent())) {
//                // Parent is an If statement
//                changeType = new ElseBranchChange(srcNode, dstNode, this.getMappings());
//            }
        }
        // CONSTRUCTOR
        else if (SpoonType.CONSTRUCTOR.isTypeOf(ctElement)) {
            changeType = new ConstructorChange(srcNode, dstNode, this.getMappings());
        }
        // METHOD
        else if (SpoonType.METHOD.isTypeOf(ctElement)) {
            changeType = new MethodChange(srcNode, dstNode, this.getMappings());
        }
        // EXPRESSION
        else if (SpoonType.EXPRESSION.isTypeOf(ctElement)) {
            //Argument
            if (SpoonType.ARGUMENT.isTypeOf(ctElement)) {
                changeType = new ArgumentChange(srcNode, dstNode, this.getMappings());
            }
//            if(ctElement.getParent() != null && SpoonType.INVOCATION.isTypeOf(ctElement.getParent()) && !((CtInvocation) ctElement.getParent()).getTarget().equals(ctElement)) {
//                //ARGUMENT
////                if(((CtInvocation) ctElement.getParent()).getArguments().contains(ctElement)) {
////                    changeType = new ArgumentChange(srcNode, dstNode, this.getMappings());
////                }
//
//                else
//                {
//                    System.out.println("FOUND A EXPRESSION WITH PARENT INVOCATION THAT IS NOT AN ARGUMENT!!!");
//                }
//            }
            //RETURN VALUE
            else if (ctElement.getParent() != null && SpoonType.RETURN.isTypeOf(ctElement.getParent())) {
                changeType = new ReturnValueChange(srcNode, dstNode, this.getMappings());
            }
            // CONDITION
            else if (SpoonType.CONDITION.isTypeOf(ctElement)) {
                changeType = new ConditionChange(srcNode, dstNode, this.getMappings());
            }
            // VARIABLES
            else if (SpoonType.VARIABLE_ACCESS.isTypeOf(ctElement)) {
                if (SpoonType.FIELD_WRITE.isTypeOf(ctElement)) {
                    changeType = new FieldWriteChange(srcNode, dstNode, this.getMappings());
                } else if (SpoonType.FIELD_READ.isTypeOf(ctElement)) {
                    changeType = new FieldReadChange(srcNode, dstNode, this.getMappings());
                } else if (SpoonType.FIELD_ACCESS.isTypeOf(ctElement)) {
                    changeType = new FieldAccessChange(srcNode, dstNode, this.getMappings());
                } else if (SpoonType.VARIABLE_WRITE.isTypeOf(ctElement)) {
                    changeType = new VariableWriteChange(srcNode, dstNode, this.getMappings());
                } else if (SpoonType.VARIABLE_READ.isTypeOf(ctElement)) {
                    changeType = new VariableReadChange(srcNode, dstNode, this.getMappings());
                } else {
                    changeType = new VariableAccessChange(srcNode, dstNode, this.getMappings());

                }
            }
            // LITERAL
            else if (SpoonType.LITERAL.isTypeOf(ctElement)) {
                changeType = new LiteralChange(srcNode, dstNode, this.getMappings());
            }
            // TYPE ACCESS // Target
            else if (SpoonType.TYPE_ACCESS.isTypeOf(ctElement)) {
                //if((getContainingTypeElement(ctElement)).getSimpleName().equals(ctElement.toString())) //Ignores implicit "this" target. Not convinced we should Ignore it.
                //return;
                changeType = new TargetChange(srcNode, dstNode, this.getMappings());
            }
            // THIS
            else if (SpoonType.THIS_ACCESS.isTypeOf(ctElement)) {
                changeType = new ThisChange(srcNode, dstNode, this.getMappings());
            }
            // GENERIC EXPRESSION
            else {
                changeType = new ExpressionChange(srcNode, dstNode, this.getMappings());
            }


        }
        // FIELD
        else if (SpoonType.FIELD.isTypeOf(ctElement)) {
            changeType = new FieldChange(srcNode, dstNode, this.getMappings());
            ((FieldChange) changeType).setType(((CtField) ctElement).getType());
        }
        // PARAMETER
        else if (SpoonType.PARAMETER.isTypeOf(ctElement)) {
            changeType = new ParameterChange(srcNode, dstNode, this.getMappings());
        }
        // TYPE REFERENCE
        else if (SpoonType.TYPE_REFERENCE.isTypeOf(ctElement)) {
            CtElement parentCt = null;
            try {
                parentCt = ctElement.getParent();
            } catch (ParentNotInitializedException e) {
                System.out.println(e.fillInStackTrace());
            }
            if (SpoonType.CLASS.isTypeOf(parentCt)) { // Parent is Class, node is TypeReference
                // Interface
                if (((CtClassImpl) ctElement.getParent()).getSuperInterfaces().contains(ctElement)) {
                    changeType = new InterfaceChange(srcNode, dstNode, this.getMappings());
                }
                // INHERITANCE (SuperClass)
                else if (((CtClassImpl) ctElement.getParent()).getSuperclass().equals(ctElement)) {
                    changeType = new InheritanceChange(srcNode, dstNode, this.getMappings());
                }
            } else if (SpoonType.INTERFACE.isTypeOf(parentCt)) { // Parent is Class, node is TypeReference
                // Interface
                if (((CtInterface) ctElement.getParent()).getSuperInterfaces().contains(ctElement)) {
                    changeType = new InheritanceChange(srcNode, dstNode, this.getMappings());
                }
            }
            // RETURN TYPE or THROWABLE
            else if (SpoonType.METHOD.isTypeOf(parentCt)) {
                // THROWABLE
                if (SpoonType.THROWABLE.isExactTypeOf(srcNode)) {
                    changeType = new ThrowableChange(srcNode, dstNode, this.getMappings());
                } else
                // RETURN TYPE
                {
                    changeType = new ReturnTypeChange(srcNode, dstNode, this.getMappings());
                }
            }
            // PARAMETER
            else if (SpoonType.PARAMETER.isTypeOf(parentCt)) {
                changeType = new ParameterTypeChange(srcNode, dstNode, this.getMappings());
            }
            // FIELD
            else if (SpoonType.FIELD.isTypeOf(parentCt)) {
                changeType = new FieldTypeChange(srcNode, dstNode, this.getMappings());
            }
            // LOCAL PARAM
            else if (SpoonType.LOCAL_VARIABLE.isTypeOf(parentCt)) {
                changeType = new VariableTypeChange(srcNode, dstNode, this.getMappings());
            }
            // TYPE ARGUMENT
            else if (SpoonType.TYPE_ARGUMENT.isTypeOf(srcNode)) { //TODO CHECK IF THIS SHOULD BE isTypeOf
                changeType = new TypeArgumentChange(srcNode, dstNode, this.getMappings());
            }
        }
        // MODIFIER
        else if (SpoonType.MODIFIER.isTypeOf(ctElement)) {
            changeType = new ModifierChange(srcNode, dstNode, this.getMappings());
        }
        // TYPE PARAMETER (aka GENERICS)
        else if (SpoonType.TYPE_PARAMETER.isTypeOf(ctElement)) {
            changeType = new GenericsChange(srcNode, dstNode, this.getMappings());
        }

        if (changeType != null) {
            if (action instanceof Insert)
                changeType.insertClassify();
            else if (action instanceof Delete)
                changeType.deleteClassify();
            else if (action instanceof Update)
                changeType.updateClassify();
            else if (action instanceof Move)
                changeType.moveClassify();
            change.setChangeType(changeType);
        }

        this.addToCodeChanges(change);
    }

    private List<SourceCodeChange> bottomUpFindMajorElement(SourceCodeChange scc) {
        if (scc.getAction() instanceof Insert) {
            return bottomUpFindMajorElementDstTree(((CtElement) scc.getNode().getMetadata(SpoonBuilder.SPOON_OBJECT)), scc.getNode(), scc);
        } else if (scc.getAction() instanceof Delete) {
            return bottomUpFindMajorElementSrcTree(((CtElement) scc.getNode().getMetadata(SpoonBuilder.SPOON_OBJECT)), scc.getNode(), scc);
        } else {
            List<SourceCodeChange> srcAndDst = bottomUpFindMajorElementSrcTree(((CtElement) scc.getNode().getMetadata(SpoonBuilder.SPOON_OBJECT)), scc.getNode(), scc);
            srcAndDst.addAll(bottomUpFindMajorElementDstTree(((CtElement) scc.getNode().getMetadata(SpoonBuilder.SPOON_OBJECT)), this.getMappings().getDst(scc.getNode()), scc));
            return srcAndDst;
        }
    }

    private List<SourceCodeChange> bottomUpFindMajorElementSrcTree(CtElement ctElement, ITree node, SourceCodeChange change) {
        List<SourceCodeChange> majorElementChanges = new ArrayList<>();
        SourceCodeChangeFactory factory = getChangeFactory();
        if (SpoonType.CLASS.isTypeOf(ctElement)) {
            change.setContainingClassSrc((CtClass) ctElement);

            SourceCodeChange sourceCodeChange = factory.create(new Meta(node, true, getMappings().hasSrc(node)));
            sourceCodeChange.setChangeType(new ClassChange(sourceCodeChange.getSrcInfo().getNode(), sourceCodeChange.getDstInfo().getNode(), getMappings()));
            change.setSrcParentChange(sourceCodeChange);
            majorElementChanges.add(sourceCodeChange); //TODO CHECK IF THIS IS OK

        } else if (SpoonType.EXECUTABLE.isTypeOf(ctElement)) {
            change.setContainingMethodSrc((CtExecutable) ctElement);

            SourceCodeChange sourceCodeChange = factory.create(new Meta(node, true, getMappings().hasSrc(node)));
            if (SpoonType.METHOD.isTypeOf(ctElement))
                sourceCodeChange.setChangeType(new MethodChange(sourceCodeChange.getSrcInfo().getNode(), sourceCodeChange.getDstInfo().getNode(), getMappings()));
            if (SpoonType.CONSTRUCTOR.isTypeOf(ctElement))
                sourceCodeChange.setChangeType(new ConstructorChange(sourceCodeChange.getSrcInfo().getNode(), sourceCodeChange.getDstInfo().getNode(), getMappings()));

            change.setSrcParentChange(sourceCodeChange);

            majorElementChanges.add(sourceCodeChange);
        } else if (SpoonType.STATEMENT.isTypeOf(ctElement)) {
            change.setContainingStatementSrc((CtStatement) ctElement);
        }
        if (node.getParent() != null) {
            majorElementChanges.addAll(bottomUpFindMajorElementSrcTree((CtElement) node.getParent().getMetadata(SpoonBuilder.SPOON_OBJECT), node.getParent(), change));
        }
        return majorElementChanges;

    }

    private List<SourceCodeChange> bottomUpFindMajorElementDstTree(CtElement ctElement, ITree node, SourceCodeChange change) {
        List<SourceCodeChange> majorElementChanges = new ArrayList<>();
        SourceCodeChangeFactory factory = getChangeFactory();

        if (SpoonType.CLASS.isTypeOf(ctElement)) {
            change.setContainingClassDst((CtClass) ctElement);
            SourceCodeChange sourceCodeChange = factory.create(new Meta(node, getMappings().hasDst(node), true));
            sourceCodeChange.setChangeType(new ClassChange(sourceCodeChange.getSrcInfo().getNode(), sourceCodeChange.getDstInfo().getNode(), getMappings()));

            change.setDstParentChange(sourceCodeChange);
            majorElementChanges.add(sourceCodeChange); //TODO CHECK IF THIS IS OK
        } else if (SpoonType.EXECUTABLE.isTypeOf(ctElement)) {
            change.setContainingMethodDst((CtExecutable) ctElement);
            SourceCodeChange sourceCodeChange = factory.create(new Meta(node, getMappings().hasDst(node), true));
            if (SpoonType.METHOD.isTypeOf(ctElement))
                sourceCodeChange.setChangeType(new MethodChange(sourceCodeChange.getSrcInfo().getNode(), sourceCodeChange.getDstInfo().getNode(), getMappings()));
            if (SpoonType.CONSTRUCTOR.isTypeOf(ctElement))
                sourceCodeChange.setChangeType(new ConstructorChange(sourceCodeChange.getSrcInfo().getNode(), sourceCodeChange.getDstInfo().getNode(), getMappings()));
            change.setDstParentChange(sourceCodeChange);
            majorElementChanges.add(sourceCodeChange); //TODO CHECK IF THIS IS OK
        } else if (SpoonType.STATEMENT.isTypeOf(ctElement)) {
            change.setContainingStatementDst((CtStatement) ctElement);
        }
        if (node.getParent() != null) {
            majorElementChanges.addAll(bottomUpFindMajorElementDstTree((CtElement) node.getParent().getMetadata(SpoonBuilder.SPOON_OBJECT), node.getParent(), change));
        }
        return majorElementChanges;

    }

    /**
     * This Methods Creates a new UPD type Change, classifies it and adds it to the result
     *
     * @param srcTree
     * @return returns the Change (for Hierarchy purposes)
     */
    private SourceCodeChange createUPDChangeInHierarchySrc(ITree srcTree) {
        return createUPDChangeInHierarchySrcDst(srcTree, this.getMappings().getSrc(srcTree));
    }

    /**
     * This Methods Creates a new UPD type Change, classifies it and adds it to the result
     *
     * @param dstTree
     * @return returns the Change (for Hierarchy purposes)
     */
    private SourceCodeChange createUPDChangeInHierarchyDst(ITree dstTree) {
        return createUPDChangeInHierarchySrcDst(this.getMappings().getDst(dstTree), dstTree);
    }

    /**
     * This Methods Creates a new UPD type Change, classifies it and adds it to the result
     *
     * @param srcTree
     * @param dstTree
     * @return
     */
    private SourceCodeChange createUPDChangeInHierarchySrcDst(ITree srcTree, ITree dstTree) { //TODO REMOVE DOUBLE
        Update update = new Update(srcTree, srcTree.getLabel());
        SourceCodeChange change = this.getChangeFactory().create(update);
        classify(update, srcTree, dstTree);
        addToCodeChanges(change);
        return change;
    }


    private void addToCodeChanges(SourceCodeChange change) {
        if (includeUnclassified || !change.isUnclassified())
            this.addCodeChange(change);
    }

//    public SourceCodeChange getChangeFromSrcNode(ITree node) {
//        for (SourceCodeChange scc : getCodeChanges()) {
//            if (scc.getAction().getNode().equals(node))
//                return scc;
//        }
//        return null;
//    }
//
//    public SourceCodeChange getChangeFromDstNode(ITree node) {
//        for (SourceCodeChange scc : getCodeChanges()) {
//            if (this.getMappings().getDst(scc.getAction().getNode()) != null)
//                if (this.getMappings().getDst(scc.getAction().getNode()).equals(node))
//                    return scc;
//        }
//        return null;
//    }

//    public FileDependencyChanges getExtractor() {
//        return extractor;
//    }
}