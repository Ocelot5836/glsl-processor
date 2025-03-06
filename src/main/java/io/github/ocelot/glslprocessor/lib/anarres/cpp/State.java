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

/*
 * NOTE: This File was edited by the Veil Team based on this commit: https://github.com/shevek/jcpp/commit/5e50e75ec33f5b4567cabfd60b6baca39524a8b7
 *
 * - Updated formatting to more closely follow project standards
 * - Removed all file/IO
 * - Fixed minor errors
 */

import org.jetbrains.annotations.ApiStatus;

@ApiStatus.Internal
class State {

    boolean parent;
    boolean active;
    boolean sawElse;

    State() {
        this.parent = true;
        this.active = true;
        this.sawElse = false;
    }

    State(State parent) {
        this.parent = parent.isParentActive() && parent.isActive();
        this.active = true;
        this.sawElse = false;
    }

    /* Required for #elif */
    void setParentActive(boolean b) {
        this.parent = b;
    }

    boolean isParentActive() {
        return this.parent;
    }

    void setActive(boolean b) {
        this.active = b;
    }

    boolean isActive() {
        return this.active;
    }

    void setSawElse() {
        this.sawElse = true;
    }

    boolean sawElse() {
        return this.sawElse;
    }

    @Override
    public String toString() {
        return "parent=" + this.parent
                + ", active=" + this.active
                + ", sawelse=" + this.sawElse;
    }
}
