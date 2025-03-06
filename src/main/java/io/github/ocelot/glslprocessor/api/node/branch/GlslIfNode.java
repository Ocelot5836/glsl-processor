package io.github.ocelot.glslprocessor.api.node.branch;

import io.github.ocelot.glslprocessor.api.node.GlslNode;
import io.github.ocelot.glslprocessor.api.node.GlslNodeList;
import io.github.ocelot.glslprocessor.api.visitor.GlslIfVisitor;
import io.github.ocelot.glslprocessor.api.visitor.GlslNodeVisitor;

import java.util.Collection;
import java.util.stream.Stream;

/**
 * if/else
 *
 * @author Ocelot
 * @since 1.0.0
 */
public class GlslIfNode implements GlslNode {

    private GlslNode expression;
    private final GlslNodeList first;
    private final GlslNodeList second;

    public GlslIfNode(GlslNode expression, Collection<GlslNode> first, Collection<GlslNode> second) {
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

    public GlslIfNode setFirst(Collection<GlslNode> first) {
        this.first.clear();
        this.first.addAll(first);
        return this;
    }

    public GlslIfNode setSecond(Collection<GlslNode> first) {
        this.first.clear();
        this.first.addAll(first);
        return this;
    }

    @Override
    public void visit(GlslNodeVisitor visitor) {
        GlslIfVisitor bodyVisitor = visitor.visitIf(this);
        if (bodyVisitor != null) {
            GlslNodeVisitor ifVisitor = bodyVisitor.visitIf();
            if (ifVisitor != null) {
                for (GlslNode node : this.first) {
                    node.visit(ifVisitor);
                }
            }

            if (!this.second.isEmpty()) {
                GlslNodeVisitor elseVisitor = bodyVisitor.visitElse();
                if (elseVisitor != null) {
                    for (GlslNode node : this.second) {
                        node.visit(elseVisitor);
                    }
                }
            }

            bodyVisitor.visitIfEnd(this);
        }
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

        GlslIfNode that = (GlslIfNode) o;
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
