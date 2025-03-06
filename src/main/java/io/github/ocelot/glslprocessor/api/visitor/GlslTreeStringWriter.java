package io.github.ocelot.glslprocessor.api.visitor;

import io.github.ocelot.glslprocessor.api.grammar.GlslVersionStatement;
import io.github.ocelot.glslprocessor.api.node.GlslNode;
import io.github.ocelot.glslprocessor.api.node.GlslTree;
import io.github.ocelot.glslprocessor.api.node.function.GlslFunctionNode;
import io.github.ocelot.glslprocessor.api.node.variable.GlslNewFieldNode;
import io.github.ocelot.glslprocessor.api.node.variable.GlslStructDeclarationNode;
import io.github.ocelot.glslprocessor.api.node.variable.GlslVariableDeclarationNode;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;

/**
 * @author Ocelot
 * @since 1.0.0
 */
public final class GlslTreeStringWriter extends GlslTreeVisitor {

    private final Map<GlslNode, String> markedNodes;
    private final GlslNodeStringWriter visitor;
    private final StringBuilder builder;
    private String value;

    public GlslTreeStringWriter() {
        this.markedNodes = new HashMap<>();
        this.visitor = new GlslNodeStringWriter(false);
        this.builder = new StringBuilder();
        this.value = "";
    }

    private String formatExpression(GlslNode node) {
        this.visitor.clear();
        node.visit(this.visitor);
        return this.visitor.toString();
    }

    @Override
    public void visitMarkers(Map<String, GlslNode> markers) {
        for (Map.Entry<String, GlslNode> entry : markers.entrySet()) {
            this.markedNodes.put(entry.getValue(), entry.getKey());
        }
    }

    @Override
    public void visitVersion(GlslVersionStatement version) {
        this.builder.append("#version ").append(version.getVersionStatement()).append("\n");
    }

    @Override
    public void visitDirective(String directive) {
        this.builder.append(directive).append('\n');
    }

    @Override
    public void visitMacro(String key, String value) {
        this.builder.append("#define ").append(key).append(" ").append(value).append("\n");
    }

    @Override
    public void visitNewField(GlslNewFieldNode node) {
        String marker = this.markedNodes.get(node);
        if (marker != null) {
            this.builder.append("/* #").append(marker).append(" */\n");
        }
        this.builder.append(this.formatExpression(node));
    }

    @Override
    public void visitStructDeclaration(GlslStructDeclarationNode node) {
        String marker = this.markedNodes.get(node);
        if (marker != null) {
            this.builder.append("/* #").append(marker).append(" */\n");
        }
        this.builder.append(this.formatExpression(node));
    }

    @Override
    public void visitDeclaration(GlslVariableDeclarationNode node) {
        String marker = this.markedNodes.get(node);
        if (marker != null) {
            this.builder.append("/* #").append(marker).append(" */\n");
        }
        this.builder.append(this.formatExpression(node));
    }

    @Override
    public @Nullable GlslNodeVisitor visitFunction(GlslFunctionNode node) {
        String marker = this.markedNodes.get(node);
        if (marker != null) {
            this.builder.append("/* #").append(marker).append(" */\n");
        }
        this.builder.append(this.formatExpression(node));
        return null;
    }

    @Override
    public void visitTreeEnd(GlslTree tree) {
        this.markedNodes.clear();
        this.value = this.builder.toString();
        this.builder.setLength(0);
    }

    @Override
    public String toString() {
        return this.value;
    }
}
