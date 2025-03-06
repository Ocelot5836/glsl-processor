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

    public void visitNewField(GlslNewFieldNode node) {
        if (this.parent != null) {
            this.parent.visitNewField(node);
        }
    }

    public void visitStructDeclaration(GlslStructDeclarationNode node) {
        if (this.parent != null) {
            this.parent.visitStructDeclaration(node);
        }
    }

    public void visitDeclaration(GlslVariableDeclarationNode node) {
        if (this.parent != null) {
            this.parent.visitDeclaration(node);
        }
    }

    public @Nullable GlslNodeVisitor visitFunction(GlslFunctionNode node) {
        return this.parent != null ? this.parent.visitFunction(node) : null;
    }

    public void visitFunctionEnd(GlslFunctionNode node) {
        if (this.parent != null) {
            this.parent.visitFunctionEnd(node);
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
