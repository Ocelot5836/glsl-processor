package io.github.ocelot.test;

import io.github.ocelot.glslprocessor.api.node.GlslTree;
import org.junit.jupiter.api.DynamicNode;
import org.junit.jupiter.api.DynamicTest;
import org.junit.jupiter.api.TestFactory;

import java.util.Arrays;
import java.util.stream.Stream;

import static io.github.ocelot.test.GlslTestHelper.parseGlsl;

public class GlslParserTest {

    private static final String[] TEST_EXPRESSIONS = {
            "4",
            "72",
            "length(vec4(1.0, 0.0, 0.0, 1.0))"
    };

    @TestFactory
    public Stream<DynamicNode> testFor() {
        return Arrays.stream(TEST_EXPRESSIONS).map(expression -> DynamicTest.dynamicTest("testFor " + expression.replaceAll(" ", "_"), () -> {
            GlslTree tree = parseGlsl("""
                    void main() {
                        vec4 color = vec4(0.0);
                        for (int i = int(%s); i < 1; i++) {
                            color.r += 1.0;
                        }
                        color.a = 1.0;
                    }
                    """.formatted(expression));
            // TODO test structure
        }));
    }

    @TestFactory
    public Stream<DynamicNode> testWhile() {
        return Arrays.stream(TEST_EXPRESSIONS).map(expression -> DynamicTest.dynamicTest("testWhile " + expression.replaceAll(" ", "_"), () -> {
            GlslTree tree = parseGlsl("""
                    void main() {
                        vec4 color = vec4(0.0);
                        int i = int(%s);
                        while (true) {
                            color.r += 1.0;
                            i++;
                        }
                        color.a = 1.0;
                    }
                    """.formatted(expression));
            // TODO test structure
        }));
    }
}
