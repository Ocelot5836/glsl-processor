package io.github.ocelot.glslprocessor.api.grammar;

import org.jetbrains.annotations.Nullable;

import java.util.Collection;

/**
 * Represents a single parameter declaration. Includes the name and full data operand of the parameter.
 *
 * @author Ocelot
 * @since 1.0.0
 */
public final class GlslParameterDeclaration {

    private GlslSpecifiedType type;
    private String name;

    public GlslParameterDeclaration(GlslType type, @Nullable String name) {
        this.name = name;
        this.type = type.asSpecifiedType();
    }

    /**
     * @return The parameter data operand
     */
    public GlslSpecifiedType getType() {
        return this.type;
    }

    /**
     * @return The name of the parameter or <code>null</code> if declared like <code>void foo(int)</code>
     */
    public @Nullable String getName() {
        return this.name;
    }

    /**
     * Sets the data operand of this parameter.
     *
     * @param type The new operand
     */
    public GlslParameterDeclaration setType(GlslType type) {
        this.type = type.asSpecifiedType();
        return this;
    }

    /**
     * Sets the name of this parameter.
     *
     * @param name The new name
     */
    public GlslParameterDeclaration setName(@Nullable String name) {
        this.name = name;
        return this;
    }

    /**
     * Sets The qualifiers of this parameter operand.
     *
     * @param qualifiers The new qualifiers
     */
    public GlslParameterDeclaration setQualifiers(GlslTypeQualifier... qualifiers) {
        this.type.setQualifiers(qualifiers);
        return this;
    }

    /**
     * Sets The qualifiers of this parameter operand.
     *
     * @param qualifiers The new qualifiers
     */
    public GlslParameterDeclaration setQualifiers(Collection<GlslTypeQualifier> qualifiers) {
        this.type.setQualifiers(qualifiers);
        return this;
    }

    /**
     * @return A deep copy of this parameter declaration
     */
    public GlslParameterDeclaration copy() {
        return new GlslParameterDeclaration(this.type.copy(), this.name);
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || this.getClass() != o.getClass()) {
            return false;
        }

        GlslParameterDeclaration that = (GlslParameterDeclaration) o;
        return this.name.equals(that.name) && this.type.equals(that.type);
    }

    @Override
    public int hashCode() {
        int result = this.name.hashCode();
        result = 31 * result + this.type.hashCode();
        return result;
    }

    @Override
    public String toString() {
        return "GlslParameterDeclaration{type='" + this.type + "', name=" + this.name + '}';
    }
}
