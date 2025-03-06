package io.github.ocelot.glslprocessor.api.grammar;

import io.github.ocelot.glslprocessor.api.node.GlslNode;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

/**
 * Specifies the full operand of something in GLSL in addition to all qualifiers.
 *
 * @author Ocelot
 * @since 1.0.0
 */
public final class GlslSpecifiedType implements GlslType {

    private GlslTypeSpecifier specifier;
    private final List<GlslTypeQualifier> qualifiers;

    public GlslSpecifiedType(GlslTypeSpecifier specifier) {
        this.specifier = specifier;
        this.qualifiers = new ArrayList<>();
    }

    public GlslSpecifiedType(GlslTypeSpecifier specifier, GlslTypeQualifier... qualifiers) {
        this(specifier);
        this.qualifiers.addAll(Arrays.asList(qualifiers));
    }

    public GlslSpecifiedType(GlslTypeSpecifier specifier, Collection<GlslTypeQualifier> qualifiers) {
        this(specifier);
        this.qualifiers.addAll(qualifiers);
    }

    /**
     * @return The operand of the field, method, etc
     */
    public GlslTypeSpecifier getSpecifier() {
        return this.specifier;
    }

    /**
     * Adds a layout id to the qualifier list, or adds to an existing layout.
     *
     * @param identifier The name of the identifier
     * @param expression The value to assign it to
     */
    public GlslSpecifiedType addLayoutId(String identifier, @Nullable GlslNode expression) {
        for (int i = 0; i < this.qualifiers.size(); i++) {
            if (this.qualifiers.get(i) instanceof GlslTypeQualifier.Layout layout) {
                this.qualifiers.set(i, layout.addLayoutId(identifier, expression));
                return this;
            }
        }

        this.qualifiers.add(0, GlslTypeQualifier.layout(GlslTypeQualifier.layoutId(identifier, expression)));
        return this;
    }

    /**
     * @return The qualifiers applied to it, for example <code>layout()</code> or <code>flat</code>
     */
    public List<GlslTypeQualifier> getQualifiers() {
        return this.qualifiers;
    }

    public GlslSpecifiedType setSpecifier(GlslTypeSpecifier specifier) {
        this.specifier = specifier;
        return this;
    }

    public GlslSpecifiedType setQualifiers(GlslTypeQualifier... qualifiers) {
        this.qualifiers.clear();
        this.qualifiers.addAll(Arrays.asList(qualifiers));
        return this;
    }

    public GlslSpecifiedType setQualifiers(Collection<GlslTypeQualifier> qualifiers) {
        this.qualifiers.clear();
        this.qualifiers.addAll(qualifiers);
        return this;
    }

    /**
     * @return A deep copy of this type
     */
    public GlslSpecifiedType copy() {
        return new GlslSpecifiedType(this.specifier, this.qualifiers);
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || this.getClass() != o.getClass()) {
            return false;
        }

        GlslSpecifiedType that = (GlslSpecifiedType) o;
        return this.specifier.equals(that.specifier) && this.qualifiers.equals(that.qualifiers);
    }

    @Override
    public int hashCode() {
        int result = this.specifier.hashCode();
        result = 31 * result + this.qualifiers.hashCode();
        return result;
    }

    @Override
    public String toString() {
        return "GlslSpecifiedType[specifier=" + this.specifier + ", qualifiers=" + this.qualifiers;
    }

    @Override
    public GlslSpecifiedType asSpecifiedType() {
        return this;
    }
}
