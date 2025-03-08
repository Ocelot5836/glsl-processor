package io.github.ocelot.test.lexer;

import io.github.ocelot.glslprocessor.impl.GlslLexer;
import org.junit.jupiter.api.DynamicNode;
import org.junit.jupiter.api.DynamicTest;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestFactory;

import java.util.*;
import java.util.stream.Stream;

import static io.github.ocelot.test.GlslTestHelper.*;
import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class GlslLexerTest {

    private static final GlslLexer.TokenType[] WORD_TOKENS = Arrays.stream(GlslLexer.TokenType.values())
            .filter(type -> type.getWords() != null)
            .toArray(GlslLexer.TokenType[]::new);
    private static final int WORD_TOKENS_COUNT = Arrays.stream(GlslLexer.TokenType.values())
            .filter(type -> type.getWords() != null)
            .mapToInt(type -> type.getWords().length)
            .sum();

    @Test
    public void testEmpty() {
        assertArrayEquals(new GlslLexer.Token[0], lexGlsl(""));
    }

    @TestFactory
    public Collection<DynamicNode> testSingleToken() {
        List<DynamicNode> tests = new ArrayList<>(WORD_TOKENS_COUNT);

        for (GlslLexer.TokenType type : WORD_TOKENS) {
            for (String word : type.getWords()) {
                GlslLexer.Token token = new GlslLexer.Token(type, word);
                tests.add(DynamicTest.dynamicTest("testSingleToken " + token, () -> {
                    GlslLexer.Token[] tokens = lexGlsl(token.value());
                    assertArrayEquals(new GlslLexer.Token[]{token}, tokens);
                }));
            }
        }

        return tests;
    }

    @TestFactory
    public Stream<DynamicNode> testDirectives() {
        String[] directives = {
                "#version 420",
                "#extension test",
                "#include test",
                "#line 42",
                "#line 42 1"
        };

        return Arrays.stream(directives).map(directive -> DynamicTest.dynamicTest("testDirective " + directive, () -> {
            assertArrayEquals(new GlslLexer.Token[]{
                    new GlslLexer.Token(GlslLexer.TokenType.DIRECTIVE, directive),
            }, lexGlsl(directive));
        }));
    }

    @TestFactory
    public Stream<DynamicNode> testComments() {
        String[] comments = {
                "// this is a comment",
                "// this is also another comment",
                "/* multi-line comment on 1 line */",
                """
/*
True multi-line
comment that should
be fully supported
*/"""
        };

        return Arrays.stream(comments).map(comment -> DynamicTest.dynamicTest("testComment " + comment.replaceAll(" +", "_"), () -> {
            assertArrayEquals(new GlslLexer.Token[0], lexGlsl(comment), "Expected no tokens");
            Map<Integer, GlslLexer.Token> map = lexGlslComment(comment);
            assertEquals(1, map.size(), "Expected a single comment");
            Map.Entry<Integer, GlslLexer.Token> entry = map.entrySet().iterator().next();
            assertEquals(0, entry.getKey(), "Expected comment on first line");
            GlslLexer.TokenType type = comment.startsWith("/*") ? GlslLexer.TokenType.MULTI_COMMENT : GlslLexer.TokenType.COMMENT;
            assertEquals(new GlslLexer.Token(type, comment), entry.getValue());
        }));
    }
}
