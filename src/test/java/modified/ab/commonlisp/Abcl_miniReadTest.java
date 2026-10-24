/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/UnitTests/JUnit4TestClass.java to edit this template
 */
package modified.ab.commonlisp;

import java.io.StringReader;
import modified.ab.util.LineNumber;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 * ABCLのREADERが読んだオブジェクトを、そのままテストしている
 *
 * @author henmi
 */
public class Abcl_miniReadTest {

    @Test
    public void testReadFrom0() {

        StringReader sr = new StringReader(" 2147483647");
        Object real = Abcl_mini.readFromString(sr);

        if (!(real instanceof Fixnum)) {
            fail();
        }
        Fixnum fn = (Fixnum) real;      // FIXNUMは intしか表現できない
        assertEquals(2147483647, fn.value);
        assertEquals(false, fn.isLong);
    }

    @Test
    public void testReadFrom0L() {

        StringReader sr = new StringReader(" 2147483648");
        Object real = Abcl_mini.readFromString(sr);
        if (!(real instanceof Fixnum)) {
            fail();
        }
        Fixnum fn = (Fixnum) real;      // FIXNUM long拡張改造
        assertEquals(2147483648L, fn.longValue);
        assertEquals(true, fn.isLong);
    }

    @Test
    public void testReadFrom0M() {

        StringReader sr = new StringReader(" -2147483648");

        Object real = Abcl_mini.readFromString(sr);

        if (!(real instanceof Fixnum)) {
            fail();
        }
        Fixnum fn = (Fixnum) real;      // FIXNUMは intしか表現できない

        assertEquals(-2147483648, fn.value);
        assertEquals(false, fn.isLong);
    }

    @Test
    public void testReadFrom0LM() {

        StringReader sr = new StringReader(" -2147483649");
        Object real = Abcl_mini.readFromString(sr);

        if (!(real instanceof Fixnum)) {
            fail();
        }
        Fixnum fn = (Fixnum) real;      // FIXNUMは long拡張改造
        assertEquals(-2147483649L, fn.longValue);
        assertEquals(true, fn.isLong);
    }

    @Test
    public void testReadFrom1() {

        StringReader sr = new StringReader("123");
        Object real = Abcl_mini.readFromString(sr);
        if (!(real instanceof Fixnum)) {
            fail();
        }
        Fixnum fn = (Fixnum) real;      // FIXNUMは intしか表現できない
        assertEquals(123, fn.value);
        assertEquals(false, fn.isLong);
    }

    public void testReadFrom2() {

        StringReader sr = new StringReader("abc");
        Object real = Abcl_mini.readFromString(sr);
        assertEquals("ABC", real);
    }

    @Test
    public void testReadFrom3() {
        //                                 先頭にピリオドなし
        StringReader sr = new StringReader("toString");
        Object real = Abcl_mini.readFromString(sr);

        assertTrue(real instanceof ABSymbol);
        ABSymbol sym = (ABSymbol) real;
        String qualifiedName = sym.getQualifiedName();
        //                                        ▽ READERのモードを INVERTにしているので、大文字・小文字が保たれている
        assertEquals(Abcl_mini.USER_NS_STR + "::toString", qualifiedName);
    }

    public void testReadFrom4() {

        StringReader sr = new StringReader("*a*");
        Object real = Abcl_mini.readFromString(sr);
        assertEquals("*A*", real);
    }

    @Test
    public void testReadFrom5() {
        //                                 先頭にピリオド付き
        StringReader sr = new StringReader(".toString");
        Object real = Abcl_mini.readFromString(sr);

        assertTrue(real instanceof ABSymbol);
        ABSymbol sym = (ABSymbol) real;
        String qualifiedName = sym.getQualifiedName();
        //                                        ▽ READERのモードを INVERTにしているので、大文字・小文字が保たれている
        assertEquals(Abcl_mini.USER_NS_STR + "::.toString", qualifiedName);
    }

    public void testReadFrom6() {

        StringReader sr = new StringReader("( a . b)");  // (A . B)
        Object real = Abcl_mini.readFromString(sr);
        assertEquals("(A . B)", real);
    }

    public void testReadFrom8() {

        StringReader sr = new StringReader("\"abc\"");  // (A . B)
        Object real = Abcl_mini.readFromString(sr);
        assertEquals("\"abc\"", real);
    }

    @Test
    public void testReadFrom9() {

        StringReader sr = new StringReader("()");
        Object real = Abcl_mini.readFromString(sr);

        //                     想像：行・カラムを付けようとした際に、たぶんNilクラスからABSymbolに変わっている
        assertTrue(real instanceof ABSymbol);
        ABSymbol sym = (ABSymbol) real;
        String qualifiedName = sym.getQualifiedName();
        assertEquals(CL + "::NIL", qualifiedName);
    }

    static final String CL = "cl";

    @Test
    public void testReadFrom10() {

        StringReader sr = new StringReader("nil");

        Object real = Abcl_mini.readFromString(sr);

        //                     想像：行・カラムを付けようとした際に、たぶんNilクラスからABSymbolに変わっている
        assertTrue(real instanceof ABSymbol);
        ABSymbol sym = (ABSymbol) real;
        String qualifiedName = sym.getQualifiedName();
        //                () では CLになるが、 nilを書いた時は、なぜか、 shliになる
        assertEquals("cl" + "::NIL", qualifiedName);
    }

    @Test
    public void testReadFrom11() {

        StringReader sr = new StringReader("#| Arithmetic |#  a");

        Object real = Abcl_mini.readFromString(sr);

        assertTrue(real instanceof ABSymbol);
        ABSymbol sym = (ABSymbol) real;
        String qualifiedName = sym.getQualifiedName();
        assertEquals(Abcl_mini.USER_NS_STR + "::a", qualifiedName);
    }

    public void testReadFrom12() {

        StringReader sr = new StringReader("#o777");

        Object real = Abcl_mini.readFromString(sr);

        assertEquals("511", real);
    }

    public void testReadFrom13() {

        StringReader sr = new StringReader("#o177777");

        Object real = Abcl_mini.readFromString(sr);

        assertEquals("65535", real);
    }

    public void testReadFrom14() {

        StringReader sr = new StringReader("#b11111111111111111111111111111111");

        Object real = Abcl_mini.readFromString(sr);

        assertEquals("4294967295", real);
    }

    @Test
    public void testReadFrom16() {

        StringReader sr = new StringReader("t");   // tは大文字のTになる

        Object real = Abcl_mini.readFromString(sr);

        assertTrue(real instanceof ABSymbol);
        ABSymbol sym = (ABSymbol) real;
        String qualifiedName = sym.getQualifiedName();
        assertEquals(Abcl_mini.USER_NS_STR + "::t", qualifiedName);
    }

    @Test
    public void testReadFrom19() {

        StringReader sr = new StringReader("#2a((2 3) (4 5))");

        Object real = Abcl_mini.readFromString(sr);
        if (real instanceof SimpleArray_T) {
            System.out.println("----- SimpleArray_T OK");
        } else {
            fail();
        }

    }

    public void testReadFrom20() {

        StringReader sr = new StringReader(":element-type");

        Object real = Abcl_mini.readFromString(sr); // テスト対象

        if (real instanceof ABSymbol) {
            ABSymbol sym = (ABSymbol) real;
            modified.ab.commonlisp.ABPackage aPackage = sym.getPackage();
            System.out.println("155)  aPackage= " + aPackage.getName());

        } else {
            fail();
        }
    }

    public void testReadFrom21() {

        StringReader sr = new StringReader("#'abc");

        Object real = Abcl_mini.readFromString(sr); // 

        modified.ab.commonlisp.ABCons cons = (modified.ab.commonlisp.ABCons) real;

        Object car = cons.getCar();

        System.out.println("car = " + car);

        if (!(car instanceof ABSymbol)) {
            fail();
        }
        ABSymbol sym = (ABSymbol) car;

        assertEquals("function", sym.getName());

        modified.ab.commonlisp.ABPackage aPackage = sym.getPackage();

        System.out.println("155)  aPackage= " + aPackage.getName());
        assertEquals("sys", aPackage.getName());
    }

    @Test
    public void testReadFrom22() {

        StringReader sr = new StringReader("NULL");
        Object real = Abcl_mini.readFromString(sr); // 
        System.out.println("real = " + real);

        assertTrue(real instanceof ABSymbol);

        ABSymbol sym = (ABSymbol) real;
        String qualifiedName = sym.getQualifiedName();
        //                                ▽なぜコロンが２つなのか、わかっていない
        assertEquals(Abcl_mini.USER_NS_STR + "::NULL", qualifiedName);
    }

    @Test
    public void testReadFrom23() {

        StringReader sr = new StringReader("#:qlqs-network");

        Object real = Abcl_mini.readFromString(sr); // 

        System.out.println("real = " + real);

        assertTrue(real instanceof ABSymbol);

        ABSymbol sym = (ABSymbol) real;
        String qualifiedName = sym.getQualifiedName();

        //                                ▽なぜコロンが２つなのか、わかっていない
        //                                 ▽ invertにしたので、小文字のnullに変わってしまう
        assertEquals("#:qlqs-network", qualifiedName);
    }

//    Test  まだ出来ていない
    public void testReadFrom30() {

        StringReader sr = new StringReader("#+sbcl (format \"SBCL de jikko chu\")");

        Object real = Abcl_mini.readFromString(sr); // 

        System.out.println("real = " + real);

        assertTrue(real instanceof ABSymbol);

        ABSymbol sym = (ABSymbol) real;
        String qualifiedName = sym.getQualifiedName();

        //                                ▽なぜコロンが２つなのか、わかっていない
        //                                 ▽ invertにしたので、小文字のnullに変わってしまう
        assertEquals("#:qlqs-network", qualifiedName);
    }

//    Test
    public void testReadFrom40() {

        StringReader sr = new StringReader("#U1F363");
        Object real = Abcl_mini.readFromString(sr); // 
        System.out.println("real = " + real);
        assertTrue(real instanceof ABLispCharacter);
    }

    @Test
    public void testReadFrom41() {

        StringReader sr = new StringReader("#xFFFFFFFF");
        Object real = Abcl_mini.readFromString(sr); // 
        System.out.println("real = " + real);

        assertTrue(real instanceof Fixnum);

        Fixnum num = (Fixnum) real;
        assertTrue(num.isLong);
        assertEquals(4294967295L, num.longValue);
    }

    @Test
    public void testReadFrom42() { // 複素数

        StringReader sr = new StringReader("#C(5 -3)");
        Object complex = Abcl_mini.readFromString(sr); // 
        System.out.println("complex = " + complex);

        assertTrue(complex instanceof ABComplex);

        if (complex instanceof ABComplex cx) {
            Object realPart = cx.getRealPart();
            Object imaginaryPart = cx.getImaginaryPart();
            
            System.out.println("realPart = "+realPart);
            System.out.println("imaginaryPart = "+ imaginaryPart);
            
            Fixnum fix5 = Fixnum.getInstance(5);
            Fixnum fix_3 = Fixnum.getInstance(-3);
            
            assertEquals(fix5, realPart);
            assertEquals(fix_3, imaginaryPart);
            
        } else {
            fail();
        }
        
    }

    @Test
    public void testReadFrom43() {

        StringReader sr = new StringReader("^Integer *a*");
        Object real = Abcl_mini.readFromString(sr);
        if (real instanceof ABSymbol abs) {
            LineNumber meta = abs.meta();
            System.out.println("392: " + meta.getStringForPrint());
            //      ^{:tag java.lang.Integer} *a*
        }
//        assertEquals(Abcl_mini.USER_NS_STR + ":*a*", real);
    }

    @Test
    public void testReadFrom44() {

        StringReader sr = new StringReader("^:foo *a*");
        Object real = Abcl_mini.readFromString(sr);
        if (real instanceof ABSymbol abs) {
            LineNumber meta = abs.meta();
            System.out.print("405: " + meta.getStringForPrint());
            // ^{:foo true } *a*

            System.out.println(" " + abs.toString());

        }
//        assertEquals(Abcl_mini.USER_NS_STR + ":*a*", real);
    }


    @Test
    public void testReadFrom50() {

        StringReader sr = new StringReader("#(1 2 3 4)");
        Object real = Abcl_mini.readFromString(sr);
        
        System.out.println("420) "+real);
        /*
        if (real instanceof ABSymbol abs) {
            LineNumber meta = abs.meta();
            System.out.print("405: " + meta.getStringForPrint());
            // ^{:foo true } *a*

            System.out.println(" " + abs.toString());

        }*/
//        assertEquals(Abcl_mini.USER_NS_STR + ":*a*", real);
    }


    @Test
    public void testReadFrom52() { // 複素数

        StringReader sr = new StringReader("#C(5.3 -3.2)");
        Object complex = Abcl_mini.readFromString(sr); // 
        System.out.println("complex = " + complex);

        assertTrue(complex instanceof ABComplex);

        if (complex instanceof ABComplex cx) {
            Object realPart = cx.getRealPart();
            Object imaginaryPart = cx.getImaginaryPart();
            
            System.out.println("realPart = "+realPart);
            System.out.println("imaginaryPart = "+ imaginaryPart);

            if (realPart instanceof DoubleFloat df) {
                System.out.println("452) "+
                        df.getValue());
            }
            if (imaginaryPart instanceof DoubleFloat df2) {
                System.out.println("456) "+
                        df2.getValue());
            }

        } else {
            fail();
        }
        
    }

    @Test
    public void testReadFrom53() { // #p"abc"

        StringReader sr = new StringReader("#p\"abc\"");
        Object pathObj = Abcl_mini.readFromString(sr); // 
        System.out.println("pathObj = " + pathObj);

        assertTrue(pathObj instanceof ABPathname);
        if (pathObj instanceof ABPathname pa) {
            assertEquals("abc", pa.getPath());
        }
    }

}
