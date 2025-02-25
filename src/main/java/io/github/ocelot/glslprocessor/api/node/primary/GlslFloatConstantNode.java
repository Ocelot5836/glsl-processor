package io.github.ocelot.glslprocessor.api.node.primary;

import io.github.ocelot.glslprocessor.api.node.GlslConstantNode;

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
    public String getSourceString() {
        return Float.toString(this.value);
    }

    @Override
    public String toString() {
        return this.getSourceString();
    }
}
