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

/*
 * NOTE: This File was edited by the Veil Team based on this commit: https://github.com/shevek/jcpp/commit/5e50e75ec33f5b4567cabfd60b6baca39524a8b7
 *
 * - Updated formatting to more closely follow project standards
 * - Removed all file/IO
 * - Fixed minor errors
 */

/**
 * A Preprocessor token.
 *
 * @see Preprocessor
 */
@ApiStatus.Internal
public final class Token {

    // public static final int	EOF        = -1;
    private final int type;
    private int line;
    private int column;
    private final Object value;
    private final String text;

    public Token(int type, int line, int column,
                 String text, Object value) {
        this.type = type;
        this.line = line;
        this.column = column;
        this.text = text;
        this.value = value;
    }

    public Token(int type, int line, int column, String text) {
        this(type, line, column, text, null);
    }

    Token(int type, String text, Object value) {
        this(type, -1, -1, text, value);
    }

    Token(int type, String text) {
        this(type, text, null);
    }

    Token(int type) {
        this(type, TokenType.getTokenText(type));
    }

    /**
     * Returns the semantic type of this token.
     *
     * @return the semantic type of this token.
     * @see #getTokenName(int)
     */
    public int getType() {
        return this.type;
    }

    void setLocation(int line, int column) {
        this.line = line;
        this.column = column;
    }

    /**
     * Returns the line at which this token started.
     * <p>
     * Lines are numbered from 1.
     *
     * @return the line at which this token started.
     * @see LexerSource#getLine()
     */
    // Not  - might not have been assigned?
    public int getLine() {
        return this.line;
    }

    /**
     * Returns the column at which this token started.
     * <p>
     * Columns are numbered from 0.
     *
     * @return the column at which this token started.
     * @see LexerSource#getColumn()
     */
    // Not  - might not have been assigned?
    public int getColumn() {
        return this.column;
    }

    /**
     * Returns the original or generated text of this token.
     * <p>
     * This is distinct from the semantic value of the token.
     *
     * @return the original or generated text of this token.
     * @see #getValue()
     */
    // Not @NotNull - might not have been assigned?
    public String getText() {
        return this.text;
    }

    /**
     * Returns the semantic value of this token.
     * <p>
     * For strings, this is the parsed String.
     * For integers, this is an Integer object.
     * For other token types, as appropriate.
     *
     * @return the semantic value of this token, or null.
     * @see #getText()
     */
    // @Nullable    // Not useful to annotate, as we have usually checked the type before calling this.
    public Object getValue() {
        return this.value;
    }

    /**
     * Returns a description of this token, for debugging purposes.
     */
    @Override
    public String toString() {
        StringBuilder buf = new StringBuilder();

        buf.append('[').append(getTokenName(this.type));
        if (this.line != -1) {
            buf.append('@').append(this.line);
            if (this.column != -1) {
                buf.append(',').append(this.column);
            }
        }
        buf.append("]:");
        if (this.text != null) {
            buf.append('"').append(this.text).append('"');
        } else if (this.type > 3 && this.type < 256) {
            buf.append((char) this.type);
        } else {
            buf.append('<').append(this.type).append('>');
        }
        if (this.value != null) {
            buf.append('=').append(this.value);
        }
        return buf.toString();
    }

    /**
     * Returns the descriptive name of the given token type.
     * <p>
     * This is mostly used for stringification and debugging.
     *
     * @param type The type constant from this class to name.
     * @return the descriptive name of the given token type.
     * @see Token#getType()
     */
    @NotNull
    public static String getTokenName(int type) {
        return TokenType.getTokenName(type);
    }

    public static final int AND_EQ = 257;
    public static final int ARROW = 258;
    public static final int CHARACTER = 259;
    public static final int CCOMMENT = 260;
    public static final int CPPCOMMENT = 261;
    public static final int DEC = 262;
    public static final int DIV_EQ = 263;
    public static final int ELLIPSIS = 264;
    public static final int EOF = 265;
    public static final int EQ = 266;
    public static final int GE = 267;
    public static final int HASH = 268;
    public static final int HEADER = 269;
    public static final int IDENTIFIER = 270;
    public static final int INC = 271;
    public static final int NUMBER = 272;
    public static final int LAND = 273;
    public static final int LAND_EQ = 274;
    public static final int LE = 275;
    public static final int LITERAL = 276;
    public static final int LOR = 277;
    public static final int LOR_EQ = 278;
    public static final int LSH = 279;
    public static final int LSH_EQ = 280;
    public static final int MOD_EQ = 281;
    public static final int MULT_EQ = 282;
    public static final int NE = 283;
    public static final int NL = 284;
    public static final int OR_EQ = 285;
    public static final int PASTE = 286;
    public static final int PLUS_EQ = 287;
    public static final int RANGE = 288;
    public static final int RSH = 289;
    public static final int RSH_EQ = 290;
    public static final int SQSTRING = 291;
    public static final int STRING = 292;
    public static final int SUB_EQ = 293;
    public static final int WHITESPACE = 294;
    public static final int XOR_EQ = 295;
    public static final int M_ARG = 296;
    public static final int M_PASTE = 297;
    public static final int M_STRING = 298;
    public static final int P_LINE = 299;
    public static final int INVALID = 300;

    /**
     * The position-less space token.
     */
    static final Token space = new Token(WHITESPACE, -1, -1, " ");
}
