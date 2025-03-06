package io.github.ocelot.glslprocessor.api.node.branch;

import io.github.ocelot.glslprocessor.api.node.GlslNode;
import io.github.ocelot.glslprocessor.api.visitor.GlslNodeVisitor;
import org.jetbrains.annotations.Nullable;

import java.util.stream.Stream;

/**
 * @author Ocelot
 * @since 1.0.0
 */
public class GlslReturnNode implements GlslNode {

    private GlslNode value;

    public GlslReturnNode(@Nullable GlslNode value) {
        this.value = value;
    }

    public @Nullable GlslNode getValue() {
        return this.value;
    }

    public void setValue(@Nullable GlslNode value) {
        this.value = value;
    }

    @Override
    public void visit(GlslNodeVisitor visitor) {
        visitor.visitReturn(this);
    }

    @Override
    public Stream<GlslNode> stream() {
        return Stream.concat(Stream.of(this), this.value.stream());
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || this.getClass() != o.getClass()) {
            return false;
        }

        GlslReturnNode that = (GlslReturnNode) o;
        return this.value.equals(that.value);
    }

    @Override
    public int hashCode() {
        return this.value.hashCode();
    }

    @Override
    public String toString() {
        return "ReturnNode{value=" + this.value + '}';
    }
}
