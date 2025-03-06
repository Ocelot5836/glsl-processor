package io.github.ocelot.glslprocessor.api.node;

import io.github.ocelot.glslprocessor.api.node.function.GlslFunctionNode;
import io.github.ocelot.glslprocessor.api.node.variable.GlslNewFieldNode;
import io.github.ocelot.glslprocessor.api.node.variable.GlslStructDeclarationNode;
import io.github.ocelot.glslprocessor.api.node.variable.GlslVariableDeclarationNode;
import org.jetbrains.annotations.Nullable;

/**
 * @author Ocelot
 * @since 1.0.0
 */
public interface GlslRootNode extends GlslNode {

    @Nullable String getName();

    GlslRootNode setName(@Nullable String name);

    default boolean isDeclaration() {
        return this instanceof GlslVariableDeclarationNode;
    }

    default boolean isFunction() {
        return this instanceof GlslFunctionNode;
    }

    default boolean isField() {
        return this instanceof GlslNewFieldNode;
    }

    default boolean isStruct() {
        return this instanceof GlslStructDeclarationNode;
    }

    default GlslVariableDeclarationNode asDeclaration() {
        if (this instanceof GlslVariableDeclarationNode node) {
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

    default GlslNewFieldNode asField() {
        if (this instanceof GlslNewFieldNode node) {
            return node;
        }
        throw new IllegalStateException("This node is not a GlslNewNode");
    }

    default GlslStructDeclarationNode asStruct() {
        if (this instanceof GlslStructDeclarationNode node) {
            return node;
        }
        throw new IllegalStateException("This node is not a GlslStructNode");
    }
}
