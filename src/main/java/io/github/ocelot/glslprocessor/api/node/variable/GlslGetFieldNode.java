package io.github.ocelot.glslprocessor.api.node.variable;

import io.github.ocelot.glslprocessor.api.node.GlslNode;
import io.github.ocelot.glslprocessor.api.node.GlslNodeType;
import io.github.ocelot.glslprocessor.api.visitor.GlslNodeVisitor;

import java.util.stream.Stream;

/**
 * @author Ocelot
 * @since 1.0.0
 */
public final class GlslGetFieldNode implements GlslNode {

    private GlslNode expression;
    private String fieldSelection;

    public GlslGetFieldNode(GlslNode expression, String fieldSelection) {
        this.expression = expression;
        this.fieldSelection = fieldSelection;
    }

    @Override
    public void visit(GlslNodeVisitor visitor) {
        visitor.visitGetField(this);
    }

    @Override
    public GlslNodeType getNodeType() {
        return GlslNodeType.GET_FIELD;
    }

    public GlslNode getExpression() {
        return this.expression;
    }

    public String getFieldSelection() {
        return this.fieldSelection;
    }

    public GlslGetFieldNode setExpression(GlslNode expression) {
        this.expression = expression;
        return this;
    }

    public GlslGetFieldNode setFieldSelection(String fieldSelection) {
        this.fieldSelection = fieldSelection;
        return this;
    }

    @Override
    public Stream<GlslNode> stream() {
        return Stream.concat(Stream.of(this), this.expression.stream());
    }

    @Override
    public String toString() {
        return "GlslFieldNode{expression=" + this.expression + ", fieldSelection=" + this.fieldSelection + '}';
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || this.getClass() != o.getClass()) {
            return false;
        }

        GlslGetFieldNode that = (GlslGetFieldNode) o;
        return this.expression.equals(that.expression) && this.fieldSelection.equals(that.fieldSelection);
    }

    @Override
    public int hashCode() {
        int result = this.expression.hashCode();
        result = 31 * result + this.fieldSelection.hashCode();
        return result;
    }
}
