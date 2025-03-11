package io.github.ocelot.glslprocessor.api.match;

import io.github.ocelot.glslprocessor.api.GlslSyntaxException;
import io.github.ocelot.glslprocessor.api.grammar.GlslFunctionHeader;
import io.github.ocelot.glslprocessor.api.grammar.GlslTypeSpecifier;
import io.github.ocelot.glslprocessor.api.node.function.GlslFunctionNode;
import io.github.ocelot.glslprocessor.impl.GlslParserImpl;
import io.github.ocelot.glslprocessor.impl.GlslTokenReader;
import org.jetbrains.annotations.ApiStatus;

import java.util.Collections;
import java.util.function.Function;

@ApiStatus.Experimental
public sealed interface GlslMatcher {

    private static <T> T parse(String input, Function<GlslTokenReader, T> parser) throws GlslSyntaxException {
        GlslTokenReader reader = new GlslTokenReader(input);
        T value = parser.apply(reader);
        reader.assertEOF();
        return value;
    }

    /**
     * @return Matches the main function in the tree
     */
    static GlslMatcher mainFunction() {
        return function(new GlslFunctionHeader("main", GlslTypeSpecifier.BuiltinType.VOID, Collections.emptyList()), true);
    }

    static GlslMatcher function(String header, boolean body) throws GlslSyntaxException {
        return function(parse(header, GlslParserImpl::parseFunctionHeaderWithParameters), body);
    }

    static GlslMatcher function(GlslFunctionHeader functionHeader, boolean body) {
        return new FunctionMatcher(functionHeader, body);
    }

    /**
     * Matches a specific function defined in the code.
     *
     * @param header  The header of the function
     * @param hasBody Whether the function has a body or is just a template
     */
    record FunctionMatcher(GlslFunctionHeader header, boolean hasBody) implements GlslMatcher {

        /**
         * Checks if the specified node matches this matcher.
         *
         * @param node The node to check
         * @return Whether this matcher matches the specified node
         */
        @ApiStatus.Internal
        public boolean matches(GlslFunctionNode node) {
            return node.getHeader().equals(this.header) && node.getBody() != null == this.hasBody;
        }
    }

    /**
     * Matches part of a for loop.
     */
    enum ForLoopMatcher implements GlslMatcher {
        INIT,
        CONDITION,
        INCREMENT,
        BODY
    }

    /**
     * Matches parts of an if statement.
     */
    enum IfMatcher implements GlslMatcher {
        CONDITION,
        IF,
        ELSE,
    }

    /**
     * Matches parts of an if statement.
     */
    enum SwitchMatcher implements GlslMatcher {
        CONDITION,
        IF_BODY,
        ELSE_BODY,
    }

    /**
     * Matches parts of a while loop.
     */
    enum WhileLoopMatcher implements GlslMatcher {
        CONDITION,
        BODY,
    }
}
