package at.aau.softwaredynamics.util;

import spoon.reflect.cu.CompilationUnit;
import spoon.reflect.cu.SourcePosition;
import spoon.reflect.cu.position.DeclarationSourcePosition;
import spoon.reflect.declaration.ModifierKind;

import java.io.File;

public class ModifierSourcePosition implements SourcePosition {

    DeclarationSourcePosition srcPos;
    String modifier;

    public ModifierSourcePosition(DeclarationSourcePosition bodyHolderSourcePosition) {
        this.srcPos = bodyHolderSourcePosition;
    }

    public ModifierSourcePosition(DeclarationSourcePosition bodyHolderSourcePosition, ModifierKind modifier) {
        this.srcPos = bodyHolderSourcePosition;
        this.modifier = modifier.toString();
    }

    /**
     * @return true if this instance holds start/end indexes of related sources.
     * false if they are unknown
     */
    @Override
    public boolean isValidPosition() {
        return true;
    }

    @Override
    public File getFile() {
        return srcPos.getFile();
    }

    @Override
    public CompilationUnit getCompilationUnit() {
        return srcPos.getCompilationUnit();
    }

    @Override
    public int getLine() {
        return srcPos.getLine();
    }

    @Override
    public int getEndLine() {
        return srcPos.getEndLine();
    }

    @Override
    public int getColumn() {
        return srcPos.getColumn();
    }

    @Override
    public int getEndColumn() {
        return srcPos.getEndColumn();
    }

    @Override
    public int getSourceEnd() {
        // this is changed
        return srcPos.getModifierSourceEnd();
    }

    @Override
    public int getSourceStart() {
        // this is changed
        return srcPos.getModifierSourceStart();
    }
}
