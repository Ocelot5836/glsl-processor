package io.github.ocelot.glslprocessor.api.grammar;

/**
 * Allows both a {@link GlslSpecifiedType} and {@link GlslSpecifiedType} to act as a specified type.
 *
 * @author Ocelot
 * @since 1.0.0
 */
public sealed interface GlslType permits GlslSpecifiedType, GlslTypeSpecifier {

    /**
     * @return This type represented as a specified type
     */
    GlslSpecifiedType asSpecifiedType();
}
