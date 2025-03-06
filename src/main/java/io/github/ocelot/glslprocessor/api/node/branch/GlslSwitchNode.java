package io.github.ocelot.glslprocessor.api.node.branch;

import io.github.ocelot.glslprocessor.api.node.GlslNode;
import io.github.ocelot.glslprocessor.api.visitor.GlslNodeVisitor;
import io.github.ocelot.glslprocessor.api.visitor.GlslSwitchVisitor;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.stream.Stream;

/**
 * Switch statement.
 *
 * @author Ocelot
 * @since 1.0.0
 */
public class GlslSwitchNode implements GlslNode {

    private GlslNode condition;
    private final List<GlslNode> branches;

    public GlslSwitchNode(GlslNode condition, Collection<GlslNode> branches) {
        this.condition = condition;
        this.branches = new ArrayList<>(branches);
    }

    /**
     * @return The condition inside the switch <code>switch(condition) {}</code>
     */
    public GlslNode getCondition() {
        return this.condition;
    }

    /**
     * @return All code inside the switch, including all labels and code under those labels
     */
    public List<GlslNode> getBranches() {
        return this.branches;
    }

    /**
     * Sets the input condition for this switch statement.
     *
     * @param condition The new condition to use
     */
    public GlslSwitchNode setCondition(GlslNode condition) {
        this.condition = condition;
        return this;
    }

    /**
     * Replaces all branches with the specified values.
     *
     * @param branches The new branches
     */
    public GlslSwitchNode setBranches(Collection<GlslNode> branches) {
        this.branches.clear();
        this.branches.addAll(branches);
        return this;
    }

    /**
     * Replaces all branches with the specified values.
     *
     * @param branches The new branches
     */
    public GlslSwitchNode setBranches(GlslNode... branches) {
        this.branches.clear();
        this.branches.addAll(Arrays.asList(branches));
        return this;
    }

    @Override
    public void visit(GlslNodeVisitor visitor) {
        GlslSwitchVisitor switchVisitor = visitor.visitSwitch(this);
        if (switchVisitor != null) {
            GlslNodeVisitor nodeVisitor = null;
            for (GlslNode branch : this.branches) {
                if (branch instanceof GlslCaseLabelNode node) {
                    nodeVisitor = switchVisitor.visitLabel(node);
                } else if (nodeVisitor != null) {
                    branch.visit(nodeVisitor);
                }
            }
            switchVisitor.visitSwitchEnd(this);
        }
    }

    @Override
    public Stream<GlslNode> stream() {
        return Stream.concat(Stream.of(this), Stream.concat(this.condition.stream(), this.branches.stream().flatMap(GlslNode::stream)));
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || this.getClass() != o.getClass()) {
            return false;
        }

        GlslSwitchNode that = (GlslSwitchNode) o;
        return this.condition.equals(that.condition) && this.branches.equals(that.branches);
    }

    @Override
    public int hashCode() {
        int result = this.condition.hashCode();
        result = 31 * result + this.branches.hashCode();
        return result;
    }

    @Override
    public String toString() {
        return "GlslSwitchNode{" +
                "condition=" + this.condition + ", " +
                "branches=" + this.branches + '}';
    }
}
