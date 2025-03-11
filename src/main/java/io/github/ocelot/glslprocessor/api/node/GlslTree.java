package io.github.ocelot.glslprocessor.api.node;

import io.github.ocelot.glslprocessor.api.grammar.GlslSpecifiedType;
import io.github.ocelot.glslprocessor.api.grammar.GlslTypeQualifier;
import io.github.ocelot.glslprocessor.api.grammar.GlslVersionStatement;
import io.github.ocelot.glslprocessor.api.match.GlslMatchResult;
import io.github.ocelot.glslprocessor.api.match.GlslMatcher;
import io.github.ocelot.glslprocessor.api.node.branch.GlslIfNode;
import io.github.ocelot.glslprocessor.api.node.function.GlslFunctionNode;
import io.github.ocelot.glslprocessor.api.node.variable.GlslNewFieldNode;
import io.github.ocelot.glslprocessor.api.node.variable.GlslStructDeclarationNode;
import io.github.ocelot.glslprocessor.api.node.variable.GlslVariableDeclarationNode;
import io.github.ocelot.glslprocessor.api.visitor.GlslNodeVisitor;
import io.github.ocelot.glslprocessor.api.visitor.GlslTreeStringWriter;
import io.github.ocelot.glslprocessor.api.visitor.GlslTreeVisitor;
import io.github.ocelot.glslprocessor.impl.match.GlslSearcher;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.stream.Stream;

/**
 * Represents an entire GLSL file.
 *
 * @author Ocelot
 * @since 1.0.0
 */
public final class GlslTree {

    private final GlslVersionStatement versionStatement;
    private final GlslNodeList body;
    private final List<String> directives;
    private final Map<String, GlslNode> markers;
    private final Map<String, String> macros;

    public GlslTree() {
        this.versionStatement = new GlslVersionStatement();
        this.body = new GlslNodeList();
        this.directives = new ArrayList<>();
        this.markers = Collections.emptyMap();
        this.macros = new HashMap<>();
    }

    public GlslTree(GlslVersionStatement versionStatement, Collection<GlslNode> body, Collection<String> directives, Map<String, GlslNode> markers) {
        this.versionStatement = versionStatement;
        this.body = new GlslNodeList(body);
        this.directives = new ArrayList<>(directives);
        this.markers = Collections.unmodifiableMap(markers);
        this.macros = new HashMap<>();
    }

    private void flatten() {
        for (int i = 0; i < this.body.size(); i++) {
            GlslNode node = this.body.get(i);
            if (node instanceof GlslEmptyNode) {
                this.body.remove(i);
                i--;
                continue;
            }

            if (node instanceof GlslCompoundNode compoundNode) {
                this.body.remove(i);

                List<GlslNode> children = compoundNode.children;
                for (int j = children.size() - 1; j >= 0; j--) {
                    this.body.add(j, children.get(j));
                }
            }
        }
    }

    private void visit(GlslTreeVisitor visitor, int index, GlslNode node) {
        if (node instanceof GlslFunctionNode functionNode) {
            GlslNodeVisitor functionVisitor = visitor.visitFunction(index, functionNode);
            if (functionVisitor != null) {
                functionNode.visit(functionVisitor);
            }
            visitor.visitFunctionEnd(index, functionNode);
            return;
        }
        if (node instanceof GlslNewFieldNode newNode) {
            visitor.visitNewField(index, newNode);
            return;
        }
        if (node instanceof GlslStructDeclarationNode struct) {
            visitor.visitStructDeclaration(index, struct);
            return;
        }
        if (node instanceof GlslVariableDeclarationNode declaration) {
            visitor.visitDeclaration(index, declaration);
            return;
        }
        throw new AssertionError("Not Possible: " + node.getClass());
    }

    /**
     * Visits this GLSL file and all nodes inside.
     *
     * @param visitor The visitor instance
     */
    public void visit(GlslTreeVisitor visitor) {
        this.flatten(); // Make sure the tree is flattened

        visitor.visitMarkers(this.markers);
        visitor.visitVersion(this.versionStatement);
        for (String directive : this.directives) {
            visitor.visitDirective(directive);
        }
        for (Map.Entry<String, String> entry : this.macros.entrySet()) {
            visitor.visitMacro(entry.getKey(), entry.getValue());
        }

        for (int i = 0; i < this.body.size(); i++) {
            GlslNode node = this.body.get(i);
            if (node instanceof GlslEmptyNode || node instanceof GlslCompoundNode) {
                throw new AssertionError();
            }
            this.visit(visitor, i, node);
        }

        visitor.visitTreeEnd(this);
    }

    /**
     * Explicitly marks all outputs with a layout location if not specified.
     */
    public void markOutputs() {
        List<GlslNewFieldNode> outputs = new ArrayList<>();
        this.visit(new GlslTreeVisitor() {
            @Override
            public void visitNewField(int index, GlslNewFieldNode node) {
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
                                int location = Integer.parseInt(expression.toSourceString());
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
            }
        });

        for (GlslNewFieldNode output : outputs) {
            output.getType().addLayoutId("location", GlslNode.intConstant(0));
        }
    }

    /**
     * Extracts nodes from the tree by traversing down the structure.
     *
     * @param filter The filters to apply in order
     * @return The nodes found from the tree
     */
    @ApiStatus.Experimental
    public GlslMatchResult get(GlslMatcher... filter) {
        if (filter.length == 0) {
            List<GlslSearcher.Result> results = new ArrayList<>(this.body.size());
            for (int i = 0; i < this.body.size(); i++) {
                int replaceIndex = i;
                results.add(new GlslSearcher.Result(replace -> this.body.set(replaceIndex, replace != null ? replace : GlslEmptyNode.INSTANCE), this.body.get(i)));
            }
            return new GlslMatchResult(results);
        }

        List<GlslSearcher.Result> found = new ArrayList<>();
        GlslSearcher searcher = new GlslSearcher(found, 1, filter);
        GlslTreeVisitor visitor = new GlslTreeVisitor() {
            @Override
            public void visitNewField(int index, GlslNewFieldNode node) {
                // TODO
            }

            @Override
            public void visitStructDeclaration(int index, GlslStructDeclarationNode node) {
                // TODO
            }

            @Override
            public void visitDeclaration(int index, GlslVariableDeclarationNode node) {
                // TODO
            }

            @Override
            public @Nullable GlslNodeVisitor visitFunction(int index, GlslFunctionNode node) {
                if (filter[0] instanceof GlslMatcher.FunctionMatcher matcher) {
                    if (matcher.matches(node)) {
                        if (filter.length == 1) {
                            found.add(new GlslSearcher.Result(replace -> GlslTree.this.body.set(index, replace), node));
                            return null;
                        }

                        return searcher;
                    }
                }
                return null;
            }
        };
        this.visit(visitor);
        return new GlslMatchResult(found);
    }

    /**
     * Performs a code replacement with the specified replacement
     *
     * @param method     The method to replace code in or <code>null</code> to replace code in the first level
     * @param targetType The expected type to target
     * @param replace
     */
    public void replace(@Nullable String method, @Nullable GlslNodeType targetType, int index, String target, String replace) {

    }

    public Optional<GlslFunctionNode> mainFunction() {
        return this.functions().filter(node -> node.getHeader().getName().equals("main")).findFirst();
    }

    public Stream<GlslFunctionNode> functions() {
        return this.body.stream().filter(node -> node instanceof GlslFunctionNode).map(node -> (GlslFunctionNode) node);
    }

    public Optional<GlslNewFieldNode> field(String name) {
        return this.body.stream().filter(node -> node instanceof GlslNewFieldNode newNode && name.equals(newNode.getName())).findFirst().map(newNode -> (GlslNewFieldNode) newNode);
    }

    public Stream<GlslNewFieldNode> fields() {
        return this.body.stream().filter(node -> node instanceof GlslNewFieldNode).map(node -> (GlslNewFieldNode) node);
    }

    public Stream<GlslNewFieldNode> searchField(String name) {
        return this.body.stream().flatMap(GlslNode::stream).filter(node -> node instanceof GlslNewFieldNode newNode && name.equals(newNode.getName())).map(node -> (GlslNewFieldNode) node);
    }

    public Optional<GlslBlock> containingBlock(GlslNode node) {
        GlslBlock block = this.containingBlock(this.body, node);
        return block != null && block.body == this.body ? Optional.of(new GlslBlock(this.mainFunction().orElseThrow().getBody(), 0)) : Optional.ofNullable(block);
    }

    private @Nullable GlslBlock containingBlock(GlslNodeList body, GlslNode node) {
        for (int i = 0; i < body.size(); i++) {
            GlslNode element = body.get(i);
            if (element == node) {
                return new GlslBlock(body, i);
            }
            if (element instanceof GlslIfNode selectionNode) {
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

    /**
     * Holds data about what block nodes are contained inside of.
     *
     * @param body  The body the node is inside
     * @param index The index inside the body the node is listed
     * @author Ocelot
     */
    public record GlslBlock(GlslNodeList body, int index) {

        /**
         * @return The original node
         */
        public GlslNode node() {
            return this.body.get(this.index);
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
        GlslTreeStringWriter writer = new GlslTreeStringWriter();
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
