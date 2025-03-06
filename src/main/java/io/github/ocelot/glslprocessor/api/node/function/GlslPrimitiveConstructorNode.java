package io.github.ocelot.glslprocessor.api.node.function;

import io.github.ocelot.glslprocessor.api.grammar.GlslTypeSpecifier;
import io.github.ocelot.glslprocessor.api.node.GlslNode;
import io.github.ocelot.glslprocessor.api.visitor.GlslNodeVisitor;

import java.util.stream.Stream;

/**
 * @author Ocelot
 * @since 1.0.0
 */
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
    public void visit(GlslNodeVisitor visitor) {
        visitor.visitPrimitiveConstructor(this);
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
    public Stream<GlslNode> stream() {
        return Stream.of(this);
    }
}
