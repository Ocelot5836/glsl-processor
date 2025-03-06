package io.github.ocelot.glslprocessor.api.node.constant;

import io.github.ocelot.glslprocessor.api.node.GlslNodeType;

/**
 * @author Ocelot
 * @since 1.0.0
 */
public record GlslFloatConstantNode(float value) implements GlslConstantNode {

    @Override
    public Number numberValue() {
        return this.value;
    }

    @Override
    public float floatValue() {
        return this.value;
    }

    @Override
    public boolean booleanValue() {
        return this.value != 0.0;
    }

    @Override
    public boolean isNumber() {
        return true;
    }

    @Override
    public String toString() {
        return Float.toString(this.value);
    }

    @Override
    public GlslNodeType getNodeType() {
        return GlslNodeType.FLOAT_CONSTANT;
    }
}
