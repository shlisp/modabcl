/*
 * Lisp.java
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

/**
 *
 * @author Masahito Hemmi
 */
public class Lisp {

    public final static ABPackage PACKAGE_SYS = Packages.createPackage("sys", 1024);

    public static final ABPackage PACKAGE_CL  = Packages.createPackage("cl", 2048);

    public static final ABPackage PACKAGE_KEYWORD  = Packages.createPackage("KEYWORD", 1024);

  public static final ABSymbol NIL =        ABNil.NIL;
  public static final ABSymbol T =        new ABSymbol("T");

    public static final LispObject error(ABCondition condition) {
   
        if (  condition instanceof          EndOfFile) {
            
            throw new ParserException(  "End of file"       );
        }
   
        throw new ParserException(     condition.getMessage()        );
    }

    public static final int ierror(ABCondition condition) {
        error(condition);
        return 0; // Not reached
    }

    public static final String serror(ABCondition condition) {
        error(condition);
        return ""; // Not reached
    }

    public static final ABSymbol internSpecial(String name, ABPackage pkg,
            LispObject value) {
        ABSymbol symbol = pkg.intern(name);
   //     symbol.setSpecial(true);
        symbol.setSymbolValue(value);
        return symbol;
    }

    public static final Object eval(final Object obj,
                                      final Environment env,
                                      final LispThread thread)
  {
      throw new RuntimeException();
  }    
    

  public static final AbstractVector checkVector(LispObject obj)

  {
          if (obj instanceof AbstractVector)
                  return (AbstractVector) obj;
      throw new RuntimeException();
  }


}
