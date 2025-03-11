package io.github.ocelot.glslprocessor.impl;

import io.github.ocelot.glslprocessor.api.node.GlslNode;
import io.github.ocelot.glslprocessor.api.node.GlslNodeType;
import io.github.ocelot.glslprocessor.api.node.GlslTree;
import io.github.ocelot.glslprocessor.api.node.function.GlslFunctionNode;
import io.github.ocelot.glslprocessor.api.node.variable.GlslNewFieldNode;
import io.github.ocelot.glslprocessor.api.visitor.GlslNodeVisitor;
import io.github.ocelot.glslprocessor.api.visitor.GlslTreeVisitor;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.Nullable;

@ApiStatus.Internal
public class GlslPatcher extends GlslTreeVisitor {

    private final String method;
    private final GlslNodeType targetType;
    private final int index;
    private final String targetName;
    private final GlslNode[] replace;
    private int currentIndex;

    public GlslPatcher(
            @Nullable String method,
            @Nullable GlslNodeType targetType,
            int index,
            String targetName,
            GlslNode[] replace) {
        this.method = method;
        this.targetType = targetType;
        this.index = index;
        this.targetName = targetName;
        this.replace = replace;
    }

    private void processMatch(GlslNode node) {
        System.out.println("Found match: " + node);
    }

    private void processNewField(GlslNewFieldNode node) {
        if (this.targetType != null && !this.targetType.equals(node.getNodeType())) {
            return;
        }

        if (!this.targetName.equals(node.getName())) {
            return;
        }

        this.currentIndex++;
        if (this.index == -1 || this.currentIndex - 1 == this.index) {
            this.processMatch(node);
        }
    }

    @Override
    public void visitNewField(int index, GlslNewFieldNode node) {
        if (this.method == null) {
            this.processNewField(node);
        }
    }

    @Override
    public @Nullable GlslNodeVisitor visitFunction(int index, GlslFunctionNode node) {
        if (this.method == null && node.getName().equals(this.targetName)) {
            this.processMatch(node);
            return null;
        }
        if (node.getName().equals(this.method)) {
            return new GlslNodeVisitor() {
                @Override
                public void visitNewField(GlslNewFieldNode node) {
                    GlslPatcher.this.processNewField(node);
                }
            };
        }
        return null;
    }

    @Override
    public void visitTreeEnd(GlslTree tree) {
        this.currentIndex = 0;
    }
}
