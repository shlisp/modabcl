/*
 * ABComplex.java
 *
 * Copyright (C) 2003-2006 Peter Graves
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

/**
 * RENAME　complex to ABComplex
 * @author henmi
 */
public class ABComplex {
    
  public final Object realpart;
  public final Object imagpart;

  private ABComplex(Object realpart, Object imagpart)
  {
    this.realpart = realpart;
    this.imagpart = imagpart;
  }

  public static Object getInstance(Object realpart,
                                       Object imagpart)

  {
      /*
    if (!realpart.realp())
      return type_error(realpart, Symbol.REAL);
    if (!imagpart.realp())
      return type_error(imagpart, Symbol.REAL);
    if (realpart instanceof DoubleFloat)
      imagpart = DoubleFloat.coerceToFloat(imagpart);
    else if (imagpart instanceof DoubleFloat)
      realpart = DoubleFloat.coerceToFloat(realpart);
    else if (realpart instanceof SingleFloat)
      imagpart = SingleFloat.coerceToFloat(imagpart);
    else if (imagpart instanceof SingleFloat)
      realpart = SingleFloat.coerceToFloat(realpart);
    if (imagpart instanceof Fixnum)
      {
        if (((Fixnum)imagpart).value == 0)
          return realpart;
      }
*/
    return new ABComplex(realpart, imagpart);
  }

  public Object getRealPart()
  {
    return realpart;
  }

  public Object getImaginaryPart()
  {
    return imagpart;
  }

}
