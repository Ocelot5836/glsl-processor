package io.github.ocelot.glslprocessor.api.node.branch;

import io.github.ocelot.glslprocessor.api.node.GlslNode;
import io.github.ocelot.glslprocessor.api.node.GlslNodeList;
import io.github.ocelot.glslprocessor.api.visitor.GlslNodeVisitor;

import java.util.Collection;
import java.util.stream.Stream;

/**
 * Represents both while and do/while loops.
 *
 * @author Ocelot
 * @since 1.0.0
 */
public class GlslWhileLoopNode implements GlslNode {

    private GlslNode condition;
    private final GlslNodeList body;
    private Type loopType;

    public GlslWhileLoopNode(GlslNode condition, Collection<GlslNode> body, Type loopType) {
        this.condition = condition;
        this.body = new GlslNodeList(body);
        this.loopType = loopType;
    }

    public GlslNode getCondition() {
        return this.condition;
    }

    @Override
    public GlslNodeList getBody() {
        return this.body;
    }

    public Type getLoopType() {
        return this.loopType;
    }

    public GlslWhileLoopNode setCondition(GlslNode condition) {
        this.condition = condition;
        return this;
    }

    public GlslWhileLoopNode setLoopType(Type loopType) {
        this.loopType = loopType;
        return this;
    }

    @Override
    public void visit(GlslNodeVisitor visitor) {
        GlslNodeVisitor bodyVisitor = visitor.visitWhileLoop(this);
        if (bodyVisitor != null) {
            for (GlslNode node : this.body) {
                node.visit(bodyVisitor);
            }
            bodyVisitor.visitWhileLoopEnd(this);
        }
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || this.getClass() != o.getClass()) {
            return false;
        }

        GlslWhileLoopNode that = (GlslWhileLoopNode) o;
        return this.condition.equals(that.condition) && this.body.equals(that.body) && this.loopType == that.loopType;
    }

    @Override
    public int hashCode() {
        int result = this.condition.hashCode();
        result = 31 * result + this.body.hashCode();
        result = 31 * result + this.loopType.hashCode();
        return result;
    }

    @Override
    public Stream<GlslNode> stream() {
        return Stream.concat(Stream.of(this), Stream.concat(this.condition.stream(), this.body.stream()));
    }

    public enum Type {
        WHILE, DO
    }
}
