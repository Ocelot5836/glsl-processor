package io.github.ocelot.glslprocessor.api.visitor;

import io.github.ocelot.glslprocessor.api.node.GlslNode;

@FunctionalInterface
public interface GlslNodeVisitor {

    void visitNode(GlslNode node);
}
