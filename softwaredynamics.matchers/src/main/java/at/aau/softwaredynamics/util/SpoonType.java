package at.aau.softwaredynamics.util;

import at.aau.softwaredynamics.gen.SpoonBuilder;
import com.github.gumtreediff.tree.ITree;
import spoon.reflect.code.*;
import spoon.reflect.declaration.*;
import spoon.reflect.reference.*;

public enum SpoonType {
    ANNOTATION_FIELD_ACCESS(CtAnnotationFieldAccess.class),
    ANNOTATION(CtAnnotation.class),
    ANNOTATION_METHOD(CtAnnotationMethod.class),
    ANNOTATION_TYPE(CtAnnotationType.class),
    ANONYMOUS_EXECUTABLE(CtAnonymousExecutable.class),
    ARRAY_ACCESS(CtArrayAccess.class),
    ARRAY_READ(CtArrayRead.class),
    ARRAY_TYPE_REFERENCE(CtArrayTypeReference.class),
    ARRAY_WRITE(CtArrayWrite.class),
    ASSERT(CtAssert.class),
    ASSIGNMENT(CtAssignment.class),
    BINARY_OPERATOR(CtBinaryOperator.class),
    BLOCK(CtBlock.class),
    BREAK(CtBreak.class),
    CASE(CtCase.class),
    CATCH(CtCatch.class),
    CATCH_VARIABLE(CtCatchVariable.class),
    CATCH_VARIABLE_REFERNCE(CtCatchVariableReference.class),
    CLASS(CtClass.class),
    CODE_ELEMENT(CtCodeElement.class),
    CODE_SNIPPET_EXPRESSION(CtCodeSnippetExpression.class),
    CODE_SNIPPET_STATEMENT(CtCodeSnippetStatement.class),
    COMMENT(CtComment.class),
    CONDITIONAL(CtConditional.class),
    CONSTRUCTOR_CALL(CtConstructorCall.class),
    CONSTRUCTOR(CtConstructor.class),
    CONTINUE(CtContinue.class),
    DO(CtDo.class),
    ELEMENT(CtElement.class),
    ENUM(CtEnum.class),
    ENUM_VALUE(CtEnumValue.class),
    EXECUTABLE(CtExecutable.class),
    EXECUTABLE_REFERENCE_REFERENCE(CtExecutableReferenceExpression.class),
    EXECUTABLE_REFERENCE(CtExecutableReference.class),
    EXPRESSION(CtExpression.class),
    FIELD_ACCESS(CtFieldAccess.class),
    FIELD(CtField.class),
    FIELD_READ(CtFieldRead.class),
    FIELD_REFERENCE(CtFieldReference.class),
    FIELD_WRITE(CtFieldWrite.class),
    FOR_EACH(CtForEach.class),
    FOR(CtFor.class),
    IF(CtIf.class),
    // IMPORT (CtImport.class)i
    INTERFACE(CtInterface.class),
    INTERSECTION_TYPE_REFERENCE(CtIntersectionTypeReference.class),
    INVOCATION(CtInvocation.class),
    JAVA_DOC(CtJavaDoc.class),
    JAVA_DOC_TAG(CtJavaDocTag.class),
    LAMBDA(CtLambda.class),
    LITERAL(CtLiteral.class),
    LOCAL_VARIABLE(CtLocalVariable.class),
    LOCAL_VARIABLE_REFERENCE(CtLocalVariableReference.class),
    LOOP(CtLoop.class),
    METHOD(CtMethod.class),
    //ROOT_PACKAGE(CtModelImpl.CtRootPackageIm.class),
    // MODULE (CtModule.class),
    // MODULE_REFERENCE (CtModuleReference.class),
    // MODULE_REQUIREMENT (CtModuleRequirement.class),
    NAMED_ELEMENT(CtNamedElement.class),
    NEW_ARRAY(CtNewArray.class),
    NEW_CLASS(CtNewClass.class),
    OPERATOR_ASSIGNMENT(CtOperatorAssignment.class),
    // PACKAGE_EXPORT (CtPackageExport.class),
    PACKAGE(CtPackage.class),
    PACKAGE_REFERENCE(CtPackageReference.class),
    PARAMETER(CtParameter.class),
    PARAMETER_REFERENCE(CtParameterReference.class),
    // PROVIDED_SERVICE (CtProvidedService.class),
    REFERENCE(CtReference.class),
    RETURN(CtReturn.class),
    STATEMENT(CtStatement.class),
    STATEMENT_LIST(CtStatementList.class),
    SUPER_ACCESS(CtSuperAccess.class),
    SWITCH(CtSwitch.class),
    SYNCHRONIZED(CtSynchronized.class),
    TARGETED_EXPRESSION(CtTargetedExpression.class),
    THIS_ACCESS(CtThisAccess.class),
    THROW(CtThrow.class),
    TRY(CtTry.class),
    TRY_WITH_RESOURCE(CtTryWithResource.class),
    TYPE_ACCESS(CtTypeAccess.class),
    TYPE(CtType.class),
    TYPED_ELEMENT(CtTypedElement.class),
    TYPE_PARAMETER(CtTypeParameter.class),
    TYPE_PARAMETER_REFERENCE(CtTypeParameterReference.class),
    TYPE_REFERENCE(CtTypeReference.class),
    UNARY_OPERATOR(CtUnaryOperator.class),
    UNBOUND_VARIABLE_REFERENCE(CtUnboundVariableReference.class),
    // USED_SERVICE (CtUsedService.class),
    VARIABLE_ACCESS(CtVariableAccess.class),
    VARIABLE_READ(CtVariableRead.class),
    VARIABLE_REFERENCE(CtVariableReference.class),
    VARIABLE_WRITE(CtVariableWrite.class),
    WHILE(CtWhile.class),
    WILDCARD_REFERENCE(CtWildcardReference.class),

    ABSTRACT_INVOCATION(CtAbstractInvocation.class),
    // WILDCARD_STATIC_TYPE_MEMBER_REFERENCE (CtWildcardStaticTypeMemberReference.class),

    // self-added:
    MODIFIER(CtModifier.class),
    CONDITION(CtCondition.class),
    BRANCH(CtBranch.class),
    ARGUMENT(CtArgument.class),
    THROWABLE(TYPE_REFERENCE, "THROWABLE"),
    TYPE_ARGUMENT(TYPE_REFERENCE, "TYPE_ARGUMENT");

    private final Class<? extends CtElement> ctType;
    private final Integer treeType;

    SpoonType(Class<? extends CtElement> ctClass) {
        this.ctType = ctClass;
        this.treeType = getTypeName(this.ctType.getSimpleName()).hashCode();
    }

    SpoonType(SpoonType ctClass, String customString) {
        this.ctType = ctClass.ctType;
        this.treeType = customString.hashCode();
    }

    public boolean isTypeOf(CtElement element) {
        if (element == null) return false;
        return ctType.isInstance(element);
    }

    public boolean isTypeOf(ITree node) {
        if (node == null) return false;
        CtElement element = (CtElement) node.getMetadata(SpoonBuilder.SPOON_OBJECT);
        return ctType.isInstance(element);
    }

    public boolean isExactTypeOf(ITree tree) {
        return tree.getType() == this.treeType;
    }

    public boolean isExactTypeOf(CtElement element) {
        return element.getClass() == ctType;
    }

    private String getTypeName(String simpleName) {
        // Removes the "Ct" at the beginning
        return simpleName.substring(2);
    }

    public static boolean isStatement(CtElement ctElement) {
        return IF.isTypeOf(ctElement) || LOCAL_VARIABLE.isTypeOf(ctElement) ||
                FIELD_READ.isTypeOf(ctElement) ||
                VARIABLE_READ.isTypeOf(ctElement) ||
                INVOCATION.isTypeOf(ctElement) ||
                ASSIGNMENT.isTypeOf(ctElement) ||
                UNARY_OPERATOR.isTypeOf(ctElement);
    }

    public static boolean isCondition(CtElement ctElement) {
        if (CONDITION.isTypeOf(ctElement)) return true;
        if (EXPRESSION.isTypeOf(ctElement) && IF.isTypeOf(ctElement.getParent())) {
            try {
                String exprType = ((CtExpression) ctElement).getType().getTypeDeclaration().getActualClass().getSimpleName();
                if (exprType != null && exprType.equals("boolean")) return true;
            } catch (Exception e) {
                return false;
            }
        }
        return false;
    }


    /**
     * Used to determine whether a node is "important" on it own, or if an element higher up in the Tree should be modified
     *
     * @param ctElement
     * @return
     */
    public static boolean isMinorElement(CtElement ctElement) {
        return ASSIGNMENT.isTypeOf(ctElement) ||
                ARRAY_ACCESS.isTypeOf(ctElement) ||
                ARRAY_READ.isTypeOf(ctElement) ||
                ARRAY_WRITE.isTypeOf(ctElement) ||
                ARRAY_TYPE_REFERENCE.isTypeOf(ctElement) ||
                BINARY_OPERATOR.isTypeOf(ctElement) ||
                CATCH_VARIABLE.isTypeOf(ctElement) ||
                CATCH_VARIABLE_REFERNCE.isTypeOf(ctElement) ||
                CONDITIONAL.isTypeOf(ctElement) ||
                CONTINUE.isTypeOf(ctElement) ||
                DO.isTypeOf(ctElement) || //Not certain about this
                FIELD_READ.isTypeOf(ctElement) ||
                FIELD_ACCESS.isTypeOf(ctElement) ||
                FIELD_REFERENCE.isTypeOf(ctElement) ||
                FIELD_WRITE.isTypeOf(ctElement) ||
                LITERAL.isTypeOf(ctElement) ||
                LOCAL_VARIABLE_REFERENCE.isTypeOf(ctElement) ||
                PARAMETER.isTypeOf(ctElement) ||
                PARAMETER_REFERENCE.isTypeOf(ctElement) ||
                RETURN.isTypeOf(ctElement) ||
                THIS_ACCESS.isTypeOf(ctElement) ||
                THROW.isTypeOf(ctElement) ||
                TYPE_PARAMETER.isTypeOf(ctElement) ||
                TYPE_PARAMETER_REFERENCE.isTypeOf(ctElement) ||
                UNARY_OPERATOR.isTypeOf(ctElement) ||
                VARIABLE_READ.isTypeOf(ctElement) ||
                VARIABLE_ACCESS.isTypeOf(ctElement) ||
                VARIABLE_REFERENCE.isTypeOf(ctElement) ||
                VARIABLE_WRITE.isTypeOf(ctElement) ||
                TYPE_REFERENCE.isTypeOf(ctElement) ||
                TYPE_ACCESS.isTypeOf(ctElement);
    }


}
