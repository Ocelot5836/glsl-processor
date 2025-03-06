package io.github.ocelot.glslprocessor.core;

import org.jetbrains.annotations.ApiStatus;

/**
 * @author Ocelot
 */
@ApiStatus.Internal
class GlslStringReader {

    public final String string;
    public final char[] chars;
    public int cursor;

    public GlslStringReader(String string) {
        this.string = string;
        this.chars = string.toCharArray();
    }

    public boolean canRead() {
        return this.cursor < this.chars.length;
    }

    public void skip(int amount) {
        this.cursor += amount;
    }

    public void skip() {
        this.cursor++;
    }

    public void skipWhitespace() {
        while (this.cursor < this.chars.length && Character.isWhitespace(this.chars[this.cursor])) {
            this.skip();
        }
    }
}
