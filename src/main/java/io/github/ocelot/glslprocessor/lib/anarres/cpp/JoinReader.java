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

import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;

/*
 * NOTE: This File was edited by the Veil Team based on this commit: https://github.com/shevek/jcpp/commit/5e50e75ec33f5b4567cabfd60b6baca39524a8b7
 *
 * - Updated formatting to more closely follow project standards
 * - Removed all file/IO
 * - Fixed minor errors
 */

@ApiStatus.Internal
class JoinReader {

    private final Reader in;

    private LexerSource source;
    private boolean trigraphs;
    private boolean warnings;

    private int newlines;
    private boolean flushnl;
    private final int[] unget;
    private int uptr;

    public JoinReader(String in) {
        this.in = new StringReader(in);
        this.trigraphs = false;
        this.newlines = 0;
        this.flushnl = false;
        this.unget = new int[2];
        this.uptr = 0;
    }

    public void setTrigraphs(boolean enable, boolean warnings) {
        this.trigraphs = enable;
        this.warnings = warnings;
    }

    void init(Preprocessor pp, LexerSource s) {
        this.source = s;
        this.setTrigraphs(pp.getFeature(Feature.TRIGRAPHS), pp.getWarning(Warning.TRIGRAPHS));
    }

    private int __read() {
        if (this.uptr > 0) {
            return this.unget[--this.uptr];
        }
        try {
            return this.in.read();
        } catch (IOException e) {
            // not possible
            throw new AssertionError(e);
        }
    }

    private void _unread(int c) {
        if (c != -1) {
            this.unget[this.uptr++] = c;
        }
        assert this.uptr <= this.unget.length : "JoinReader ungets too many characters";
    }

    protected void warning(String msg) throws LexerException {
        if (this.source != null) {
            this.source.warning(msg);
        } else {
            throw new LexerException(msg);
        }
    }

    private char trigraph(char raw, char repl) throws LexerException {
        if (this.trigraphs) {
            if (this.warnings) {
                this.warning("trigraph ??" + raw + " converted to " + repl);
            }
            return repl;
        } else {
            if (this.warnings) {
                this.warning("trigraph ??" + raw + " ignored");
            }
            this._unread(raw);
            this._unread('?');
            return '?';
        }
    }

    private int _read() throws LexerException {
        int c = this.__read();
        if (c == '?' && (this.trigraphs || this.warnings)) {
            int d = this.__read();
            if (d == '?') {
                int e = this.__read();
                switch (e) {
                    case '(':
                        return this.trigraph('(', '[');
                    case ')':
                        return this.trigraph(')', ']');
                    case '<':
                        return this.trigraph('<', '{');
                    case '>':
                        return this.trigraph('>', '}');
                    case '=':
                        return this.trigraph('=', '#');
                    case '/':
                        return this.trigraph('/', '\\');
                    case '\'':
                        return this.trigraph('\'', '^');
                    case '!':
                        return this.trigraph('!', '|');
                    case '-':
                        return this.trigraph('-', '~');
                }
                this._unread(e);
            }
            this._unread(d);
        }
        return c;
    }

    public int read() throws LexerException {
        if (this.flushnl) {
            if (this.newlines > 0) {
                this.newlines--;
                return '\n';
            }
            this.flushnl = false;
        }

        while (true) {
            int c = this._read();
            switch (c) {
                case '\\':
                    int d = this._read();
                    switch (d) {
                        case '\n':
                            this.newlines++;
                            continue;
                        case '\r':
                            this.newlines++;
                            int e = this._read();
                            if (e != '\n') {
                                this._unread(e);
                            }
                            continue;
                        default:
                            this._unread(d);
                            return c;
                    }
                case '\r':
                case '\n':
                case '\u2028':
                case '\u2029':
                case '\u000B':
                case '\u000C':
                case '\u0085':
                    this.flushnl = true;
                    return c;
                case -1:
                    if (this.newlines > 0) {
                        this.newlines--;
                        return '\n';
                    }
                default:
                    return c;
            }
        }
    }

    @Override
    public String toString() {
        return "JoinReader(nl=" + this.newlines + ")";
    }
}
