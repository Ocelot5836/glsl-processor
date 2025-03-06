package io.github.ocelot.glslprocessor.api.node.constant;

/**
 * @author Ocelot
 * @since 1.0.0
 */
public record GlslBoolConstantNode(boolean value) implements GlslConstantNode {

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
    public String toString() {
        return Boolean.toString(this.value);
    }
}
