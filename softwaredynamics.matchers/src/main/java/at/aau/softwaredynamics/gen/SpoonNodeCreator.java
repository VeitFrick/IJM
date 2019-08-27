package at.aau.softwaredynamics.gen;

import at.aau.softwaredynamics.util.CtModifier;
import at.aau.softwaredynamics.util.ModifierSourcePosition;
import com.github.gumtreediff.tree.ITree;
import spoon.reflect.code.CtExpression;
import spoon.reflect.code.CtFor;
import spoon.reflect.code.CtIf;
import spoon.reflect.cu.position.DeclarationSourcePosition;
import spoon.reflect.declaration.CtMethod;
import spoon.reflect.declaration.CtModifiable;
import spoon.reflect.declaration.CtType;
import spoon.reflect.declaration.CtVariable;
import spoon.reflect.reference.CtTypeReference;
import spoon.reflect.visitor.CtInheritanceScanner;
import spoon.support.reflect.CtExtendedModifier;

/**
 * responsible to add additional nodes
 * only overrides scan* to add new nodes
 */
class SpoonNodeCreator extends CtInheritanceScanner {
    private final SpoonTreeScanner builder;

    SpoonNodeCreator(SpoonTreeScanner builder) {
        this.builder = builder;
    }

    @Override
    public void scanCtModifiable(CtModifiable m) {

        for (CtExtendedModifier extendedModifier : m.getExtendedModifiers()) {
            ITree modifier = builder.createNode("MODIFIER", extendedModifier.toString());

            CtModifier metaMod = new CtModifier(m, extendedModifier.getKind().toString());
            // if the modifier position is defined, replace the position of the element with that.
            if(metaMod.getPosition() instanceof DeclarationSourcePosition) {
                /* this should always be the case
                 * see https://github.com/INRIA/spoon/issues/1968
                 */
                ModifierSourcePosition newPos = new ModifierSourcePosition((DeclarationSourcePosition) metaMod.getPosition());
                metaMod.setPosition(newPos);
            }

            modifier.setMetadata(SpoonBuilder.SPOON_OBJECT, metaMod);
            builder.addSiblingNode(modifier);
        }
        super.scanCtModifiable(m);
    }

    @Override
    public void visitCtIf(CtIf e) {
//        if (e.getElseStatement() != null) { // add the else statement to the tree if it exists
//            ITree elseStatement = builder.createNode("THEN", e.getElseStatement().toString());
//            elseStatement.setMetadata(SpoonBuilder.SPOON_OBJECT, e.getElseStatement());
//            builder.addSiblingNode(elseStatement);
//            return;
//        }

//        ITree condition = builder.createNode("CONDITION", e.getCondition().toString());
//        condition.setMetadata(SpoonBuilder.SPOON_OBJECT, (CtExpression<Boolean>)e.getCondition());
//        builder.pushNodeToTree(condition);
//        super.scan(e.getCondition());
        super.visitCtIf(e);
    }

    @Override
    public void visitCtFor(CtFor e) {
//        if (e.getExpression() != null) {
//            ITree condition = builder.createNode("CONDITION", e.getExpression().toString());
//            condition.setMetadata(SpoonBuilder.SPOON_OBJECT, new CtCondition(e.getExpression()));
//            builder.addSiblingNode(condition);
//        }

        super.visitCtFor(e);
    }

    @Override
    public <T> void scanCtVariable(CtVariable<T> v) {
        if (v.getType() != null) {
            ITree variableType = builder.createNode("VARIABLE_TYPE", v.getType().getQualifiedName());
            variableType.setMetadata(SpoonBuilder.SPOON_OBJECT, v.getType());
            builder.addSiblingNode(variableType);
            this.visitCtTypeReference(v.getType());
        }
        super.scanCtVariable(v);
    }

    @Override
    public <T> void visitCtMethod(CtMethod<T> e) {

        // add the return type of the method
        if(e.getType() != null) {// if the method is a constructor
            ITree returnType = builder.createNode("RETURN_TYPE", e.getType().getQualifiedName());
            returnType.setMetadata(SpoonBuilder.SPOON_OBJECT, e.getType());
            builder.addSiblingNode(returnType);
        }


        // add the Exceptions/Throwables
        for (CtTypeReference<? extends Throwable> typeReference :  e.getThrownTypes()) {
            ITree throwable = builder.createNode("THROWABLE", typeReference.getQualifiedName());
            throwable.setMetadata(SpoonBuilder.SPOON_OBJECT, typeReference);
            builder.addSiblingNode(throwable);
        }
        super.visitCtMethod(e);

    }

    @Override
    public <T> void scanCtType(CtType<T> e) {
        if (e.getSuperclass() != null) {
            ITree superClass = builder.createNode("SUPERCLASS", e.getSuperclass().getQualifiedName());
            superClass.setMetadata(SpoonBuilder.SPOON_OBJECT, e.getSuperclass());

            builder.addSiblingNode(superClass);

            // this is used to get the generic type parameters of a superclass
            visitCtTypeReference(e.getSuperclass());
        }

        for (CtTypeReference<?> ctInterface : e.getSuperInterfaces()) {
            ITree superInterface = builder.createNode("INTERFACE", ctInterface.getQualifiedName());
            superInterface.setMetadata(SpoonBuilder.SPOON_OBJECT, ctInterface);

            builder.addSiblingNode(superInterface);
        }
        super.scanCtType(e);
    }

    @Override
    public <T> void visitCtTypeReference(CtTypeReference<T> e) {
        for (CtTypeReference<?> typeArgument : e.getActualTypeArguments()) {
            ITree type = builder.createNode("TYPE_ARGUMENT", typeArgument.toString());
            type.setMetadata(SpoonBuilder.SPOON_OBJECT, typeArgument);
            builder.addSiblingNode(type);
            visitCtTypeReference(typeArgument); //TODO preserve type parenting for better matching?
        }
        super.visitCtTypeReference(e);
    }

    @Override
    public <T> void scanCtExpression(CtExpression<T> expression) {
        for (CtTypeReference<?> typeCast : expression.getTypeCasts()) {
            ITree cast = builder.createNode("TYPE_CAST", typeCast.getQualifiedName());
            cast.setMetadata(SpoonBuilder.SPOON_OBJECT, typeCast);
            builder.addSiblingNode(cast);
            visitCtTypeReference(typeCast);
        }
        super.scanCtExpression(expression);
    }

}
