package io.github.ocelot.glslprocessor.api.visitor;

import io.github.ocelot.glslprocessor.api.grammar.GlslVersionStatement;
import io.github.ocelot.glslprocessor.api.node.GlslNode;
import io.github.ocelot.glslprocessor.api.node.function.GlslFunctionNode;
import io.github.ocelot.glslprocessor.api.node.variable.GlslDeclarationNode;
import io.github.ocelot.glslprocessor.api.node.variable.GlslNewNode;
import io.github.ocelot.glslprocessor.api.node.variable.GlslStructNode;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;

public class GlslStringWriter implements GlslTreeVisitor {

    private final Map<GlslNode, String> markedNodes;
    private final StringBuilder builder;
    private String value;

    public GlslStringWriter() {
        this.markedNodes = new HashMap<>();
        this.builder = new StringBuilder();
        this.value = "";
    }

    private String formatExpression(GlslNode node) {
        return node.getSourceString();
    }

    @Override
    public void visitMarkers(Map<String, GlslNode> markers) {
        for (Map.Entry<String, GlslNode> entry : markers.entrySet()) {
            this.markedNodes.put(entry.getValue(), entry.getKey());
        }
    }

    @Override
    public void visitVersion(GlslVersionStatement version) {
        this.builder.append("#version ").append(version.getVersionStatement()).append("\n\n");
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
    public void visitField(GlslNewNode newNode) {
        String marker = this.markedNodes.get(newNode);
        if (marker != null) {
            this.builder.append("/* #").append(marker).append(" */\n");
        }
        this.builder.append(this.formatExpression(newNode)).append(";\n");
    }

    @Override
    public void visitStruct(GlslStructNode structSpecifier) {
        String marker = this.markedNodes.get(structSpecifier);
        if (marker != null) {
            this.builder.append("/* #").append(marker).append(" */\n");
        }
        this.builder.append(this.formatExpression(structSpecifier)).append(";\n");
    }

    @Override
    public void visitDeclaration(GlslDeclarationNode declaration) {
        String marker = this.markedNodes.get(declaration);
        if (marker != null) {
            this.builder.append("/* #").append(marker).append(" */\n");
        }
        this.builder.append(this.formatExpression(declaration)).append(";\n");
    }

    @Override
    public @Nullable GlslFunctionVisitor visitFunction(GlslFunctionNode node) {
        String marker = this.markedNodes.get(node);
        if (marker != null) {
            this.builder.append("/* #").append(marker).append(" */\n");
        }
        this.builder.append(node.getSourceString());
        return null;
    }

    @Override
    public void visitTreeEnd() {
        this.markedNodes.clear();
        this.value = this.builder.toString();
        this.builder.setLength(0);
    }

    @Override
    public String toString() {
        return this.value;
    }
}
