package io.github.ocelot.glslprocessor.api.node.variable;

import io.github.ocelot.glslprocessor.api.grammar.GlslSpecifiedType;
import io.github.ocelot.glslprocessor.api.grammar.GlslType;
import io.github.ocelot.glslprocessor.api.node.GlslNode;
import io.github.ocelot.glslprocessor.api.node.GlslNodeType;
import io.github.ocelot.glslprocessor.api.node.GlslRootNode;
import io.github.ocelot.glslprocessor.api.visitor.GlslNodeVisitor;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;
import java.util.stream.Stream;

/**
 * @author Ocelot
 * @since 1.0.0
 */
public final class GlslNewFieldNode implements GlslRootNode {

    private GlslSpecifiedType type;
    private String name;
    private GlslNode initializer;

    public GlslNewFieldNode(GlslType type, @Nullable String name, @Nullable GlslNode initializer) {
        this.type = type.asSpecifiedType();
        this.name = name;
        this.initializer = initializer;
    }

    @Override
    public void visit(GlslNodeVisitor visitor) {
        visitor.visitNewField(this);
    }

    @Override
    public GlslNodeType getNodeType() {
        return GlslNodeType.NEW_FIELD;
    }

    @Override
    public @NotNull GlslSpecifiedType getType() {
        return this.type;
    }

    @Override
    public Stream<GlslNode> stream() {
        return this.initializer != null ? Stream.concat(Stream.of(this), this.initializer.stream()) : Stream.of(this);
    }

    @Override
    public @Nullable String getName() {
        return this.name;
    }

    public @Nullable GlslNode getInitializer() {
        return this.initializer;
    }

    public GlslNewFieldNode setType(GlslType type) {
        this.type = type.asSpecifiedType();
        return this;
    }

    @Override
    public GlslNewFieldNode setName(@Nullable String name) {
        this.name = name;
        return this;
    }

    public GlslNewFieldNode setInitializer(@Nullable GlslNode initializer) {
        this.initializer = initializer;
        return this;
    }

    @Override
    public String toString() {
        return "GlslNewNode{operand=" + this.type + ", name='" + this.name + "', initializer=" + this.initializer + '}';
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || this.getClass() != o.getClass()) {
            return false;
        }

        GlslNewFieldNode that = (GlslNewFieldNode) o;
        return this.type.equals(that.type) && this.name.equals(that.name) && Objects.equals(this.initializer, that.initializer);
    }

    @Override
    public int hashCode() {
        int result = this.type.hashCode();
        result = 31 * result + this.name.hashCode();
        result = 31 * result + Objects.hashCode(this.initializer);
        return result;
    }
}