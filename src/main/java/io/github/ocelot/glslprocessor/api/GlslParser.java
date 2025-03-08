package io.github.ocelot.glslprocessor.api;

import io.github.ocelot.glslprocessor.api.node.GlslNode;
import io.github.ocelot.glslprocessor.api.node.GlslTree;
import io.github.ocelot.glslprocessor.impl.GlslLexer;
import io.github.ocelot.glslprocessor.impl.GlslParserImpl;
import io.github.ocelot.glslprocessor.impl.GlslTokenReader;
import io.github.ocelot.glslprocessor.lib.anarres.cpp.LexerException;
import org.jetbrains.annotations.ApiStatus;

import java.util.List;
import java.util.Map;

/**
 * Parses GLSL source code strings into a mutable AST.
 *
 * @author Ocelot
 * @since 1.0.0
 */
public final class GlslParser {

    private GlslParser() {
    }

    /**
     * Runs the C preprocessor on the specified source before passing it off to the parser.
     *
     * @param input  The source code input
     * @param macros All macros to evaluate during pre-processing
     * @return A new tree of all nodes
     * @throws GlslSyntaxException If there is a syntax error in the GLSL source code
     * @throws LexerException      If there is any issue pre-processing the code
     */
    public static GlslTree preprocessParse(String input, Map<String, String> macros) throws GlslSyntaxException, LexerException {
        return GlslParserImpl.parse(GlslParserImpl.preprocess(input, macros));
    }

    /**
     * Parses the specified input code into a GLSL tree.
     *
     * @param input The GLSL source input
     * @return A new tree of all nodes
     * @throws GlslSyntaxException If there is a syntax error in the GLSL source code
     */
    public static GlslTree parse(String input) throws GlslSyntaxException {
        return GlslParserImpl.parse(input);
    }

    /**
     * Parses the specified input code as a single GLSL expression.
     *
     * @param input The GLSL source input
     * @return A single node
     * @throws GlslSyntaxException If there is a syntax error in the GLSL source code
     */
    public static GlslNode parseExpression(String input) throws GlslSyntaxException {
        GlslLexer.Token[] tokens = GlslLexer.createTokens(input + ";");
        GlslTokenReader reader = new GlslTokenReader(tokens);
        List<GlslNode> expression = GlslParserImpl.parseStatement(reader);
        if (expression == null) {
            reader.throwError();
        }
        while (reader.canRead()) {
            if (reader.peek().type() != GlslLexer.TokenType.SEMICOLON) {
                break;
            }
            reader.skip();
        }
        if (reader.canRead()) {
            throw reader.error("Too many tokens provided");
        }
        return GlslNode.compound(expression);
    }

    /**
     * Parses the specified token array as a single GLSL expression.
     *
     * @param tokens The tokens to parse
     * @return A single node
     * @throws GlslSyntaxException If there is a syntax error in the GLSL source code
     * @deprecated Use {@link #parseExpression(String)} instead
     */
    @ApiStatus.ScheduledForRemoval(inVersion = "2.0.0")
    @Deprecated(since = "0.2.0", forRemoval = true)
    public static GlslNode parseExpression(GlslLexer.Token... tokens) throws GlslSyntaxException {
        GlslTokenReader reader = new GlslTokenReader(tokens);
        List<GlslNode> expression = GlslParserImpl.parseStatement(reader);
        if (expression == null) {
            reader.throwError();
        }
        while (reader.canRead()) {
            if (reader.peek().type() != GlslLexer.TokenType.SEMICOLON) {
                break;
            }
            reader.skip();
        }
        if (reader.canRead()) {
            throw reader.error("Too many tokens provided");
        }
        return GlslNode.compound(expression);
    }

    /**
     * Parses the specified input code as multiple GLSL expressions. This only supports multiple statements in a list.
     *
     * @param input The GLSL source input
     * @return A single node
     * @throws GlslSyntaxException If there is a syntax error in the GLSL source code
     */
    public static List<GlslNode> parseExpressionList(String input) throws GlslSyntaxException {
        GlslLexer.Token[] tokens = GlslLexer.createTokens(input + ";");
        GlslTokenReader reader = new GlslTokenReader(tokens);
        List<GlslNode> expressions = GlslParserImpl.parseStatementList(reader);
        if (reader.canRead()) {
            throw reader.error("Too many tokens provided");
        }
        return expressions;
    }

    /**
     * Parses the specified token array as multiple GLSL expressions. This only supports multiple statements in a list.
     *
     * @param tokens The tokens to parse
     * @return A single node
     * @throws GlslSyntaxException If there is a syntax error in the GLSL source code
     * @deprecated Use {@link #parseExpressionList(String)} instead
     */
    @ApiStatus.ScheduledForRemoval(inVersion = "2.0.0")
    @Deprecated(since = "0.2.0", forRemoval = true)
    public static List<GlslNode> parseExpressionList(GlslLexer.Token[] tokens) throws GlslSyntaxException {
        GlslTokenReader reader = new GlslTokenReader(tokens);
        List<GlslNode> expressions = GlslParserImpl.parseStatementList(reader);
        if (reader.canRead()) {
            throw reader.error("Too many tokens provided");
        }
        return expressions;
    }
}
