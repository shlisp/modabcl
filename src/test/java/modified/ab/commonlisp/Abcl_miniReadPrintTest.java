/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/UnitTests/JUnit4TestClass.java to edit this template
 */
package modified.ab.commonlisp;

import java.io.StringReader;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 * ABCLのREADERが読んだオブジェクトを一度PRINTして文字列にして、テストしている
 * @author henmi
 */
@Deprecated
public class Abcl_miniReadPrintTest {
    
    public Abcl_miniReadPrintTest() {
    }
    
    @Test
    public void testReadFrom1() {

        StringReader sr = new StringReader("123");  // (A . B)

        String real = Abcl_mini.readPrint(sr);

        assertEquals("123", real);
    }

    @Test
    public void testReadFrom2() {

        StringReader sr = new StringReader("abc");

        String real = Abcl_mini.readPrint(sr);

        assertEquals("abc", real);
    }


    @Test
    public void testReadFrom3() {

        StringReader sr = new StringReader("toString");

        String real = Abcl_mini.readPrint(sr);

        assertEquals("toString", real);
    }

    @Test
    public void testReadFrom4() {

        StringReader sr = new StringReader("*a*");

        String real = Abcl_mini.readPrint(sr);

        assertEquals("*a*", real);
    }

    @Test
    public void testReadFrom5() {

        StringReader sr = new StringReader(".toString");

        String real = Abcl_mini.readPrint(sr);

        assertEquals(".toString", real);
    }

    @Test
    public void testReadFrom6() {

        StringReader sr = new StringReader("( a . b)"); 

        String real = Abcl_mini.readPrint(sr);

        assertEquals("(a . b)", real);
    }

    @Test
    public void testReadFrom8() {

        StringReader sr = new StringReader("\"abc\""); 

        String real = Abcl_mini.readPrint(sr);

        assertEquals("\"abc\"", real);
    }

    @Test
    public void testReadFrom9() {

        StringReader sr = new StringReader("()");

        String real = Abcl_mini.readPrint(sr);

        assertEquals("NIL", real);
    }

    @Test
    public void testReadFrom10() {

        StringReader sr = new StringReader("#| Arithmetic |#  a");

        String real = Abcl_mini.readPrint(sr);

        assertEquals("a", real);
    }

    @Test
    public void testReadFrom12() {

        StringReader sr = new StringReader("#o777");

        String real = Abcl_mini.readPrint(sr);

        assertEquals("511", real);
    }

    @Test
    public void testReadFrom13() {

        StringReader sr = new StringReader("#o177777");

        String real = Abcl_mini.readPrint(sr);

        assertEquals("65535", real);
    }

    public void testReadFrom14() {

        StringReader sr = new StringReader("#b11111111111111111111111111111111");

        String real = Abcl_mini.readPrint(sr);

        assertEquals("4294967295", real);
    }

    @Test
    public void testReadFrom16() {

        StringReader sr = new StringReader("t");

        String real = Abcl_mini.readPrint(sr);

        assertEquals("t", real);
    }

    @Test
    public void testReadFrom19() {

        StringReader sr = new StringReader("#2a()");

        Object real = Abcl_mini.readFromString(sr);
        if (real instanceof SimpleArray_T) {
            System.out.println("----- SimpleArray_T OK");
        } else {
            fail();
        }

    }

    @Test
    public void testReadFrom20() {

        StringReader sr = new StringReader(":element-type");

        Object real = Abcl_mini.readFromString(sr); // テスト対象

        if (real instanceof ABSymbol) {
            ABSymbol sym = (ABSymbol) real;
//            assertEquals(  ":element-type", sym.  );
            modified.ab.commonlisp.ABPackage aPackage = sym.getPackage();

            System.out.println("155)  aPackage= " + aPackage.getName());

        } else {
            fail();
        }

//        System.out.println("---------END----------");

    }

    @Test
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

//        System.out.println("---------END----------");
    }

    @Test
    public void testReadFrom22() {

        StringReader sr = new StringReader("NULL");

        Object real = Abcl_mini.readFromString(sr); // 

        System.out.println("real = " + real);
        
        assertTrue(real instanceof  ABSymbol);
        
        ABSymbol sym = (ABSymbol ) real;
        String qualifiedName = sym.getQualifiedName();
        
        //                     ▽なぜコロンが２つなのか、わかっていない
        assertEquals("shli.user::NULL", qualifiedName);
    }

    
}
