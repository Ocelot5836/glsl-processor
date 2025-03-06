package io.github.ocelot.glslprocessor.api.node.branch;

import io.github.ocelot.glslprocessor.api.node.GlslNode;
import io.github.ocelot.glslprocessor.api.node.GlslNodeList;
import io.github.ocelot.glslprocessor.api.visitor.GlslNodeVisitor;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;
import java.util.stream.Stream;

/**
 * Represents for loops.
 *
 * @author Ocelot
 * @since 1.0.0
 */
public class GlslForLoopNode implements GlslNode {

    private GlslNode init;
    private GlslNode condition;
    private GlslNode increment;
    private final GlslNodeList body;

    public GlslForLoopNode(GlslNode init, GlslNode condition, @Nullable GlslNode increment, Collection<GlslNode> body) {
        this.init = init;
        this.condition = condition;
        this.increment = increment;
        this.body = new GlslNodeList(body);
    }

    public GlslNode getInit() {
        return this.init;
    }

    public GlslNode getCondition() {
        return this.condition;
    }

    public @Nullable GlslNode getIncrement() {
        return this.increment;
    }

    @Override
    public GlslNodeList getBody() {
        return this.body;
    }

    public GlslForLoopNode setInit(GlslNode init) {
        this.init = init;
        return this;
    }

    public GlslForLoopNode setCondition(GlslNode condition) {
        this.condition = condition;
        return this;
    }

    public GlslForLoopNode setIncrement(@Nullable GlslNode increment) {
        this.increment = increment;
        return this;
    }

    @Override
    public void visit(GlslNodeVisitor visitor) {
        GlslNodeVisitor bodyVisitor = visitor.visitForLoop(this);
        if (bodyVisitor != null) {
            for (GlslNode node : this.body) {
                node.visit(bodyVisitor);
            }
            bodyVisitor.visitForLoopEnd(this);
        }
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || this.getClass() != o.getClass()) {
            return false;
        }

        GlslForLoopNode that = (GlslForLoopNode) o;
        return this.init.equals(that.init) && this.condition.equals(that.condition) && this.increment.equals(that.increment) && this.body.equals(that.body);
    }

    @Override
    public int hashCode() {
        int result = this.init.hashCode();
        result = 31 * result + this.condition.hashCode();
        result = 31 * result + this.increment.hashCode();
        result = 31 * result + this.body.hashCode();
        return result;
    }

    @Override
    public String toString() {
        return "ForLoopNode{init=" + this.init + ", condition=" + this.condition + ", increment=" + this.increment + ", body=" + this.body + '}';
    }

    @Override
    public Stream<GlslNode> stream() {
        return Stream.concat(this.init.stream(), Stream.concat(this.condition.stream(), this.increment.stream()));
    }
}
