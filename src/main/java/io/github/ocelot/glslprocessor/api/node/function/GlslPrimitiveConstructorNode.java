package io.github.ocelot.glslprocessor.api.node.function;

import io.github.ocelot.glslprocessor.api.grammar.GlslTypeSpecifier;
import io.github.ocelot.glslprocessor.api.node.GlslNode;

import java.util.stream.Stream;

public class GlslPrimitiveConstructorNode implements GlslNode {

    private GlslTypeSpecifier primitiveType;

    public GlslPrimitiveConstructorNode(GlslTypeSpecifier primitiveType) {
        this.primitiveType = primitiveType;
    }

    public GlslTypeSpecifier getPrimitiveType() {
        return this.primitiveType;
    }

    public void setPrimitiveType(GlslTypeSpecifier primitiveType) {
        this.primitiveType = primitiveType;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || this.getClass() != o.getClass()) {
            return false;
        }

        GlslPrimitiveConstructorNode that = (GlslPrimitiveConstructorNode) o;
        return this.primitiveType.equals(that.primitiveType);
    }

    @Override
    public int hashCode() {
        return this.primitiveType.hashCode();
    }

    @Override
    public String toString() {
        return "PrimitiveConstructorNode{operand=" + this.primitiveType + '}';
    }

    @Override
    public String getSourceString() {
        return this.primitiveType.getSourceString() + this.primitiveType.getPostSourceString();
    }

    @Override
    public Stream<GlslNode> stream() {
        return Stream.of(this);
    }
}
