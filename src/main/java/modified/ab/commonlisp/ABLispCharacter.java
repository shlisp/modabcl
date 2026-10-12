/*
 * ABLispCharacter.java
 *
 * Copyright (C) 2002-2007 Peter Graves
 * $Id$
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.
 *
 * As a special exception, the copyright holders of this library give you
 * permission to link this library with independent modules to produce an
 * executable, regardless of the license terms of these independent
 * modules, and to copy and distribute the resulting executable under
 * terms of your choice, provided that you also meet, for each linked
 * independent module, the terms and conditions of the license of that
 * module.  An independent module is a module which is not derived from
 * or based on this library.  If you modify this library, you may extend
 * this exception to your version of the library, but you are not
 * obligated to do so.  If you do not wish to do so, delete this
 * exception statement from your version.
 */
package modified.ab.commonlisp;

import java.util.HashMap;
import java.util.Map;

/**
 *
 * @author Masahito Hemmi
 */
public final class ABLispCharacter extends LispObject {

    private final int value;


    public ABLispCharacter(int cha) {
        this.value = cha;
    }

    public Character toChar() {

        if (0 <= value && value < 65536) {
            char c = (char) value;
            return Character.valueOf(c);
        }
        throw new IllegalStateException("Can not convert to java.lang.Character : because LispCharacter >= 65536");
    }

    @Override
    public String toString() {

        if (0 <= value && value < 65536) {
            char c = (char) value;
            return "LispCharacter[" + c + "]";
        }
        return "LispCharacter[" + value + "]";
    }

    public int getCodepoint() {
        return value;
    }

    public static final char toLowerCase(char c) {

        return Character.toLowerCase(c);
    }

    public static final char toUpperCase(char c) {
        /*
    if (c < 128)
      return UPPER_CASE_CHARS[c];
         */
        return Character.toUpperCase(c);
    }

    public static ABLispCharacter getInstance(char c) {
        return new ABLispCharacter(c);
    }

    public static ABLispCharacter valOf(int codePoint) {
        return new ABLispCharacter(codePoint);
    }

    static Map<String, ABLispCharacter> namedToChar = new HashMap<>();

    public static final int nameToChar(String s) {
        String lower = s.toLowerCase();
        ABLispCharacter lc = namedToChar.get(lower);
        if (lc != null) {
            return lc.value;
        }
        if (lower.length() == 5
                && lower.startsWith("u")) {
            try {
                final int i = Integer.parseInt(lower.substring(1, 5), 16);
                return i;
            } catch (NumberFormatException e) {
            }
        }
        if (lower.equals("nul")) {
            return 0;
        }
        if (lower.equals("bel")) {
            return 7;
        }
        if (lower.equals("bs")) {
            return '\b';
        }
        if (lower.equals("ht")) {
            return '\t';
        }
        if (lower.equals("linefeed") || lower.equals("lf")) {
            return '\n';
        }
        if (lower.equals("ff")) {
            return '\f';
        }
        if (lower.equals("cr") || lower.equals("return")) {
            return '\r';
        }
        if (lower.equals("esc")) {
            return 27;
        }
        if (lower.equals("space") || lower.equals("sp")) {
            return ' ';
        }
        if (lower.equals("rubout") || lower.equals("del") || lower.equals("delete")) {
            return 127;
        }
        if (lower.equals("no-break_space")) {
            return 160;
        }
        if (lower.startsWith("u")) {
            int length = lower.length();
            if (length > 1 && length < 5) {
                try {
                    final int i = Integer.parseInt(lower.substring(1), 16);
                    return i;
                } catch (NumberFormatException e) {
                }
                // fall through
            }
        }

        // Unknown.
        return -1;
    }

    public static final String charToName(char c) {
        switch (c) {
            case 0:
                return "Null";
            case 7:
                return "Bell";
            case '\b':
                return "Backspace";
            case '\t':
                return "Tab";
            case '\n':
                return "Newline";
            case '\f':
                return "Page";
            case '\r':
                return "Return";
            case 27:
                return "Escape";
            case ' ':
                return "Space";
            case 127:
                return "Rubout";
            case 160:
                return "No-break_space";
        }

        //   if (c<0 || c>255) return null;
        //   return lispChars.get(c).name;
        return null;
    }

    
    @Override
    public int hashCode() {
        int hash = 5;
        hash = 23 * hash + this.value;
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final ABLispCharacter other = (ABLispCharacter) obj;
        return this.value == other.value;
    }
    
}
