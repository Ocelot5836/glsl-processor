package io.github.ocelot.glslprocessor.api.node.primary;

import io.github.ocelot.glslprocessor.api.node.GlslConstantNode;

public record GlslDoubleConstantNode(double value) implements GlslConstantNode {

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
    public String getSourceString() {
        return this.value + "lf";
    }

    @Override
    public String toString() {
        return this.getSourceString();
    }
}
