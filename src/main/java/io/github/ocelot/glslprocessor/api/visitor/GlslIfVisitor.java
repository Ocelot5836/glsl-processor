package io.github.ocelot.glslprocessor.api.visitor;

import io.github.ocelot.glslprocessor.api.node.branch.GlslIfNode;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.Nullable;

/**
 * @author Ocelot
 * @since 1.0.0
 */
@ApiStatus.Experimental
public class GlslIfVisitor {

    private final GlslIfVisitor parent;

    public GlslIfVisitor() {
        this(null);
    }

    public GlslIfVisitor(@Nullable GlslIfVisitor parent) {
        this.parent = parent;
    }

    public @Nullable GlslNodeVisitor visitIf() {
        return this.parent != null ? this.parent.visitIf() : null;
    }

    public @Nullable GlslNodeVisitor visitElse() {
        return this.parent != null ? this.parent.visitElse() : null;
    }

    public void visitIfEnd(GlslIfNode node) {
        if (this.parent != null) {
            this.parent.visitIfEnd(node);
        }
    }
}
