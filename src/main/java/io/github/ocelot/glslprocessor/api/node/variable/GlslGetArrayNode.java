package io.github.ocelot.glslprocessor.api.node.variable;

import io.github.ocelot.glslprocessor.api.node.GlslNode;
import io.github.ocelot.glslprocessor.api.visitor.GlslNodeVisitor;

import java.util.stream.Stream;

/**
 * @author Ocelot
 * @since 1.0.0
 */
public class GlslGetArrayNode implements GlslNode {

    private GlslNode expression;
    private GlslNode index;

    public GlslGetArrayNode(GlslNode expression, GlslNode index) {
        this.expression = expression;
        this.index = index;
    }

    @Override
    public void visit(GlslNodeVisitor visitor) {
        visitor.visitGetArray(this);
    }

    public GlslNode getExpression() {
        return this.expression;
    }

    public GlslNode getIndex() {
        return this.index;
    }

    public GlslGetArrayNode setExpression(GlslNode expression) {
        this.expression = expression;
        return this;
    }

    public GlslGetArrayNode setIndex(GlslNode index) {
        this.index = index;
        return this;
    }

    @Override
    public Stream<GlslNode> stream() {
        return Stream.concat(Stream.of(this), Stream.concat(this.expression.stream(), this.index.stream()));
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || this.getClass() != o.getClass()) {
            return false;
        }

        GlslGetArrayNode that = (GlslGetArrayNode) o;
        return this.expression.equals(that.expression) && this.index.equals(that.index);
    }

    @Override
    public int hashCode() {
        int result = this.expression.hashCode();
        result = 31 * result + this.index.hashCode();
        return result;
    }

    @Override
    public String toString() {
        return "GlslArrayNode{expression=" + this.expression + ", index=" + this.index + '}';
    }
}
