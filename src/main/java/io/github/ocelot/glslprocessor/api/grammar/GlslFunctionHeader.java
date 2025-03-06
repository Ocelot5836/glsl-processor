package io.github.ocelot.glslprocessor.api.grammar;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

/**
 * Information about a function definition, not including a body.
 *
 * @author Ocelot
 * @since 1.0.0
 */
public final class GlslFunctionHeader {

    private String name;
    private GlslSpecifiedType returnType;
    private final List<GlslParameterDeclaration> parameters;

    public GlslFunctionHeader(String name, GlslType returnType, Collection<GlslParameterDeclaration> parameters) {
        this(name, returnType, new ArrayList<>(parameters));
    }

    private GlslFunctionHeader(String name, GlslType returnType, List<GlslParameterDeclaration> parameters) {
        this.name = name;
        this.returnType = returnType.asSpecifiedType();
        this.parameters = parameters;
    }

    public String getName() {
        return this.name;
    }

    public GlslSpecifiedType getReturnType() {
        return this.returnType;
    }

    public List<GlslParameterDeclaration> getParameters() {
        return this.parameters;
    }

    public GlslFunctionHeader setName(String name) {
        this.name = name;
        return this;
    }

    public GlslFunctionHeader setReturnType(GlslType returnType) {
        this.returnType = returnType.asSpecifiedType();
        return this;
    }

    /**
     * Sets the parameters in this function.
     *
     * @param parameters The new parameters to use
     */
    public GlslFunctionHeader setParameters(GlslParameterDeclaration... parameters) {
        return this.setParameters(Arrays.asList(parameters));
    }

    /**
     * Sets the parameters in this function.
     *
     * @param parameters The new parameters to use
     */
    public GlslFunctionHeader setParameters(Collection<GlslParameterDeclaration> parameters) {
        this.parameters.clear();
        this.parameters.addAll(parameters);
        return this;
    }

    /**
     * @return A deep copy of this function header
     */
    public GlslFunctionHeader copy() {
        List<GlslParameterDeclaration> declarations = new ArrayList<>();
        for (GlslParameterDeclaration parameter : this.parameters) {
            declarations.add(parameter.copy());
        }
        return new GlslFunctionHeader(this.name, this.returnType, declarations);
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || this.getClass() != o.getClass()) {
            return false;
        }

        GlslFunctionHeader that = (GlslFunctionHeader) o;
        return this.name.equals(that.name) && this.returnType.equals(that.returnType) && this.parameters.equals(that.parameters);
    }

    @Override
    public int hashCode() {
        int result = this.name.hashCode();
        result = 31 * result + this.returnType.hashCode();
        result = 31 * result + this.parameters.hashCode();
        return result;
    }

    @Override
    public String toString() {
        return "GlslFunctionHeader{name='" + this.name + "', returnType=" + this.returnType + ", parameters=" + this.parameters + '}';
    }
}
