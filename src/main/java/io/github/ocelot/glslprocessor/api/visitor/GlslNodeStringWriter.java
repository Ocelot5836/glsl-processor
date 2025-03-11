package io.github.ocelot.glslprocessor.api.visitor;

import io.github.ocelot.glslprocessor.api.grammar.*;
import io.github.ocelot.glslprocessor.api.node.GlslNode;
import io.github.ocelot.glslprocessor.api.node.branch.*;
import io.github.ocelot.glslprocessor.api.node.constant.*;
import io.github.ocelot.glslprocessor.api.node.expression.*;
import io.github.ocelot.glslprocessor.api.node.function.GlslFunctionNode;
import io.github.ocelot.glslprocessor.api.node.function.GlslInvokeFunctionNode;
import io.github.ocelot.glslprocessor.api.node.function.GlslPrimitiveConstructorNode;
import io.github.ocelot.glslprocessor.api.node.variable.*;
import org.jetbrains.annotations.ApiStatus;

import java.util.List;
import java.util.Locale;

/**
 * @author Ocelot
 * @since 1.0.0
 */
public final class GlslNodeStringWriter extends GlslNodeVisitor {

    private final String prefix;
    private final String base;
    private final StringBuilder builder;
    private final boolean forceInline;

    public GlslNodeStringWriter(boolean inline) {
        this("", "", new StringBuilder(), inline);
    }

    private GlslNodeStringWriter(String base, String prefix, StringBuilder builder, boolean forceInline) {
        this.base = base;
        this.prefix = prefix;
        this.builder = builder;
        this.forceInline = forceInline;
    }

    @ApiStatus.Internal
    public GlslNodeStringWriter indent() {
        return new GlslNodeStringWriter(this.prefix + this.base, "\t", this.builder, this.forceInline);
    }

    @ApiStatus.Internal
    public GlslNodeStringWriter inline() {
        return new GlslNodeStringWriter("", "", this.builder, true);
    }

    private void accept(CharSequence text, boolean inline, boolean semicolon) {
        this.builder.append(text);
        if (semicolon) {
            this.builder.append(';');
        }
        if (!inline && !this.forceInline) {
            this.builder.append('\n');
        }
    }

    private void addIndent() {
        if (!this.forceInline) {
            this.builder.append(this.base).append(this.prefix);
        }
    }

    private void acceptClosing() {
        this.builder.append(this.base).append(this.forceInline ? "}" : "}\n");
    }

    private void visitTypeQualifier(GlslTypeQualifier qualifier) {
        if (qualifier instanceof GlslTypeQualifier.StorageSubroutine storageSubroutine) {
            String[] typeNames = storageSubroutine.typeNames();
            if (typeNames.length > 0) {
                this.builder.append("subroutine(").append(String.join(",", typeNames)).append(")");
                return;
            }

            this.builder.append("subroutine");
        } else if (qualifier instanceof GlslTypeQualifier.Layout layout) {
            GlslNodeVisitor visitor = this.inline();
            this.builder.append("layout(");
            for (GlslTypeQualifier.LayoutId layoutId : layout.layoutIds()) {
                this.builder.append(layoutId.identifier());
                GlslNode expression = layoutId.expression();
                if (expression != null) {
                    this.builder.append(" = ");
                    expression.visit(visitor);
                }
                this.builder.append(", ");
            }
            if (!layout.layoutIds().isEmpty()) {
                this.builder.delete(this.builder.length() - 2, this.builder.length());
            }
            this.builder.append(')');
        } else if (qualifier instanceof GlslTypeQualifier.Precision precision) {
            switch (precision) {
                case HIGH_PRECISION -> this.builder.append("highp");
                case MEDIUM_PRECISION -> this.builder.append("mediump");
                case LOW_PRECISION -> this.builder.append("lowp");
            }
        } else {
            this.builder.append(((Enum<?>) qualifier).name().toLowerCase(Locale.ROOT));
        }
    }

    private void visitArraySuffix(GlslTypeSpecifier specifier) {
        if (specifier instanceof GlslTypeSpecifier.Array array) {
            GlslNode size = array.getSize();
            if (size != null) {
                this.builder.append('[');
                size.visit(this.inline());
                this.builder.append("]");
            } else {
                this.builder.append("[]");
            }
        }
    }

    public void trimSemicolon() {
        if (this.builder.codePointAt(this.builder.length() - 1) == ';') {
            this.builder.deleteCharAt(this.builder.length() - 1);
        }
    }

    public void visitTypeSpecifier(GlslTypeSpecifier typeSpecifier) {
        if (!typeSpecifier.isStruct()) {
            this.builder.append(typeSpecifier.getName());
            return;
        }

        GlslStructSpecifier structSpecifier = typeSpecifier.asStructSpecifier();
        this.addIndent();
        this.accept(structSpecifier.getName() + " {", false, false);
        for (GlslStructField field : structSpecifier.getFields()) {
            this.addIndent();
            this.builder.append('\t');
            this.visitSpecifiedType(field.getType());
            this.builder.append(' ').append(field.getName());
            this.visitArraySuffix(field.getType().getSpecifier());
            this.accept("", false, true);
        }
        this.addIndent();
        this.accept("}", true, false);
    }

    public void visitSpecifiedType(GlslSpecifiedType type) {
        GlslTypeSpecifier specifier = type.getSpecifier();
        boolean block = false;
        for (GlslTypeQualifier qualifier : type.getQualifiers()) {
            this.visitTypeQualifier(qualifier);
            this.builder.append(' ');
            if (qualifier == GlslTypeQualifier.StorageType.UNIFORM ||
                    qualifier == GlslTypeQualifier.StorageType.BUFFER) {
                block = true;
            }
        }
        if (specifier.isStruct() && !block) {
            this.builder.append("struct ");
        }
        this.visitTypeSpecifier(specifier);
    }

    public void visitFunctionHeader(GlslFunctionHeader header) {
        GlslSpecifiedType returnType = header.getReturnType();
        this.visitSpecifiedType(returnType);
        this.visitArraySuffix(returnType.getSpecifier());

        this.builder.append(' ');
        this.builder.append(header.getName());
        this.builder.append('(');

        List<GlslParameterDeclaration> parameters = header.getParameters();
        for (GlslParameterDeclaration parameter : parameters) {
            String name = parameter.getName();
            GlslSpecifiedType type = parameter.getType();
            this.visitSpecifiedType(type);
            if (name != null) {
                this.builder.append(' ');
                this.builder.append(name);
            }
            this.visitArraySuffix(type.getSpecifier());
            this.builder.append(", ");
        }
        if (!parameters.isEmpty()) {
            this.builder.delete(this.builder.length() - 2, this.builder.length());
        }
        this.builder.append(')');
    }

    /**
     * Clears the currently building string.
     */
    public void clear() {
        this.builder.setLength(0);
    }

    @Override
    public GlslNodeVisitor visitForLoop(GlslForLoopNode node) {
        GlslNodeStringWriter inline = this.inline();

        this.builder.append(this.base).append(this.prefix);
        this.builder.append("for(");
        node.getInit().visit(inline);
        this.trimSemicolon();
        this.trimSemicolon();
        this.builder.append("; ");
        node.getCondition().visit(inline);
        this.trimSemicolon();
        this.builder.append(';');
        if (node.getIncrement() != null) {
            this.builder.append(' ');
            node.getIncrement().visit(inline);
            this.trimSemicolon();
        }
        this.builder.append(this.forceInline ? ") {" : ") {\n");

        return this.indent();
    }

    @Override
    public void visitForLoopEnd(GlslForLoopNode node) {
        this.acceptClosing();
    }

    @Override
    public GlslNodeVisitor visitWhileLoop(GlslWhileLoopNode node) {
        GlslNodeStringWriter inline = this.inline();

        this.builder.append(this.base).append(this.prefix);
        this.builder.append("while(");
        node.getCondition().visit(inline);
        this.trimSemicolon();
        this.builder.append(this.forceInline ? ") {" : ") {\n");

        return this.indent();
    }

    @Override
    public void visitWhileLoopEnd(GlslWhileLoopNode node) {
        this.acceptClosing();
    }

    @Override
    public void visitJump(GlslJumpNode node) {
        this.addIndent();
        switch (node) {
            case CONTINUE -> this.accept("continue", false, true);
            case BREAK -> this.accept("break", false, true);
            case DISCARD -> this.accept("discard", false, true);
        }
    }

    @Override
    public void visitReturn(GlslReturnNode node) {
        this.addIndent();

        GlslNode value = node.getValue();
        if (value != null) {
            this.accept("return ", true, false);
            value.visit(new GlslNodeStringWriter("", "", this.builder, true));
        } else {
            this.accept("return", false, false);
        }

        this.trimSemicolon();
        this.accept("", false, true);
    }

    @Override
    public GlslIfVisitor visitIf(GlslIfNode node) {
        this.addIndent();
        this.accept("if(", true, false);
        node.getCondition().visit(this.inline());
        this.trimSemicolon();
        this.accept(") {", false, false);

        return new GlslIfVisitor() {
            @Override
            public GlslNodeVisitor visitIf() {
                return GlslNodeStringWriter.this.indent();
            }

            @Override
            public GlslNodeVisitor visitElse() {
                GlslNodeStringWriter.this.addIndent();
                GlslNodeStringWriter.this.accept("} else {", false, false);
                return GlslNodeStringWriter.this.indent();
            }

            @Override
            public void visitIfEnd(GlslIfNode node) {
                GlslNodeStringWriter.this.addIndent();
                GlslNodeStringWriter.this.accept("}", false, false);
            }
        };
    }

    @Override
    public GlslSwitchVisitor visitSwitch(GlslSwitchNode node) {
        this.addIndent();
        this.accept("switch(", true, false);
        node.getCondition().visit(this.inline());
        this.trimSemicolon();
        this.accept(") {", false, false);
        GlslNodeStringWriter indent = GlslNodeStringWriter.this.indent();
        return new GlslSwitchVisitor() {
            @Override
            public GlslNodeVisitor visitLabel(GlslCaseLabelNode node) {
                indent.addIndent();
                if (node.isDefault()) {
                    indent.accept("default:", false, false);
                } else {
                    indent.accept("case ", true, false);
                    node.getCondition().visit(indent.inline());
                    GlslNodeStringWriter.this.trimSemicolon();
                    indent.accept(":", false, false);
                }
                return indent.indent();
            }

            @Override
            public void visitSwitchEnd(GlslSwitchNode node) {
                indent.acceptClosing();
            }
        };
    }

    @Override
    public GlslBitwiseVisitor visitBitwise(GlslBitwiseNode node) {
        this.builder.append('(');
        return new GlslBitwiseVisitor() {
            @Override
            public GlslNodeVisitor visitNode(int index) {
                if (index != 0) {
                    GlslNodeStringWriter.this.trimSemicolon();
                    GlslNodeStringWriter.this.builder.append(' ').append(node.getOperand().getDelimiter()).append(' ');
                }
                return GlslNodeStringWriter.this.inline();
            }

            @Override
            public void visitBitwiseExpressionEnd(GlslBitwiseNode node) {
                GlslNodeStringWriter.this.trimSemicolon();
                GlslNodeStringWriter.this.builder.append(')');
            }
        };
    }

    @Override
    public void visitAssign(GlslAssignmentNode node) {
        GlslNodeStringWriter inline = this.inline();
        this.addIndent();
        node.getFirst().visit(inline);
        this.trimSemicolon();
        this.builder.append(' ');
        this.builder.append(node.getOperand().getDelimiter());
        this.builder.append(' ');
        node.getSecond().visit(inline);
        this.trimSemicolon();
        this.accept("", false, true);
    }

    @Override
    public void visitOperation(GlslOperationNode node) {
        GlslNodeStringWriter inline = this.inline();
        this.addIndent();
        this.builder.append('(');
        node.getFirst().visit(inline);
        this.trimSemicolon();
        this.builder.append(' ');
        this.builder.append(node.getOperand().getDelimiter());
        this.builder.append(' ');
        node.getSecond().visit(inline);
        this.trimSemicolon();
        this.accept(")", false, false);
    }

    @Override
    public void visitCompare(GlslCompareNode node) {
        GlslNodeStringWriter inline = this.inline();
        this.addIndent();
        node.getFirst().visit(inline);
        this.trimSemicolon();
        this.builder.append(' ');
        this.builder.append(node.getOperand().getDelimiter());
        this.builder.append(' ');
        node.getSecond().visit(inline);
        this.trimSemicolon();
        this.accept("", false, false);
    }

    @Override
    public void visitCondition(GlslConditionalNode node) {
        GlslNodeStringWriter inline = this.inline();
        this.addIndent();
        node.getCondition().visit(inline);
        this.trimSemicolon();
        this.builder.append(" ? ");
        node.getFirst().visit(inline);
        this.trimSemicolon();
        this.builder.append(" : ");
        node.getSecond().visit(inline);
        this.trimSemicolon();
        this.accept("", false, false);
    }

    @Override
    public void visitPrecision(GlslPrecisionNode node) {
        this.addIndent();
        this.builder.append("precision ");
        this.visitTypeQualifier(node.getPrecision());
        this.builder.append(" ");
        this.visitTypeSpecifier(node.getTypeSpecifier());
        this.accept("", false, true);
    }

    @Override
    public void visitUnary(GlslUnaryNode node) {
        GlslUnaryNode.Operand operand = node.getOperand();
        GlslNode expression = node.getExpression();
        switch (operand) {
            case PRE_INCREMENT,
                 PRE_DECREMENT,
                 PLUS,
                 DASH,
                 BANG,
                 TILDE -> {
                this.builder.append(operand.getDelimiter());
                boolean simple = expression instanceof GlslConstantNode ||
                        expression instanceof GlslGetArrayNode ||
                        expression instanceof GlslGetFieldNode ||
                        expression instanceof GlslVariableNode;
                if (!simple) {
                    this.builder.append('(');
                }
                expression.visit(this.inline());
                this.trimSemicolon();
                if (!simple) {
                    this.builder.append(')');
                }
            }
            case POST_INCREMENT, POST_DECREMENT -> {
                expression.visit(this.inline());
                this.trimSemicolon();
                this.builder.append(operand.getDelimiter());
            }
        }
    }

    public GlslNodeVisitor visitFunctionDeclaration(GlslFunctionNode node) {
        this.visitFunctionHeader(node.getHeader());
        this.accept(" {", false, false);
        if (node.getBody() == null) {
            this.accept("", false, true);
            return null;
        }
        return this.indent();
    }

    public void visitFunctionDeclarationEnd(GlslFunctionNode node) {
        this.acceptClosing();
    }

    @Override
    public GlslInvokeVisitor visitFunctionInvocation(GlslInvokeFunctionNode node) {
        return new GlslInvokeVisitor() {
            @Override
            public void visitHeader() {
                GlslNodeStringWriter.this.addIndent();
                node.getHeader().visit(GlslNodeStringWriter.this.inline());
                GlslNodeStringWriter.this.trimSemicolon();
                GlslNodeStringWriter.this.builder.append('(');
            }

            @Override
            public GlslNodeVisitor visitParameter(int index) {
                GlslNodeStringWriter.this.trimSemicolon();
                if (index > 0) {
                    GlslNodeStringWriter.this.builder.append(", ");
                }
                return GlslNodeStringWriter.this.inline();
            }

            @Override
            public void visitInvokeEnd(GlslInvokeFunctionNode node) {
                GlslNodeStringWriter.this.trimSemicolon();
                GlslNodeStringWriter.this.accept(")", false, true);
            }
        };
    }

    @Override
    public void visitPrimitiveConstructor(GlslPrimitiveConstructorNode node) {
        this.addIndent();
        this.visitTypeSpecifier(node.getPrimitiveType());
        this.visitArraySuffix(node.getPrimitiveType());
    }

    @Override
    public void visitConstant(GlslConstantNode node) {
        this.addIndent();
        if (node instanceof GlslDoubleConstantNode) {
            this.accept(node.doubleValue() + "lf", false, false);
        } else if (node instanceof GlslFloatConstantNode) {
            this.accept(Float.toString(node.floatValue()), false, false);
        } else if (node instanceof GlslBoolConstantNode) {
            this.accept(Boolean.toString(node.booleanValue()), false, false);
        } else if (node instanceof GlslIntConstantNode constantNode) {
            int value = node.intValue();
            switch (constantNode.format()) {
                case HEXADECIMAL -> this.builder.append("0x%X".formatted(value));
                case OCTAL -> this.builder.append(value > 0 ? "0" : "").append(Integer.toOctalString(value));
                case DECIMAL -> this.builder.append(value);
            }
            if (!constantNode.signed()) {
                this.builder.append('u');
            }
        }
    }

    @Override
    public void visitVariableDeclaration(GlslVariableDeclarationNode node) {
        this.addIndent();
        for (GlslTypeQualifier qualifier : node.getTypeQualifiers()) {
            this.visitTypeQualifier(qualifier);
            this.builder.append(' ');
        }
        for (String name : node.getNames()) {
            this.builder.append(name).append(", ");
        }
        if (!node.getNames().isEmpty()) {
            this.builder.delete(this.builder.length() - 2, this.builder.length());
        }
        this.trimSemicolon();
        this.accept("", false, true);
    }

    @Override
    public void visitGetArray(GlslGetArrayNode node) {
        GlslNodeStringWriter inline = this.inline();
        this.addIndent();
        node.getExpression().visit(inline);
        this.trimSemicolon();
        this.builder.append('[');
        node.getIndex().visit(inline);
        this.trimSemicolon();
        this.builder.append(']');
    }

    @Override
    public void visitGetField(GlslGetFieldNode node) {
        this.addIndent();
        node.getExpression().visit(this.inline());
        this.trimSemicolon();
        this.builder.append('.');
        this.builder.append(node.getFieldSelection());
    }

    @Override
    public void visitNewField(GlslNewFieldNode node) {
        this.addIndent();
        this.visitSpecifiedType(node.getType());
        String name = node.getName();
        if (name != null) {
            this.builder.append(' ').append(name);
            this.visitArraySuffix(node.getType().getSpecifier());
            GlslNode initializer = node.getInitializer();
            if (initializer != null) {
                this.builder.append(" = ");
                initializer.visit(this.inline());
                this.trimSemicolon();
            }
        }
        this.accept("", false, true);
    }

    @Override
    public void visitStructDeclaration(GlslStructDeclarationNode node) {
        this.addIndent();
        this.visitSpecifiedType(node.getSpecifiedType());
        this.accept("", false, true);
    }

    @Override
    public void visitVariable(GlslVariableNode node) {
        this.addIndent();
        this.builder.append(node.getName());
    }

    @Override
    public String toString() {
        this.builder.trimToSize();
        return this.builder.toString();
    }
}
