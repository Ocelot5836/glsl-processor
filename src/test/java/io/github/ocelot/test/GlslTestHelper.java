package io.github.ocelot.test;

import io.github.ocelot.glslprocessor.api.GlslParser;
import io.github.ocelot.glslprocessor.api.GlslSyntaxException;
import io.github.ocelot.glslprocessor.api.node.GlslTree;
import io.github.ocelot.glslprocessor.api.visitor.GlslTreeStringWriter;
import io.github.ocelot.glslprocessor.impl.GlslLexer;

import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

public class GlslTestHelper {

    private static final boolean PRELOAD = true;

    public static String tokensToString(GlslLexer.Token[] tokens, String delim) {
        StringBuilder build = new StringBuilder();
        for (GlslLexer.Token token : tokens) {
            build.append(token.value()).append(delim);
        }
        return build.toString();
    }

    public static GlslLexer.Token[] lexGlsl(String source) {
        return assertDoesNotThrow(() -> GlslLexer.createTokens(source));
    }

    public static Map<Integer, GlslLexer.Token> lexGlslComment(String source) {
        Map<Integer, GlslLexer.Token> comments = new HashMap<>();
        assertDoesNotThrow(() -> GlslLexer.createTokens(source, (token, value) -> comments.put(value, token)));
        return comments;
    }

    public static GlslTree testSpeed(String source) throws GlslSyntaxException {
        if (PRELOAD) {
            // Load classes
            for (int i = 0; i < 10; i++) {
                GlslTree tree = GlslParser.parse(source);
                GlslTreeStringWriter stringWriter = new GlslTreeStringWriter();
                tree.visit(stringWriter);
            }
        }

        long start = System.nanoTime();
        GlslTree tree = GlslParser.parse(source);
        long parseEnd = System.nanoTime();

        GlslTreeStringWriter stringWriter = new GlslTreeStringWriter();
        tree.visit(stringWriter);
        long end = System.nanoTime();

        System.out.println(stringWriter);
        System.out.printf("Took %.3fms to parse, %.3fms to stringify%n", (parseEnd - start) / 1_000_000.0F, (end - parseEnd) / 1_000_000.0F);

        return tree;
    }
}
