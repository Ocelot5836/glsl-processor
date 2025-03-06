package io.github.ocelot.glslprocessor.api.node.branch;

import io.github.ocelot.glslprocessor.api.node.GlslNode;
import io.github.ocelot.glslprocessor.api.node.GlslNodeType;
import io.github.ocelot.glslprocessor.api.visitor.GlslNodeVisitor;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;
import java.util.stream.Stream;

/**
 * @author Ocelot
 * @since 1.0.0
 */
public final class GlslCaseLabelNode implements GlslNode {

    private GlslNode condition;

    public GlslCaseLabelNode(@Nullable GlslNode condition) {
        this.condition = condition;
    }

    public boolean isDefault() {
        return this.condition == null;
    }

    public GlslNode getCondition() {
        return this.condition;
    }

    public void setCondition(@Nullable GlslNode condition) {
        this.condition = condition;
    }

    @Override
    public void visit(GlslNodeVisitor visitor) {
        throw new UnsupportedOperationException("Cannot call visit() on GlslCaseLabelNode");
    }

    @Override
    public GlslNodeType getNodeType() {
        return GlslNodeType.CASE_LABEL;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || this.getClass() != o.getClass()) {
            return false;
        }

        GlslCaseLabelNode that = (GlslCaseLabelNode) o;
        return this.condition.equals(that.condition);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(this.condition);
    }

    @Override
    public String toString() {
        return "GlslCaseLabelNode{condition=" + (this.condition == null ? "default" : this.condition) + '}';
    }

    @Override
    public Stream<GlslNode> stream() {
        return Stream.concat(Stream.of(this), this.condition.stream());
    }
}
