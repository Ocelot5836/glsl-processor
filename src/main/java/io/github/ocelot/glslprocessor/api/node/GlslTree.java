package io.github.ocelot.glslprocessor.api.node;

import io.github.ocelot.glslprocessor.api.grammar.GlslSpecifiedType;
import io.github.ocelot.glslprocessor.api.grammar.GlslTypeQualifier;
import io.github.ocelot.glslprocessor.api.grammar.GlslVersionStatement;
import io.github.ocelot.glslprocessor.api.node.branch.GlslSelectionNode;
import io.github.ocelot.glslprocessor.api.node.function.GlslFunctionNode;
import io.github.ocelot.glslprocessor.api.node.variable.GlslDeclarationNode;
import io.github.ocelot.glslprocessor.api.node.variable.GlslNewNode;
import io.github.ocelot.glslprocessor.api.node.variable.GlslStructNode;
import io.github.ocelot.glslprocessor.api.visitor.GlslFunctionVisitor;
import io.github.ocelot.glslprocessor.api.visitor.GlslStringWriter;
import io.github.ocelot.glslprocessor.api.visitor.GlslTreeVisitor;
import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.stream.Stream;

public class GlslTree {

    private final GlslVersionStatement versionStatement;
    private final GlslNodeList body;
    private final List<String> directives;
    private final Map<String, GlslNode> markers;
    private final Map<String, String> macros;

    public GlslTree() {
        this(new GlslVersionStatement(), Collections.emptyList(), Collections.emptyList(), Collections.emptyMap());
    }

    public GlslTree(GlslVersionStatement versionStatement, Collection<GlslNode> body, Collection<String> directives, Map<String, GlslNode> markers) {
        this.versionStatement = versionStatement;
        this.body = new GlslNodeList(body);
        this.directives = new ArrayList<>(directives);
        this.markers = Collections.unmodifiableMap(markers);
        this.macros = new HashMap<>();
    }

    private void visit(GlslTreeVisitor visitor, GlslNode node) {
        if (node instanceof GlslFunctionNode functionNode) {
            GlslFunctionVisitor functionVisitor = visitor.visitFunction(functionNode);
            if (functionVisitor != null) {
                functionNode.visit(functionVisitor);
            }
            return;
        }
        if (node instanceof GlslNewNode newNode) {
            visitor.visitField(newNode);
            return;
        }
        if (node instanceof GlslStructNode struct) {
            visitor.visitStruct(struct);
            return;
        }
        if (node instanceof GlslDeclarationNode declaration) {
            visitor.visitDeclaration(declaration);
            return;
        }
        throw new AssertionError("Not Possible: " + node.getClass());
    }

    public void visit(GlslTreeVisitor visitor) {
        visitor.visitMarkers(this.markers);
        visitor.visitVersion(this.versionStatement);
        for (String directive : this.directives) {
            visitor.visitDirective(directive);
        }
        for (Map.Entry<String, String> entry : this.macros.entrySet()) {
            visitor.visitMacro(entry.getKey(), entry.getValue());
        }

        for (GlslNode node : this.body) {
            if (node instanceof GlslEmptyNode) {
                continue;
            }
            if (node instanceof GlslCompoundNode compoundNode) {
                for (GlslNode child : compoundNode.children()) {
                    this.visit(visitor, child);
                }
                continue;
            }
            this.visit(visitor, node);
        }

        visitor.visitTreeEnd();
    }

    /**
     * Explicitly marks all outputs with a layout location if not specified.
     */
    public void markOutputs() {
        List<GlslNewNode> outputs = new ArrayList<>();
        this.fields().forEach(node -> {
            GlslSpecifiedType type = node.getType();
            boolean valid = false;
            for (GlslTypeQualifier qualifier : type.getQualifiers()) {
                if (qualifier == GlslTypeQualifier.StorageType.OUT) {
                    valid = true;
                    break;
                }
            }

            if (!valid) {
                return;
            }

            for (GlslTypeQualifier qualifier : type.getQualifiers()) {
                if (qualifier instanceof GlslTypeQualifier.Layout layout) {
                    for (GlslTypeQualifier.LayoutId layoutId : layout.layoutIds()) {
                        if (!"location".equals(layoutId.identifier())) {
                            continue;
                        }

                        GlslNode expression = layoutId.expression();
                        if (expression == null) {
                            continue;
                        }

                        try {
                            int location = Integer.parseInt(expression.getSourceString());
                            if (location == 0) {
                                outputs.clear();
                                return;
                            }
                            valid = false;
                            break;
                        } catch (NumberFormatException ignored) {
                        }
                    }
                }
            }

            if (valid) {
                outputs.add(node);
            }
        });

        for (GlslNewNode output : outputs) {
            output.getType().addLayoutId("location", GlslNode.intConstant(0));
        }
    }

    public Optional<GlslFunctionNode> mainFunction() {
        return this.functions().filter(node -> node.getHeader().getName().equals("main")).findFirst();
    }

    public Stream<GlslFunctionNode> functions() {
        return this.body.stream().filter(node -> node instanceof GlslFunctionNode).map(node -> (GlslFunctionNode) node);
    }

    public Optional<GlslNewNode> field(String name) {
        return this.body.stream().filter(node -> node instanceof GlslNewNode newNode && name.equals(newNode.getName())).findFirst().map(newNode -> (GlslNewNode) newNode);
    }

    public Stream<GlslNewNode> fields() {
        return this.body.stream().filter(node -> node instanceof GlslNewNode).map(node -> (GlslNewNode) node);
    }

    public Stream<GlslNewNode> searchField(String name) {
        return this.body.stream().flatMap(GlslNode::stream).filter(node -> node instanceof GlslNewNode newNode && name.equals(newNode.getName())).map(node -> (GlslNewNode) node);
    }

    public Optional<GlslBlock> containingBlock(GlslNode node) {
        GlslBlock block = this.containingBlock(this.body, node);
        return block != null && block.list == this.body ? Optional.of(new GlslBlock(this.mainFunction().orElseThrow().getBody(), 0)) : Optional.ofNullable(block);
    }

    private @Nullable GlslBlock containingBlock(GlslNodeList body, GlslNode node) {
        for (int i = 0; i < body.size(); i++) {
            GlslNode element = body.get(i);
            if (element == node) {
                return new GlslBlock(body, i);
            }
            if (element instanceof GlslSelectionNode selectionNode) {
                GlslBlock firstFound = this.containingBlock(selectionNode.getFirst(), node);
                if (firstFound != null) {
                    return firstFound;
                }

                GlslBlock secondFound = this.containingBlock(selectionNode.getSecond(), node);
                if (secondFound != null) {
                    return secondFound;
                }
            }
            GlslNodeList elementBody = element.getBody();
            if (elementBody != null) {
                GlslBlock found = this.containingBlock(elementBody, node);
                if (found != null) {
                    return found;
                }
            }
        }
        return null;
    }

    public record GlslBlock(GlslNodeList list, int index) {
        public GlslNode node() {
            return this.list.get(this.index);
        }
    }

    public GlslVersionStatement getVersionStatement() {
        return this.versionStatement;
    }

    public List<String> getDirectives() {
        return this.directives;
    }

    public Map<String, GlslNode> getMarkers() {
        return this.markers;
    }

    public Map<String, String> getMacros() {
        return this.macros;
    }

    public GlslNodeList getBody() {
        return this.body;
    }

    public String toSourceString() {
        GlslStringWriter writer = new GlslStringWriter();
        this.visit(writer);
        return writer.toString();
    }

    public static void stripGLMacros(Map<String, String> macros) {
        macros.keySet().removeIf(macro -> macro.startsWith("GL_"));
        macros.remove("__VERSION__");
    }

    @Override
    public String toString() {
        return "GlslTree{version=" + this.versionStatement + ", body=" + this.body + '}';
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || this.getClass() != o.getClass()) {
            return false;
        }

        GlslTree glslTree = (GlslTree) o;
        return this.versionStatement.equals(glslTree.versionStatement) && this.body.equals(glslTree.body) && this.directives.equals(glslTree.directives);
    }

    @Override
    public int hashCode() {
        int result = this.versionStatement.hashCode();
        result = 31 * result + this.body.hashCode();
        result = 31 * result + this.directives.hashCode();
        return result;
    }
}
