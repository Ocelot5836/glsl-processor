package io.github.ocelot.glslprocessor.api.node.branch;

import io.github.ocelot.glslprocessor.api.node.GlslNode;
import io.github.ocelot.glslprocessor.api.node.GlslNodeList;

import java.util.Collection;
import java.util.stream.Stream;

/**
 * Represents both while and do/while loops.
 *
 * @author Ocelot
 */
public class WhileLoopNode implements GlslNode {

    private GlslNode condition;
    private final GlslNodeList body;
    private Type loopType;

    public WhileLoopNode(GlslNode condition, Collection<GlslNode> body, Type loopType) {
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

    public WhileLoopNode setCondition(GlslNode condition) {
        this.condition = condition;
        return this;
    }

    public WhileLoopNode setLoopType(Type loopType) {
        this.loopType = loopType;
        return this;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || this.getClass() != o.getClass()) {
            return false;
        }

        WhileLoopNode that = (WhileLoopNode) o;
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
    public String getSourceString() {
        StringBuilder builder = new StringBuilder("while (" + this.condition.getSourceString() + ") {\n");
        for (GlslNode node : this.body) {
            builder.append('\t').append(NEWLINE.matcher(node.getSourceString()).replaceAll("\n\t")).append(";\n");
        }
        builder.append('}');
        return builder.toString();
    }

    @Override
    public Stream<GlslNode> stream() {
        return Stream.concat(Stream.of(this), Stream.concat(this.condition.stream(), this.body.stream()));
    }

    public enum Type {
        WHILE, DO
    }
}
