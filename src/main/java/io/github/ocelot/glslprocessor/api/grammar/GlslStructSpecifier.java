package io.github.ocelot.glslprocessor.api.grammar;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

/**
 * @author Ocelot
 * @since 1.0.0
 */
public final class GlslStructSpecifier implements GlslTypeSpecifier {

    private String name;
    private final List<GlslStructField> fields;

    GlslStructSpecifier(String name, Collection<GlslStructField> fields) {
        this(name, new ArrayList<>(fields));
    }

    private GlslStructSpecifier(String name, List<GlslStructField> fields) {
        this.name = name;
        this.fields = fields;
    }

    @Override
    public String getName() {
        return this.name;
    }

    public List<GlslStructField> getFields() {
        return this.fields;
    }

    public GlslStructSpecifier setName(String name) {
        this.name = name;
        return this;
    }

    /**
     * Sets the fields in this struct.
     *
     * @param fields The new fields to use
     */
    public GlslStructSpecifier setFields(GlslStructField... fields) {
        return this.setFields(Arrays.asList(fields));
    }

    /**
     * Sets the fields in this struct.
     *
     * @param fields The new fields to use
     */
    public GlslStructSpecifier setFields(Collection<GlslStructField> fields) {
        this.fields.clear();
        this.fields.addAll(fields);
        return this;
    }

    /**
     * @return A deep copy of this struct
     */
    public GlslStructSpecifier copy() {
        List<GlslStructField> fields = new ArrayList<>(this.fields.size());
        for (GlslStructField field : this.fields) {
            fields.add(field.copy());
        }
        return new GlslStructSpecifier(this.name, fields);
    }

    @Override
    public boolean isStruct() {
        return true;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || this.getClass() != o.getClass()) {
            return false;
        }

        GlslStructSpecifier that = (GlslStructSpecifier) o;
        return this.name.equals(that.name) && this.fields.equals(that.fields);
    }

    @Override
    public int hashCode() {
        int result = this.name.hashCode();
        result = 31 * result + this.fields.hashCode();
        return result;
    }

    @Override
    public String toString() {
        return "GlslStructSpecifier{name='" + this.name + "', fields=" + this.fields + '}';
    }
}
