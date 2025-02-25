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

import java.io.Closeable;
import java.io.IOException;
import java.io.Reader;

/*
 * NOTE: This File was edited by the Veil Team based on this commit: https://github.com/shevek/jcpp/commit/5e50e75ec33f5b4567cabfd60b6baca39524a8b7
 *
 * - Updated formatting to more closely follow project standards
 * - Removed all file/IO
 * - Fixed minor errors
 */

/* pp */ class JoinReader /* extends Reader */ implements Closeable {

    private final Reader in;

    private LexerSource source;
    private boolean trigraphs;
    private boolean warnings;

    private int newlines;
    private boolean flushnl;
    private final int[] unget;
    private int uptr;

    public JoinReader(Reader in, boolean trigraphs) {
        this.in = in;
        this.trigraphs = trigraphs;
        this.newlines = 0;
        this.flushnl = false;
        this.unget = new int[2];
        this.uptr = 0;
    }

    public JoinReader(Reader in) {
        this(in, false);
    }

    public void setTrigraphs(boolean enable, boolean warnings) {
        this.trigraphs = enable;
        this.warnings = warnings;
    }

    /* pp */ void init(Preprocessor pp, LexerSource s) {
        PreprocessorListener listener = pp.getListener();
        this.source = s;
        this.setTrigraphs(pp.getFeature(Feature.TRIGRAPHS),
                pp.getWarning(Warning.TRIGRAPHS));
    }

    private int __read() throws IOException {
        if (uptr > 0) {
            return unget[--uptr];
        }
        return in.read();
    }

    private void _unread(int c) {
        if (c != -1) {
            unget[uptr++] = c;
        }
        assert uptr <= unget.length :
                "JoinReader ungets too many characters";
    }

    protected void warning(String msg)
            throws LexerException {
        if (source != null) {
            source.warning(msg);
        } else {
            throw new LexerException(msg);
        }
    }

    private char trigraph(char raw, char repl)
            throws IOException, LexerException {
        if (trigraphs) {
            if (warnings) {
                this.warning("trigraph ??" + raw + " converted to " + repl);
            }
            return repl;
        } else {
            if (warnings) {
                this.warning("trigraph ??" + raw + " ignored");
            }
            this._unread(raw);
            this._unread('?');
            return '?';
        }
    }

    private int _read()
            throws IOException, LexerException {
        int c = this.__read();
        if (c == '?' && (trigraphs || warnings)) {
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

    public int read()
            throws IOException, LexerException {
        if (flushnl) {
            if (newlines > 0) {
                newlines--;
                return '\n';
            }
            flushnl = false;
        }

        for (; ; ) {
            int c = this._read();
            switch (c) {
                case '\\':
                    int d = this._read();
                    switch (d) {
                        case '\n':
                            newlines++;
                            continue;
                        case '\r':
                            newlines++;
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
                    flushnl = true;
                    return c;
                case -1:
                    if (newlines > 0) {
                        newlines--;
                        return '\n';
                    }
                default:
                    return c;
            }
        }
    }

    public int read(char[] cbuf, int off, int len)
            throws IOException, LexerException {
        for (int i = 0; i < len; i++) {
            int ch = this.read();
            if (ch == -1) {
                return i;
            }
            cbuf[off + i] = (char) ch;
        }
        return len;
    }

    @Override
    public void close()
            throws IOException {
        in.close();
    }

    @Override
    public String toString() {
        return "JoinReader(nl=" + newlines + ")";
    }

    /*
     public static void main(String[] args) throws IOException {
     FileReader		f = new FileReader(new File(args[0]));
     BufferedReader	b = new BufferedReader(f);
     JoinReader		r = new JoinReader(b);
     BufferedWriter	w = new BufferedWriter(
     new java.io.OutputStreamWriter(System.out)
     );
     int				c;
     while ((c = r.read()) != -1) {
     w.write((char)c);
     }
     w.close();
     }
     */
}
