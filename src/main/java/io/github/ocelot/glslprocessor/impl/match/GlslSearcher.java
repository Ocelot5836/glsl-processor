package io.github.ocelot.glslprocessor.impl.match;

import io.github.ocelot.glslprocessor.api.match.GlslMatcher;
import io.github.ocelot.glslprocessor.api.node.GlslNode;
import io.github.ocelot.glslprocessor.api.node.branch.GlslForLoopNode;
import io.github.ocelot.glslprocessor.api.node.branch.GlslIfNode;
import io.github.ocelot.glslprocessor.api.visitor.GlslIfVisitor;
import io.github.ocelot.glslprocessor.api.visitor.GlslNodeVisitor;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.function.Consumer;

@ApiStatus.Internal
public class GlslSearcher extends GlslNodeVisitor {

    private final List<Result> found;
    private final int index;
    private final GlslMatcher[] filters;

    public GlslSearcher(List<Result> found, int index, GlslMatcher[] filters) {
        this.found = found;
        this.index = index;
        this.filters = filters;
    }

    private GlslSearcher(GlslSearcher searcher) {
        this.found = searcher.found;
        this.index = searcher.index + 1;
        this.filters = searcher.filters;
    }

    private boolean matches() {
        return this.index + 1 >= this.filters.length;
    }

    @Override
    public @Nullable GlslNodeVisitor visitForLoop(GlslForLoopNode node) {
        if (this.filters[this.index] instanceof GlslMatcher.ForLoopMatcher matcher) {
            switch (matcher) {
                case INIT -> {
                    if (this.matches()) {
                        this.found.add(new Result(node::setInit, node));
                        return null;
                    }
                    node.getInit().visit(new GlslSearcher(this));
                }
                case CONDITION -> {
                    if (this.matches()) {
                        this.found.add(new Result(node::setCondition, node));
                        return null;
                    }
                    node.getCondition().visit(new GlslSearcher(this));
                }
                case INCREMENT -> {
                    GlslNode increment = node.getIncrement();
                    if (increment != null) {
                        if (this.matches()) {
                            this.found.add(new Result(node::setIncrement, node));
                            return null;
                        }
                        increment.visit(new GlslSearcher(this));
                    }
                }
                case BODY -> {
                    if (this.matches()) {
                        this.found.add(new Result(replace -> node.setBody(GlslNode.unwrap(replace)), node));
                        return null;
                    }
                    return new GlslSearcher(this);
                }
            }
        }
        return null;
    }

    @Override
    public @Nullable GlslIfVisitor visitIf(GlslIfNode node) {
        if (this.filters[this.index] instanceof GlslMatcher.IfMatcher matcher) {
            if (matcher == GlslMatcher.IfMatcher.CONDITION) {
                if (this.matches()) {
                    this.found.add(new Result(node::setCondition, node));
                    return null;
                }
                node.getCondition().visit(new GlslSearcher(this));
            } else {
                return new GlslIfVisitor() {
                    @Override
                    public GlslNodeVisitor visitIf() {
                        if (matcher != GlslMatcher.IfMatcher.IF) {
                            return null;
                        }
                        if (GlslSearcher.this.matches()) {
                            GlslSearcher.this.found.add(new Result(replace -> node.setFirst(GlslNode.unwrap(replace)), node));
                            return null;
                        }
                        return new GlslSearcher(GlslSearcher.this);
                    }

                    @Override
                    public GlslNodeVisitor visitElse() {
                        if (matcher != GlslMatcher.IfMatcher.ELSE) {
                            return null;
                        }
                        if (GlslSearcher.this.matches()) {
                            GlslSearcher.this.found.add(new Result(replace -> node.setSecond(GlslNode.unwrap(replace)), node));
                            return null;
                        }
                        return new GlslSearcher(GlslSearcher.this);
                    }
                };
            }
        }
        return null;
    }

    public record Result(Consumer<GlslNode> replace, GlslNode found) {
    }
}
