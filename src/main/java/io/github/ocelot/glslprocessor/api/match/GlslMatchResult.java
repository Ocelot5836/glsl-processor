package io.github.ocelot.glslprocessor.api.match;

import io.github.ocelot.glslprocessor.api.node.GlslNode;
import io.github.ocelot.glslprocessor.impl.match.GlslSearcher;
import org.jetbrains.annotations.ApiStatus;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.stream.Stream;

@ApiStatus.Experimental
public final class GlslMatchResult {

    private final List<GlslSearcher.Result> matches;

    @ApiStatus.Internal
    public GlslMatchResult(List<GlslSearcher.Result> matches) {
        this.matches = matches;
    }

    public void replace(GlslNode... nodes) {
        this.replace(Arrays.asList(nodes));
    }

    public void replace(Collection<GlslNode> nodes) {
        GlslNode node = GlslNode.compound(nodes);
        for (GlslSearcher.Result result : this.matches) {
            result.replace().accept(node);
        }
    }

    public boolean matches() {
        return !this.matches.isEmpty();
    }

    public int getMatchCount() {
        return this.matches.size();
    }

    public GlslNode getMatch(int index) {
        return this.matches.get(index).found();
    }

    public Stream<GlslNode> getMatches() {
        return this.matches.stream().map(GlslSearcher.Result::found);
    }
}
