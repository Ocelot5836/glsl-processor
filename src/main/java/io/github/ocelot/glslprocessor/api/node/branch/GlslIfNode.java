package io.github.ocelot.glslprocessor.api.node.branch;

import io.github.ocelot.glslprocessor.api.node.GlslNode;
import io.github.ocelot.glslprocessor.api.node.GlslNodeList;
import io.github.ocelot.glslprocessor.api.node.GlslNodeType;
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
public final class GlslIfNode implements GlslNode {

    private GlslNode condition;
    private final GlslNodeList first;
    private final GlslNodeList second;

    public GlslIfNode(GlslNode condition, Collection<GlslNode> first, Collection<GlslNode> second) {
        this.condition = condition;
        this.first = new GlslNodeList(first);
        this.second = new GlslNodeList(second);
    }

    public GlslNode getCondition() {
        return this.condition;
    }

    public GlslNodeList getFirst() {
        return this.first;
    }

    public GlslNodeList getSecond() {
        return this.second;
    }

    public void setCondition(GlslNode condition) {
        this.condition = condition;
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
            if (!this.first.isEmpty()) {
                GlslNodeVisitor ifVisitor = bodyVisitor.visitIf();
                if (ifVisitor != null) {
                    for (GlslNode node : this.first) {
                        node.visit(ifVisitor);
                    }
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
    public GlslNodeType getNodeType() {
        return GlslNodeType.IF_ELSE;
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
        return "GlslIfNode{" +
                "condition=" + this.condition + ", " +
                "first=" + this.first + ", " +
                "second=" + this.second + '}';
    }
}
