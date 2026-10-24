/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package modified.ab.commonlisp;

import io.github.shlisp.objects.Readtable;
import java.io.PushbackReader;
import java.io.StringReader;
import java.io.StringWriter;
import java.util.List;
import io.github.shlisp.objects.IPushbackReaderImpl;
import modified.ab.util.TinyLispString;
import modified.ab.util.ILocation;
import modified.ab.util.LineNumber;
import modified.ab.util.PMap;
import modified.ab.util.PVector;

/**
 *
 * @author henmi
 */
public class Abcl_mini {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {

        StringReader sr = new StringReader("`(a ,b)");

        Object o = readFromString(sr);

        printSymbolPkg = true;

        String print = printMeta(o);

        System.out.println("print = " + print);

        List<String> keys = Packages.getKeys();
        System.out.println("pkgs = " + keys);

        System.out.println("------END--------");

    }

    public static String readPrint(StringReader sr) {

        Object o = readFromString(sr);

        String print = print(o);
        return print;
    }

    public static final String USER_NS_STR = "shli.user";

    public static Object readFromString(StringReader sr) {

        Readtable rTable = Readtable.makeExtended();

        IPushbackReaderImpl pri = new IPushbackReaderImpl(new PushbackReader(sr, 5),
        "Abcl_mini");

        modified.ab.commonlisp.ABPackage pkg = Packages.findOrCreatePackage(USER_NS_STR);
        Stream stream = new Stream(null, pri, pkg);

        boolean eofError = false;
        LispObject eofValue = new LispObject();
        boolean recursive = false;

        LispThread thread = LispThread.currentThread();
        thread.setReadTable(rTable);

        Object res = stream.read(eofError, eofValue, recursive, thread);
        return res;
    }

    static String print(Object o) {

        StringWriter sw = new StringWriter();

        print(sw, o);
        return sw.toString();
    }

    static String printMeta(Object o) {

        StringWriter sw = new StringWriter();

        if (o instanceof ILocation) {
            ILocation loc = (ILocation) o;
            LineNumber meta = loc.meta();
            if (meta != null) {
                String strMeta = meta.getStringForPrint();

                sw.write("Meta<");
                sw.write(strMeta);
                sw.write(">  ");
            }
        }

        print(sw, o);
        return sw.toString();
    }

    static boolean printSymbolPkg = false;

    static void print(StringWriter sw, Object o) {

        if (o == null) {
            sw.write("NULL");
            return;
        }
        if (o instanceof Fixnum) {
            Fixnum fn = (Fixnum) o;
            sw.write(String.valueOf(fn.value));
        } else if (o instanceof SingleFloat) {
            SingleFloat sf = (SingleFloat) o;
            sw.write(Float.toString(sf.getValue()));

        } else if (o instanceof DoubleFloat) {
            DoubleFloat df = (DoubleFloat) o;
            sw.write(Double.toString(df.getValue()));

        } else if (o instanceof SimpleString) {
            SimpleString ss = (SimpleString) o;
            sw.write("\"");
            sw.write(ss.getStringValue());
            sw.write("\"");

        } else if (o instanceof PVector) {
            // Consのサブクラスなので、Consより先に判定する

//            printVector(sw, pm);
            throw new UnsupportedOperationException();

        } else if (o instanceof PMap) {
            // Consのサブクラスなので、Consより先に判定する

            throw new UnsupportedOperationException();
//            printMap(sw, pm);

        } else if (o instanceof ABCons) {
            printCons(sw, (ABCons) o);
        } else if (o instanceof ABSymbol) {
            ABSymbol sym = (ABSymbol) o;
            if (printSymbolPkg) {
                sw.write(sym.toString());
            } else {
                sw.write(sym.getName());
            }
        } else if (o instanceof Bignum) {
            Bignum bn = (Bignum) o;
            sw.write(bn.getAsString());
        } else if (o instanceof SimpleArray_T) {
            SimpleArray_T sat = (SimpleArray_T) o;

            printSimpleArray(sw, sat);
            //        sw.write("#<simple-array>");

        } else if (o instanceof Ratio) {

            sw.write("#<ratio>");

        } else if (o instanceof ABLispCharacter) {

            ABLispCharacter lc = (ABLispCharacter) o;
            sw.write(lc.toString());

        } else if (o instanceof TinyLispString) {
            TinyLispString tls = (TinyLispString) o;
            sw.write("\"");
            sw.write(tls.toJavaString());
            sw.write("\"");

        } else {
            throw new RuntimeException("------------ " + o.getClass().getName());
        }

    }

    static void printSimpleArray(StringWriter sw, SimpleArray_T sat) {

        sw.write("#<simple-array  ");
        sw.write(Integer.toString(sat.getRank(), 10));
        sw.write(" #");
        Object inner = sat.getContents();
        print(sw, inner);
        sw.write("     >");
    }

    static void printVector(StringWriter sw, final ABCons _cons) {

        ABCons cons = _cons;
        sw.write("[");
        String sep = "";
        for (;;) {
            sw.write(sep);
            print(sw, cons.getCar());

            Object cdr = cons.getCdr();
            if (cdr instanceof ABNil) {
                break;
            }
            if (cdr instanceof ABCons) {
                cons = (ABCons) cdr;
            } else {
                sw.write(" . ");
                if (cdr == null) {
                    sw.write("NULL");
                } else {
                    //sw.write(cdr.toString());
                    print(sw, cdr);
                }
                break;
            }
            sep = " ";
        }
        sw.write("]");
    }

    static void printMap(StringWriter sw, final ABCons _cons) {

        ABCons cons = _cons;
        sw.write("{");
        String sep = "";
        for (;;) {
            sw.write(sep);
            print(sw, cons.getCar());

            Object cdr = cons.getCdr();
            if (cdr instanceof ABNil) {
                break;
            }
            if (cdr instanceof ABCons) {
                cons = (ABCons) cdr;
            } else {
                sw.write(" . ");
                if (cdr == null) {
                    sw.write("NULL");
                } else {
                    //sw.write(cdr.toString());
                    print(sw, cdr);
                }
                break;
            }
            sep = " ";
        }
        sw.write("}");
    }

    static void printCons(StringWriter sw, final ABCons _cons) {

        ABCons cons = _cons;
        sw.write("(");
        String sep = "";
        for (;;) {
            sw.write(sep);
            print(sw, cons.getCar());

            Object cdr = cons.getCdr();
            if (cdr instanceof ABNil) {
                break;
            }
            if (cdr instanceof ABCons) {
                cons = (ABCons) cdr;
            } else {
                sw.write(" . ");
                if (cdr == null) {
                    sw.write("NULL");
                } else {
                    //sw.write(cdr.toString());
                    print(sw, cdr);
                }
                break;
            }
            sep = " ";
        }
        sw.write(")");
    }

}
