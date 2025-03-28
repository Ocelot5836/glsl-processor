package io.github.ocelot.glslprocessor.api.node.expression;

import io.github.ocelot.glslprocessor.api.grammar.GlslSpecifiedType;
import io.github.ocelot.glslprocessor.api.node.GlslNode;
import io.github.ocelot.glslprocessor.api.node.GlslNodeType;
import io.github.ocelot.glslprocessor.api.visitor.GlslNodeVisitor;
import org.jetbrains.annotations.Nullable;

import java.util.stream.Stream;

/**
 * @author Ocelot
 * @since 1.0.0
 */
public final class GlslAssignmentNode implements GlslNode {

    private GlslNode first;
    private GlslNode second;
    private Operand operand;

    /**
     * @param first   The first operand
     * @param second  The second operand
     * @param operand The operand to perform when setting the first to the second
     */
    public GlslAssignmentNode(GlslNode first, GlslNode second, Operand operand) {
        this.first = first;
        this.second = second;
        this.operand = operand;
    }

    @Override
    public void visit(GlslNodeVisitor visitor) {
        visitor.visitAssign(this);
    }

    @Override
    public GlslNodeType getNodeType() {
        return GlslNodeType.ASSIGN;
    }

    @Override
    public @Nullable GlslSpecifiedType getType() {
        return this.first.getType();
    }

    /**
     * @return The first operand
     */
    public GlslNode getFirst() {
        return this.first;
    }

    /**
     * @return The second operand
     */
    public GlslNode getSecond() {
        return this.second;
    }

    /**
     * @return The operand to perform when setting the first to the second
     */
    public Operand getOperand() {
        return this.operand;
    }

    public GlslAssignmentNode setFirst(GlslNode first) {
        this.first = first;
        return this;
    }

    public GlslAssignmentNode setSecond(GlslNode second) {
        this.second = second;
        return this;
    }

    public GlslAssignmentNode setOperand(Operand operand) {
        this.operand = operand;
        return this;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || this.getClass() != o.getClass()) {
            return false;
        }

        GlslAssignmentNode that = (GlslAssignmentNode) o;
        return this.first.equals(that.first) && this.second.equals(that.second) && this.operand == that.operand;
    }

    @Override
    public int hashCode() {
        int result = this.first.hashCode();
        result = 31 * result + this.second.hashCode();
        result = 31 * result + this.operand.hashCode();
        return result;
    }

    @Override
    public Stream<GlslNode> stream() {
        return Stream.concat(Stream.of(this), Stream.concat(this.first.stream(), this.second.stream()));
    }

    @Override
    public String toString() {
        return "GlslAssignmentNode{" +
                "first=" + this.first + ", " +
                "second=" + this.second + ", " +
                "operand=" + this.operand + ']';
    }

    public enum Operand {
        EQUAL("="),
        MUL_ASSIGN("*="),
        DIV_ASSIGN("/="),
        MOD_ASSIGN("%="),
        ADD_ASSIGN("+="),
        SUB_ASSIGN("-="),
        LEFT_ASSIGN("<<="),
        RIGHT_ASSIGN(">>="),
        AND_ASSIGN("&="),
        XOR_ASSIGN("^="),
        OR_ASSIGN("|=");

        private final String delimiter;

        Operand(String delimiter) {
            this.delimiter = delimiter;
        }

        public String getDelimiter() {
            return this.delimiter;
        }
    }
}
