package io.github.ocelot.test;

import io.github.ocelot.glslprocessor.api.GlslParser;
import io.github.ocelot.glslprocessor.api.match.GlslMatchResult;
import io.github.ocelot.glslprocessor.api.match.GlslMatcher;
import io.github.ocelot.glslprocessor.api.node.GlslTree;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class GlslSearchTest {

    @Test
    public void testSearch() {
        GlslTree tree = assertDoesNotThrow(() -> GlslParser.parse("""
                #version 330 core
                
                uniform sampler2D Sampler;
                
                in vec2 texCoord;
                
                out vec4 outColor;
                
                void main() {
                    vec4 color = texture(Sampler, texCoord);
                    if (color.a < 0.1) {
                        discard;
                    }
                    outColor = color;
                }
                """));

        GlslMatchResult result = tree.get(GlslMatcher.mainFunction(), GlslMatcher.IfMatcher.CONDITION);
        assertEquals(1, result.getMatchCount());
        result.replace(GlslTestHelper.parseGlslExpressionList("color.a < 0.001"));

        System.out.println(tree.toSourceString());
    }
}
