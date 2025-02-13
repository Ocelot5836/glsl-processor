package io.github.ocelot.glslprocessor.api.node;

import io.github.ocelot.glslprocessor.api.node.function.GlslFunctionNode;
import io.github.ocelot.glslprocessor.api.node.variable.GlslDeclarationNode;
import io.github.ocelot.glslprocessor.api.node.variable.GlslNewNode;
import io.github.ocelot.glslprocessor.api.node.variable.GlslStructNode;
import org.jetbrains.annotations.Nullable;

public interface GlslRootNode extends GlslNode {

    @Nullable String getName();

    GlslRootNode setName(@Nullable String name);

    default boolean isDeclaration() {
        return this instanceof GlslDeclarationNode;
    }

    default boolean isFunction() {
        return this instanceof GlslFunctionNode;
    }

    default boolean isField() {
        return this instanceof GlslNewNode;
    }

    default boolean isStruct() {
        return this instanceof GlslStructNode;
    }

    default GlslDeclarationNode asDeclaration() {
        if (this instanceof GlslDeclarationNode node) {
            return node;
        }
        throw new IllegalStateException("This node is not a GlslDeclarationNode");
    }

    default GlslFunctionNode asFunction() {
        if (this instanceof GlslFunctionNode node) {
            return node;
        }
        throw new IllegalStateException("This node is not a GlslFunctionNode");
    }

    default GlslNewNode asField() {
        if (this instanceof GlslNewNode node) {
            return node;
        }
        throw new IllegalStateException("This node is not a GlslNewNode");
    }

    default GlslStructNode asStruct() {
        if (this instanceof GlslStructNode node) {
            return node;
        }
        throw new IllegalStateException("This node is not a GlslStructNode");
    }
}
