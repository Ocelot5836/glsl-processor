package io.github.ocelot.glslprocessor.api.node.expression;

import io.github.ocelot.glslprocessor.api.node.GlslNode;
import io.github.ocelot.glslprocessor.api.node.GlslNodeType;
import io.github.ocelot.glslprocessor.api.visitor.GlslBitwiseVisitor;
import io.github.ocelot.glslprocessor.api.visitor.GlslNodeVisitor;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.stream.Stream;

/**
 * @author Ocelot
 * @since 1.0.0
 */
public class GlslBitwiseNode implements GlslNode {

    private final List<GlslNode> expressions;
    private Operand operand;

    public GlslBitwiseNode(Collection<GlslNode> expressions, Operand operand) {
        this.expressions = new ArrayList<>(expressions);
        this.operand = operand;
    }

    public static GlslBitwiseNode bitAnd(Collection<GlslNode> expressions) {
        return new GlslBitwiseNode(expressions, Operand.BITWISE_AND);
    }

    public static GlslBitwiseNode bitOr(Collection<GlslNode> expressions) {
        return new GlslBitwiseNode(expressions, Operand.BITWISE_INCLUSIVE_OR);
    }

    public static GlslBitwiseNode bitXor(Collection<GlslNode> expressions) {
        return new GlslBitwiseNode(expressions, Operand.BITWISE_EXCLUSIVE_OR);
    }

    public static GlslBitwiseNode logicalAnd(Collection<GlslNode> expressions) {
        return new GlslBitwiseNode(expressions, Operand.LOGICAL_AND);
    }

    public static GlslBitwiseNode logicalOr(Collection<GlslNode> expressions) {
        return new GlslBitwiseNode(expressions, Operand.LOGICAL_OR);
    }

    public static GlslBitwiseNode logicalXor(Collection<GlslNode> expressions) {
        return new GlslBitwiseNode(expressions, Operand.LOGICAL_EXCLUSIVE_OR);
    }

    public List<GlslNode> getExpressions() {
        return this.expressions;
    }

    public Operand getOperand() {
        return this.operand;
    }

    public GlslBitwiseNode setExpressions(GlslNode... expressions) {
        return this.setExpressions(Arrays.asList(expressions));
    }

    public GlslBitwiseNode setExpressions(Collection<GlslNode> expressions) {
        this.expressions.clear();
        this.expressions.addAll(expressions);
        return this;
    }

    public GlslBitwiseNode setOperand(Operand operand) {
        this.operand = operand;
        return this;
    }

    @Override
    public void visit(GlslNodeVisitor visitor) {
        GlslBitwiseVisitor bodyVisitor = visitor.visitBitwise(this);
        if (bodyVisitor != null) {
            List<GlslNode> expressions = this.getExpressions();
            for (int i = 0; i < expressions.size(); i++) {
                GlslNodeVisitor nodeVisitor = bodyVisitor.visitNode(i);
                if (nodeVisitor != null) {
                    expressions.get(i).visit(nodeVisitor);
                }
            }

            bodyVisitor.visitBitwiseExpressionEnd(this);
        }
    }

    @Override
    public Stream<GlslNode> stream() {
        return Stream.concat(Stream.of(this), this.getExpressions().stream().flatMap(GlslNode::stream));
    }

    @Override
    public final boolean equals(Object o) {
        if (!(o instanceof GlslBitwiseNode that)) return false;
        return this.expressions.equals(that.expressions) && this.operand == that.operand;
    }

    @Override
    public int hashCode() {
        int result = this.expressions.hashCode();
        result = 31 * result + this.operand.hashCode();
        return result;
    }

    @Override
    public String toString() {
        return "GlslBitwiseNode{expressions=" + this.expressions + ", operator=" + this.operand + '}';
    }

    public enum Operand {
        BITWISE_AND("&", GlslNodeType.AND),
        BITWISE_INCLUSIVE_OR("|", GlslNodeType.OR),
        BITWISE_EXCLUSIVE_OR("^", GlslNodeType.XOR),
        LOGICAL_AND("&&", GlslNodeType.LOGICAL_AND),
        LOGICAL_OR("||", GlslNodeType.LOGICAL_OR),
        LOGICAL_EXCLUSIVE_OR("^^", GlslNodeType.LOGICAL_XOR);

        private final String delimiter;
        private final GlslNodeType nodeType;

        Operand(String delimiter, GlslNodeType nodeType) {
            this.delimiter = delimiter;
            this.nodeType = nodeType;
        }

        public String getDelimiter() {
            return this.delimiter;
        }

        public GlslNodeType getNodeType() {
            return this.nodeType;
        }
    }
}
