package io.github.ocelot.glslprocessor.api.visitor;

import io.github.ocelot.glslprocessor.api.node.function.GlslInvokeFunctionNode;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.Nullable;

/**
 * @author Ocelot
 * @since 1.0.0
 */
@ApiStatus.Experimental
public class GlslInvokeVisitor {

    private final GlslInvokeVisitor parent;

    public GlslInvokeVisitor() {
        this(null);
    }

    public GlslInvokeVisitor(@Nullable GlslInvokeVisitor parent) {
        this.parent = parent;
    }

    /**
     * Visits the invoke function header.
     */
    public void visitHeader() {
        if (this.parent != null) {
            this.parent.visitHeader();
        }
    }

    /**
     * Visits the specified parameter.
     *
     * @param index The index of the parameter to visit
     * @return A visitor for the header or <code>null</code> to skip
     */
    public @Nullable GlslNodeVisitor visitParameter(int index) {
        return this.parent != null ? this.parent.visitParameter(index) : null;
    }

    /**
     * Visits the end of the function invocation.
     *
     * @param node The node that was visited
     */
    public void visitInvokeEnd(GlslInvokeFunctionNode node) {
        if (this.parent != null) {
            this.parent.visitInvokeEnd(node);
        }
    }
}
