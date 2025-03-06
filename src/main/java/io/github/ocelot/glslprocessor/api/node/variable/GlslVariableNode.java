package io.github.ocelot.glslprocessor.api.node.variable;

import io.github.ocelot.glslprocessor.api.node.GlslNode;
import io.github.ocelot.glslprocessor.api.node.GlslNodeType;
import io.github.ocelot.glslprocessor.api.visitor.GlslNodeVisitor;

import java.util.stream.Stream;

/**
 * @author Ocelot
 * @since 1.0.0
 */
public final class GlslVariableNode implements GlslNode {

    private String name;

    public GlslVariableNode(String name) {
        this.name = name;
    }

    @Override
    public void visit(GlslNodeVisitor visitor) {
        visitor.visitVariable(this);
    }

    @Override
    public GlslNodeType getNodeType() {
        return GlslNodeType.VARIABLE;
    }

    public String getName() {
        return this.name;
    }

    public GlslVariableNode setName(String name) {
        this.name = name;
        return this;
    }

    @Override
    public Stream<GlslNode> stream() {
        return Stream.of(this);
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || this.getClass() != o.getClass()) {
            return false;
        }

        GlslVariableNode that = (GlslVariableNode) o;
        return this.name.equals(that.name);
    }

    @Override
    public int hashCode() {
        return this.name.hashCode();
    }

    @Override
    public String toString() {
        return "GlslVariableNode{name=" + this.name + '}';
    }
}
