
import org.junit.Test;
import pt.up.fe.comp.TestUtils;
import pt.up.fe.comp.jmm.JmmParserResult;

import static org.junit.Assert.*;

public class ExampleTest {

    @Test
    public void testExpression1() {
        // Ler de ficheiro

        JmmParserResult res = TestUtils.parse("test/fixtures/public/Array1.jmm");
        assertEquals("Program",res.getRootNode().getKind());
        TestUtils.noErrors(res.getReports());
	}

    @Test
    public void testExpression2() {
        // Ler de ficheiro
        assertEquals("Parse", TestUtils.parse("test/fixtures/public/Array2.jmm").getRootNode().getKind());

    }

    @Test
    public void testExpression3() {
        // Ler de ficheiro
        assertEquals("Parse", TestUtils.parse("test/fixtures/public/Array3.jmm").getRootNode().getKind());


    }

    @Test
    public void testExpression4() {
        // Ler de ficheiro
        assertEquals("Parse", TestUtils.parse("test/fixtures/public/Custom.jmm").getRootNode().getKind());


    }

    @Test
    public void testExpression5() {
        // Ler de ficheiro
        assertEquals("Parse", TestUtils.parse("test/fixtures/public/FindMaximum.jmm").getRootNode().getKind());


    }

    @Test
    public void testExpression6() {
        // Ler de ficheiro
        assertEquals("Parse", TestUtils.parse("test/fixtures/public/HelloWorld.jmm").getRootNode().getKind());


    }

    @Test
    public void testExpression7() {
        // Ler de ficheiro
        assertEquals("Parse", TestUtils.parse("test/fixtures/public/Lazysort.jmm").getRootNode().getKind());


    }

    @Test
    public void testExpression8() {
        // Ler de ficheiro
        assertEquals("Parse", TestUtils.parse("test/fixtures/public/Lif.jmm").getRootNode().getKind());


    }

    @Test
    public void testExpression9() {
        // Ler de ficheiro
        assertEquals("Parse", TestUtils.parse("test/fixtures/public/MonteCarloPi.jmm").getRootNode().getKind());


    }

    @Test
    public void testExpression10() {
        // Ler de ficheiro
        assertEquals("Parse", TestUtils.parse("test/fixtures/public/notInitInIf.jmm").getRootNode().getKind());


    }

    @Test
    public void testExpression11() {
        // Ler de ficheiro
        assertEquals("Parse", TestUtils.parse("test/fixtures/public/notInitInIf2.jmm").getRootNode().getKind());


    }

    @Test
    public void testExpression12() {
        // Ler de ficheiro
        assertEquals("Parse", TestUtils.parse("test/fixtures/public/notInitInIf3.jmm").getRootNode().getKind());


    }

    @Test
    public void testExpression13() {
        // Ler de ficheiro
        assertEquals("Parse", TestUtils.parse("test/fixtures/public/notInitInIf4.jmm").getRootNode().getKind());



    }

    @Test
    public void testExpression14() {
        // Ler de ficheiro
        assertEquals("Parse", TestUtils.parse("test/fixtures/public/notInitInIf3.jmm").getRootNode().getKind());


    }

    @Test
    public void QuickSort() {

        JmmParserResult res = TestUtils.parse("test/fixtures/public/QuickSort.jmm");
        assertEquals("Program",res.getRootNode().getKind());
        TestUtils.noErrors(res.getReports());
    }

    @Test
    public void Simple() {

        JmmParserResult res = TestUtils.parse("test/fixtures/public/Simple.jmm");
        assertEquals("Program",res.getRootNode().getKind());
        TestUtils.noErrors(res.getReports());
    }

    @Test
    public void test2() {
        JmmParserResult res = TestUtils.parse("test/fixtures/public/test2.jmm");
        assertEquals("Program",res.getRootNode().getKind());
        TestUtils.noErrors(res.getReports());
    }

    @Test
    public void test3() {
        JmmParserResult res = TestUtils.parse("test/fixtures/public/test3.jmm");
        assertEquals("Program",res.getRootNode().getKind());
        TestUtils.noErrors(res.getReports());
    }

    @Test
    public void test4() {
        JmmParserResult res = TestUtils.parse("test/fixtures/public/test4.jmm");
        assertEquals("Program",res.getRootNode().getKind());
        TestUtils.noErrors(res.getReports());
    }

    @Test
    public void test5() {
        JmmParserResult res = TestUtils.parse("test/fixtures/public/test5.jmm");
        assertEquals("Program",res.getRootNode().getKind());
        TestUtils.noErrors(res.getReports());
    }

    @Test
    public void TicTacToe() {
        JmmParserResult res = TestUtils.parse("test/fixtures/public/TicTacToe.jmm");
        assertEquals("Program",res.getRootNode().getKind());
        TestUtils.noErrors(res.getReports());

    }

    @Test
    public void testExpression22() {
        // Ler de ficheiro
        assertEquals("Parse", TestUtils.parse("test/fixtures/public/varNotInit.jmm").getRootNode().getKind());



    }

    @Test
    public void testExpression23() {
        // Ler de ficheiro
        assertEquals("Parse", TestUtils.parse("test/fixtures/public/WhileAndIF.jmm").getRootNode().getKind());



    }





}
