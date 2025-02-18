package io.github.ocelot.glslprocessor.api;

import io.github.ocelot.glslprocessor.api.node.GlslTree;

/**
 * Specific locations inside a {@link GlslTree} nodes can be added to.
 *
 * @author Ocelot
 */
public enum GlslInjectionPoint {

    BEFORE_DECLARATIONS,
    AFTER_DECLARATIONS,
    BEFORE_MAIN,
    AFTER_MAIN,
    BEFORE_FUNCTIONS,
    AFTER_FUNCTIONS
}
