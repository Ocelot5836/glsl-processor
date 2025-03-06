package io.github.ocelot.glslprocessor.api.node.variable;

import io.github.ocelot.glslprocessor.api.grammar.GlslTypeQualifier;
import io.github.ocelot.glslprocessor.api.node.GlslNode;
import io.github.ocelot.glslprocessor.api.node.GlslRootNode;
import io.github.ocelot.glslprocessor.api.visitor.GlslNodeVisitor;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.stream.Stream;

/**
 * @author Ocelot
 * @since 1.0.0
 */
public class GlslVariableDeclarationNode implements GlslRootNode {

    private final List<GlslTypeQualifier> typeQualifiers;
    private final List<String> names;

    public GlslVariableDeclarationNode(Collection<GlslTypeQualifier> typeQualifiers, Collection<String> names) {
        this.typeQualifiers = new ArrayList<>(typeQualifiers);
        this.names = new ArrayList<>(names);
    }

    @Override
    public void visit(GlslNodeVisitor visitor) {
        visitor.visitVariableDeclaration(this);
    }

    public List<GlslTypeQualifier> getTypeQualifiers() {
        return this.typeQualifiers;
    }

    public List<String> getNames() {
        return this.names;
    }

    @Override
    public Stream<GlslNode> stream() {
        return Stream.of(this);
    }

    @Override
    public @Nullable String getName() {
        throw new UnsupportedOperationException("Use getNames() instead");
    }

    @Override
    public GlslRootNode setName(@Nullable String name) {
        throw new UnsupportedOperationException("Use setNames() instead");
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || this.getClass() != o.getClass()) {
            return false;
        }

        GlslVariableDeclarationNode that = (GlslVariableDeclarationNode) o;
        return this.typeQualifiers.equals(that.typeQualifiers) && this.names.equals(that.names);
    }

    @Override
    public int hashCode() {
        int result = this.typeQualifiers.hashCode();
        result = 31 * result + this.names.hashCode();
        return result;
    }

    @Override
    public String toString() {
        return "GlslDeclarationNode{typeQualifiers=" + this.typeQualifiers + ", names=" + this.names + '}';
    }
}
