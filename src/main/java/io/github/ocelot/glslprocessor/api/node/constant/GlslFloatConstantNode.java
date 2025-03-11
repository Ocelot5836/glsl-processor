package io.github.ocelot.glslprocessor.api.node.constant;

import io.github.ocelot.glslprocessor.api.node.GlslNodeType;
import org.jetbrains.annotations.ApiStatus;

/**
 * @author Ocelot
 * @since 1.0.0
 */
public final class GlslFloatConstantNode implements GlslConstantNode {

    private float value;

    @ApiStatus.Internal
    public GlslFloatConstantNode(float value) {
        this.value = value;
    }

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
    public void set(Number value) {
        this.value = value.floatValue();
    }

    @Override
    public void set(boolean value) {
        this.value = value ? 1 : 0;
    }

    @Override
    public String toString() {
        return Float.toString(this.value);
    }

    @Override
    public GlslNodeType getNodeType() {
        return GlslNodeType.FLOAT_CONSTANT;
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof GlslFloatConstantNode that)) return false;
        return Float.compare(this.value, that.value) == 0;
    }

    @Override
    public int hashCode() {
        return Float.hashCode(this.value);
    }
}
