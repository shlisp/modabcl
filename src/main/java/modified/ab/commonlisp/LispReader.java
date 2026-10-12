/*
 * LispReader.java
 *
 * Copyright (C) 2004-2007 Peter Graves
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

import io.github.shlisp.objects.Readtable;
import modified.ab.util.PVector;
import modified.ab.util.PMap;
import modified.ab.util.LineNumber;
import modified.ab.util.ILocation;

import java.io.IOException;

import static modified.ab.commonlisp.Lisp.NIL;
import static modified.ab.commonlisp.Lisp.PACKAGE_SYS;
import static modified.ab.commonlisp.Lisp.error;

public final class LispReader {

    // ### read-comment
    public static final ReaderMacroFunction READ_COMMENT
            = new ReaderMacroFunction("read-comment", PACKAGE_SYS, false,
                    "stream character") {
        @Override
        public LispObject execute(Stream stream, char ignored) {
            try {
                while (true) {
                    int n = stream._readChar();
                    if (n < 0) {
                        return LispThread.currentThread().setValues();
                    }
                    if (n == '\n') {
                        return LispThread.currentThread().setValues();
                    }
                }
            } catch (java.io.IOException e) {
                return LispThread.currentThread().setValues();
            }
        }
    };

    // ### read-string
    public static final ReaderMacroFunction READ_STRING
            = new ReaderMacroFunction("read-string", PACKAGE_SYS, false,
                    "stream character") {
        @Override
        public Object execute(Stream stream, char terminator) {
            return stream.readString(terminator, Stream.currentReadtable);
        }
    };

    // ### read-list
    public static final ReaderMacroFunction READ_LIST
            = new ReaderMacroFunction("read-list", PACKAGE_SYS, false,
                    "stream character") {
        @Override
        public Object execute(Stream stream, char ignored) {
            
            int line = stream.reader.getLineNumber();
            int col = stream.reader.getColumnNumber();

            Object obj = stream.readList(false, Stream.currentReadtable, ')');

            if (obj instanceof ILocation iobj) {

                int line2 = stream.reader.getLineNumber();
                int col2 = stream.reader.getColumnNumber();

                LineNumber meta = new LineNumber(line, col - 1, line2, col2);

                return iobj.withMeta(meta);

            } else {

                return obj;
            }
        }
    };

    // ### read-map (add)
    public static final ReaderMacroFunction READ_MAP
            = new ReaderMacroFunction("read-map", PACKAGE_SYS, false,
                    "stream character") {
        @Override
        public Object execute(Stream stream, char ignored) {
            
            int line = stream.reader.getLineNumber();
            int col = stream.reader.getColumnNumber();

            Object obj = stream.readList(false, Stream.currentReadtable, '}');
            if (obj instanceof ABCons) {

                ABCons loc = (ABCons) obj;
                LineNumber meta = loc.meta();
                if (meta == null) {

                    int line2 = stream.reader.getLineNumber();
                    int col2 = stream.reader.getColumnNumber();

                    meta = new LineNumber(line, col - 1, line2, col2);
                    // 先頭のセルだけを、PMapに変更する
                    return new PMap(meta, loc.getCar(), loc.getCdr());
                } else {
                    LineNumber newLN = new LineNumber(meta, line, col);
                    // 先頭のセルだけを、PMapに変更する
                    return new PMap(newLN, loc.getCar(), loc.getCdr());
                }

            } else {
                return obj;
            }

        }
    };

    // ### read-vector (add)
    public static final ReaderMacroFunction READ_VECTOR
            = new ReaderMacroFunction("read-map", PACKAGE_SYS, false,
                    "stream character") {
        @Override
        public Object execute(Stream stream, char ignored) {
            
            int line = stream.reader.getLineNumber();
            int col = stream.reader.getColumnNumber();

            Object obj = stream.readList(false, Stream.currentReadtable, ']');
            if (obj instanceof ABCons) {

                ABCons loc = (ABCons) obj;
                LineNumber meta = loc.meta();
                if (meta == null) {
                    int line2 = stream.reader.getLineNumber();
                    int col2 = stream.reader.getColumnNumber();

                    meta = new LineNumber(line, col - 1, line2, col2);
                    // 先頭のセルだけを、PVector に変更する
                    return new PVector(meta, loc.getCar(), loc.getCdr());
                } else {
                    LineNumber newLN = new LineNumber(meta, line, col);
                    // 先頭のセルだけを、PVector に変更する
                    return new PVector(newLN, loc.getCar(), loc.getCdr());
                }

            } else {
                return obj;
            }

        }
    };

    // ### read-right-paren
    public static final ReaderMacroFunction READ_RIGHT_PAREN
            = new ReaderMacroFunction("read-right-paren", PACKAGE_SYS, false,
                    "stream character") {
        @Override
        public LispObject execute(Stream stream, char ignored) {
            return error(new ReaderError("Unmatched right parenthesis.", stream));
        }
    };

    // ### read-quote
    public static final ReaderMacroFunction READ_QUOTE
            = new ReaderMacroFunction("read-quote", PACKAGE_SYS, false,
                    "stream character") {
        @Override
        public Object execute(Stream stream, char ignored) {
            return new ABCons(ABSymbol.QUOTE,
                    new ABCons(stream.read(true, NIL, true,
                            LispThread.currentThread(),
                            Stream.currentReadtable)));
        }
    };

    // ### read-backquote  (add)
    public static final ReaderMacroFunction READ_BACKQUOTE
            = new ReaderMacroFunction("read-backquote", PACKAGE_SYS, false,
                    "stream character") {
        @Override
        public Object execute(Stream stream, char ignored) {

            stream.backquoteCount++;

            Object next = stream.read(true, NIL, true,
                    LispThread.currentThread(),
                    Stream.currentReadtable);

            stream.backquoteCount--;

            return new ABCons(ABSymbol.BACKQUOTE, new ABCons(next));
        }
    };

    // ### read-comma  (add)
    public static final ReaderMacroFunction READ_COMMA
            = new ReaderMacroFunction("read-comma", PACKAGE_SYS, false,
                    "stream character") {
        @Override
        public Object execute(Stream stream, char ignored) {

            if (stream.backquoteCount <= 0) {
                throw new ParserException("comma not inside ` ");
            }

            stream.backquoteCount--;

            boolean atMark = false;
            try {
                if (stream._charReady()) {

                    int ch = stream._readChar();
                    if (ch == '@') {
                        atMark = true;
                    } else {
                        stream._unreadChar(ch);
                    }
                }
            } catch (IOException e) {
                return error(new ABStreamError(stream, e));
            }

            Object next = stream.read(true, NIL, true,
                    LispThread.currentThread(),
                    Stream.currentReadtable);

            stream.backquoteCount++;

            if (atMark) {
                return new ABCons(ABSymbol.UNQUOTE_SPLICE, new ABCons(next));
            } else {
                return new ABCons(ABSymbol.UNQUOTE, new ABCons(next));
            }
        }
    };

    // ### read-^ (add)
    public static final ReaderMacroFunction READ_META
            = new ReaderMacroFunction("read-meta", PACKAGE_SYS, false,
                    "stream character") {
        @Override
        public Object execute(Stream stream, char ignored) {

            Object first = stream.read(true, NIL, true,
                    LispThread.currentThread(),
                    Stream.currentReadtable);

            Object second = stream.read(true, NIL, true,
                    LispThread.currentThread(),
                    Stream.currentReadtable);

            if (second instanceof ILocation loc) {
                LineNumber meta = loc.meta();
                if (meta == null) {
                    /*
                    if (first instanceof ABKeyword) {

                        LineNumber prop = new LineNumber(first, Boolean.TRUE);

                        return loc.withMeta(prop);

                    } else*/
                    if (first instanceof ABSymbol abs) {
                        ABPackage aPackage = abs.getPackage();
                        String pname = aPackage.getName();
                        if ("KEYWORD".equals(pname)) {

                            LineNumber prop = new LineNumber(first, true);
                            return loc.withMeta(prop);
                        } else {

                            LineNumber prop = new LineNumber(ABKeyword.TAG, first);
                            return loc.withMeta(prop);
                        }

                    } else {

                        throw new ParserException("can not  add meta - needs    symbol or keyword");
                    }

                } else {
/*
                    if (first instanceof ABKeyword) {

                        LineNumber prop2 = meta.append(first, Boolean.TRUE);

                        return loc.withMeta(prop2);

                    } else */
                    if (first instanceof ABSymbol abs) {

                        ABPackage aPackage = abs.getPackage();
                        String pname = aPackage.getName();
                        if ("KEYWORD".equals(pname)) {

                            LineNumber prop = new LineNumber(first, true);
                            return loc.withMeta(prop);
                        } else {
                            LineNumber prop = new LineNumber(ABKeyword.TAG, first);
                            return loc.withMeta(prop);
                        }

                    } else {
                        throw new ParserException("can not  add meta - needs    symbol or keyword");
                    }
                }

            } else {
                throw new ParserException("can not  add meta");
            }
        }
    };

    // ### read-dispatch-char
    public static final ReaderMacroFunction READ_DISPATCH_CHAR
            = new ReaderMacroFunction("read-dispatch-char", PACKAGE_SYS, false,
                    "stream character") {
        @Override
        public Object execute(Stream stream, char c) {
            return stream.readDispatchChar(c, Stream.currentReadtable);
        }
    };

    // ### sharp-left-paren
    public static final DispatchMacroFunction SHARP_LEFT_PAREN
            = new DispatchMacroFunction("sharp-left-paren", PACKAGE_SYS, false,
                    "stream sub-char numarg") {
        @Override
        public Object execute(Stream stream, char c, int n) {
            return stream.readSharpLeftParen(c, n, Stream.currentReadtable);
        }
    };

    // ### sharp-star
    public static final DispatchMacroFunction SHARP_STAR
            = new DispatchMacroFunction("sharp-star", PACKAGE_SYS, false,
                    "stream sub-char numarg") {
        @Override
        public LispObject execute(Stream stream, char ignored, int n) {
            return stream.readSharpStar(ignored, n, Stream.currentReadtable);
        }
    };

    // ### sharp-dot
    public static final DispatchMacroFunction SHARP_DOT
            = new DispatchMacroFunction("sharp-dot", PACKAGE_SYS, false,
                    "stream sub-char numarg") {
        @Override
        public Object execute(Stream stream, char c, int n) {
            return stream.readSharpDot(c, n, Stream.currentReadtable);
        }
    };

    // ### sharp-colon
    public static final DispatchMacroFunction SHARP_COLON
            = new DispatchMacroFunction("sharp-colon", PACKAGE_SYS, false,
                    "stream sub-char numarg") {
        @Override
        public LispObject execute(Stream stream, char c, int n) {
            return stream.readSymbol();
        }
    };

    // ### sharp-a
    public static final DispatchMacroFunction SHARP_A
            = new DispatchMacroFunction("sharp-a", PACKAGE_SYS, false,
                    "stream sub-char numarg") {
        @Override
        public LispObject execute(Stream stream, char c, int n) {
            return stream.readArray(n, Stream.currentReadtable);
        }
    };

    // ### sharp-b
    public static final DispatchMacroFunction SHARP_B
            = new DispatchMacroFunction("sharp-b", PACKAGE_SYS, false,
                    "stream sub-char numarg") {
        @Override
        public Object execute(Stream stream, char c, int n) {
            return stream.readRadix(2, Stream.currentReadtable);
        }
    };

    // ### sharp-c
    public static final DispatchMacroFunction SHARP_C
            = new DispatchMacroFunction("sharp-c", PACKAGE_SYS, false,
                    "stream sub-char numarg") {
        @Override
        public Object execute(Stream stream, char c, int n) {
            return stream.readComplex(Stream.currentReadtable);
        }
    };

    // ### sharp-o
    public static final DispatchMacroFunction SHARP_O
            = new DispatchMacroFunction("sharp-o", PACKAGE_SYS, false,
                    "stream sub-char numarg") {
        @Override
        public Object execute(Stream stream, char c, int n) {
            return stream.readRadix(8, Stream.currentReadtable);
        }
    };

    // ### sharp-p
    public static final DispatchMacroFunction SHARP_P
            = new DispatchMacroFunction("sharp-p", PACKAGE_SYS, false,
                    "stream sub-char numarg") {
        @Override
        public Object execute(Stream stream, char c, int n) {
            return stream.readPathname(Stream.currentReadtable);
        }
    };

    // ### sharp-r
    public static final DispatchMacroFunction SHARP_R
            = new DispatchMacroFunction("sharp-r", PACKAGE_SYS, false,
                    "stream sub-char numarg") {
        @Override
        public Object execute(Stream stream, char c, int n) {
            return stream.readRadix(n, Stream.currentReadtable);
        }
    };

    // ### sharp-s
    public static final DispatchMacroFunction SHARP_S
            = new DispatchMacroFunction("sharp-s", PACKAGE_SYS, false,
                    "stream sub-char numarg") {
        @Override
        public LispObject execute(Stream stream, char c, int n) {
            return stream.readStructure(Stream.currentReadtable);
        }
    };

    // ### sharp-x
    public static final DispatchMacroFunction SHARP_X
            = new DispatchMacroFunction("sharp-x", PACKAGE_SYS, false,
                    "stream sub-char numarg") {
        @Override
        public Object execute(Stream stream, char c, int n) {
            return stream.readRadix(16, Stream.currentReadtable);
        }
    };

    // ### sharp-quote
    public static final DispatchMacroFunction SHARP_QUOTE
            = new DispatchMacroFunction("sharp-quote", PACKAGE_SYS, false,
                    "stream sub-char numarg") {
        @Override
        public Object execute(Stream stream, char c, int n) {
            return new ABCons(ABSymbol.FUNCTION,
                    new ABCons(stream.read(true, NIL, true,
                            LispThread.currentThread(),
                            Stream.currentReadtable)));
        }
    };

    // ### sharp-backslash
    public static final DispatchMacroFunction SHARP_BACKSLASH
            = new DispatchMacroFunction("sharp-backslash", PACKAGE_SYS, false,
                    "stream sub-char numarg") {
        @Override
        public LispObject execute(Stream stream, char c, int n) {

            final LispThread thread = LispThread.currentThread();

            Readtable rt = thread.getReadTable(); 
            LispObject lo = stream.readCharacterLiteral(rt, thread);
            return lo;
        }
    };

    // ### sharp-vertical-bar
    public static final DispatchMacroFunction SHARP_VERTICAL_BAR
            = new DispatchMacroFunction("sharp-vertical-bar", PACKAGE_SYS, false,
                    "stream sub-char numarg") {
        @Override
        public LispObject execute(Stream stream, char c, int n) {
            stream.skipBalancedComment();
            return LispThread.currentThread().setValues();
        }
    };

    // ### sharp-illegal
    public static final DispatchMacroFunction SHARP_ILLEGAL
            = new DispatchMacroFunction("sharp-illegal", PACKAGE_SYS, false,
                    "stream sub-char numarg") {
        @Override
        public LispObject execute(Stream stream, char c, int n) {

            String sb = "Illegal # macro character: #\\";
            /*
            String s = LispCharacter.charToName(c);
            if (s != null)
                sb.append(s);
            else
                sb.append(c);
             */
            return error(new ReaderError(sb, stream));
        }
    };

    // 追加
    public static final DispatchMacroFunction SHARP_PLUS
            = new DispatchMacroFunction("sharp-plus", PACKAGE_SYS, false,
                    "stream sub-char numarg") {
        @Override
        public Object execute(Stream stream, char c, int n) {

            Object nextObj = stream.read(true, NIL, true,
                    LispThread.currentThread(),
                    Stream.currentReadtable);

            Object nextNextObj = stream.read(true, NIL, true,
                    LispThread.currentThread(),
                    Stream.currentReadtable);

            return new ABCons(nextObj, nextNextObj);

        }
    };

}
