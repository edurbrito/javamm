
import org.junit.Test;
import pt.up.fe.comp.TestUtils;
import pt.up.fe.comp.jmm.JmmParserResult;

import static org.junit.Assert.*;

public class ExampleTest {

    @Test
    public void Array1() {
        // Ler de ficheiro

        JmmParserResult res = TestUtils.parse("test/fixtures/public/Array1.jmm");
        assertEquals("Program",res.getRootNode().getKind());
        TestUtils.noErrors(res.getReports());
	}

    @Test
    public void Array2() {
        // Ler de ficheiro
        JmmParserResult res = TestUtils.parse("test/fixtures/public/Array2.jmm");
        assertEquals("Program",res.getRootNode().getKind());
        TestUtils.noErrors(res.getReports());

    }

    @Test
    public void Array3() {
        // Ler de ficheiro
        JmmParserResult res = TestUtils.parse("test/fixtures/public/Array3.jmm");
        assertEquals("Program",res.getRootNode().getKind());
        TestUtils.noErrors(res.getReports());
    }

    @Test
    public void Custom() {
        // Ler de ficheiro
        JmmParserResult res = TestUtils.parse("test/fixtures/public/Custom.jmm");
        assertEquals("Program",res.getRootNode().getKind());
        TestUtils.noErrors(res.getReports());
    }

    @Test
    public void FindMaximum() {
        // Ler de ficheiro
        JmmParserResult res = TestUtils.parse("test/fixtures/public/FindMaximum.jmm");
        assertEquals("Program",res.getRootNode().getKind());
        TestUtils.noErrors(res.getReports());

    }

    @Test
    public void HelloWorld() {
        // Ler de ficheiro
        JmmParserResult res = TestUtils.parse("test/fixtures/public/HelloWorld.jmm");
        assertEquals("Program",res.getRootNode().getKind());
        TestUtils.noErrors(res.getReports());


    }

    @Test
    public void Lazysort() {
        // Ler de ficheiro
        JmmParserResult res = TestUtils.parse("test/fixtures/public/Lazysort.jmm");
        assertEquals("Program",res.getRootNode().getKind());
        TestUtils.noErrors(res.getReports());

    }

    @Test
    public void Life() {
        // Ler de ficheiro
        JmmParserResult res = TestUtils.parse("test/fixtures/public/Life.jmm");
        assertEquals("Program",res.getRootNode().getKind());
        TestUtils.noErrors(res.getReports());

    }

    @Test
    public void MonteCarloPi() {
        // Ler de ficheiro
        JmmParserResult res = TestUtils.parse("test/fixtures/public/MonteCarloPi.jmm");
        assertEquals("Program",res.getRootNode().getKind());
        TestUtils.noErrors(res.getReports());


    }

    @Test
    public void notInitInIf() {
        // Ler de ficheiro
        JmmParserResult res = TestUtils.parse("test/fixtures/public/notInitInIf.jmm");
        assertEquals("Program",res.getRootNode().getKind());
        TestUtils.noErrors(res.getReports());


    }

    @Test
    public void notInitInIf2() {
        // Ler de ficheiro
        JmmParserResult res = TestUtils.parse("test/fixtures/public/notInitInIf2.jmm");
        assertEquals("Program",res.getRootNode().getKind());
        TestUtils.noErrors(res.getReports());


    }

    @Test
    public void notInitInIf3() {
        // Ler de ficheiro
        JmmParserResult res = TestUtils.parse("test/fixtures/public/notInitInIf3.jmm");
        assertEquals("Program",res.getRootNode().getKind());
        TestUtils.noErrors(res.getReports());


    }

    @Test
    public void NotInitInIf4() {
        // Ler de ficheiro
        JmmParserResult res = TestUtils.parse("test/fixtures/public/NotInitInIf4.jmm");
        assertEquals("Program",res.getRootNode().getKind());
        TestUtils.noErrors(res.getReports());



    }

    @Test
    public void testExpression15() {
        // Ler de ficheiro
        assertEquals("Program", TestUtils.parse("test/fixtures/public/QuickSort.jmm").getRootNode().getKind());



    }

    @Test
    public void testExpression16() {
        // Ler de ficheiro
        assertEquals("Program", TestUtils.parse("test/fixtures/public/Simple.jmm").getRootNode().getKind());



    }

    @Test
    public void testExpression17() {
        // Ler de ficheiro
        assertEquals("Program", TestUtils.parse("test/fixtures/public/test2.jmm").getRootNode().getKind());



    }

    @Test
    public void testExpression18() {
        // Ler de ficheiro
        assertEquals("Program", TestUtils.parse("test/fixtures/public/test3.jmm").getRootNode().getKind());



    }

    @Test
    public void testExpression19() {
        // Ler de ficheiro
        assertEquals("Program", TestUtils.parse("test/fixtures/public/test4.jmm").getRootNode().getKind());



    }

    @Test
    public void testExpression20() {
        // Ler de ficheiro
        assertEquals("Program", TestUtils.parse("test/fixtures/public/test5.jmm").getRootNode().getKind());



    }

    @Test
    public void testExpression21() {
        // Ler de ficheiro
        assertEquals("Program", TestUtils.parse("test/fixtures/public/TicTacToe.jmm").getRootNode().getKind());



    }

    @Test
    public void varNotInit() {
        // Ler de ficheiro
        JmmParserResult res = TestUtils.parse("test/fixtures/public/varNotInit.jmm");
        assertEquals("Program",res.getRootNode().getKind());
        TestUtils.noErrors(res.getReports());


    }

    @Test
    public void WhileAndIF() {
        // Ler de ficheiro
        JmmParserResult res = TestUtils.parse("test/fixtures/public/WhileAndIF.jmm");
        assertEquals("Program",res.getRootNode().getKind());
        TestUtils.noErrors(res.getReports());


    }





}
