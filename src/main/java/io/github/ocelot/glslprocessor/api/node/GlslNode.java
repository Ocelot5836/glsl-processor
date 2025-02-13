package io.github.ocelot.glslprocessor.api.node;

import io.github.ocelot.glslprocessor.api.grammar.GlslSpecifiedType;
import io.github.ocelot.glslprocessor.api.node.primary.*;
import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.regex.Pattern;
import java.util.stream.Stream;

public interface GlslNode {

    Pattern NEWLINE = Pattern.compile("\n");

    String getSourceString();

    /**
     * @return The type of this node if it is a field
     */
    default @Nullable GlslSpecifiedType getType() {
        return null;
    }

    /**
     * @return A new list with the child contents of this node
     */
    default List<GlslNode> toList() {
        return new ArrayList<>(Collections.singleton(this));
    }

    /**
     * @return The body of this node or <code>null</code> if there is no sub-body in this node
     */
    default @Nullable GlslNodeList getBody() {
        return null;
    }

    /**
     * Sets the body of this node.
     *
     * @param body The new body
     * @return Whether the action was successful
     */
    default boolean setBody(Collection<GlslNode> body) {
        GlslNodeList nodes = this.getBody();
        if (nodes != null) {
            nodes.clear();
            nodes.addAll(body);
            return true;
        }
        return false;
    }

    /**
     * Sets the body of this node.
     *
     * @param body The new body
     * @return Whether the action was successful
     */
    default boolean setBody(GlslNode... body) {
        return this.setBody(Arrays.asList(body));
    }

    Stream<GlslNode> stream();

    static GlslIntConstantNode intConstant(int value) {
        return new GlslIntConstantNode(GlslIntFormat.DECIMAL, true, value);
    }

    static GlslIntConstantNode unsignedIntConstant(int value) {
        return new GlslIntConstantNode(GlslIntFormat.DECIMAL, false, value);
    }

    static GlslFloatConstantNode floatConstant(float value) {
        return new GlslFloatConstantNode(value);
    }

    static GlslDoubleConstantNode doubleConstant(double value) {
        return new GlslDoubleConstantNode(value);
    }

    static GlslBoolConstantNode booleanConstant(boolean value) {
        return new GlslBoolConstantNode(value);
    }

    static GlslNode compound(Collection<GlslNode> nodes) {
        if (nodes.isEmpty()) {
            return GlslEmptyNode.INSTANCE;
        }
        if (nodes.size() == 1) {
            return nodes.iterator().next();
        }
        List<GlslNode> list = new ArrayList<>();
        for (GlslNode node : nodes) {
            if (!(node instanceof GlslCompoundNode compoundNode)) {
                list.clear();
                list.addAll(nodes);
                break;
            }
            list.addAll(compoundNode.children());
        }
        return new GlslCompoundNode(list);
    }

    static GlslNode compound(GlslNode... nodes) {
        if (nodes.length == 0) {
            return GlslEmptyNode.INSTANCE;
        }
        if (nodes.length == 1) {
            return nodes[0];
        }
        return new GlslCompoundNode(new ArrayList<>(Arrays.asList(nodes)));
    }
}
