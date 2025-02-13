package io.github.ocelot.glslprocessor.api.visitor;

import io.github.ocelot.glslprocessor.api.grammar.GlslVersionStatement;
import io.github.ocelot.glslprocessor.api.node.GlslNode;
import io.github.ocelot.glslprocessor.api.node.function.GlslFunctionNode;
import io.github.ocelot.glslprocessor.api.node.variable.GlslDeclarationNode;
import io.github.ocelot.glslprocessor.api.node.variable.GlslNewNode;
import io.github.ocelot.glslprocessor.api.node.variable.GlslStructNode;
import org.jetbrains.annotations.Nullable;

import java.util.Map;

public interface GlslTreeVisitor {

    void visitMarkers(Map<String, GlslNode> markers);

    void visitVersion(GlslVersionStatement version);

    void visitDirective(String directive);

    void visitMacro(String key, String value);

    void visitField(GlslNewNode newNode);

    void visitStruct(GlslStructNode structSpecifier);

    void visitDeclaration(GlslDeclarationNode declaration);

    @Nullable GlslFunctionVisitor visitFunction(GlslFunctionNode node);

    void visitTreeEnd();
}
