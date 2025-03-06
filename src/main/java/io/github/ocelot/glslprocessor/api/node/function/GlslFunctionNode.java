package io.github.ocelot.glslprocessor.api.node.function;

import io.github.ocelot.glslprocessor.api.grammar.GlslFunctionHeader;
import io.github.ocelot.glslprocessor.api.grammar.GlslParameterDeclaration;
import io.github.ocelot.glslprocessor.api.grammar.GlslSpecifiedType;
import io.github.ocelot.glslprocessor.api.node.GlslNode;
import io.github.ocelot.glslprocessor.api.node.GlslNodeList;
import io.github.ocelot.glslprocessor.api.node.GlslNodeType;
import io.github.ocelot.glslprocessor.api.node.GlslRootNode;
import io.github.ocelot.glslprocessor.api.visitor.GlslNodeVisitor;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.stream.Stream;

/**
 * Defines a function in a GLSL file with an optional body.
 *
 * @author Ocelot
 * @since 1.0.0
 */
public final class GlslFunctionNode implements GlslRootNode {

    private GlslFunctionHeader header;
    private GlslNodeList body;

    public GlslFunctionNode(GlslFunctionHeader header, @Nullable Collection<GlslNode> body) {
        this.header = header;
        this.body = body != null ? new GlslNodeList(body) : null;
    }

    @Override
    public void visit(GlslNodeVisitor visitor) {
        GlslNodeVisitor bodyVisitor = visitor.visitFunctionDeclaration(this);
        if (bodyVisitor != null) {
            for (GlslNode node : this.body) {
                node.visit(bodyVisitor);
            }
            bodyVisitor.visitFunctionDeclarationEnd(this);
        }
    }

    @Override
    public GlslNodeType getNodeType() {
        return GlslNodeType.FUNCTION;
    }

    /**
     * @return The full signature of this function
     */
    public GlslFunctionHeader getHeader() {
        return this.header;
    }

    @Override
    public @NotNull String getName() {
        return this.header.getName();
    }

    /**
     * @return The return type of the function
     */
    public GlslSpecifiedType getReturnType() {
        return this.header.getReturnType();
    }

    /**
     * @return The parameters of the function
     */
    public List<GlslParameterDeclaration> getParameters() {
        return this.header.getParameters();
    }

    @Override
    public @Nullable GlslNodeList getBody() {
        return this.body;
    }

    /**
     * Sets the function header of this function to the specified value.
     *
     * @param header The new header
     */
    public void setHeader(GlslFunctionHeader header) {
        this.header = header;
    }

    @Override
    public GlslFunctionNode setName(@Nullable String name) {
        this.header.setName(Objects.requireNonNull(name));
        return this;
    }

    /**
     * Sets the body of this function or <code>null</code> to make this a function prototype.
     *
     * @param body The new function body
     */
    @Override
    public boolean setBody(@Nullable Collection<GlslNode> body) {
        if (body != null) {
            if (this.body != null) {
                this.body.clear();
                this.body.addAll(body);
            } else {
                this.body = new GlslNodeList(body);
            }
        } else {
            this.body = null;
        }
        return true;
    }

    @Override
    public Stream<GlslNode> stream() {
        return Stream.concat(Stream.of(this), this.body.stream().flatMap(GlslNode::stream));
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || this.getClass() != o.getClass()) {
            return false;
        }

        GlslFunctionNode that = (GlslFunctionNode) o;
        return this.header.equals(that.header) && Objects.equals(this.body, that.body);
    }

    @Override
    public int hashCode() {
        int result = this.header.hashCode();
        result = 31 * result + Objects.hashCode(this.body);
        return result;
    }

    @Override
    public String toString() {
        return "GlslFunction{header=" + this.header + ", body=" + this.body + '}';
    }
}
