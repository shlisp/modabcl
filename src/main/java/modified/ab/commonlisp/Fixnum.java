/*
 * Fixnum.java
 *
 * Copyright (C) 2002-2006 Peter Graves
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
 *
 * @author Masahito Hemmi
 */
public class Fixnum extends LispObject {

    public static final Fixnum ZERO = new Fixnum(0);

    public final int value;
    public final boolean isLong;
    public final long longValue;

    private Fixnum(int a) {
        this.value = a;
        this.isLong = false;
        this.longValue = 0;
    }

    private Fixnum(boolean b, long c) {

        this.value = 0;
        this.isLong = true;
        this.longValue = c;
    }
    
    public double getDouble() {
        if (isLong) {
            Long ln = longValue;
            return ln.doubleValue();
        } else {
            Integer in = value;
            return in.doubleValue();
        }
    }
    

    public static Fixnum getInstance(int b) {
        return new Fixnum(b);
    }

    public static Fixnum getLongOrInt(long b) {

        if (Integer.MIN_VALUE <= b && b <= Integer.MAX_VALUE) {

            return new Fixnum((int) b);
        } else {

            return new Fixnum(true, b);
        }
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 73 * hash + this.value;
        hash = 73 * hash + (this.isLong ? 1 : 0);
        hash = 73 * hash + (int) (this.longValue ^ (this.longValue >>> 32));
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
        final Fixnum other = (Fixnum) obj;
        if (this.value != other.value) {
            return false;
        }
        if (this.isLong != other.isLong) {
            return false;
        }
        return this.longValue == other.longValue;
    }

    @Override
    public String toString() {
        return "Fixnum[" + value + "]";
    }

}
