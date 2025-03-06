package io.github.ocelot.glslprocessor.api.node.branch;

import io.github.ocelot.glslprocessor.api.node.GlslNode;
import io.github.ocelot.glslprocessor.api.node.GlslNodeType;
import io.github.ocelot.glslprocessor.api.visitor.GlslNodeVisitor;

import java.util.stream.Stream;

/**
 * @author Ocelot
 * @since 1.0.0
 */
public enum GlslJumpNode implements GlslNode {
    CONTINUE(GlslNodeType.CONTINUE),
    BREAK(GlslNodeType.BREAK),
    DISCARD(GlslNodeType.DISCARD);

    private final GlslNodeType nodeType;

    GlslJumpNode(GlslNodeType nodeType) {
        this.nodeType = nodeType;
    }

    @Override
    public void visit(GlslNodeVisitor visitor) {
        visitor.visitJump(this);
    }

    @Override
    public GlslNodeType getNodeType() {
        return this.nodeType;
    }

    @Override
    public Stream<GlslNode> stream() {
        return Stream.of(this);
    }
}
