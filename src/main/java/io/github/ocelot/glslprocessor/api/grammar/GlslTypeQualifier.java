package io.github.ocelot.glslprocessor.api.grammar;

import io.github.ocelot.glslprocessor.api.node.GlslNode;
import org.jetbrains.annotations.Nullable;

import java.util.*;

/**
 * An immutable representation of data types.
 *
 * @author Ocelot
 * @since 1.0.0
 */
public sealed interface GlslTypeQualifier {

    static GlslTypeQualifier storage(String... typeNames) {
        return new StorageSubroutine(typeNames);
    }

    /**
     * Creates a new layout with the specified layout ids.
     *
     * @param ids The ids to hold in the layout
     * @return A new layout
     */
    static Layout layout(LayoutId... ids) {
        return layout(Arrays.asList(ids));
    }

    /**
     * Creates a new layout with the specified layout ids.
     *
     * @param ids The ids to hold in the layout
     * @return A new layout
     */
    static Layout layout(Collection<LayoutId> ids) {
        return new Layout(List.copyOf(ids));
    }

    /**
     * Creates a new layout id.
     *
     * @param identifier         The name of the layout id
     * @param constantExpression The optional expression to assign it to or <code>null</code>
     * @return A new layout id
     */
    static LayoutId layoutId(String identifier, @Nullable GlslNode constantExpression) {
        if (identifier.equals("shared")) {
            return LayoutId.SHARED;
        }
        return new LayoutId(identifier, constantExpression);
    }

    /**
     * A storage qualifier for a subroutine operand.
     *
     * @param typeNames The operand names for subroutines
     * @author Ocelot
     */
    record StorageSubroutine(String[] typeNames) implements GlslTypeQualifier {
        @Override
        public String toString() {
            return "Storage[operand=SUBROUTINE, typeNames=" + Arrays.toString(this.typeNames) + "]";
        }
    }

    /**
     * Type qualifier for <code>layout(location = 0, ...)</code>.
     *
     * @param layoutIds An immutable view of the layout ids in this layout
     * @since 1.0.0
     */
    record Layout(List<LayoutId> layoutIds) implements GlslTypeQualifier {
        /**
         * Creates a new type qualifier with the added layout id.
         *
         * @param identifier         The name of the layout id
         * @param constantExpression The optional expression to assign it to or <code>null</code>
         * @return A new type qualifier with the added id
         */
        public GlslTypeQualifier addLayoutId(String identifier, @Nullable GlslNode constantExpression) {
            return this.addLayoutIds(layoutId(identifier, constantExpression));
        }

        /**
         * Creates a new type qualifier with the added layout ids.
         *
         * @param newIds The new ids to add
         * @return A new type qualifier with the added ids
         */
        public GlslTypeQualifier addLayoutIds(LayoutId... newIds) {
            return this.addLayoutIds(Arrays.asList(newIds));
        }

        /**
         * Creates a new type qualifier with the added layout ids.
         *
         * @param newIds The new ids to add
         * @return A new type qualifier with the added ids
         */
        public GlslTypeQualifier addLayoutIds(Collection<LayoutId> newIds) {
            List<LayoutId> layoutIds = new ArrayList<>(this.layoutIds.size() + newIds.size());
            layoutIds.addAll(this.layoutIds);
            layoutIds.addAll(newIds);
            return new Layout(Collections.unmodifiableList(layoutIds));
        }
    }

    /**
     * @param identifier The name of this layout id
     * @param expression The expression this is assigned to or <code>null</code>
     * @since 1.0.0
     */
    record LayoutId(String identifier, @Nullable GlslNode expression) {

        /**
         * A constant layout id representing the "shared" keyword.
         */
        public static final LayoutId SHARED = new LayoutId("shared", null);

        /**
         * @return Whether this layout id is shared
         */
        public boolean shared() {
            return "shared".equals(this.identifier);
        }
    }

    enum StorageType implements GlslTypeQualifier {
        CONST,
        IN,
        OUT,
        INOUT,
        CENTROID,
        PATCH,
        SAMPLE,
        UNIFORM,
        BUFFER,
        SHARED,
        COHERENT,
        VOLATILE,
        RESTRICT,
        READONLY,
        WRITEONLY
    }

    enum Precision implements GlslTypeQualifier {
        HIGH_PRECISION,
        MEDIUM_PRECISION,
        LOW_PRECISION
    }

    enum Interpolation implements GlslTypeQualifier {
        SMOOTH,
        FLAT,
        NOPERSPECTIVE
    }

    enum Invariant implements GlslTypeQualifier {
        INVARIANT
    }

    enum Precise implements GlslTypeQualifier {
        PRECISE
    }
}
