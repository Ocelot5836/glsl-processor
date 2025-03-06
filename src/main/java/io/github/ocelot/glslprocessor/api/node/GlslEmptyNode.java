package io.github.ocelot.glslprocessor.api.node;

import io.github.ocelot.glslprocessor.api.visitor.GlslNodeVisitor;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

/**
 * @author Ocelot
 * @since 1.0.0
 */
public enum GlslEmptyNode implements GlslNode {
    INSTANCE;

    @Override
    public void visit(GlslNodeVisitor visitor) {
    }

    @Override
    public List<GlslNode> toList() {
        return new ArrayList<>();
    }

    @Override
    public Stream<GlslNode> stream() {
        return Stream.empty();
    }

}
