package io.github.ocelot.glslprocessor.api.visitor;

import io.github.ocelot.glslprocessor.api.grammar.GlslVersionStatement;
import io.github.ocelot.glslprocessor.api.node.GlslNode;
import io.github.ocelot.glslprocessor.api.node.GlslTree;
import io.github.ocelot.glslprocessor.api.node.function.GlslFunctionNode;
import io.github.ocelot.glslprocessor.api.node.variable.GlslNewFieldNode;
import io.github.ocelot.glslprocessor.api.node.variable.GlslStructDeclarationNode;
import io.github.ocelot.glslprocessor.api.node.variable.GlslVariableDeclarationNode;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.Nullable;

import java.util.Map;

/**
 * @author Ocelot
 * @since 1.0.0
 */
@ApiStatus.Experimental
public class GlslTreeVisitor {

    private final GlslTreeVisitor parent;

    public GlslTreeVisitor() {
        this(null);
    }

    public GlslTreeVisitor(@Nullable GlslTreeVisitor parent) {
        this.parent = parent;
    }

    public void visitMarkers(Map<String, GlslNode> markers) {
        if (this.parent != null) {
            this.parent.visitMarkers(markers);
        }
    }

    public void visitVersion(GlslVersionStatement version) {
        if (this.parent != null) {
            this.parent.visitVersion(version);
        }
    }

    public void visitDirective(String directive) {
        if (this.parent != null) {
            this.parent.visitDirective(directive);
        }
    }

    public void visitMacro(String key, String value) {
        if (this.parent != null) {
            this.parent.visitMacro(key, value);
        }
    }

    public void visitNewField(int index, GlslNewFieldNode node) {
        if (this.parent != null) {
            this.parent.visitNewField(index, node);
        }
    }

    public void visitStructDeclaration(int index, GlslStructDeclarationNode node) {
        if (this.parent != null) {
            this.parent.visitStructDeclaration(index, node);
        }
    }

    public void visitDeclaration(int index, GlslVariableDeclarationNode node) {
        if (this.parent != null) {
            this.parent.visitDeclaration(index, node);
        }
    }

    /**
     * Visits the specified function declaration statement.
     *
     * @param node The node to visit
     * @return A visitor for the body or <code>null</code> to skip
     */
    public @Nullable GlslNodeVisitor visitFunction(int index, GlslFunctionNode node) {
        return this.parent != null ? this.parent.visitFunction(index, node) : null;
    }

    /**
     * Visits the specified function declaration end statement.
     *
     * @param node The node to visit
     */
    public void visitFunctionEnd(int index, GlslFunctionNode node) {
        if (this.parent != null) {
            this.parent.visitFunctionEnd(index, node);
        }
    }

    /**
     * Visits the end of the tree.
     *
     * @param tree The tree that was visited
     */
    public void visitTreeEnd(GlslTree tree) {
        if (this.parent != null) {
            this.parent.visitTreeEnd(tree);
        }
    }
}
