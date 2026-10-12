/*
 * Symbol.java
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
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301, USA.
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

import modified.ab.util.LineNumber;
import modified.ab.util.ILocation;
import static modified.ab.commonlisp.Lisp.NIL;
import static modified.ab.commonlisp.Lisp.PACKAGE_CL;
import static modified.ab.commonlisp.Lisp.PACKAGE_KEYWORD;
import static modified.ab.commonlisp.Lisp.PACKAGE_SYS;

import java.util.Objects;

/**
 * The Symbol was renamed to ABSymbol.
 *
 */
public class ABSymbol extends LispObject implements ILocation {

    public final SimpleString name;

    private final LispObject pkg; // Either a package object or NIL.

    private transient LispObject value;

    private final LineNumber _meta;

    // Construct an uninterned symbol.
    public ABSymbol(String s) {
        name = new SimpleString(s);
        pkg = NIL;
        _meta = null;
    }

    public ABSymbol(SimpleString string) {
        name = string;
        pkg = NIL;
        _meta = null;
    }

    public ABSymbol(String s, ABPackage pkg) {
        name = new SimpleString(s);
        this.pkg = pkg;
        _meta = null;
    }

    public ABSymbol(SimpleString string, ABPackage pkg) {
        name = string;
        this.pkg = pkg;
        _meta = null;
    }

    // 注意：以下は clojure風なので、internされていない
    public ABSymbol(LineNumber meta, SimpleString string, LispObject pkg) {
        name = string;
        this.pkg = pkg;
        _meta = meta;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 97 * hash + Objects.hashCode(this.name);
        hash = 97 * hash + Objects.hashCode(this.pkg);
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
        final ABSymbol other = (ABSymbol) obj;
        if (!Objects.equals(this.name, other.name)) {
            return false;
        }
        return Objects.equals(this.pkg, other.pkg);
    }

    @Override
    public LineNumber meta() {
        return _meta;
    }

    @Override
    public ILocation withMeta(LineNumber meta) {

        if (_meta == meta) {
            return this;
        }
        // 不変クラスなので、新しいオブジェクトを new する必要あり
        return new ABSymbol(meta, name, pkg);

    }

    public LispObject execute(LispObject arg) {
        throw new RuntimeException("`" + name + "`");
    }

    public final LispObject STRING() {
        return name;
    }

    public final ABPackage getPackage() {

        if (pkg instanceof ABPackage) {
            return (ABPackage) pkg;
        }
        return null;
    }

    public LispObject symbolValue(LispThread th) {
        return value;
    }

    public LispObject symbolValue() {
        return value;
    }

    public void setSymbolValue(LispObject newV) {
        value = newV;
    }

    public LispObject symbolValueNoThrow() {
        return null;
    }


    public final String getName() {
        return name.getStringValue();
    }

    public final String getQualifiedName() {
        final String n = name.getStringValue();
        if (pkg == NIL) {
            return ("#:".concat(n));
        }
        if (pkg == PACKAGE_KEYWORD) {
            return ":".concat(n);
        }
        StringBuilder sb = new StringBuilder(((ABPackage) pkg).getName());
        if (((ABPackage) pkg).findExternalSymbol(name) != null) {
            sb.append(':');
        } else {
            sb.append("::");
        }
        sb.append(n);
        return sb.toString();
    }

    @Override
    public String toString() {

        final String n = name.getStringValue();
        if (pkg == NIL) {
            return ("#:".concat(n));
        }
        if (pkg == PACKAGE_KEYWORD) {
            return ":".concat(n);
        }
        StringBuilder sb = new StringBuilder(((ABPackage) pkg).getName());
        sb.append(":");
        sb.append(n);
        return sb.toString();
    }

    public static final ABSymbol READ_EVAL
            = PACKAGE_CL.addExternalSymbol("*READ-EVAL*");

    public static final ABSymbol BACKQUOTE
//            = PACKAGE_SYS.addInternalSymbol("backquote");
            = PACKAGE_CL.addInternalSymbol("backquote");

    public static final ABSymbol UNQUOTE
//            = PACKAGE_SYS.addInternalSymbol("unquote");
            = PACKAGE_CL.addInternalSymbol("unquote");

    public static final ABSymbol UNQUOTE_SPLICE
//            = PACKAGE_SYS.addInternalSymbol("unquote-splice");
            = PACKAGE_CL.addInternalSymbol("unquote-splice");
    
    

    public static final ABSymbol QUOTE
            = PACKAGE_SYS.addInternalSymbol("quote");
  //          = PACKAGE_SYS.addInternalSymbol("QUOTE");

    public static final ABSymbol FUNCTION
            = PACKAGE_SYS.addInternalSymbol("function");
    //           = PACKAGE_CL.addInternalSymbol("function");

    public static final ABSymbol ERROR
            = PACKAGE_CL.addExternalSymbol("ERROR");

    public static final ABSymbol SINGLE_FLOAT
            = PACKAGE_CL.addExternalSymbol("SINGLE-FLOAT");

    public static final ABSymbol SHORT_FLOAT = PACKAGE_CL.addExternalSymbol("SHORT-FLOAT");

    // --------------   V add V  ----------------
    public static final ABSymbol LIST
            = PACKAGE_CL.addExternalSymbol("LIST");

    public static final ABSymbol VECTOR
            = PACKAGE_CL.addExternalSymbol("VECTOR");

    public static final ABSymbol LINE
            = PACKAGE_KEYWORD.addInternalSymbol("LINE");

    public static final ABSymbol COLUMN
            = PACKAGE_KEYWORD.addInternalSymbol("COLUMN");

    public static final ABSymbol END_LINE
            = PACKAGE_KEYWORD.addInternalSymbol("END_LINE");

    public static final ABSymbol END_COLUMN
            = PACKAGE_KEYWORD.addInternalSymbol("END_COLUMN");
    
    
    
}
