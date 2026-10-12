/*
 * Cons.java
 *
 * Copyright (C) 2002-2005 Peter Graves
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

import modified.ab.util.LineNumber;
import modified.ab.util.ILocation;
import static modified.ab.commonlisp.Lisp.NIL;

/**
 * The Cons was renamed to ABCons.
 *
 * @author Masahito Hemmi
 */
public class ABCons extends Object implements ILocation {

    // ２箇所からアクセスされるので、public
    public Object cdr;

    protected Object car;
    protected LineNumber _meta;

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
        return new ABCons(meta, car, cdr);
    }

    public ABCons(Object car, Object cdr) {
        this.car = car;
        this.cdr = cdr;
        _meta = null;
    }

    public ABCons(Object car) {
        this.car = car;
        this.cdr = NIL;
        _meta = null;
    }

    public ABCons(LineNumber meta, Object first, Object _more) {
        this.car = first;
        this.cdr = _more;
        _meta = meta;

    }

    /**
     * @return the car
     */
    public Object getCar() {
        return car;
    }

    /**
     * @return the cdr
     */
    public Object getCdr() {
        return cdr;
    }
    
    public Object getCadr() {
        if (!(cdr instanceof ABCons cons)) {
            return ABNil.NIL;
        }
        return cons.getCar();
    }
    

}
