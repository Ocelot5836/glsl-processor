package io.github.ocelot.glslprocessor.api.node;

import io.github.ocelot.glslprocessor.api.visitor.GlslNodeVisitor;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.stream.Stream;

/**
 * @author Ocelot
 * @since 1.0.0
 */
public final class GlslCompoundNode implements GlslNode {

    final List<GlslNode> children;

    public GlslCompoundNode(List<GlslNode> children) {
        this.children = children;
    }

    @Override
    public void visit(GlslNodeVisitor visitor) {
        for (GlslNode node : this.children) {
            node.visit(visitor);
        }
    }

    @Override
    public GlslNodeType getNodeType() {
        throw new AssertionError("This should never happen");
    }

    @Override
    public List<GlslNode> toList() {
        return new ArrayList<>(this.children);
    }

    @Override
    public Stream<GlslNode> stream() {
        return Stream.concat(Stream.of(this), this.children.stream().flatMap(GlslNode::stream));
    }

    public GlslCompoundNode setChildren(GlslNode... children) {
        return this.setChildren(Arrays.asList(children));
    }

    public GlslCompoundNode setChildren(Collection<GlslNode> children) {
        this.children.clear();
        this.children.addAll(children);
        return this;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || this.getClass() != o.getClass()) {
            return false;
        }

        GlslCompoundNode that = (GlslCompoundNode) o;
        return this.children.equals(that.children);
    }

    @Override
    public int hashCode() {
        return this.children.hashCode();
    }

    @Override
    public String toString() {
        return "GlslCompoundNode{children=" + this.children + '}';
    }
}
