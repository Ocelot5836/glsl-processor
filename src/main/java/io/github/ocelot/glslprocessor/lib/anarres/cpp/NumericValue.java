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

/*
 * NOTE: This File was edited by the Veil Team based on this commit: https://github.com/shevek/jcpp/commit/5e50e75ec33f5b4567cabfd60b6baca39524a8b7
 *
 * - Updated formatting to more closely follow project standards
 * - Removed all file/IO
 * - Fixed minor errors
 */

@ApiStatus.Internal
public class NumericValue extends Number {

    public static final int F_UNSIGNED = 1;
    public static final int F_INT = 2;
    public static final int F_LONG = 4;
    public static final int F_LONGLONG = 8;
    public static final int F_FLOAT = 16;
    public static final int F_DOUBLE = 32;

    public static final int FF_SIZE = F_INT | F_LONG | F_LONGLONG | F_FLOAT | F_DOUBLE;

    private final int base;
    private final String integer;
    private String fraction;
    private int expbase = 0;
    private String exponent;

    public NumericValue(int base, @NotNull String integer) {
        this.base = base;
        this.integer = integer;
    }

    public int getBase() {
        return this.base;
    }

    @NotNull
    public String getIntegerPart() {
        return this.integer;
    }

    @Nullable
    public String getFractionalPart() {
        return this.fraction;
    }

    void setFractionalPart(@NotNull String fraction) {
        this.fraction = fraction;
    }

    @Nullable
    public String getExponent() {
        return this.exponent;
    }

    void setExponent(int expbase, @NotNull String exponent) {
        this.expbase = expbase;
        this.exponent = exponent;
    }

    private int exponentValue() {
        return Integer.parseInt(this.exponent, 10);
    }

    @Override
    public int intValue() {
        // String.isEmpty() is since 1.6
        int v = this.integer.isEmpty() ? 0 : Integer.parseInt(this.integer, this.base);
        if (this.expbase == 2) {
            v = v << this.exponentValue();
        } else if (this.expbase != 0) {
            v = (int) (v * Math.pow(this.expbase, this.exponentValue()));
        }
        return v;
    }

    @Override
    public long longValue() {
        // String.isEmpty() is since 1.6
        long v = this.integer.isEmpty() ? 0 : Long.parseLong(this.integer, this.base);
        if (this.expbase == 2) {
            v = v << this.exponentValue();
        } else if (this.expbase != 0) {
            v = (long) (v * Math.pow(this.expbase, this.exponentValue()));
        }
        return v;
    }

    @Override
    public float floatValue() {
        if (this.getBase() != 10) {
            return this.longValue();
        }
        return Float.parseFloat(this.toString());
    }

    @Override
    public double doubleValue() {
        if (this.getBase() != 10) {
            return this.longValue();
        }
        return Double.parseDouble(this.toString());
    }

    @Override
    public String toString() {
        StringBuilder buf = new StringBuilder();
        switch (this.base) {
            case 8:
                buf.append('0');
                break;
            case 10:
                break;
            case 16:
                buf.append("0x");
                break;
            case 2:
                buf.append('b');
                break;
            default:
                buf.append("[base-").append(this.base).append("]");
                break;
        }
        buf.append(this.getIntegerPart());
        if (this.getFractionalPart() != null) {
            buf.append('.').append(this.getFractionalPart());
        }
        if (this.getExponent() != null) {
            buf.append(this.base > 10 ? 'p' : 'e');
            buf.append(this.getExponent());
        }
        return buf.toString();
    }
}
