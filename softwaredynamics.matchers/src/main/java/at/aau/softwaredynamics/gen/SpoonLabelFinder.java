package at.aau.softwaredynamics.gen;

import at.aau.softwaredynamics.util.CtArgument;
import at.aau.softwaredynamics.util.CtBranch;
import at.aau.softwaredynamics.util.CtCondition;
import spoon.reflect.code.*;
import spoon.reflect.declaration.CtNamedElement;
import spoon.reflect.reference.CtTypeReference;
import spoon.reflect.visitor.CtInheritanceScanner;

public class SpoonLabelFinder extends CtInheritanceScanner {
	public String label = "";

	@Override
	public void scanCtNamedElement(CtNamedElement e) {
		label = e.getSimpleName();
	}

	@Override
	public <T> void scanCtVariableAccess(CtVariableAccess<T> variableAccess) {
		label = variableAccess.getVariable().getSimpleName();
	}


	@Override
	public <T> void visitCtInvocation(CtInvocation<T> invocation) {
		if (invocation.getExecutable() != null) {
			CtTypeReference decl = invocation.getExecutable().getDeclaringType();
			label = (decl!=null?decl.getQualifiedName():"")+"#"+invocation.getExecutable().getSignature();
		}
	}

	@Override
	public <T> void visitCtConstructorCall(CtConstructorCall<T> ctConstructorCall) {
		if (ctConstructorCall.getExecutable() != null) {
			label = ctConstructorCall.getExecutable().getSignature();
		}
	}

	@Override
	public <T> void visitCtLiteral(CtLiteral<T> literal) {
		label = literal.toString();
	}

	@Override
	public void visitCtIf(CtIf e) {
		label = "if";
	}

	@Override
	public void visitCtWhile(CtWhile e) {
		label = "while";
	}

	@Override
	public void visitCtBreak(CtBreak e) {
		label = "break";
	}

	@Override
	public void visitCtContinue(CtContinue e) {
		label = "break";
	}

	@Override
	public <R> void visitCtReturn(CtReturn<R> e) {
		label = "return";
	}

	@Override
	public <T> void visitCtAssert(CtAssert<T> e) {
		label = "assert";
	}

	@Override
	public <T, A extends T> void visitCtAssignment(CtAssignment<T, A> e) {
		label = "=";
	}

	@Override
	public <T, A extends T> void visitCtOperatorAssignment(CtOperatorAssignment<T, A> e) {
		label = e.getLabel();
	}

	@Override
	public <R> void visitCtBlock(CtBlock<R> e) {
		label = "{";
	}

	@Override
	public <T> void visitCtBinaryOperator(CtBinaryOperator<T> operator) {
		label = operator.getKind().toString();
	}

	@Override
	public <T> void visitCtUnaryOperator(CtUnaryOperator<T> operator) {
		label = operator.getKind().toString();
	}

	@Override
	public <T> void visitCtThisAccess(CtThisAccess<T> thisAccess) {
		label =  thisAccess.toString();
	}

	@Override
	public <T> void visitCtSuperAccess(CtSuperAccess<T> f) {
		label =  f.toString();
	}

	@Override
	public <T> void visitCtTypeAccess(CtTypeAccess<T> typeAccess) {
		try {
			label = typeAccess.getAccessedType().getQualifiedName();
		} catch (Exception e) {
//			System.out.println(e.getMessage());
			e.printStackTrace();
			//TODO Fix this exception case and find out why/when it happens
			label = "typeaccess";
		}
	}

	public void visitCtCondition(CtCondition condElement) {
		label = condElement.getCondition().toString();
	}

    public void visitCtBranch(CtBranch branch) {
        label = "branch";
    }

    public void visitCtArgument(CtArgument ctArgument) {
        label = ctArgument.getElement().toString(); //TODO  "java.lang.ClassNotFoundException: org.eclipse.text.edits.TextEdit" on eclipse/eclipse.jdt.core ClassFile.java c910d95d
	}
}
