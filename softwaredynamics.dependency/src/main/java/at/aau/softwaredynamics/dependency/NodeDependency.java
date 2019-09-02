package at.aau.softwaredynamics.dependency;

import at.aau.softwaredynamics.classifier.util.LineNumberRange;
import at.aau.softwaredynamics.gen.SpoonBuilder;
import at.aau.softwaredynamics.util.SpoonType;
import com.github.gumtreediff.tree.ITree;
import spoon.reflect.code.*;
import spoon.reflect.declaration.*;
import spoon.reflect.reference.CtArrayTypeReference;
import spoon.reflect.reference.CtTypeReference;
import spoon.support.reflect.declaration.CtClassImpl;
import spoon.support.reflect.reference.CtTypeReferenceImpl;

/**
 * Connects a {@link ITree} node to a single {@link Dependency}.
 * This creates the {@link Dependency} on creation.
 */
public class NodeDependency {
    private static final String UNDEFINED = "UNDEFINED";
    private ITree node;
    private Dependency dependency;
    private DependencyFilter filter;
    private NodeDependencyTree parentNodeDependencyTree;
    private LineNumberRange lineNumbers;

    /**
     * Creates a new NodeDependency and classifies it according to the given filter
     *
     * @param node                           the {@link ITree} node from which the dependency originates, needs to have a Spoon object in its metadata
     * @param filter                         the {@link DependencyFilter} that constraints if this {@link Dependency} will be null
     * @param parentNodeDependencyTree the parent {@link NodeDependencyTree}, set to null if not of interest
     * @param lineNumbers                    the line number information of the node, set to null if not of interest
     */
    public NodeDependency(ITree node, DependencyFilter filter, NodeDependencyTree parentNodeDependencyTree, LineNumberRange lineNumbers) {
        this.parentNodeDependencyTree = parentNodeDependencyTree;
        this.node = node;
        this.filter = filter;
        this.lineNumbers = lineNumbers;

        CtElement ctElement = (CtElement) node.getMetadata(SpoonBuilder.SPOON_OBJECT);
        // STATEMENT
        if (SpoonType.STATEMENT.isTypeOf(ctElement)) {
            // INVOCATION or CONSTRUCTOR CALL
            if (SpoonType.INVOCATION.isTypeOf(ctElement)) {
                // TODO this() and super() invocations are not labeled correctly

                String dependentOnClass = UNDEFINED; //TODO Stays undefined in World.java in QuickGDX in commit b845fe92db22ff4386f8fa6c8c27c221489ed5dc
                String executable = ((CtInvocation) ctElement).getExecutable().getSignature();
                String target = "";

                CtExpression targetElement = ((CtInvocation) ctElement).getTarget();

                // Go up the tree until Type Access is found
                while (SpoonType.TARGETED_EXPRESSION.isTypeOf(targetElement) && !SpoonType.VARIABLE_READ.isTypeOf(targetElement) && !SpoonType.SUPER_ACCESS.isTypeOf(targetElement) && !SpoonType.TYPE_ACCESS.isTypeOf(targetElement)
                        && ((CtTargetedExpression) targetElement).getTarget() != null) {
                    targetElement = ((CtTargetedExpression) targetElement).getTarget();
                }

                // check on what the invocation was made and set it as the dependent class.
                if (SpoonType.VARIABLE_READ.isTypeOf(targetElement)) {
                    CtTypeReference ctType = parseFineGrainedVariableType((CtVariableRead) targetElement);
                    if (ctType != null) dependentOnClass = ctType.getQualifiedName();

                } else if (SpoonType.CONSTRUCTOR_CALL.isTypeOf(targetElement)) {
                    executable = ((CtConstructorCall) targetElement).getExecutable().getSignature();
                    dependentOnClass = ((CtConstructorCall) targetElement).getExecutable().getType().getQualifiedName();
                } else if (SpoonType.LITERAL.isTypeOf(targetElement)) {
                    CtTypeReference type = targetElement.getType();
                    if (!isBasicType(type)) {
                        dependentOnClass = type.getQualifiedName();
                        target = type.getQualifiedName();
                    }
                } else { // INVOCATION on an Invocation, or TypeAccess
                    if (targetElement == null) {
                        dependentOnClass = ((CtInvocation) ctElement).getExecutable().getType().getQualifiedName();
                    } else {
                        dependentOnClass = targetElement.toString();
                    }
                }

                if (target.equals("") && targetElement != null) target = targetElement.toString();
                if (targetElement != null) {
                    executable = target + "." + executable;
                }
                dependency = new Dependency(executable, DependencyType.CALL, dependentOnClass);

                //TODO Target (last arg) can be field - check targets target until "CtTypeAccess"
            }
            // CONSTRUCTOR
            else if (SpoonType.CONSTRUCTOR_CALL.isTypeOf(ctElement)) {
                String executable = ((CtConstructorCall) ctElement).getExecutable().getSignature();
                CtTypeReference targetElement = ((CtConstructorCall) ctElement).getExecutable().getType();

                dependency = new Dependency(executable, DependencyType.CALL, targetElement.getQualifiedName());
            }
            //TYPE DECLARATIONS
            else if (SpoonType.TYPE.isTypeOf(ctElement)) {
                // CLASS
                if (SpoonType.CLASS.isTypeOf(ctElement)) { //I deleted new_class here. new class is part of constructor according to ctmodel
                    //ENUM
                    if (SpoonType.ENUM.isTypeOf(ctElement)) {
                        //IF ENUM is TOP --> Then dependent on that Enum, otherwise dependent on parent class.
                        //System.out.println(ctElement + "Hello, Enums are missing!"); //TODO Hello Enums are missing!
//                        changeType = new EnumChange(srcNode, dstNode, this.getMappings());
                    }
                    //GENERIC CLASS
                    else {
//                        changeType = new ClassChange(srcNode, dstNode, this.getMappings());
                    }
                }
            }
            // Variable Declaration
            else if (SpoonType.LOCAL_VARIABLE.isTypeOf(ctElement)) {
//                changeType = new VariableDeclarationChange(srcNode, dstNode, this.getMappings());
            }
            // IF
            else if (SpoonType.IF.isTypeOf(ctElement)) {
//                changeType = new IfBranchChange(srcNode, dstNode, this.getMappings());
            }
            // ELSE
            else if (SpoonType.BRANCH.isTypeOf(ctElement)) {
//                changeType = new ElseBranchChange(srcNode, dstNode, this.getMappings());
            }

            // GENERIC STATEMENT
            else {
//                changeType = new StatementChange(srcNode, dstNode, this.getMappings());
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
//            changeType = new ConstructorChange(srcNode, dstNode, this.getMappings());
        }
        // METHOD
        else if (SpoonType.METHOD.isTypeOf(ctElement)) {
//            changeType = new MethodChange(srcNode, dstNode, this.getMappings());
        }
        // EXPRESSION
        else if (SpoonType.EXPRESSION.isTypeOf(ctElement)) {
            //Argument
//            if (SpoonType.ARGUMENT.isTypeOf(ctElement)) {
//                //TODO
//                System.out.println("");
//            }
            // CONDITION
            if (SpoonType.CONDITION.isTypeOf(ctElement)) {
//                System.out.println("condition");
//                changeType = new ConditionChange(srcNode, dstNode, this.getMappings());
            }
            // FIELDS
            else if (SpoonType.FIELD_ACCESS.isTypeOf(ctElement)) {
                //TODO check for variable getDeclaringType/getType and remove IF!!!
                if (true) {
                    String executable = ctElement.toString();
                    String target = "";
                    if (SpoonType.TYPE_ACCESS.isTypeOf(((CtFieldAccess) ctElement).getTarget())) {
                        target = ((CtTypeAccess) ((CtTargetedExpression) ctElement).getTarget()).getAccessedType().getQualifiedName();
                    } else if (SpoonType.THIS_ACCESS.isTypeOf(((CtFieldAccess) ctElement).getTarget())) {
                        //TODO NPE!
                        target = ((CtTargetedExpression) ctElement).getTarget().getType().getQualifiedName();
                    } else if (SpoonType.VARIABLE_ACCESS.isTypeOf(((CtFieldAccess) ctElement).getTarget())) {
                        if (SpoonType.VARIABLE_READ.isTypeOf(((CtFieldAccess) ctElement).getTarget())) { //Check if target if FieldRead --> Then use Declaring type. (Otherwise Nullpointer) ... //TODO Fuck, still wrong --> Extract the whole process in  a Function (getTarget(CTElement) and call that on the type?)
                            CtTypeReference declaringType = ((CtVariableRead) ((CtFieldAccess) ctElement).getTarget()).getVariable().getType();
                            if (declaringType != null) {
                                target = declaringType.getQualifiedName();
                            } else {
                                target = UNDEFINED;
                            }
                        } else{
                            target = ((CtFieldAccess) ctElement).getTarget().getType().getQualifiedName();
                        }
                    }

                    if (SpoonType.FIELD_WRITE.isTypeOf(ctElement)) {
                        dependency = new Dependency(executable, DependencyType.WRITE, target);
                    } else if (SpoonType.FIELD_READ.isTypeOf(ctElement)) {
                        dependency = new Dependency(executable, DependencyType.READ, target);
                    }
                }
            } else if (SpoonType.VARIABLE_ACCESS.isTypeOf(ctElement)) {
                // variable access are no dependency as long as it is no field
                return;
            }
            // LITERAL
            else if (SpoonType.LITERAL.isTypeOf(ctElement)) {
                CtTypeReference type = ((CtLiteral) ctElement).getType();
                if (!isBasicType(type) && !type.getSimpleName().equals("<nulltype>")) {
                    dependency = new Dependency(type.getQualifiedName(), DependencyType.TYPE, type.getQualifiedName());
                }
            }
            // TYPE ACCESS // Target
            else if (SpoonType.TYPE_ACCESS.isTypeOf(ctElement)) {
                CtTypeReference type = ((CtTypeAccess) (ctElement)).getAccessedType();
                if (type.getPackage() != null && !type.getPackage().toString().equals("")) {
                    dependency = new Dependency(type.getQualifiedName(), DependencyType.TYPE, type.getQualifiedName());
                } else {
                    //TODO no difference
                    dependency = new Dependency(type.getQualifiedName(), DependencyType.TYPE, type.getQualifiedName());
                }
            }
            // THIS
            else if (SpoonType.THIS_ACCESS.isTypeOf(ctElement)) {
//                changeType = new ThisChange(srcNode, dstNode, this.getMappings());
            } else if (SpoonType.TYPED_ELEMENT.isTypeOf(ctElement)) {
                CtTypeReference type = ((CtTypedElement) ctElement).getType();
                while (SpoonType.ARRAY_TYPE_REFERENCE.isTypeOf(type)) {
                    type = ((CtArrayTypeReference) type).getComponentType();
                }
                if (type != null && !isBasicType(type) && !type.getSimpleName().equals("<nulltype>")) {
                    dependency = new Dependency(type.getQualifiedName(), DependencyType.TYPE, type.getQualifiedName());
                }
            }
            // GENERIC EXPRESSION
            else {
//                changeType = new ExpressionChange(srcNode, dstNode, this.getMappings());
            }


        }
        // FIELD
        else if (SpoonType.FIELD.isTypeOf(ctElement)) {
//            changeType = new FieldChange(srcNode, dstNode, this.getMappings());
//            ((FieldChange) changeType).setType(((CtField)ctElement).getType());
        }
        // PARAMETER
        else if (SpoonType.PARAMETER.isTypeOf(ctElement)) {
//            changeType = new ParameterChange(srcNode, dstNode, this.getMappings());
        }
        // TYPE REFERENCE
        else if (SpoonType.TYPE_REFERENCE.isTypeOf(ctElement)) {
            CtElement ctNonArrayType = ctElement;
            while (SpoonType.ARRAY_TYPE_REFERENCE.isTypeOf(ctNonArrayType)) {
                ctNonArrayType = ((CtArrayTypeReference) ctNonArrayType).getComponentType();
            }
            //Not interested in Basic Types or Generics without Type
            if (isBasicType((CtTypeReference) ctNonArrayType) || SpoonType.TYPE_PARAMETER_REFERENCE.isTypeOf(ctElement)) {
                return;
            }

            CtElement parentCt = null;
            try {
                parentCt = ctElement.getParent();
            } catch (ParentNotInitializedException e) {
                // TODO log as warning
                //System.out.println(e.fillInStackTrace());
            }
            if (SpoonType.CLASS.isTypeOf(parentCt)) { // Parent is Class, node is TypeReference
                // Interface
                if (((CtClassImpl) ctElement.getParent()).getSuperInterfaces().contains(ctElement)) {
                    String label = ctElement.toString();
                    dependency = new Dependency(label, DependencyType.INTERFACE, label);
                }
                // INHERITANCE (SuperClass)
                else if (((CtClassImpl) ctElement.getParent()).getSuperclass().equals(ctElement)) {
                    CtTypeReference ctTypeReference = (CtTypeReferenceImpl) ctElement;
                    dependency = new Dependency(ctElement.toString(), DependencyType.INHERITANCE,
                            ctTypeReference.getPackage() + "." + ctTypeReference.getSimpleName());
                }
            } else if (SpoonType.INTERFACE.isTypeOf(parentCt)) { // Parent is Class, node is TypeReference
                // Interface
                if (((CtInterface) ctElement.getParent()).getSuperInterfaces().contains(ctElement)) {
                    //TODO
                }
            }
            // RETURN TYPE or THROWABLE
            else if (SpoonType.METHOD.isTypeOf(parentCt)) {
                // THROWABLE or RETURN TYPE
                if (SpoonType.TYPE_REFERENCE.isTypeOf(ctElement)) {
                    CtTypeReference ctTypeReference = (CtTypeReferenceImpl) ctElement;
                    dependency = new Dependency(ctElement.toString(), DependencyType.TYPE,
                            ((CtTypeReference) ctNonArrayType).getPackage() + "." + ctTypeReference.getSimpleName());
                } else {
                    dependency = new Dependency(ctElement.toString(), DependencyType.TYPE, ctElement.toString());
                }
            }
            // PARAMETER, FIELD OR LOCAL VARIABLE
            else if (SpoonType.PARAMETER.isTypeOf(parentCt) || SpoonType.FIELD.isTypeOf(parentCt) || SpoonType.LOCAL_VARIABLE.isTypeOf(parentCt)) {
                String type = ((CtTypeReference) ctNonArrayType).getQualifiedName();
                dependency = new Dependency(type, DependencyType.TYPE, type);
            }

            // TYPE REFERENCE AS PARENT => GENERIC!
            else if (SpoonType.TYPE_REFERENCE.isTypeOf(parentCt)) {
                String type = ((CtTypeReference) ctNonArrayType).getQualifiedName();
                dependency = new Dependency(type, DependencyType.TYPE, type);
            }

            // INHERITANCE on a GENERIC
            else if (SpoonType.TYPE_PARAMETER.isTypeOf(parentCt)) {
                String type = ((CtTypeReference) ctNonArrayType).getQualifiedName();
                dependency = new Dependency(type, DependencyType.INHERITANCE, type);
            } else if (SpoonType.CATCH_VARIABLE.isTypeOf(parentCt)) {
                String type = ((CtTypeReference) ctNonArrayType).getQualifiedName();
                dependency = new Dependency(type, DependencyType.TYPE, type);
            }

            // EXPRESSION AS PARENT => CAST!
            else if (SpoonType.EXPRESSION.isTypeOf(parentCt)) { // TODO check if expression can only have casts as typeReference
                String type = ((CtTypeReference) ctNonArrayType).getQualifiedName();
                dependency = new Dependency(type, DependencyType.CAST, type);
            }

        }

        // TYPE PARAMETER (aka GENERICS)
        else if (SpoonType.TYPE_PARAMETER.isTypeOf(ctElement)) {
//            for (CtType<?> nestedType : ((CtTypeParameter) ctElement).getNestedTypes()) {
//                dependency = new Dependency(nestedType.getQualifiedName(), DependencyType.TYPE, nestedType.getQualifiedName());
//            }
        }

        if (dependency != null) {
            dependency.setSelfDependency(NodeDependency.isSelfDependent(ctElement, dependency));
            filterDependency();
        }
    }

    public static Boolean isSelfDependent(CtElement el, Dependency dep) {
        CtElement ctElement = el.getParent();
        do {
            if (ctElement.getParent() != null) {
                ctElement = ctElement.getParent();
            } else {
                return false;
            }
        }
        while (!SpoonType.CLASS.isTypeOf(ctElement));

        String[] myself = ((CtTypeInformation) ctElement).getQualifiedName().split("[.\\$]");
        String[] classSplit = dep.getDependentOnClass().split("[.\\$]");

        for (int i = 0; i < myself.length; i++) {
            if (classSplit.length - 1 < i || !myself[i].equals(classSplit[i]))
                return false;
        }
        return true;
    }

    /**
     * Checks if supplied type is a basic type
     *
     * @param type the type to check
     * @return true if basic type (int, char ...)
     */
    public static boolean isBasicType(CtTypeReference type) {
        return !type.box().equals(type);
    }

    /**
     * Sets the nested dependency to null if the filter does not
     * accept it.
     */
    private void filterDependency() {
        if (!filter.accepts(dependency))
            dependency = null;
    }

    public ITree getNode() {
        return node;
    }

    public void setNode(ITree node) {
        this.node = node;
    }

    public Dependency getDependency() {
        return dependency;
    }

    @Override
    public String toString() {
        return dependency.toString() + "\n       --- " + node.toShortString();
    }

    private CtTypeReference parseFineGrainedVariableType(CtVariableRead variableRead) {
        CtTypeReference type = variableRead.getVariable().getType();
        if (type != null) {
            // parse inner type if this is an array
            if (SpoonType.ARRAY_TYPE_REFERENCE.isTypeOf(type)) {
                type = ((CtArrayTypeReference) type).getArrayType();
            }
            return type;
        } else {
            return null;
        }
    }

    public String toOutputString(String separator) {
        return dependency.toOutputString(separator) + separator + parentNodeDependencyTree + separator + lineNumbers.getStartLine() + separator + lineNumbers.getStartOffset() + separator + lineNumbers.getEndLine() + separator + lineNumbers.getEndOffset();
    }

    public LineNumberRange getLineNumbers() {
        return lineNumbers;
    }

    public void setLineNumbers(LineNumberRange lineNumbers) {
        this.lineNumbers = lineNumbers;
    }

    public NodeDependencyTree getContainingElement() {
        return this.parentNodeDependencyTree;
    }
}
