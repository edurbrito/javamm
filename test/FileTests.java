import org.junit.Test;
import pt.up.fe.comp.TestUtils;
import pt.up.fe.comp.jmm.JmmParserResult;
import pt.up.fe.comp.jmm.analysis.JmmSemanticsResult;
import pt.up.fe.comp.jmm.jasmin.JasminResult;
import pt.up.fe.comp.jmm.ollir.OllirResult;

import static org.junit.Assert.*;

public class FileTests {

    @Test
    public void Array1() {
        JmmParserResult res = TestUtils.parse("test/fixtures/public/Array1.jmm");
        assertEquals("Program",res.getRootNode().getKind());
        TestUtils.noErrors(res.getReports());
        JmmSemanticsResult semanticsResult = TestUtils.analyse(res);
        TestUtils.noErrors(semanticsResult.getReports());
    }

    @Test
    public void Array2() {
        JmmParserResult res = TestUtils.parse("test/fixtures/public/Array2.jmm");
        assertEquals("Program",res.getRootNode().getKind());
        TestUtils.noErrors(res.getReports());
        JmmSemanticsResult semanticsResult = TestUtils.analyse(res);
        TestUtils.noErrors(semanticsResult.getReports());
    }

    @Test
    public void Array3() {
        JmmParserResult res = TestUtils.parse("test/fixtures/public/Array3.jmm");
        assertEquals("Program",res.getRootNode().getKind());
        TestUtils.noErrors(res.getReports());
        JmmSemanticsResult semanticsResult = TestUtils.analyse(res);
        TestUtils.noErrors(semanticsResult.getReports());
    }

    @Test
    public void Custom() {
        JmmParserResult res = TestUtils.parse("test/fixtures/public/Custom.jmm");
        assertEquals("Program",res.getRootNode().getKind());
        TestUtils.noErrors(res.getReports());
        JmmSemanticsResult semanticsResult = TestUtils.analyse(res);
        TestUtils.noErrors(semanticsResult.getReports());
    }

    @Test
    public void FindMaximum() {
        JmmParserResult res = TestUtils.parse("test/fixtures/public/FindMaximum.jmm");
        assertEquals("Program",res.getRootNode().getKind());
        TestUtils.noErrors(res.getReports());
        JmmSemanticsResult semanticsResult = TestUtils.analyse(res);
        TestUtils.noErrors(semanticsResult.getReports());
    }

    @Test
    public void GlobalAccess() {
        JmmParserResult res = TestUtils.parse("test/fixtures/public/global_access.jmm");
        assertEquals("Program",res.getRootNode().getKind());
        TestUtils.noErrors(res.getReports());
        JmmSemanticsResult semanticsResult = TestUtils.analyse(res);
        TestUtils.noErrors(semanticsResult.getReports());
        OptimizationStage optimizationStage = new OptimizationStage();
        OllirResult ollirResult = optimizationStage.toOllir(semanticsResult);
        BackendStage backendStage = new BackendStage();
        JasminResult jasminResult = backendStage.toJasmin(ollirResult);

        jasminResult.run();
    }

    @Test
    public void HelloWorld() {
        JmmParserResult res = TestUtils.parse("test/fixtures/public/HelloWorld.jmm");
        assertEquals("Program",res.getRootNode().getKind());
        TestUtils.noErrors(res.getReports());

        JmmSemanticsResult semanticsResult = TestUtils.analyse(res);
        TestUtils.noErrors(semanticsResult.getReports());
        OptimizationStage optimizationStage = new OptimizationStage();
        OllirResult ollirResult = optimizationStage.toOllir(semanticsResult);
        BackendStage backendStage = new BackendStage();
        JasminResult jasminResult = backendStage.toJasmin(ollirResult);

        var output = jasminResult.run();
        assertEquals("12345", output.trim());
    }

    @Test
    public void Lazysort() {
        JmmParserResult res = TestUtils.parse("test/fixtures/public/Lazysort.jmm");
        assertEquals("Program",res.getRootNode().getKind());
        TestUtils.noErrors(res.getReports());
        JmmSemanticsResult semanticsResult = TestUtils.analyse(res);
        TestUtils.noErrors(semanticsResult.getReports());
    }

    @Test
    public void Life() {
        JmmParserResult res = TestUtils.parse("test/fixtures/public/Life.jmm");
        assertEquals("Program",res.getRootNode().getKind());
        TestUtils.noErrors(res.getReports());
        JmmSemanticsResult semanticsResult = TestUtils.analyse(res);
        TestUtils.noErrors(semanticsResult.getReports());
    }

    @Test
    public void MonteCarloPi() {
        JmmParserResult res = TestUtils.parse("test/fixtures/public/MonteCarloPi.jmm");
        assertEquals("Program",res.getRootNode().getKind());
        TestUtils.noErrors(res.getReports());
        JmmSemanticsResult semanticsResult = TestUtils.analyse(res);
        TestUtils.noErrors(semanticsResult.getReports());
    }

    @Test
    public void notInitInIf() {
        JmmParserResult res = TestUtils.parse("test/fixtures/public/notInitInIf.jmm");
        assertEquals("Program",res.getRootNode().getKind());
        TestUtils.noErrors(res.getReports());
        JmmSemanticsResult semanticsResult = TestUtils.analyse(res);
        TestUtils.noErrors(semanticsResult.getReports());
    }

    @Test
    public void notInitInIf2() {
        JmmParserResult res = TestUtils.parse("test/fixtures/public/notInitInIf2.jmm");
        assertEquals("Program",res.getRootNode().getKind());
        TestUtils.noErrors(res.getReports());
        JmmSemanticsResult semanticsResult = TestUtils.analyse(res);
        TestUtils.noErrors(semanticsResult.getReports());
    }

    @Test
    public void notInitInIf3() {
        JmmParserResult res = TestUtils.parse("test/fixtures/public/notInitInIf3.jmm");
        assertEquals("Program",res.getRootNode().getKind());
        TestUtils.noErrors(res.getReports());
        JmmSemanticsResult semanticsResult = TestUtils.analyse(res);
        TestUtils.noErrors(semanticsResult.getReports());
    }

    @Test
    public void NotInitInIf4() {
        JmmParserResult res = TestUtils.parse("test/fixtures/public/NotInitInIf4.jmm");
        assertEquals("Program",res.getRootNode().getKind());
        TestUtils.noErrors(res.getReports());
        JmmSemanticsResult semanticsResult = TestUtils.analyse(res);
        TestUtils.noErrors(semanticsResult.getReports());
    }

    @Test
    public void QuickSort() {
        JmmParserResult res = TestUtils.parse("test/fixtures/public/QuickSort.jmm");
        assertEquals("Program",res.getRootNode().getKind());
        TestUtils.noErrors(res.getReports());
        JmmSemanticsResult semanticsResult = TestUtils.analyse(res);
        TestUtils.noErrors(semanticsResult.getReports());
    }

    @Test
    public void Simple() {
        JmmParserResult res = TestUtils.parse("test/fixtures/public/Simple.jmm");
        assertEquals("Program",res.getRootNode().getKind());
        TestUtils.noErrors(res.getReports());
        JmmSemanticsResult semanticsResult = TestUtils.analyse(res);
        TestUtils.noErrors(semanticsResult.getReports());
        OptimizationStage optimizationStage = new OptimizationStage();
        OllirResult ollirResult = optimizationStage.toOllir(semanticsResult);
        BackendStage backendStage = new BackendStage();
        JasminResult jasminResult = backendStage.toJasmin(ollirResult);

        var output = jasminResult.run();
        assertEquals("30", output.trim());
    }

    @Test
    public void test2() {
        JmmParserResult res = TestUtils.parse("test/fixtures/public/test2.jmm");
        assertEquals("Program",res.getRootNode().getKind());
        TestUtils.noErrors(res.getReports());
        JmmSemanticsResult semanticsResult = TestUtils.analyse(res);
        TestUtils.noErrors(semanticsResult.getReports());
    }

    @Test
    public void test3() {
        JmmParserResult res = TestUtils.parse("test/fixtures/public/test3.jmm");
        assertEquals("Program",res.getRootNode().getKind());
        TestUtils.noErrors(res.getReports());
        JmmSemanticsResult semanticsResult = TestUtils.analyse(res);
        TestUtils.noErrors(semanticsResult.getReports());
    }

    @Test
    public void test4() {
        JmmParserResult res = TestUtils.parse("test/fixtures/public/test4.jmm");
        assertEquals("Program",res.getRootNode().getKind());
        TestUtils.noErrors(res.getReports());
        JmmSemanticsResult semanticsResult = TestUtils.analyse(res);
        TestUtils.noErrors(semanticsResult.getReports());
    }

    @Test
    public void test5() {
        JmmParserResult res = TestUtils.parse("test/fixtures/public/test5.jmm");
        assertEquals("Program",res.getRootNode().getKind());
        TestUtils.noErrors(res.getReports());
        JmmSemanticsResult semanticsResult = TestUtils.analyse(res);
        TestUtils.noErrors(semanticsResult.getReports());
    }

    @Test
    public void TicTacToe() {
        JmmParserResult res = TestUtils.parse("test/fixtures/public/TicTacToe.jmm");
        assertEquals("Program",res.getRootNode().getKind());
        TestUtils.noErrors(res.getReports());
        JmmSemanticsResult semanticsResult = TestUtils.analyse(res);
        TestUtils.noErrors(semanticsResult.getReports());
    }

    @Test
    public void WhileAndIF() {
        JmmParserResult res = TestUtils.parse("test/fixtures/public/WhileAndIF.jmm");
        assertEquals("Program",res.getRootNode().getKind());
        TestUtils.noErrors(res.getReports());
        JmmSemanticsResult semanticsResult = TestUtils.analyse(res);
        TestUtils.noErrors(semanticsResult.getReports());
    }
}
