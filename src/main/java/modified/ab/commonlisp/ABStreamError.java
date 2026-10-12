/*
 * StreamError.java
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

/**
  RENAME StreamError to ABStreamError


 @author MasahitoHemmi
 */
public class ABStreamError extends ABLispError {

    protected Stream stream;

    private Throwable cause;

    // add
    public ABStreamError() {

    }

    public ABStreamError(String message) {
        //      super(StandardClass.STREAM_ERROR);
        //      setFormatControl(message.replaceAll("~","~~"));
        //      setFormatArguments(NIL);
        //      setStream(NIL);
        cause = null;
    }

    public ABStreamError(Stream stream) {
        //      super(StandardClass.STREAM_ERROR);
        //      setStream(stream != null ? stream : NIL);
        cause = null;
    }

    public ABStreamError(String message, Stream stream) {
        //     super(StandardClass.STREAM_ERROR);
        //     setFormatControl(message.replaceAll("~","~~"));
        //     setFormatArguments(NIL);
        //     setStream(stream != null ? stream : NIL);
        cause = null;
    }

    public ABStreamError(LispObject initArgs) {
        //      super(StandardClass.STREAM_ERROR);
        //       initialize(initArgs);
        cause = null;
    }

    public ABStreamError(Stream stream, String message) {
//        super(StandardClass.STREAM_ERROR);
//        setFormatControl(message.replaceAll("~","~~"));
//        setFormatArguments(NIL);
//        setStream(stream != null ? stream : NIL);
        cause = null;
    }

    public ABStreamError(Stream stream, Throwable cause) {
        //      super(StandardClass.STREAM_ERROR);
        //       setStream(stream != null ? stream : NIL);
        String message = cause.getMessage();
        //       setFormatControl(message != null ? message.replaceAll("~","~~") : cause.toString().replaceAll("~","~~"));
        //      setFormatArguments(NIL);
        this.cause = cause;
    }

    protected void setStream(Stream st) {

    }

}
