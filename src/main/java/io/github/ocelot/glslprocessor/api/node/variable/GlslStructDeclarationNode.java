package io.github.ocelot.glslprocessor.api.node.variable;

import io.github.ocelot.glslprocessor.api.grammar.GlslSpecifiedType;
import io.github.ocelot.glslprocessor.api.grammar.GlslStructSpecifier;
import io.github.ocelot.glslprocessor.api.node.GlslNode;
import io.github.ocelot.glslprocessor.api.node.GlslNodeType;
import io.github.ocelot.glslprocessor.api.node.GlslRootNode;
import io.github.ocelot.glslprocessor.api.visitor.GlslNodeVisitor;

import java.util.stream.Stream;

/**
 * @author Ocelot
 * @since 1.0.0
 */
public final class GlslStructDeclarationNode implements GlslRootNode {

    private GlslSpecifiedType specifiedType;

    public GlslStructDeclarationNode(GlslSpecifiedType specifiedType) {
        if (!specifiedType.getSpecifier().isStruct()) {
            throw new IllegalArgumentException("specified type must be struct or array of structs");
        }
        this.specifiedType = specifiedType;
    }

    @Override
    public void visit(GlslNodeVisitor visitor) {
        visitor.visitStructDeclaration(this);
    }

    @Override
    public GlslNodeType getNodeType() {
        return GlslNodeType.STRUCT_DECLARATION;
    }

    public GlslSpecifiedType getSpecifiedType() {
        return this.specifiedType;
    }

    public GlslStructSpecifier getStructSpecifier() {
        return this.specifiedType.getSpecifier().asStructSpecifier();
    }

    @Override
    public Stream<GlslNode> stream() {
        return Stream.of(this);
    }

    public GlslStructDeclarationNode setSpecifiedType(GlslSpecifiedType specifiedType) {
        if (!specifiedType.getSpecifier().isStruct()) {
            throw new IllegalArgumentException("specified type must be struct or array of structs");
        }
        this.specifiedType = specifiedType;
        return this;
    }

    @Override
    public String getName() {
        return this.specifiedType.getSpecifier().getName();
    }

    @Override
    public GlslStructDeclarationNode setName(String name) {
        this.getStructSpecifier().setName(name);
        return this;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || this.getClass() != o.getClass()) {
            return false;
        }

        GlslStructDeclarationNode that = (GlslStructDeclarationNode) o;
        return this.specifiedType.equals(that.specifiedType);
    }

    @Override
    public int hashCode() {
        return this.specifiedType.hashCode();
    }

    @Override
    public String toString() {
        return "GlslStructNode{specifiedType=" + this.specifiedType + '}';
    }
}
