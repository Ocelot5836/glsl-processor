/*
 * Anarres C Preprocessor
 * Copyright (c) 2007-2015, Shevek
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
 * or implied.  See the License for the specific language governing
 * permissions and limitations under the License.
 */
package io.github.ocelot.glslprocessor.lib.anarres.cpp;

import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import static io.github.ocelot.glslprocessor.lib.anarres.cpp.Token.*;

/*
 * NOTE: This File was edited by the Veil Team based on this commit: https://github.com/shevek/jcpp/commit/5e50e75ec33f5b4567cabfd60b6baca39524a8b7
 *
 * - Updated formatting to more closely follow project standards
 * - Removed all file/IO
 * - Fixed minor errors
 */

/**
 * An input to the Preprocessor.
 * <p>
 * Inputs may come from Files, Strings or other sources. The
 * preprocessor maintains a stack of Sources. Operations such as
 * file inclusion or token pasting will push a new source onto
 * the Preprocessor stack. Sources pop from the stack when they
 * are exhausted; this may be transparent or explicit.
 * <p>
 * BUG: Error messages are not handled properly.
 */
@ApiStatus.Internal
public abstract class Source {

    private Source parent;
    private boolean autopop;
    private boolean werror;

    public Source() {
        this.parent = null;
        this.autopop = false;
        this.werror = false;
    }

    /**
     * Sets the parent source of this source.
     * <p>
     * Sources form a singly linked list.
     */
    void setParent(Source parent, boolean autopop) {
        this.parent = parent;
        this.autopop = autopop;
    }

    /**
     * Returns the parent source of this source.
     * <p>
     * Sources form a singly linked list.
     */
    /* pp */
    final Source getParent() {
        return this.parent;
    }


    // @OverrideMustInvoke
    void init(Preprocessor pp) {
        this.werror = pp.getWarnings().contains(Warning.ERROR);
    }

    /**
     * Returns the human-readable name of the current Source.
     */
    @Nullable
    public String getName() {
        Source parent = this.getParent();
        if (parent != null) {
            return parent.getName();
        }
        return null;
    }

    /**
     * Returns the current line number within this Source.
     */
    public int getLine() {
        Source parent = this.getParent();
        if (parent == null) {
            return 0;
        }
        return parent.getLine();
    }

    /**
     * Returns the current column number within this Source.
     */
    public int getColumn() {
        Source parent = this.getParent();
        if (parent == null) {
            return 0;
        }
        return parent.getColumn();
    }

    /**
     * Returns true if this Source is expanding the given macro.
     * <p>
     * This is used to prevent macro recursion.
     */
    boolean isExpanding(@NotNull Macro m) {
        Source parent = this.getParent();
        if (parent != null) {
            return parent.isExpanding(m);
        }
        return false;
    }

    /**
     * Returns true if this Source should be transparently popped
     * from the input stack.
     * <p>
     * Examples of such sources are macro expansions.
     */
    boolean isAutopop() {
        return this.autopop;
    }

    /**
     * Returns true if this source has line numbers.
     */
    boolean isNumbered() {
        return false;
    }

    /**
     * Returns the next Token parsed from this input stream.
     *
     * @see Token
     */
    public abstract Token token() throws LexerException;

    /**
     * Skips tokens until the end of line.
     *
     * @param white true if only whitespace is permitted on the
     *              remainder of the line.
     * @return the NL token.
     */
    public Token skipline(boolean white) throws LexerException {
        while (true) {
            Token tok = this.token();
            switch (tok.getType()) {
                case EOF:
                    /* There ought to be a newline before EOF.
                     * At least, in any skipline context. */
                    /* XXX Are we sure about this? */
                    this.warning(tok.getLine(), tok.getColumn(), "No newline before end of file");
                    return new Token(NL, tok.getLine(), tok.getColumn(), "\n");
                // return tok;
                case NL:
                    /* This may contain one or more newlines. */
                    return tok;
                case CCOMMENT:
                case CPPCOMMENT:
                case WHITESPACE:
                    break;
                default:
                    /* XXX Check white, if required. */
                    if (white) {
                        this.warning(tok.getLine(), tok.getColumn(), "Unexpected nonwhite token");
                    }
                    break;
            }
        }
    }

    protected void error(int line, int column, String msg) throws LexerException {
        throw new LexerException("Error at " + line + ":" + column + ": " + msg);
    }

    protected void warning(int line, int column, String msg) throws LexerException {
        if (this.werror) {
            this.error(line, column, msg);
        } else {
            throw new LexerException("Warning at " + line + ":" + column + ": " + msg);
        }
    }
}
