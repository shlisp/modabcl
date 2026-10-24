/*
 * Stream.java
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


import io.github.shlisp.objects.Readtable;
import io.github.shlisp.objects.IPushbackReader;

import static modified.ab.commonlisp.Lisp.ierror;
import static modified.ab.commonlisp.Lisp.serror;
import static modified.ab.commonlisp.Lisp.NIL;
import static modified.ab.commonlisp.Lisp.PACKAGE_KEYWORD;
import static modified.ab.commonlisp.Lisp.error;
import static modified.ab.commonlisp.Lisp.eval;

import modified.ab.util.TinyLispString;
import modified.ab.util.LineNumber;
import modified.ab.util.ILocation;
import modified.ab.util.ABConsUtil;


import java.io.IOException;
import java.math.BigInteger;
import java.util.BitSet;

/**
 *
 */
public class Stream extends LispObject {

    protected boolean isInputStream;
    protected boolean isOutputStream;
    protected boolean isCharacterStream;
    protected boolean isBinaryStream;
    
    // 読まないが、書き込みはしている
    private boolean pastEnd = false;
    private boolean interactive;
 //   private boolean open = true;
    
    // Character input.
    protected  final  IPushbackReader reader;
    protected int offset;
    protected int lineNumber;
    
    
    
//    protected char eolChar = (eolStyle == EolStyle.CR) ? '\r' : '\n';
    protected char eolChar =  '\n';
    
    
    public enum EolStyle {
        RAW,
        CR,
        CRLF,
        LF
    }
    
//    public final static EolStyle platformEolStyle = Utilities.isPlatformWindows ? EolStyle.CRLF : EolStyle.LF;
//    public final static EolStyle platformEolStyle = EolStyle.LF;
    public final static EolStyle platformEolStyle = EolStyle.CRLF;

    protected EolStyle eolStyle = platformEolStyle;
    
    
    public static int READ_BASE = 10;
    public static ABSymbol READ_SUPPRESS = ABNil.NIL;
    
    
    
    final ABPackage currentPackage;
    
    ABPackage getCurrentPackage() {
        return currentPackage;
    }
    
    
    static LispObject READ_DEFAULT_FLOAT_FORMAT = new LispObject();
    
    public int backquoteCount = 0;
    
   // _BACKQUOTE_COUNT_
    
    
    public Stream(ABSymbol structureClass, IPushbackReader r, ABPackage current) {
//        this(structureClass);
        this.reader = r;
        this.currentPackage = current;
//        initAsCharacterInputStream(r);
        isInputStream = true;
        isCharacterStream = true;
    }
/*    
    private void initAsCharacterInputStream(IPushbackReader  reader) {
        
        isInputStream = true;
        isCharacterStream = true;
    }
    */
    
    //----------------------------------------
    
    public boolean isInputStream() {
        return isInputStream;
    }

    public boolean isOutputStream() {
        return isOutputStream;
    }

    public boolean isCharacterInputStream() {
        return isCharacterStream && isInputStream;
    }

    public boolean isBinaryInputStream() {
        return isBinaryStream && isInputStream;
    }

    public boolean isCharacterOutputStream() {
        return isCharacterStream && isOutputStream;
    }

    public boolean isBinaryOutputStream() {
        return isBinaryStream && isOutputStream;
    }

    public boolean isInteractive() {
        return interactive;
    }

    public void setInteractive(boolean b) {
        interactive = b;
    }
    
    //----------------------------------------
    
    public Object readComplex(Object arg) {
        
        final LispThread thread = LispThread.currentThread();
        Object obj = read(true, NIL, true, thread /*, rta*/);
        /*
        if (Symbol.READ_SUPPRESS.symbolValue(thread) != NIL)
            return NIL;
        */
        if (obj instanceof ABCons cons) {
            int len = ABConsUtil.length(cons);
            if (len == 2) {
                return ABComplex.getInstance(cons.getCar(), cons.getCadr());
            }
        }
        // Error.
        String sb = "Invalid complex number format";
        /*
        if (this instanceof FileStream) {
            Pathname p = ((FileStream)this).getPathname();
            if (p != null) {
                String namestring = p.getNamestring();
                if (namestring != null) {
                    sb.append(" in #P\"");
                    sb.append(namestring);
                    sb.append('"');
                }
            }
            sb.append(" at offset ");
            sb.append(_getFilePosition());
        }
        sb.append(": #C");
        sb.append(obj.printObject());*/
        return error(new ReaderError(sb, this));
    }
    
    public int _readChar() throws IOException {
        if (reader == null) {
//            streamNotCharacterInputStream();
            throw new RuntimeException();
        }
        int n = reader.read();

        if (n < 0) {
            pastEnd = true;
            return -1;
        }

        ++offset;
        if (n == '\r' && eolStyle == EolStyle.CRLF) {
            n = _readChar();
            if (n != '\n') {
                _unreadChar(n);
                return '\r';
            } else
                return '\n';
        }

        if (n == eolChar) {
            ++lineNumber;
            return '\n';
        }

        return n;
        
    }

    /*
     * Class to abstract readtable access
     *
     * Many of the functions below (used to) exist in 2 variants. One with
     * hardcoded access to the FaslReadtable, the other with hardcoded access to
     * the *readtable* variable.
     *
     * In order to prevent code duplication, this class abstracts access.
    public static abstract class ReadtableAccessor {

        public abstract Readtable rt(LispThread thread);
    }
     */

    public static ReadtableAccessor currentReadtable
            = new ReadtableAccessor() {
        @Override
        public Readtable rt(LispThread thread) {
            
//            return (Readtable) Symbol.CURRENT_READTABLE.symbolValue(thread);

            return thread.getReadTable();
        }
    };
    
    public Object read(boolean eofError,   Object eofValue,
                           boolean recursive, LispThread thread)
    {
    
         ReadtableAccessor rta = Stream.currentReadtable;
        Object obj = read(  eofError,     eofValue,   recursive,   thread     , rta           );
        return obj;
    }    
    
    // Line 497
    
    Object read(boolean eofError,   Object eofValue,
                           boolean recursive, LispThread thread,
                           ReadtableAccessor rta)
    {
        Object result = readPreservingWhitespace(eofError, eofValue,
                                                     recursive, thread, rta);
        if (result != eofValue && !recursive) {
            try {
                if (_charReady()) {
                    int n = _readChar();
                    if (n >= 0) {
                        char c = (char) n; // ### BUG: Codepoint conversion
                        AbstractReadtable rt = rta.rt(thread);
                        if (!rt.isWhitespace(c))
                            _unreadChar(c);
                    }
                }
            } catch (IOException e) {
                return error(new ABStreamError(this, e));
            }
        }
        if (!eofError && result == eofValue) return result;
        if (READ_SUPPRESS != NIL)
            return NIL;
        else
            return result;
    }
    
    // Line 532
    
    public Object readPreservingWhitespace(boolean eofError,
                                              Object eofValue,
                                               boolean recursive,
                                               LispThread thread,
                                               ReadtableAccessor rta)

    {
        if (recursive) {
            final AbstractReadtable rt = rta.rt(thread);
            while (true) {
                int n = -1;
                try {
                    n = _readChar();
                } catch (IOException e) {
                    Debug.trace(e);
                    error(new ABStreamError(this, e));
                }
                if (n < 0) {
                    if (eofError)
                        return error(new EndOfFile(this));
                    else
                        return eofValue;
                }
                char c = (char) n; // ### BUG: Codepoint conversion
                if (rt.isWhitespace(c))
                    continue;
                Object result = processChar(thread, c, rt);
                if (result != null)
                    return result;
            }
        } else {
            
            final SpecialBindingsMark mark = thread.markSpecialBindings();
  //          thread.bindSpecial(_SHARP_EQUAL_ALIST_, NIL);
  //          thread.bindSpecial(_SHARP_SHARP_ALIST_, NIL);
            try {
                return readPreservingWhitespace(eofError, eofValue, true,
                                                thread, rta);
            } finally {
                thread.resetSpecialBindings(mark);
            }
        }
    }
    
    /** Dispatch macro function if 'c' has one associated,
     * read a token otherwise.
     *
     * When the macro function returns zero values, this function
     * returns null or the token or returned value otherwise.
     */
    private  Object processChar(LispThread thread,
                                         char c, AbstractReadtable rt)
    {
        final LispObject handler = rt.getReaderMacroFunction(c);
        Object value;

        if (handler instanceof ReaderMacroFunction) {
            thread._values = null;
            value = ((ReaderMacroFunction)handler).execute(this, c);
        }
        else if (handler != null && handler != NIL) {
            thread._values = null;
            value = handler.execute(this, ABLispCharacter.getInstance(c));
        }
        else {
            int line = this.reader.getLineNumber();
            int col = this.reader.getColumnNumber();
            
            Object symOrNum = readToken(c, rt);
            if (symOrNum instanceof ILocation loc) {

                int line2 = this.reader.getLineNumber();
                int col2 = this.reader.getColumnNumber();
                
                LineNumber meta = new LineNumber(line, col -1, line2, col2);

                return    loc.withMeta(meta);
            }

            return symOrNum;
        }
        // If we're looking at zero return values, set 'value' to null
        if (value == NIL) {
            LispObject[] values = thread._values;
            if (values != null && values.length == 0) {
                value = null;
                thread._values = null; // reset 'no values' indicator
            }
        }
        return value;
    }
    
    
    public  Object readString(char terminator, ReadtableAccessor rta)
    {
      final LispThread thread = LispThread.currentThread();
      final AbstractReadtable rt = rta.rt(thread);
      StringBuilder sb = new StringBuilder();
      try
      {
        while (true) {
          int n = _readChar();
          if (n < 0)
            return error(new EndOfFile(this));

          char c = (char) n; // ### BUG: Codepoint conversion
          if (rt.getSyntaxType(c) == Readtable.SYNTAX_TYPE_SINGLE_ESCAPE) {
            // Single escape.
            n = _readChar();
            if (n < 0)
              return error(new EndOfFile(this));

            sb.append((char)n); // ### BUG: Codepoint conversion
            continue;
          }
          if (c == terminator)
            break;
          // Default.
          sb.append(c);
        }
      }
      catch (java.io.IOException e)
      {
        error(new EndOfFile(this));
   //     return new SimpleString(sb);
      return new TinyLispString(sb.toString());
      }
//      return new SimpleString(sb);
      return new TinyLispString(sb.toString());
    }
    
    public Object readList(boolean requireProperList,
                               ReadtableAccessor rta,   char readTo)
    {
        final LispThread thread = LispThread.currentThread();
        ABCons first = null;
        ABCons last = null;
        AbstractReadtable      rt;
        try {
            while (true) {
                rt = rta.rt(thread);
                char c = flushWhitespace(rt);

                if (c == readTo) {
                    return first == null ? NIL : first;
                }
                if (c == '.') {
                    int n = _readChar();
                    if (n < 0)
                        return error(new EndOfFile(this));
                    char nextChar = (char) n; // ### BUG: Codepoint conversion
                    if (isTokenDelimiter(nextChar, rt)) {
                        if (last == null) {
                            if (READ_SUPPRESS != NIL)
                                return NIL;
                            else
                                return error(new ReaderError("Nothing appears before . in list.",
                                                             this));
                        }
                        _unreadChar(nextChar);
                        Object obj = read(true, NIL, true, thread, rta);
                        if (requireProperList) {
                            if (!listp(obj))
                                error(new ReaderError("The value " +
                                                      princToString(obj) +
                                                      " is not of type " +
                                                      princToString(ABSymbol.LIST) + ".",
                                                      this));
                        }
                        last.cdr = obj;
                        continue;
                    }
                    // normal token beginning with '.'
                    _unreadChar(nextChar);
                }

                Object obj = processChar(thread, c, rt);
                if (obj == null)
                    continue;


                if (first == null) {
                    first = new ABCons(obj);
                    last = first;
                } else {
                    ABCons newCons = new ABCons(obj);
                    last.cdr = newCons;
                    last = newCons;
                }
            }
        } catch (IOException e) {
            error(new ABStreamError(this, e));
            return null;
        }
    }
    // #(3 -5) => Vector
    public Object readSharpLeftParen(char c, int n, 
                                         ReadtableAccessor rta) 
    {
        
//        final LispThread thread = LispThread.currentThread();
        Object list = readList(true, rta, ')');
        return new SimpleVector(list);
    }
    
    public Object readSharpDot(char c, int n, 
                                   ReadtableAccessor rta) 
    {
        final LispThread thread = LispThread.currentThread();
        if (ABSymbol.READ_EVAL.symbolValue(thread) == NIL)
            return error(new ReaderError("Can't read #. when *READ-EVAL* is NIL.",
                                         this));
        else
            return eval(read(true, NIL, true, thread, rta),
                        new Environment(), thread);
    }
    
    public LispObject readSharpStar(char ignored, int n, 
                                    ReadtableAccessor rta) 
    {
        /*
        final LispThread thread = LispThread.currentThread();
        final Readtable rt = rta.rt(thread);

        final boolean suppress =
            (Symbol.READ_SUPPRESS.symbolValue(thread) != NIL);
        StringBuilder sb = new StringBuilder();
        try 
            {
                while (true) {
                    int ch = _readChar();
                    if (ch < 0)
                        break;
                    char c = (char) ch;
                    if (c == '0' || c == '1')
                        sb.append(c);
                    else {
                        int syntaxType = rt.getSyntaxType(c);
                        if (syntaxType == Readtable.SYNTAX_TYPE_WHITESPACE ||
                            syntaxType == Readtable.SYNTAX_TYPE_TERMINATING_MACRO) {
                            _unreadChar(c);
                            break;
                        } else if (!suppress) {
                            String name = ABLispCharacter.charToName(c);
                            if (name == null)
                                name = "#\\" + c;
                            error(new ReaderError("Illegal element for bit-vector: " + name,
                                                  this));
                        }
                    }
                }
            }
        catch (java.io.IOException e)
            {
                error(new ReaderError("IO error: ",
                                      this));
                return NIL;
            }
        
        if (suppress)
            return NIL;
        if (n >= 0) {
            // n was supplied.
            final int length = sb.length();
            if (length == 0) {
                if (n > 0)
                    return error(new ReaderError("No element specified for bit vector of length " +
                                                 n + '.',
                                                 this));
            }
            if (n > length) {
                final char c = sb.charAt(length - 1);
                for (int i = length; i < n; i++)
                    sb.append(c);
            } else if (n < length) {
                return error(new ReaderError("Bit vector is longer than specified length: #" +
                                             n + '*' + sb.toString(),
                                             this));
            }
        }
        return new SimpleBitVector(sb.toString());
        */
        throw new RuntimeException();
    }
    // #2a(...) と書いた時に、 rank=2
    public LispObject readArray(int rank, ReadtableAccessor rta) {
        
        final LispThread thread = LispThread.currentThread();
        Object obj = read(true, NIL, true, thread, rta);
        if (READ_SUPPRESS != NIL)
            return NIL;
        switch (rank) {
        case -1:
            return error(new ReaderError("No dimensions argument to #A.", this));
/*
        case 0:
            return new ZeroRankArray(T, obj, false);
        case 1: {
            if (obj.listp() || obj instanceof AbstractVector)
                return new SimpleVector(obj);
            return error(new ReaderError(obj.princToString() + " is not a sequence.",
                                         this));
        }*/
        // 2026-10-24 #0A は今は対応しない
        case 0:
            return error(new ReaderError("#0A (zero-dimensional array) is not supported.", this));
        default:
            return new SimpleArray_T(rank, obj);
        }
        
    }
    public Object readPathname(ReadtableAccessor rta) {
        
        Object obj = read(true, NIL, false,
                              LispThread.currentThread(), rta);
        if (obj instanceof TinyLispString str) {
            return new ABPathname(str.toJavaString());
        }
        /*
        if (obj.listp())
            return Pathname.makePathname(obj);
        return error(new TypeError("#p requires a string argument."));
        */

        throw new ParserException("#p requires a string argument.");
    }

    public LispObject readStructure(ReadtableAccessor rta) {
        /*
        final LispThread thread = LispThread.currentThread();
        LispObject obj = read(true, NIL, true, thread, rta);
        if (Symbol.READ_SUPPRESS.symbolValue(thread) != NIL)
            return NIL;
        if (obj.listp()) {
            Symbol structure = checkSymbol(obj.car());
            LispClass c = LispClass.findClass(structure);
            if (!(c instanceof StructureClass))
                return error(new ReaderError(structure.getName() +
                                             " is not a defined structure type.",
                                             this));
            LispObject args = obj.cdr();
            Symbol DEFSTRUCT_DEFAULT_CONSTRUCTOR =
                PACKAGE_SYS.intern("DEFSTRUCT-DEFAULT-CONSTRUCTOR");
            LispObject constructor =
                DEFSTRUCT_DEFAULT_CONSTRUCTOR.getSymbolFunctionOrDie().execute(structure);
            final int length = args.length();
            if ((length % 2) != 0)
                return error(new ReaderError("Odd number of keyword arguments following #S: " +
                                             obj.princToString(),
                                             this));
            LispObject[] array = new LispObject[length];
            LispObject rest = args;
            for (int i = 0; i < length; i += 2) {
                LispObject key = rest.car();
                if (key instanceof Symbol && ((Symbol)key).getPackage() == PACKAGE_KEYWORD) {
                    array[i] = key;
                } else {
                    array[i] = PACKAGE_KEYWORD.intern(javaString(key));
                }
                array[i + 1] = rest.cadr();
                rest = rest.cddr();
            }
            return funcall(constructor.getSymbolFunctionOrDie(), array,
                           thread);
        }
        return error(new ReaderError("Non-list following #S: " +
                                     obj.princToString(),
                                     this));
        */
        throw new ParserException("not impl yet");
    }
    
    public LispObject readCharacterLiteral(Readtable rt, LispThread ___thread)

    {
        try {
            int n = _readChar();
            if (n < 0)
                return error(new EndOfFile(this));
            char c = (char) n; // ### BUG: Codepoint conversion
            StringBuilder sb = new StringBuilder(String.valueOf(c));
            while (true) {
                n = _readChar();
                if (n < 0)
                    break;
                c = (char) n; // ### BUG: Codepoint conversion
                if (rt.isWhitespace(c))
                    break;
                if (rt.getSyntaxType(c) == 
                    Readtable.SYNTAX_TYPE_TERMINATING_MACRO) {
                    _unreadChar(c);
                    break;
                }
                sb.append(c);
            }
            if (READ_SUPPRESS != NIL)
                return NIL;
            if (sb.length() == 1)
                return ABLispCharacter.getInstance(sb.charAt(0));
            String token = sb.toString();
            // 以下のメソッドで、 #u3042 とかを読む。戻り値は int
            n = ABLispCharacter.nameToChar(token);
            if (n >= 0) {
//                return ABLispCharacter.getInstance((char)n); // ### BUG: Codepoint conversion
                return ABLispCharacter.valOf(n);
            }
            return error(new ABLispError("Unrecognized character name: \"" + token + '"'));
        } catch (IOException e) {
            return error(new ABStreamError(this, e));
        }
    }
    
    public void skipBalancedComment() {
        try {
            while (true) {
                int n = _readChar();
                if (n < 0)
                    return;
                if (n == '|') {
                    n = _readChar();
                    if (n == '#')
                        return;
                    else
                        _unreadChar(n);
                } else if (n == '#') {
                    n = _readChar();
                    if (n == '|')
                        skipBalancedComment(); // Nested comment. Recurse!
                    else
                        _unreadChar(n);
                }
            }
        } catch (IOException e) {
            error(new ABStreamError(this, e));
        }
    }
    
    
    private static  boolean isTokenDelimiter(char c, AbstractReadtable rt)
    {
        byte type = rt.getSyntaxType(c);

        return type == Readtable.SYNTAX_TYPE_TERMINATING_MACRO ||
                type == Readtable.SYNTAX_TYPE_WHITESPACE;
    }
    
    private char flushWhitespace(AbstractReadtable rt) {
        try {
            while (true) {
                int n = _readChar();
                if (n < 0)
                    return (char)ierror(new EndOfFile(this));

                char c = (char) n; // ### BUG: Codepoint conversion
                if (!rt.isWhitespace(c))
                    return c;
            }
        } catch (IOException e) {
            error(new ABStreamError(this, e));
            return 0;
        }
    }
    
    
    private Object readToken(char c, AbstractReadtable rt)

    {
        StringBuilder sb = new StringBuilder(String.valueOf(c));
        final LispThread thread = LispThread.currentThread();
        BitSet flags = _readToken(sb, rt);
        if (READ_SUPPRESS != NIL)
            return NIL;
        final LispObject readtableCase = rt.getReadtableCase();
        final String token =  sb.toString();                      // この時点で読み込んだトークンが完成している 例) "nil"
        final boolean invert = readtableCase == ABKeyword.INVERT;
        final int length = token.length();
        if (length > 0) {
            final char firstChar = token.charAt(0);
            if (flags == null) {
                if (firstChar == '.') {
                    // Section 2.3.3: "If a token consists solely of dots (with
                    // no escape characters), then an error of type READER-
                    // ERROR is signaled, except in one circumstance: if the
                    // token is a single dot and appears in a situation where
                    // dotted pair notation permits a dot, then it is accepted
                    // as part of such syntax and no error is signaled."
                    boolean ok = false;
                    for (int i = length; i-- > 1;) {
                        if (token.charAt(i) != '.') {
                            ok = true;
                            break;
                        }
                    }
                    if (!ok) {
                        final String message;
                        if (length > 1)
                            message = "Too many dots.";
                        else
                            message = "Dot context error.";
                        return error(new ReaderError(message, this));
                    }
                }
                final int radix = getReadBase(thread);
                if ("+-.0123456789".indexOf(firstChar) >= 0) {
                    Object number = makeNumber(token, length, radix);
                    if (number != null)
                        return number;
                } else if (Character.digit(firstChar, radix) >= 0) {
                    Object number = makeNumber(token, length, radix);
                    if (number != null)
                        return number;
                }
            }
            
            String symbolName;
            String packageName = null;
            BitSet symbolFlags;
            BitSet packageFlags = null;
            ABPackage pkg = null;
            boolean internSymbol = true;
            if (firstChar == ':' && (flags == null || !flags.get(0))) {
                    symbolName = token.substring(1);
                    pkg = PACKAGE_KEYWORD;
                    if (flags != null)
                        symbolFlags = flags.get(1, flags.size());
                    else
                        symbolFlags = null;
            } else {
                int index = findUnescapedDoubleColon(token, flags);
                if (index > 0) {
                    /*
                    packageName = token.substring(0, index);
                    packageFlags = (flags != null) ? flags.get(0, index) :  null;
                    symbolName = token.substring(index + 2);
                    symbolFlags = (flags != null) ? flags.get(index+2, flags.size()) : null;
                    */
                    
                    return error(new ReaderError("This parser does not support ::", this));
                    
                } else {
                    index = findUnescapedSingleColon(token, flags);
                    if (index > 0) {
                        packageName = token.substring(0, index);
                        packageFlags = (flags != null) ? flags.get(0, index) : null;
                        symbolName = token.substring(index + 1);
                        symbolFlags = (flags != null) ? flags.get(index+2, flags.size()) : null;
  //                      internSymbol = false;
                    } else {
//                        pkg = (Package)Symbol._PACKAGE_.symbolValue(thread);  // 現在のパッケージ。例） "CL-USER"
                        pkg = getCurrentPackage();
                        symbolName = token;
                        symbolFlags = flags;
                    }
                }
            }
            if (pkg == null) {
                if (invert)
                    packageName = invert(packageName, packageFlags);

//                pkg = getCurrentPackage().findPackage(packageName);
                pkg = Packages.findOrCreatePackage(packageName);
                if (pkg == null)
                    return error(new ReaderError("The package \"" + packageName + "\" can't be found.", this));
            }
            if (invert)
                symbolName = invert(symbolName, symbolFlags);
            
            if (internSymbol) {
                return pkg.intern(symbolName);
            } else {
                ABSymbol symbol = pkg.findExternalSymbol(symbolName);
                if (symbol != null)
                    return symbol;

                // Error!
                if (pkg.findInternalSymbol(symbolName) != null) {
                    return error(new ReaderError("The symbol \"~A\" is not external in package ~A.",
                                                 this,
                                                 new SimpleString(symbolName),
                                                 new SimpleString(packageName)));
                } else {
                  if (packageName.equals("KEYWORD")) {
                    return pkg.intern(symbolName);
                  }
                  else {
                    return error(new ReaderError("The symbol \"~A\" was not found in package ~A.",
                                                 this,
                                                 new SimpleString(symbolName),
                                                 new SimpleString(packageName)));
                  }
                }
            }
        } else {                // token.length == 0
//            Package pkg = (Package)Symbol._PACKAGE_.symbolValue(thread);
            ABPackage pkg = getCurrentPackage();
            return pkg.intern("");
        }
    }
    
    // Line 1063
    private static  int findUnescapedSingleColon(String s, BitSet flags) {
        if (flags == null)
            return s.indexOf(':');
        final int limit = s.length();
        for (int i = 0; i < limit; i++) {
            if (s.charAt(i) == ':' && !flags.get(i)) {
                return i;
            }
        }
        return -1;
    }
    
    
    // Line 1076
    private static  int findUnescapedDoubleColon(String s, BitSet flags) {
        if (flags == null)
            return s.indexOf("::");
        final int limit = s.length() - 1;
        for (int i = 0; i < limit; i++) {
            if (s.charAt(i) == ':' && !flags.get(i)) {
                if (s.charAt(i + 1) == ':' && !flags.get(i + 1)) {
                    return i;
                }
            }
        }
        return -1;
    }
    
    
    // Line 1342
    private static  int getReadBase(LispThread thread)

    {
/*        final int readBase;
        final LispObject readBaseObject = Symbol.READ_BASE.symbolValue(thread);
        if (readBaseObject instanceof Fixnum) {
            readBase = ((Fixnum)readBaseObject).value;
        } else
            // The value of *READ-BASE* is not a Fixnum.
            return ierror(new LispError("The value of *READ-BASE* is not " +
                                        "of type '(INTEGER 2 36)."));
*/
        int readBase = READ_BASE;
        
        if (readBase < 2 || readBase > 36)
            return ierror(new ABLispError("The value of *READ-BASE* is not " +
                                        "of type '(INTEGER 2 36)."));

        return readBase;
    }
    
    // Line 1361
    private   Object makeNumber(String token, int length, int radix)
    {
        if (length == 0)
            return null;
        if (token.indexOf('/') >= 0)
            return makeRatio(token, radix);
        if (token.charAt(length - 1) == '.') {
            radix = 10;
            token = token.substring(0, --length);
        }
        boolean numeric = true;
        if (radix == 10) {
            for (int i = length; i-- > 0;) {
                char c = token.charAt(i);
                if (c < '0' || c > '9') {
                    if (i > 0 || (c != '-' && c != '+')) {
                        numeric = false;
                        break;
                    }
                }
            }
        } else {
            for (int i = length; i-- > 0;) {
                char c = token.charAt(i);
                if (Character.digit(c, radix) < 0) {
                    if (i > 0 || (c != '-' && c != '+')) {
                        numeric = false;
                        break;
                    }
                }
            }
        }
        if (!numeric) // Can't be an integer.
            return makeFloat(token, length);
        if (token.charAt(0) == '+')
            token = token.substring(1);
        try {
            /*
            int n = Integer.parseInt(token, radix);
//            return (n >= 0 && n <= 255) ? Fixnum.constants[n] : Fixnum.getInstance(n);
            return Fixnum.getInstance(n);
*/
            
            long ln = Long.parseLong(token, radix);

            return Fixnum.getLongOrInt(ln);
            
        } catch (NumberFormatException e) {}
        // parseInt() failed.
        try {
            return Bignum.getInstance(token, radix);
        } catch (NumberFormatException e) {}
        // Not a number.
        return null;
    }

    // Line 1431
    private static  LispObject makeFloat(final String token,
            final int length)
    {
        if (length == 0)
            return null;
        StringBuilder sb = new StringBuilder();
        int i = 0;
        boolean maybe = false;
        char marker = 0;
        char c = token.charAt(i);
        if (c == '-' || c == '+') {
            sb.append(c);
            ++i;
        }
        while (i < length) {
            c = token.charAt(i);
            if (c == '.' || (c >= '0' && c <= '9')) {
                if (c == '.')
                    maybe = true;
                sb.append(c);
                ++i;
            } else
                break;
        }
        if (i < length) {
            c = token.charAt(i);
            if ("esfdlESFDL".indexOf(c) >= 0) {
                // Exponent marker.
                maybe = true;
                marker = ABLispCharacter.toUpperCase(c);
                if (marker == 'S')
                    marker = 'F';
                else if (marker == 'L')
                    marker = 'D';
                else if (marker == 'E') {
                    LispObject format = READ_DEFAULT_FLOAT_FORMAT;
                    if (format == ABSymbol.SINGLE_FLOAT || format == ABSymbol.SHORT_FLOAT)
                        marker = 'F';
                    else
                        marker = 'D';
                }
                sb.append('E');
                ++i;
            }
        }
        if (!maybe)
            return null;
        // Append rest of token.
        sb.append(token.substring(i));
        c = sb.charAt(sb.length()-1);
        if (! ('0' <= c && c <= '9'))
            // we need to check that the last item is a number:
            // the Double.parseDouble routine accepts numbers ending in 'D'
            // like 1e2d. The same is true for Float.parseFloat and the 'F'
            // character. However, these are not valid Lisp floats.
            return null;
        try {
            if (marker == 0) {
                LispObject format = READ_DEFAULT_FLOAT_FORMAT;
                if (format == ABSymbol.SINGLE_FLOAT || format == ABSymbol.SHORT_FLOAT)
                    marker = 'F';
                else
                    marker = 'D';
            }
            if (marker == 'D')
                return new DoubleFloat(Double.parseDouble(sb.toString()));
            else
                return new SingleFloat(Float.parseFloat(sb.toString()));
        } catch (NumberFormatException e) {
            return null;
        }
    }

    
    // Line 1847
    /** Returns a boolean indicating input readily available
     *
     * @return true if a character is available
     * @throws java.io.IOException
     */
    protected boolean _charReady() throws IOException {
        if (reader == null) {
//            streamNotCharacterInputStream();
            throw new RuntimeException();
        }
        return reader.ready();
    }
    
    // Line 620
    
    public LispObject readSymbol() {
        
        LispThread th = LispThread.currentThread();
        Readtable rt =  th.getReadTable();

        return readSymbol(rt);
    }

    public LispObject readSymbol(Readtable rt) {
        final StringBuilder sb = new StringBuilder();
        final BitSet flags = _readToken(sb, rt);
        return new ABSymbol(rt.getReadtableCase() == ABKeyword.INVERT
                          ? invert(sb.toString(), flags)
                          : sb.toString());
    }

    public static final String invert(String s, BitSet flags) {
        // Section 23.1.2: "When the readtable case is :INVERT, then if all of
        // the unescaped letters in the extended token are of the same case,
        // those (unescaped) letters are converted to the opposite case."
        final int limit = s.length();
        final int LOWER = 1;
        final int UPPER = 2;
        int state = 0;
        for (int i = 0; i < limit; i++) {
            // We only care about unescaped characters.
            if (flags != null && flags.get(i))
                continue;
            char c = s.charAt(i);
            if (Character.isUpperCase(c)) {
                if (state == LOWER)
                    return s; // Mixed case.
                state = UPPER;
            }
            if (Character.isLowerCase(c)) {
                if (state == UPPER)
                    return s; // Mixed case.
                state = LOWER;
            }
        }
        StringBuilder sb = new StringBuilder(limit);
        for (int i = 0; i < limit; i++) {
            char c = s.charAt(i);
            if (flags != null && flags.get(i)) // Escaped.
                sb.append(c);
            else if (Character.isUpperCase(c))
                sb.append(Character.toLowerCase(c));
            else if (Character.isLowerCase(c))
                sb.append(Character.toUpperCase(c));
            else
                sb.append(c);
        }
        return sb.toString();
    }

    // Line 786
    // #2A(...)
    public Object readDispatchChar(char dispChar,
                                       ReadtableAccessor rta)
    {
        int numArg = -1;
        char c = 0;
        try {
            while (true) {
                int n = _readChar();
                if (n < 0)
                    return error(new EndOfFile(this));
                c = (char) n; // ### BUG: Codepoint conversion
                if (c < '0' || c > '9')
                    break;
                if (numArg < 0)
                    numArg = 0;
                numArg = numArg * 10 + c - '0';
            }
        } catch (IOException e) {
            error(new ABStreamError(this, e));
        }

        
        final LispThread thread = LispThread.currentThread();
        final AbstractReadtable rt = rta.rt(thread);
        LispObject fun = rt.getDispatchMacroCharacter(dispChar, c);


        if (fun != NIL) {
            LispObject result;

            thread._values = null;
            if (fun instanceof DispatchMacroFunction)
                return ((DispatchMacroFunction)fun).execute(this, c, numArg);
            else
                return
                    thread.execute(fun, this, ABLispCharacter.getInstance(c),
                       (numArg < 0) ? NIL : Fixnum.getInstance(numArg));
        }

        if (READ_SUPPRESS != NIL)
            return null;

        return error(new ReaderError("No dispatch function defined for #\\" + c,
                                     this));
    }
    
    // Line 1036
    
    private String readMultipleEscape(AbstractReadtable rt) {
        StringBuilder sb = new StringBuilder();
        try {
            while (true) {
                int n = _readChar();
                if (n < 0)
                    return serror(new EndOfFile(this));

                char c = (char) n; // ### BUG: Codepoint conversion
                byte syntaxType = rt.getSyntaxType(c);
                if (syntaxType == Readtable.SYNTAX_TYPE_SINGLE_ESCAPE) {
                    n = _readChar();
                    if (n < 0)
                        return serror(new EndOfFile(this));

                    sb.append((char)n); // ### BUG: Codepoint conversion
                    continue;
                }
                if (syntaxType == Readtable.SYNTAX_TYPE_MULTIPLE_ESCAPE)
                    break;
                sb.append(c);
            }
        } catch (IOException e) {
            return serror(new ABStreamError(this, e));
        }
        return sb.toString();
    }
    
    
    // Line 1217
    
    private  BitSet _readToken(StringBuilder sb, AbstractReadtable rt)
    {
        BitSet flags = null;
        final LispObject readtableCase = rt.getReadtableCase();
        if (sb.length() > 0) {
            Debug.assertTrue(sb.length() == 1);
            char c = sb.charAt(0);
            byte syntaxType = rt.getSyntaxType(c);
            if (syntaxType == Readtable.SYNTAX_TYPE_SINGLE_ESCAPE) {
                int n = -1;
                try {
                    n = _readChar();
                } catch (IOException e) {
                    error(new ABStreamError(this, e));
                    return flags;
                }
                if (n < 0) {
                    error(new EndOfFile(this));
                    return null; // Not reached
                }

                sb.setCharAt(0, (char) n); // ### BUG: Codepoint conversion
                flags = new BitSet(1);
                flags.set(0);
            } else if (syntaxType == Readtable.SYNTAX_TYPE_MULTIPLE_ESCAPE) {
                sb.setLength(0);
                sb.append(readMultipleEscape(rt));
                flags = new BitSet(sb.length());
                flags.set(0, sb.length());
            } else if (rt.isInvalid(c)) {
                rt.checkInvalid(c, this); // Signals a reader-error.
            } else if (readtableCase == ABKeyword.UPCASE) {
                sb.setCharAt(0, ABLispCharacter.toUpperCase(c));
            } else if (readtableCase == ABKeyword.DOWNCASE) {
                sb.setCharAt(0, ABLispCharacter.toLowerCase(c));
            }
        }
        try {
            while (true) {
                int n = _readChar();
                if (n < 0)
                    break;
                char c = (char) n; // ### BUG: Codepoint conversion
                if (rt.isWhitespace(c)) {
                    _unreadChar(n);
                    break;
                }
                byte syntaxType = rt.getSyntaxType(c);
                if (syntaxType == Readtable.SYNTAX_TYPE_TERMINATING_MACRO) {
                    _unreadChar(c);
                    break;
                }
                rt.checkInvalid(c, this);
                if (syntaxType == Readtable.SYNTAX_TYPE_SINGLE_ESCAPE) {
                    n = _readChar();
                    if (n < 0)
                        break;
                    sb.append((char)n); // ### BUG: Codepoint conversion
                    if (flags == null)
                        flags = new BitSet(sb.length());
                    flags.set(sb.length() - 1);
                    continue;
                }
                if (syntaxType == Readtable.SYNTAX_TYPE_MULTIPLE_ESCAPE) {
                    int begin = sb.length();
                    sb.append(readMultipleEscape(rt));
                    int end = sb.length();
                    if (flags == null)
                        flags = new BitSet(sb.length());
                    flags.set(begin, end);
                    continue;
                }
                if (readtableCase == ABKeyword.UPCASE)
                    c = ABLispCharacter.toUpperCase(c);
                else if (readtableCase == ABKeyword.DOWNCASE)
                    c = ABLispCharacter.toLowerCase(c);
                sb.append(c);
            }
        } catch (IOException e) {
            error(new ABStreamError(this, e));
            return flags;
        }

        return flags;
    }

    // Line 1410
    private Object makeRatio(String token, int radix) {

        final int index = token.indexOf('/');
        if (index < 0)
            return null;
        try {
            BigInteger numerator =
                new BigInteger(token.substring(0, index), radix);
            BigInteger denominator =
                new BigInteger(token.substring(index + 1), radix);
            // Check the denominator here, before calling number(), so we can
            // signal a READER-ERROR, as required by ANSI, instead of DIVISION-
            // BY-ZERO.
            if (denominator.signum() == 0)
                error(new ReaderError("Division by zero.", this));
            return number(numerator, denominator);
        } catch (NumberFormatException e) {
            return null;
        }
    }
    
  private static final BigInteger INT_MIN = BigInteger.valueOf(Integer.MIN_VALUE);
  private static final BigInteger INT_MAX = BigInteger.valueOf(Integer.MAX_VALUE);
    
    // 1446
    
  public static final Object number(BigInteger numerator,
                                        BigInteger denominator)

  {
    if (denominator.signum() == 0) {
        /*
      LispObject operands = new Cons(Bignum.getInstance(numerator),
                                     new Cons(Bignum.getInstance(denominator)));
      LispObject args = new Cons(Keyword.OPERATION,
                                 new Cons(Symbol.SLASH,
                                          new Cons(Keyword.OPERANDS,
                                                   new Cons(operands))));

      error(new DivisionByZero(args));
        */
         error(new ReaderError("Division by zero."));
    }
    if (denominator.signum() < 0)
      {
        numerator = numerator.negate();
        denominator = denominator.negate();
      }
    BigInteger gcd = numerator.gcd(denominator);
    if (!gcd.equals(BigInteger.ONE))
      {
        numerator = numerator.divide(gcd);
        denominator = denominator.divide(gcd);
      }
    if (denominator.equals(BigInteger.ONE))
      return number(numerator);
    else
      return new Ratio(numerator, denominator);
  }
  public static final LispObject number(BigInteger n)
  {
    if (n.compareTo(INT_MIN) >= 0 && n.compareTo(INT_MAX) <= 0)
      return Fixnum.getInstance(n.intValue());
    else
      return Bignum.getInstance(n);
  }
    

    // Line 1505

    public Object readRadix(int radix, ReadtableAccessor rta) {
        StringBuilder sb = new StringBuilder();
        final LispThread thread = LispThread.currentThread();
        final AbstractReadtable rt = rta.rt(thread);
        boolean escaped = (_readToken(sb, rt) != null);
        if (READ_SUPPRESS != NIL)
            return NIL;
        if (escaped)
            return error(new ReaderError("Illegal syntax for number.", this));
        String s = sb.toString();
        if (s.indexOf('/') >= 0)
            return makeRatio(s, radix);
        if (s.length() == 0) {
          return error(new ReaderError("Malformed number in a #b/#o/#x/#r macro."));
        }
        // Integer.parseInt() below handles a prefixed '-' character correctly, but
        // does not accept a prefixed '+' character, so we skip over it here
        if (s.charAt(0) == '+')
            s = s.substring(1);
        try {
//            int n = Integer.parseInt(s, radix);
//            return (n >= 0 && n <= 255) ? Fixnum.constants[n] : Fixnum.getInstance(n);
//            return Fixnum.getInstance(n);
            long ln = Long.parseLong(s, radix);

            return Fixnum.getLongOrInt(ln);


        } catch (NumberFormatException e) {}
        // parseInt() failed.
        try {
            return Bignum.getInstance(s, radix);
        } catch (NumberFormatException e) {}
        // Not a number.
        return error(new ABLispError());
    }
    
    
   // Line 1833
    
    /** Puts a character back into the (underlying) stream
     *
     * @param n
     * @throws java.io.IOException
     */
    protected void _unreadChar(int n) throws IOException {
//        if (reader == null)
//            streamNotCharacterInputStream();

        --offset;
        if (n == '\n') {
            n = eolChar;
            --lineNumber;
        }

        reader.unread(n);
        pastEnd = false;
    }
    
}
