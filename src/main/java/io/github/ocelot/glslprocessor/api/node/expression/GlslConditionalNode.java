package io.github.ocelot.glslprocessor.api.node.expression;

import io.github.ocelot.glslprocessor.api.node.GlslNode;
import io.github.ocelot.glslprocessor.api.visitor.GlslNodeVisitor;

import java.util.stream.Stream;

/**
 * @author Ocelot
 * @since 1.0.0
 */
public class GlslConditionalNode implements GlslNode {

    private GlslNode condition;
    private GlslNode first;
    private GlslNode second;

    public GlslConditionalNode(GlslNode condition, GlslNode first, GlslNode second) {
        this.condition = condition;
        this.first = first;
        this.second = second;
    }

    @Override
    public void visit(GlslNodeVisitor visitor) {
        visitor.visitCondition(this);
    }

    public GlslNode getCondition() {
        return this.condition;
    }

    public GlslNode getFirst() {
        return this.first;
    }

    public GlslNode getSecond() {
        return this.second;
    }

    public GlslConditionalNode setCondition(GlslNode condition) {
        this.condition = condition;
        return this;
    }

    public GlslConditionalNode setFirst(GlslNode first) {
        this.first = first;
        return this;
    }

    public GlslConditionalNode setSecond(GlslNode second) {
        this.second = second;
        return this;
    }

    @Override
    public Stream<GlslNode> stream() {
        return Stream.concat(Stream.of(this), Stream.concat(this.first.stream(), this.second.stream()));
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || this.getClass() != o.getClass()) {
            return false;
        }

        GlslConditionalNode that = (GlslConditionalNode) o;
        return this.condition.equals(that.condition) && this.first.equals(that.first) && this.second.equals(that.second);
    }

    @Override
    public int hashCode() {
        int result = this.condition.hashCode();
        result = 31 * result + this.first.hashCode();
        result = 31 * result + this.second.hashCode();
        return result;
    }

    @Override
    public String toString() {
        return "GlslConditionalNode{" +
                "condition=" + this.condition + ", " +
                "first=" + this.first + ", " +
                "second=" + this.second + '}';
    }
}
