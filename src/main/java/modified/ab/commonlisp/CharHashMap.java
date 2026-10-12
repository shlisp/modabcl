/*
 * CharHashMap.java
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

import java.lang.reflect.Array;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;

public class CharHashMap<T> {

        final public T[] constants;
        final public T NULL;
        final static int CACHE_SIZE = 256; 
        final HashMap<Character, T> backing;

        @SuppressWarnings("unchecked")
        public CharHashMap(Class componentType, T def) {
                NULL = def;
                constants = (T[]) Array.newInstance(componentType, CACHE_SIZE);
                Arrays.fill(constants, NULL);
                backing = new HashMap<Character, T>();
        }
        
        @Override
        public Object clone() {
                CharHashMap<T> n = new CharHashMap<T>(constants.getClass().getComponentType(),NULL);
                System.arraycopy(constants,0, n.constants,0,CACHE_SIZE);
                n.backing.putAll(backing);
                return n;
        }
        
        public T get(char key) {
                if (key<CACHE_SIZE) return constants[key];
                T value = backing.get(key);
                return (value==null) ? NULL:value;
        }

        public void clear() {
                Arrays.fill(constants,NULL);
                backing.clear();
        }

        public T put(char key, T value) {
                if (key<CACHE_SIZE) {
                        T old = constants[key];
                        constants[key] = value;
                        return old;
                }
                else return backing.put(key, value);
        }

        public Iterator<Character> getCharIterator() {
                return new Iterator<Character>() {                      
                        final Iterator<Character> carIt =  backing.keySet().iterator();
                        int charNum = -1;
                        @Override
                        public boolean hasNext() {
                                if ( charNum<CACHE_SIZE) return true;
                                return carIt.hasNext();
                        }
                        @Override
                        public Character next() {
                                if ( charNum<CACHE_SIZE) return (char)++charNum;
                                return carIt.next();
                        }
                        @Override
                        public void remove() {
                                throw new UnsupportedOperationException();                      
                        }
                        
                };
        }

}
