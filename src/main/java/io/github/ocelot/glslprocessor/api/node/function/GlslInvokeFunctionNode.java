package io.github.ocelot.glslprocessor.api.node.function;

import io.github.ocelot.glslprocessor.api.node.GlslNode;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class GlslInvokeFunctionNode implements GlslNode {

    private GlslNode header;
    private final List<GlslNode> parameters;

    public GlslInvokeFunctionNode(GlslNode header, Collection<GlslNode> parameters) {
        this.header = header;
        this.parameters = new ArrayList<>(parameters);
    }

    public GlslNode getHeader() {
        return this.header;
    }

    public List<GlslNode> getParameters() {
        return this.parameters;
    }

    public void setHeader(GlslNode header) {
        this.header = header;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || this.getClass() != o.getClass()) {
            return false;
        }

        GlslInvokeFunctionNode that = (GlslInvokeFunctionNode) o;
        return this.header.equals(that.header) && this.parameters.equals(that.parameters);
    }

    @Override
    public int hashCode() {
        int result = this.header.hashCode();
        result = 31 * result + this.parameters.hashCode();
        return result;
    }

    @Override
    public String toString() {
        return "GlslInvokeFunctionNode{name=" + this.header + ", parameters=" + this.parameters + '}';
    }

    @Override
    public String getSourceString() {
        String parameters = this.parameters.stream().map(GlslNode::getSourceString).collect(Collectors.joining(", "));
        return this.header.getSourceString() + "(" + parameters + ")";
    }

    @Override
    public Stream<GlslNode> stream() {
        return Stream.concat(Stream.of(this), this.parameters.stream().flatMap(GlslNode::stream));
    }
}
