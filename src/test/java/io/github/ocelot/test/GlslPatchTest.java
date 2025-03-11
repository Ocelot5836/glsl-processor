package io.github.ocelot.test;

import io.github.ocelot.glslprocessor.api.GlslParser;
import io.github.ocelot.glslprocessor.api.node.GlslNode;
import io.github.ocelot.glslprocessor.api.node.GlslTree;
import io.github.ocelot.glslprocessor.impl.GlslPatcher;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

public class GlslPatchTest {

    @Test
    public void testMatches() {
        GlslNode[] replace = assertDoesNotThrow(() -> GlslParser.parseExpressionList("").toArray(GlslNode[]::new));
        GlslPatcher patcher = new GlslPatcher("main", null, -1, "color", replace);
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

        tree.visit(patcher);
    }
}
