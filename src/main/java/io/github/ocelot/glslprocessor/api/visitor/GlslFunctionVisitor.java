package io.github.ocelot.glslprocessor.api.visitor;

import io.github.ocelot.glslprocessor.api.node.branch.GlslReturnNode;
import io.github.ocelot.glslprocessor.api.node.expression.GlslAssignmentNode;
import io.github.ocelot.glslprocessor.api.node.expression.GlslPrecisionNode;
import io.github.ocelot.glslprocessor.api.node.function.GlslInvokeFunctionNode;
import io.github.ocelot.glslprocessor.api.node.variable.GlslNewNode;
import org.jetbrains.annotations.ApiStatus;

@ApiStatus.Experimental
public interface GlslFunctionVisitor {

    void visitReturn(GlslReturnNode node);

    void visitAssignment(GlslAssignmentNode node);

    void visitPrecision(GlslPrecisionNode node);

    void visitInvokeFunction(GlslInvokeFunctionNode node);

    void visitNew(GlslNewNode node);

    void visitFunctionEnd();
}
