/*
 * SimpleArray_T.java
 *
 * Copyright (C) 2003-2007 Peter Graves
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
 * @author  Masahito Henmi
 */
public class SimpleArray_T  extends LispObject {


    private int rank;
    
    private Object contents;
    
    public SimpleArray_T(int rank, Object initialContents) {

        // 2026-10-24 #1A も受け付ける（中身の確認は shlisp の ListTransformer で行う）
        if (rank < 1) {
            Debug.assertTrue(false);
        }
    
    this.rank = rank;
    this.contents = initialContents;
    
    }

/*
  private int setInitialContents(int axis, int[] dims, LispObject contents,
                                 int index)

  {
    if (dims.length == 0)
      {
        try
          {
            data[index] = contents;
          }
        catch (ArrayIndexOutOfBoundsException e)
          {
            error(new LispError("Bad initial contents for array."));
            return -1;
          }
        ++index;
      }
    else
      {
        int dim = dims[0];
        if (dim != contents.length())
          {
            error(new LispError("Bad initial contents for array."));
            return -1;
          }
        int[] newDims = new int[dims.length-1];
        for (int i = 1; i < dims.length; i++)
          newDims[i-1] = dims[i];
        if (contents.listp(this))
          {
            for (int i = contents.length();i-- > 0;)
              {
                LispObject content = contents.car();
                index =
                  setInitialContents(axis + 1, newDims, content, index);
                contents = contents.cdr();
              }
          }
        else
          {
            AbstractVector v = checkVector(contents);
            final int length = v.length();
            for (int i = 0; i < length; i++)
              {
                LispObject content = v.AREF(i);
                index =
                  setInitialContents(axis + 1, newDims, content, index);
              }
          }
      }
    return index;
  }
*/

    /**
     * @return the rank
     */
    public int getRank() {
        return rank;
    }

    /**
     * @return the contents
     */
    public Object getContents() {
        return contents;
    }


}
