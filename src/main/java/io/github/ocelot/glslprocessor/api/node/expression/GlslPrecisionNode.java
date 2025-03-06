package io.github.ocelot.glslprocessor.api.node.expression;

import io.github.ocelot.glslprocessor.api.grammar.GlslTypeQualifier;
import io.github.ocelot.glslprocessor.api.grammar.GlslTypeSpecifier;
import io.github.ocelot.glslprocessor.api.node.GlslNode;
import io.github.ocelot.glslprocessor.api.visitor.GlslNodeVisitor;

import java.util.stream.Stream;

/**
 * @author Ocelot
 * @since 1.0.0
 */
public class GlslPrecisionNode implements GlslNode {

    private GlslTypeQualifier.Precision precision;
    private GlslTypeSpecifier typeSpecifier;

    public GlslPrecisionNode(GlslTypeQualifier.Precision precision, GlslTypeSpecifier typeSpecifier) {
        this.precision = precision;
        this.typeSpecifier = typeSpecifier;
    }

    @Override
    public void visit(GlslNodeVisitor visitor) {
        visitor.visitPrecision(this);
    }

    public GlslTypeQualifier.Precision getPrecision() {
        return this.precision;
    }

    public GlslTypeSpecifier getTypeSpecifier() {
        return this.typeSpecifier;
    }

    public GlslPrecisionNode setPrecision(GlslTypeQualifier.Precision precision) {
        this.precision = precision;
        return this;
    }

    public GlslPrecisionNode setTypeSpecifier(GlslTypeSpecifier typeSpecifier) {
        this.typeSpecifier = typeSpecifier;
        return this;
    }

    @Override
    public Stream<GlslNode> stream() {
        return Stream.of(this);
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || this.getClass() != o.getClass()) {
            return false;
        }

        GlslPrecisionNode that = (GlslPrecisionNode) o;
        return this.precision == that.precision && this.typeSpecifier.equals(that.typeSpecifier);
    }

    @Override
    public int hashCode() {
        int result = this.precision.hashCode();
        result = 31 * result + this.typeSpecifier.hashCode();
        return result;
    }

    @Override
    public String toString() {
        return "GlslPrecisionNode{precision=" + this.precision + ", typeSpecifier=" + this.typeSpecifier + '}';
    }
}
