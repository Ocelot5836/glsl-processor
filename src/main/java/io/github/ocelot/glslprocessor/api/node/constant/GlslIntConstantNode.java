package io.github.ocelot.glslprocessor.api.node.constant;

import io.github.ocelot.glslprocessor.api.node.GlslNodeType;
import org.jetbrains.annotations.ApiStatus;

/**
 * @author Ocelot
 * @since 1.0.0
 */
public final class GlslIntConstantNode implements GlslConstantNode {

    private GlslIntFormat format;
    private boolean signed;
    private int value;

    @ApiStatus.Internal
    public GlslIntConstantNode(GlslIntFormat format, boolean signed, int value) {
        this.format = format;
        this.signed = signed;
        this.value = value;
    }

    public GlslIntFormat format() {
        return this.format;
    }

    public boolean signed() {
        return this.signed;
    }

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
    public void set(Number value) {
        this.value = value.intValue();
    }

    @Override
    public void set(boolean value) {
        this.value = value ? 1 : 0;
    }

    public void setFormat(GlslIntFormat format) {
        this.format = format;
    }

    public void setSigned(boolean signed) {
        this.signed = signed;
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

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof GlslIntConstantNode that)) return false;
        return this.signed == that.signed && this.value == that.value && this.format == that.format;
    }

    @Override
    public int hashCode() {
        int result = this.format.hashCode();
        result = 31 * result + Boolean.hashCode(this.signed);
        result = 31 * result + this.value;
        return result;
    }
}
