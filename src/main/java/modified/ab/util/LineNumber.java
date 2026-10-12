/*
 * Masahito Hemmi
 *
 *   This file is licensed under the same terms as "Armed Bear Common Lisp (ABCL)".

Armed Bear Common Lisp is distributed under the GNU General Public
License with a classpath exception (see "Classpath Exception" below).

A copy of GNU General Public License (GPLv2) is included in this
distribution, in <file:COPYING>.

We have modified our GPLv2 license section 13 to read:

     13. Linking this library statically or dynamically with other
     modules is making a combined work based on this library. Thus, the
     terms and conditions of the GNU General Public License cover the
     whole combination.

     The following paragraph details the "classpath exception" which ABCL
     allows as an exception to the statement about linking libraries.

     As a special exception, the copyright holders of this software give
     you permission to link this software with independent modules to
     produce an executable, regardless of the license terms of these
     independent modules, and to copy and distribute the resulting
     executable under terms of your choice, provided that you also meet,
     for each linked independent module, the terms and conditions of the
     license of that module. An independent module is a module which is
     not derived from or based on this software. If you modify this
     software, you may extend this exception to your version of the
     software, but you are not obligated to do so. If you do not wish to
     do so, delete this exception statement from your version.

 */
package modified.ab.util;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import modified.ab.commonlisp.ABSymbol;

public final class LineNumber {

    final HashMap<Object, Object> prop = new HashMap<>();

    private LineNumber() {
    }

    public LineNumber(Object line, Object column, Object endLine, Object endColumn) {
        prop.put(ABSymbol.LINE, line);
        prop.put(ABSymbol.COLUMN, column);
        prop.put(ABSymbol.END_LINE, endLine);
        prop.put(ABSymbol.END_COLUMN, endColumn);
    }

    // ^:tag XXX,   ^Integer
    public LineNumber(Object key, Object val) {

        prop.put(key, val);
    }

    public LineNumber(LineNumber original, int line, int column) {

        prop.putAll(original.prop);

        prop.put(ABSymbol.LINE, line);
        prop.put(ABSymbol.COLUMN, column);
    }

    public LineNumber append(Object key, Object val) {
        LineNumber newInst = new LineNumber();
        newInst.prop.putAll(this.prop);
        newInst.prop.put(key, val);
        return newInst;

    }

    public Object get(ABSymbol key) {
        return prop.get(key);
    }

    public String getStringForPrint() {

        return prop.toString();
    }

    public Map<Object, Object> getUnm() {
        return Collections.unmodifiableMap(prop);
    }

}
