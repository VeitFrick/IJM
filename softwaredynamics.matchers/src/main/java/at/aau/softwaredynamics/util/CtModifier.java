package at.aau.softwaredynamics.util;

import spoon.reflect.code.CtComment;
import spoon.reflect.cu.SourcePosition;
import spoon.reflect.declaration.CtAnnotation;
import spoon.reflect.declaration.CtElement;
import spoon.reflect.declaration.ParentNotInitializedException;
import spoon.reflect.factory.Factory;
import spoon.reflect.path.CtPath;
import spoon.reflect.path.CtRole;
import spoon.reflect.reference.CtTypeReference;
import spoon.reflect.visitor.CtVisitor;
import spoon.reflect.visitor.Filter;
import spoon.reflect.visitor.chain.CtConsumableFunction;
import spoon.reflect.visitor.chain.CtFunction;
import spoon.reflect.visitor.chain.CtQuery;

import java.lang.annotation.Annotation;
import java.util.*;

public class CtModifier implements CtElement {

    SourcePosition pos;
    String name;

    public CtModifier(CtElement element) {
        this.setPosition(element.getPosition());
        this.name = element.toString();
    }

    public CtModifier(CtElement element, String name) {
        this.setPosition(element.getPosition());
        this.name = name;
    }

    @Override
    public <A extends Annotation> A getAnnotation(Class<A> aClass) {
        return null;
    }

    @Override
    public <A extends Annotation> CtAnnotation<A> getAnnotation(CtTypeReference<A> ctTypeReference) {
        return null;
    }

    @Override
    public <A extends Annotation> boolean hasAnnotation(Class<A> aClass) {
        return false;
    }

    @Override
    public List<CtAnnotation<? extends Annotation>> getAnnotations() {
        return null;
    }

    @Override
    public String getDocComment() {
        return null;
    }

    @Override
    public String getShortRepresentation() {
        return null;
    }

    @Override
    public SourcePosition getPosition() {
        return this.pos;
    }

    @Override
    public void replace(CtElement ctElement) {

    }

    @Override
    public <E extends CtElement> void replace(Collection<E> collection) {

    }

    @Override
    public <E extends CtElement> E addAnnotation(CtAnnotation<? extends Annotation> ctAnnotation) {
        return null;
    }

    @Override
    public boolean removeAnnotation(CtAnnotation<? extends Annotation> ctAnnotation) {
        return false;
    }

    @Override
    public <E extends CtElement> E setDocComment(String s) {
        return null;
    }

    @Override
    public <E extends CtElement> E setPosition(SourcePosition sourcePosition) {
        this.pos = sourcePosition;
        return (E) this;
    }

    @Override
    public <E extends CtElement> List<E> getAnnotatedChildren(Class<? extends Annotation> aClass) {
        return null;
    }

    @Override
    public boolean isImplicit() {
        return false;
    }

    @Override
    public <E extends CtElement> E setImplicit(boolean b) {
        return null;
    }

    @Override
    public Set<CtTypeReference<?>> getReferencedTypes() {
        return null;
    }

    @Override
    public <E extends CtElement> List<E> getElements(Filter<E> filter) {
        return null;
    }

    @Override
    public <E extends CtElement> E setPositions(SourcePosition sourcePosition) {
        this.pos = sourcePosition;
        return (E) this;
    }

    @Override
    public <E extends CtElement> E setAnnotations(List<CtAnnotation<? extends Annotation>> list) {
        return null;
    }

    @Override
    public CtElement getParent() throws ParentNotInitializedException {
        return null;
    }

    @Override
    public <P extends CtElement> P getParent(Class<P> aClass) throws ParentNotInitializedException {
        return null;
    }

    @Override
    public <E extends CtElement> E getParent(Filter<E> filter) throws ParentNotInitializedException {
        return null;
    }

    @Override
    public <E extends CtElement> E setParent(E e) {
        return null;
    }

    @Override
    public boolean isParentInitialized() {
        return false;
    }

    @Override
    public boolean hasParent(CtElement ctElement) {
        return false;
    }

    @Override
    public void updateAllParentsBelow() {

    }

    @Override
    public CtRole getRoleInParent() {
        return null;
    }

    @Override
    public void delete() {

    }

    /**
     * Saves a bunch of metadata inside an Element
     *
     * @param metadata
     */
    @Override
    public <E extends CtElement> E setAllMetadata(Map<String, Object> metadata) {
        return null;
    }

    @Override
    public <E extends CtElement> E putMetadata(String s, Object o) {
        return null;
    }

    @Override
    public Object getMetadata(String s) {
        return null;
    }

    /**
     * Retrieves all metadata stored in an element.
     */
    @Override
    public Map<String, Object> getAllMetadata() {
        return null;
    }

    @Override
    public Set<String> getMetadataKeys() {
        return null;
    }

    @Override
    public <E extends CtElement> E setComments(List<CtComment> list) {
        return null;
    }

    @Override
    public List<CtComment> getComments() {
        return null;
    }

    @Override
    public <E extends CtElement> E addComment(CtComment ctComment) {
        return null;
    }

    @Override
    public <E extends CtElement> E removeComment(CtComment ctComment) {
        return null;
    }

    @Override
    public CtElement clone() {
        return null;
    }

    @Override
    public <T> T getValueByRole(CtRole ctRole) {
        return null;
    }

    @Override
    public <E extends CtElement, T> E setValueByRole(CtRole ctRole, T t) {
        return null;
    }

    @Override
    public CtPath getPath() {
        return null;
    }

    /**
     * Returns an iterator over this CtElement's descendants.
     *
     * @return An iterator over this CtElement's descendants.
     */
    @Override
    public Iterator<CtElement> descendantIterator() {
        return null;
    }

    /**
     * Returns an Iterable instance of this CtElement, allowing for dfs traversal of its descendants.
     *
     * @return an Iterable object that allows iterating through this CtElement's descendants.
     */
    @Override
    public Iterable<CtElement> asIterable() {
        return null;
    }

    @Override
    public Factory getFactory() {
        return null;
    }

    @Override
    public void setFactory(Factory factory) {

    }

    @Override
    public void accept(CtVisitor ctVisitor) {

    }

    @Override
    public <R extends CtElement> CtQuery filterChildren(Filter<R> filter) {
        return null;
    }

    @Override
    public <I, R> CtQuery map(CtFunction<I, R> ctFunction) {
        return null;
    }

    @Override
    public <I> CtQuery map(CtConsumableFunction<I> ctConsumableFunction) {
        return null;
    }

    @Override
    public String toString() {
        return this.name;
    }
}
