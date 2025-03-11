package io.github.ocelot.glslprocessor.api.node.constant;

import io.github.ocelot.glslprocessor.api.node.GlslNodeType;
import org.jetbrains.annotations.ApiStatus;

/**
 * @author Ocelot
 * @since 1.0.0
 */
public final class GlslBoolConstantNode implements GlslConstantNode {

    private boolean value;

    @ApiStatus.Internal
    public GlslBoolConstantNode(boolean value) {
        this.value = value;
    }

    @Override
    public Number numberValue() {
        return this.value ? 1 : 0;
    }

    @Override
    public boolean booleanValue() {
        return this.value;
    }

    @Override
    public boolean isNumber() {
        return false;
    }

    @Override
    public void set(Number value) {
        this.value = value.longValue() == 1;
    }

    @Override
    public void set(boolean value) {
        this.value = value;
    }

    @Override
    public String toString() {
        return Boolean.toString(this.value);
    }

    @Override
    public GlslNodeType getNodeType() {
        return GlslNodeType.BOOL_CONSTANT;
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof GlslBoolConstantNode that)) return false;
        return this.value == that.value;
    }

    @Override
    public int hashCode() {
        return Boolean.hashCode(this.value);
    }
}
