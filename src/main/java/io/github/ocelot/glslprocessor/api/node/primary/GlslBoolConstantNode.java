package io.github.ocelot.glslprocessor.api.node.primary;

import io.github.ocelot.glslprocessor.api.node.GlslConstantNode;

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
    public String getSourceString() {
        return Boolean.toString(this.value);
    }

    @Override
    public String toString() {
        return this.getSourceString();
    }
}
