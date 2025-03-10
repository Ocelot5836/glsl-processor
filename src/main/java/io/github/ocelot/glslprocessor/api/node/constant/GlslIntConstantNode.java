package io.github.ocelot.glslprocessor.api.node.constant;

import io.github.ocelot.glslprocessor.api.node.GlslNodeType;

/**
 * @author Ocelot
 * @since 1.0.0
 */
public record GlslIntConstantNode(GlslIntFormat format, boolean signed, int value) implements GlslConstantNode {

    @Override
    public Number numberValue() {
        return this.value;
    }

    @Override
    public int intValue() {
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
        return switch (this.format) {
            case HEXADECIMAL -> "0x" + Integer.toHexString(this.value) + (this.signed ? "" : "u");
            case OCTAL -> (this.value > 0 ? "0" : "") + Integer.toOctalString(this.value) + (this.signed ? "" : "u");
            case DECIMAL -> this.value + (this.signed ? "" : "u");
        };
    }

    @Override
    public GlslNodeType getNodeType() {
        return GlslNodeType.INT_CONSTANT;
    }
}
