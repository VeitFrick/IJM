package at.aau.softwaredynamics.gen;


import at.aau.softwaredynamics.util.CtArgument;
import at.aau.softwaredynamics.util.CtBranch;
import at.aau.softwaredynamics.util.CtCondition;
import com.github.gumtreediff.tree.ITree;
import com.github.gumtreediff.tree.TreeContext;
import spoon.reflect.code.*;
import spoon.reflect.cu.position.NoSourcePosition;
import spoon.reflect.declaration.CtElement;
import spoon.reflect.path.CtRole;
import spoon.reflect.reference.CtReference;
import spoon.reflect.visitor.CtScanner;

import java.util.Stack;

//import gumtree.spoon.builder.SpoonGumTreeBuilder;

public class SpoonTreeScanner extends CtScanner {
	public static final String NOTYPE = "<notype>";
	private final TreeContext treeContext;
	private final Stack<ITree> nodes = new Stack<>();

	SpoonTreeScanner(TreeContext treeContext, ITree root) {
		this.treeContext = treeContext;
		nodes.push(root);
	}

	/**
	 * Generically scans a meta-model element.
	 */
	public void scan(CtRole role, CtElement element) {
		//intercept and add pseudo-nodes to improve matching
		if (role == CtRole.CONDITION && element != null) {
			CtCondition cond = new CtCondition((CtExpression<Boolean>) element);
			scan(cond);
		} else if (role == CtRole.ELSE && element != null) {
			CtBranch branch = null;
			if(element instanceof CtExpression)
				branch = new CtBranch((CtExpression) element);
			else if(element instanceof CtStatement)
				branch = new CtBranch((CtStatement) element);
			scan(branch);
		} else if (role == CtRole.ARGUMENT && element != null) {
			CtArgument arg = new CtArgument((CtExpression) element);
			scan(arg);
		} else {
			scan(element);
		}
	}

	@Override
	public void enter(CtElement element) {
		if (isToIgnore(element)) {
			super.enter(element);
			return;
		}

		SpoonLabelFinder lf = new SpoonLabelFinder();
		lf.scan(element);
		pushNodeToTree(createNode(element, lf.label));

		int depthBefore = nodes.size();

		new SpoonNodeCreator(this).scan(element);

//		if (nodes.size() != depthBefore ) {
//			// contract: this should never happen
//			throw new RuntimeException("too many nodes pushed");
//		}
	}

	/**
	 * Ignore some element from the AST
	 * @param element
	 * @return
	 */
	private boolean isToIgnore(CtElement element) {
		return element.isImplicit() || element instanceof CtReference || element instanceof CtStatementList || element instanceof CtJavaDocTag;
	}

	@Override
	public void exit(CtElement element) {
		if (!isToIgnore(element)) {
			nodes.pop();
		}
		super.exit(element);
	}

	void pushNodeToTree(ITree node) {
		ITree parent = nodes.peek();
		if (parent != null) { // happens when nodes.push(null)
			parent.addChild(node);
		}
		nodes.push(node);
	}

	void addSiblingNode(ITree node) {
		ITree parent = nodes.peek();
		if (parent != null) { // happens when nodes.push(null)
			parent.addChild(node);
		}
	}

	private ITree createNode(CtElement element, String label) {
		String nodeTypeName = NOTYPE;
		if (element != null) {
			nodeTypeName = getTypeName(element.getClass().getSimpleName());
		}

		ITree newNode = createNode(nodeTypeName, label);
		newNode.setMetadata(SpoonBuilder.SPOON_OBJECT, element);
		//Integer startLineOffset = element.getPosition().getColumn();
		//Integer startLine = element.getPosition().getLine();
		if (!(element.getPosition() instanceof NoSourcePosition)) {
			newNode.setPos(element.getPosition().getSourceStart());
			newNode.setLength(element.getPosition().getSourceEnd() - element.getPosition().getSourceStart());
		} else {
//			System.out.println("warning: no source position element!");
		}

		return newNode;
	}

	private String getTypeName(String simpleName) {
		// Removes the "Ct" at the beginning and the "Impl" at the end.
		return simpleName.substring(2, simpleName.length() - 4);
	}

	public ITree createNode(String typeClass, String label) {
		return treeContext.createTree(typeClass.hashCode(), label, typeClass);
	}

	public void visitCtCondition(final CtCondition condElement) {
		enter(condElement);
		scan(CtRole.EXPRESSION, condElement.getCondition());
		exit(condElement);
	}

	public void visitCtBranch(final CtBranch condElement) {
		enter(condElement);
		scan(CtRole.EXPRESSION, condElement.getExpression());
        scan(CtRole.STATEMENT, condElement.getStatement());
		exit(condElement);
	}

	public void visitCtWhile(final CtWhile whileLoop) {
		enter(whileLoop);
		scan(CtRole.ANNOTATION, whileLoop.getAnnotations());
		scan(CtRole.CONDITION, whileLoop.getLoopingExpression());
		scan(CtRole.BODY, whileLoop.getBody());
		scan(CtRole.COMMENT, whileLoop.getComments());
		exit(whileLoop);
	}

	public void visitCtFor(final CtFor forLoop) {
		enter(forLoop);
		scan(CtRole.ANNOTATION, forLoop.getAnnotations());
		scan(CtRole.FOR_INIT, forLoop.getForInit());
		scan(CtRole.CONDITION, forLoop.getExpression());
		scan(CtRole.FOR_UPDATE, forLoop.getForUpdate());
		scan(CtRole.BODY, forLoop.getBody());
		scan(CtRole.COMMENT, forLoop.getComments());
		exit(forLoop);
	}

	public void visitCtArgument(CtArgument ctArgument) {
		enter(ctArgument);
		scan(CtRole.EXPRESSION, ctArgument.getElement());
		exit(ctArgument);
	}

	@Override
	public <T> void visitCtTypeAccess(CtTypeAccess<T> e) {
	    // If the type access has no position is it implicit ( call() vs this.call() ) and we want
        // to not add those, it is more intuitive for the dev that
        // type accesses (targets) get "inserted" when they get changed to explicit.
		if(!(e.getPosition() instanceof NoSourcePosition))
			super.visitCtTypeAccess(e);
	}
}
