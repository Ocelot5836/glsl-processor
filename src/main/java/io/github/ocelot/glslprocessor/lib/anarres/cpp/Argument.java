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

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/*
 * NOTE: This File was edited by the Veil Team based on this commit: https://github.com/shevek/jcpp/commit/5e50e75ec33f5b4567cabfd60b6baca39524a8b7
 *
 * - Updated formatting to more closely follow project standards
 * - Removed all file/IO
 * - Fixed minor errors
 */

/**
 * A macro argument.
 * <p>
 * This encapsulates a raw and preprocessed token stream.
 */
@ApiStatus.Internal
class Argument extends ArrayList<Token> {

    private List<Token> expansion;

    public Argument() {
        this.expansion = null;
    }

    public void addToken(@NotNull Token tok) {
        this.add(tok);
    }

    void expand(@NotNull Preprocessor p) throws IOException, LexerException {
        /* Cache expansion. */
        if (this.expansion == null) {
            this.expansion = p.expand(this);
        }
    }

    @NotNull
    public Iterator<Token> expansion() {
        return this.expansion.iterator();
    }

    @Override
    public String toString() {
        StringBuilder buf = new StringBuilder();
        buf.append("Argument(");
        buf.append("raw=[ ");
        for (Token value : this) {
            buf.append(value.getText());
        }
        buf.append(" ];expansion=[ ");
        if (this.expansion == null) {
            buf.append("null");
        } else {
            for (Token token : this.expansion) {
                buf.append(token.getText());
            }
        }
        buf.append(" ])");
        return buf.toString();
    }
}
