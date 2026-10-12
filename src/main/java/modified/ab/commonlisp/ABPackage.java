/*
 * Package.java
 *
 * Copyright (C) 2002-2007 Peter Graves <peter@armedbear.org>
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

import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;

/**
 * The Package was renamed to ABPackage.
 * @author Masahito Hemmi
 */
public final class ABPackage extends LispObject {

    private final String name ;

    /** Symbols internal to the package. */
    private transient final ConcurrentHashMap<String, ABSymbol> internalSymbols
            = new ConcurrentHashMap<>(16);
    
    /** Symbols exported from the package.
     *
     * Those symbols in this collection are not contained in the internalSymbols
     */
    private transient final ConcurrentHashMap<String, ABSymbol> externalSymbols
            = new ConcurrentHashMap<>(16);
    
    public ABPackage(String name) {
        this.name = name;
    }
    
    public ABSymbol addExternalSymbol(String symbolName) {
        
        final ABSymbol symbol = new ABSymbol(symbolName, this);
        externalSymbols.put(symbolName, symbol);
        return symbol;
    }

    public ABSymbol addInternalSymbol(String name) {
        return new ABSymbol(name, this);
    }

    public ABSymbol intern(String name) {
        
        if ("nil".equals(name)) {
            return Lisp.NIL;
        }
        
        return new ABSymbol(name, this);
    }

    public ABSymbol findInternalSymbol(String symbolName) {
        return null;
    }
    
     public ABSymbol findExternalSymbol(SimpleString name)
    {
        return externalSymbols.get(name.toString());
    }
   
    public ABSymbol findExternalSymbol(String name)
    {
        return externalSymbols.get(name);
    }
    
    public ABPackage findPackage(String name) {
        return null;
    }

    public final String getName()
    {
        return name;
    }

    @Override
    public int hashCode() {
        int hash = 5;
        hash = 19 * hash + Objects.hashCode(this.name);
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
        final ABPackage other = (ABPackage) obj;
        return Objects.equals(this.name, other.name);
    }
    
}
