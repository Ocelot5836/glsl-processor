package io.github.ocelot.glslprocessor.api.node.constant;

import io.github.ocelot.glslprocessor.api.node.GlslNode;
import io.github.ocelot.glslprocessor.api.node.GlslNodeType;
import io.github.ocelot.glslprocessor.api.visitor.GlslNodeVisitor;

import java.util.stream.Stream;

/**
 * @author Ocelot
 * @since 1.0.0
 */
public interface GlslConstantNode extends GlslNode {

    Number numberValue();

    default double doubleValue() {
        return this.numberValue().doubleValue();
    }

    default float floatValue() {
        return this.numberValue().floatValue();
    }

    default int intValue() {
        return this.numberValue().intValue();
    }

    default long unsignedIntValue() {
        return Integer.toUnsignedLong(this.intValue());
    }

    boolean booleanValue();

    boolean isNumber();

    void set(Number value);

    void set(boolean value);

    @Override
    default void visit(GlslNodeVisitor visitor) {
        visitor.visitConstant(this);
    }

    @Override
    default Stream<GlslNode> stream() {
        return Stream.of(this);
    }
}
