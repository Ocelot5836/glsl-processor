package io.github.ocelot.glslprocessor.api.node.constant;

import io.github.ocelot.glslprocessor.api.node.GlslNodeType;
import org.jetbrains.annotations.ApiStatus;

/**
 * @author Ocelot
 * @since 1.0.0
 */
public final class GlslDoubleConstantNode implements GlslConstantNode {

    private double value;

    @ApiStatus.Internal
    public GlslDoubleConstantNode(double value) {
        this.value = value;
    }

    @Override
    public Number numberValue() {
        return this.value;
    }

    @Override
    public double doubleValue() {
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
        this.value = value.doubleValue();
    }

    @Override
    public void set(boolean value) {
        this.value = value ? 1 : 0;
    }

    @Override
    public String toString() {
        return this.value + "lf";
    }

    @Override
    public GlslNodeType getNodeType() {
        return GlslNodeType.DOUBLE_CONSTANT;
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof GlslDoubleConstantNode that)) return false;
        return Double.compare(this.value, that.value) == 0;
    }

    @Override
    public int hashCode() {
        return Double.hashCode(this.value);
    }
}
