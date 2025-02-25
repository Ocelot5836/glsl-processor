package io.github.ocelot.glslprocessor.api.node.branch;

import io.github.ocelot.glslprocessor.api.node.GlslNode;
import io.github.ocelot.glslprocessor.api.node.GlslNodeList;

import java.util.Collection;
import java.util.stream.Stream;

/**
 * if/else
 *
 * @author Ocelot
 */
public class GlslSelectionNode implements GlslNode {

    private GlslNode expression;
    private final GlslNodeList first;
    private final GlslNodeList second;

    public GlslSelectionNode(GlslNode expression, Collection<GlslNode> first, Collection<GlslNode> second) {
        this.expression = expression;
        this.first = new GlslNodeList(first);
        this.second = new GlslNodeList(second);
    }

    public GlslNode getExpression() {
        return this.expression;
    }

    public GlslNodeList getFirst() {
        return this.first;
    }

    public GlslNodeList getSecond() {
        return this.second;
    }

    public void setExpression(GlslNode expression) {
        this.expression = expression;
    }

    public GlslSelectionNode setFirst(Collection<GlslNode> first) {
        this.first.clear();
        this.first.addAll(first);
        return this;
    }

    public GlslSelectionNode setSecond(Collection<GlslNode> first) {
        this.first.clear();
        this.first.addAll(first);
        return this;
    }

    @Override
    public String getSourceString() {
        StringBuilder builder = new StringBuilder("if (");
        builder.append(NEWLINE.matcher(this.expression.getSourceString()).replaceAll("\n\t")).append(") {\n");
        for (GlslNode node : this.first) {
            builder.append('\t').append(NEWLINE.matcher(node.getSourceString()).replaceAll("\n\t")).append(";\n");
        }
        builder.append("}");
        if (!this.second.isEmpty()) {
            builder.append(" else {\n");
            for (GlslNode node : this.second) {
                builder.append('\t').append(NEWLINE.matcher(node.getSourceString()).replaceAll("\n\t")).append(";\n");
            }
            builder.append("}");
        }
        return builder.toString();
    }

    @Override
    public Stream<GlslNode> stream() {
        return Stream.concat(Stream.of(this), Stream.concat(this.first.stream().flatMap(GlslNode::stream), this.second.stream().flatMap(GlslNode::stream)));
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || this.getClass() != o.getClass()) {
            return false;
        }

        GlslSelectionNode that = (GlslSelectionNode) o;
        return this.expression.equals(that.expression) && this.first.equals(that.first) && this.second.equals(that.second);
    }

    @Override
    public int hashCode() {
        int result = this.expression.hashCode();
        result = 31 * result + this.first.hashCode();
        result = 31 * result + this.second.hashCode();
        return result;
    }

    @Override
    public String toString() {
        return "GlslSelectionNode{" +
                "expression=" + this.expression + ", " +
                "first=" + this.first + ", " +
                "branch=" + this.second + '}';
    }
}
