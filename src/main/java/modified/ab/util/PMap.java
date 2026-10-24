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


public final class PMap {

    private final LineNumber attr;
    private final Object obj;
    
    public PMap(LineNumber meta, Object first) {

        this.attr = meta;
        this.obj = first;
    }

    public LineNumber meta() {
        return attr;
    }

    public Object getInnerObject() {
        return obj;
    }

}
