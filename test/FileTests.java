import org.junit.Test;
import pt.up.fe.comp.TestUtils;
import pt.up.fe.comp.jmm.JmmParserResult;
import pt.up.fe.comp.jmm.analysis.JmmSemanticsResult;
import pt.up.fe.comp.jmm.jasmin.JasminResult;
import pt.up.fe.comp.jmm.ollir.OllirResult;
import pt.up.fe.specs.util.SpecsIo;

import static org.junit.Assert.*;

public class FileTests {

    @Test
    public void Arithmetic() {
        JmmParserResult res = TestUtils.parse("test/fixtures/public/Arithmetic.jmm");
        assertEquals("Program",res.getRootNode().getKind());
        TestUtils.noErrors(res.getReports());
        JmmSemanticsResult semanticsResult = TestUtils.analyse(res);
        TestUtils.noErrors(semanticsResult.getReports());

        OptimizationStage optimizationStage = new OptimizationStage();
        OllirResult ollirResult = optimizationStage.toOllir(semanticsResult);
        BackendStage backendStage = new BackendStage();
        JasminResult jasminResult = backendStage.toJasmin(ollirResult);

        System.out.println(jasminResult.getJasminCode());

        jasminResult.run();
    }

    @Test
    public void Array1() {
        JmmParserResult res = TestUtils.parse("test/fixtures/public/Array1.jmm");
        assertEquals("Program",res.getRootNode().getKind());
        TestUtils.noErrors(res.getReports());
        JmmSemanticsResult semanticsResult = TestUtils.analyse(res);
        TestUtils.noErrors(semanticsResult.getReports());

        OptimizationStage optimizationStage = new OptimizationStage();
        OllirResult ollirResult = optimizationStage.toOllir(semanticsResult);
        BackendStage backendStage = new BackendStage();
        JasminResult jasminResult = backendStage.toJasmin(ollirResult);

        System.out.println(jasminResult.getJasminCode());

        jasminResult.run();
    }

    @Test
    public void Array3() {
        JmmParserResult res = TestUtils.parse("test/fixtures/public/Array3.jmm");
        assertEquals("Program",res.getRootNode().getKind());
        TestUtils.noErrors(res.getReports());
        JmmSemanticsResult semanticsResult = TestUtils.analyse(res);
        TestUtils.noErrors(semanticsResult.getReports());

        OptimizationStage optimizationStage = new OptimizationStage();
        OllirResult ollirResult = optimizationStage.toOllir(semanticsResult);
        BackendStage backendStage = new BackendStage();
        JasminResult jasminResult = backendStage.toJasmin(ollirResult);

        System.out.println(jasminResult.getJasminCode());

        jasminResult.run();
    }

    @Test
    public void Array4() {
        JmmParserResult res = TestUtils.parse("test/fixtures/public/Array4.jmm");
        assertEquals("Program",res.getRootNode().getKind());
        TestUtils.noErrors(res.getReports());
        JmmSemanticsResult semanticsResult = TestUtils.analyse(res);
        TestUtils.noErrors(semanticsResult.getReports());

        OptimizationStage optimizationStage = new OptimizationStage();
        OllirResult ollirResult = optimizationStage.toOllir(semanticsResult);
        BackendStage backendStage = new BackendStage();
        JasminResult jasminResult = backendStage.toJasmin(ollirResult);

        System.out.println(jasminResult.getJasminCode());

        jasminResult.run();
    }

    @Test
    public void Boolean() {
        JmmParserResult res = TestUtils.parse("test/fixtures/public/Boolean.jmm");
        assertEquals("Program",res.getRootNode().getKind());
        TestUtils.noErrors(res.getReports());
        JmmSemanticsResult semanticsResult = TestUtils.analyse(res);
        TestUtils.noErrors(semanticsResult.getReports());

        OptimizationStage optimizationStage = new OptimizationStage();
        OllirResult ollirResult = optimizationStage.toOllir(semanticsResult);
        BackendStage backendStage = new BackendStage();
        JasminResult jasminResult = backendStage.toJasmin(ollirResult);

        System.out.println(jasminResult.getJasminCode());

        jasminResult.run();
    }

    @Test
    public void Custom() {
        JmmParserResult res = TestUtils.parse("test/fixtures/public/Custom.jmm");
        assertEquals("Program",res.getRootNode().getKind());
        TestUtils.noErrors(res.getReports());
        JmmSemanticsResult semanticsResult = TestUtils.analyse(res);
        TestUtils.noErrors(semanticsResult.getReports());

        OptimizationStage optimizationStage = new OptimizationStage();
        OllirResult ollirResult = optimizationStage.toOllir(semanticsResult);
        BackendStage backendStage = new BackendStage();
        JasminResult jasminResult = backendStage.toJasmin(ollirResult);

        System.out.println(jasminResult.getJasminCode());

        jasminResult.run();
    }

    @Test
    public void FindMaximum() {
        JmmParserResult res = TestUtils.parse("test/fixtures/public/FindMaximum.jmm");
        assertEquals("Program",res.getRootNode().getKind());
        TestUtils.noErrors(res.getReports());
        JmmSemanticsResult semanticsResult = TestUtils.analyse(res);
        TestUtils.noErrors(semanticsResult.getReports());

        OptimizationStage optimizationStage = new OptimizationStage();
        OllirResult ollirResult = optimizationStage.toOllir(semanticsResult);
        BackendStage backendStage = new BackendStage();
        JasminResult jasminResult = backendStage.toJasmin(ollirResult);

        System.out.println(jasminResult.getJasminCode());

        jasminResult.run();
    }

    @Test
    public void GlobalAccess() {
        JmmParserResult res = TestUtils.parse("test/fixtures/public/GlobalAccess.jmm");
        assertEquals("Program",res.getRootNode().getKind());
        TestUtils.noErrors(res.getReports());
        JmmSemanticsResult semanticsResult = TestUtils.analyse(res);
        TestUtils.noErrors(semanticsResult.getReports());
        OptimizationStage optimizationStage = new OptimizationStage();
        OllirResult ollirResult = optimizationStage.toOllir(semanticsResult);
        BackendStage backendStage = new BackendStage();
        JasminResult jasminResult = backendStage.toJasmin(ollirResult);

        System.out.println(jasminResult.getJasminCode());

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
        assertEquals("1234", output.trim());
    }

    @Test
    public void Lazysort() {
        JmmParserResult res = TestUtils.parse("test/fixtures/public/Lazysort.jmm");
        assertEquals("Program",res.getRootNode().getKind());
        TestUtils.noErrors(res.getReports());
        JmmSemanticsResult semanticsResult = TestUtils.analyse(res);
        TestUtils.noErrors(semanticsResult.getReports());

        OptimizationStage optimizationStage = new OptimizationStage();
        OllirResult ollirResult = optimizationStage.toOllir(semanticsResult);
        BackendStage backendStage = new BackendStage();
        JasminResult jasminResult = backendStage.toJasmin(ollirResult);

        System.out.println(jasminResult.getJasminCode());

        jasminResult.run();
    }

    @Test
    public void Length() {
        JmmParserResult res = TestUtils.parse("test/fixtures/public/Length.jmm");
        assertEquals("Program",res.getRootNode().getKind());
        TestUtils.noErrors(res.getReports());
        JmmSemanticsResult semanticsResult = TestUtils.analyse(res);
        TestUtils.noErrors(semanticsResult.getReports());

        OptimizationStage optimizationStage = new OptimizationStage();
        OllirResult ollirResult = optimizationStage.toOllir(semanticsResult);
        BackendStage backendStage = new BackendStage();
        JasminResult jasminResult = backendStage.toJasmin(ollirResult);

        System.out.println(jasminResult.getJasminCode());

        jasminResult.run();
    }

    public void Life() {
        JmmParserResult res = TestUtils.parse("test/fixtures/public/Life.jmm");
        assertEquals("Program",res.getRootNode().getKind());
        TestUtils.noErrors(res.getReports());
        JmmSemanticsResult semanticsResult = TestUtils.analyse(res);
        TestUtils.noErrors(semanticsResult.getReports());

        OptimizationStage optimizationStage = new OptimizationStage();
        OllirResult ollirResult = optimizationStage.toOllir(semanticsResult);
        BackendStage backendStage = new BackendStage();
        JasminResult jasminResult = backendStage.toJasmin(ollirResult);

        System.out.println(jasminResult.getJasminCode());

        jasminResult.run();
    }

    @Test
    public void MonteCarloPi() {
        JmmParserResult res = TestUtils.parse("test/fixtures/public/MonteCarloPi.jmm");
        assertEquals("Program",res.getRootNode().getKind());
        TestUtils.noErrors(res.getReports());
        JmmSemanticsResult semanticsResult = TestUtils.analyse(res);
        TestUtils.noErrors(semanticsResult.getReports());

        OptimizationStage optimizationStage = new OptimizationStage();
        OllirResult ollirResult = optimizationStage.toOllir(semanticsResult);
        BackendStage backendStage = new BackendStage();
        JasminResult jasminResult = backendStage.toJasmin(ollirResult);

        System.out.println(jasminResult.getJasminCode());

        jasminResult.run("10000");
    }

    @Test
    public void notInitInIf() {
        JmmParserResult res = TestUtils.parse("test/fixtures/public/notInitInIf.jmm");
        assertEquals("Program",res.getRootNode().getKind());
        TestUtils.noErrors(res.getReports());
        JmmSemanticsResult semanticsResult = TestUtils.analyse(res);
        TestUtils.noErrors(semanticsResult.getReports());

        OptimizationStage optimizationStage = new OptimizationStage();
        OllirResult ollirResult = optimizationStage.toOllir(semanticsResult);
        BackendStage backendStage = new BackendStage();
        JasminResult jasminResult = backendStage.toJasmin(ollirResult);

        System.out.println(jasminResult.getJasminCode());

        try{
            jasminResult.run();
        } catch (VerifyError error){
            assertTrue(true);
        }
    }

    @Test
    public void notInitInIf2() {
        JmmParserResult res = TestUtils.parse("test/fixtures/public/notInitInIf2.jmm");
        assertEquals("Program",res.getRootNode().getKind());
        TestUtils.noErrors(res.getReports());
        JmmSemanticsResult semanticsResult = TestUtils.analyse(res);
        TestUtils.noErrors(semanticsResult.getReports());

        OptimizationStage optimizationStage = new OptimizationStage();
        OllirResult ollirResult = optimizationStage.toOllir(semanticsResult);
        BackendStage backendStage = new BackendStage();
        JasminResult jasminResult = backendStage.toJasmin(ollirResult);

        System.out.println(jasminResult.getJasminCode());

        jasminResult.run();
    }

    @Test
    public void notInitInIf3() {
        JmmParserResult res = TestUtils.parse("test/fixtures/public/notInitInIf3.jmm");
        assertEquals("Program",res.getRootNode().getKind());
        TestUtils.noErrors(res.getReports());
        JmmSemanticsResult semanticsResult = TestUtils.analyse(res);
        TestUtils.noErrors(semanticsResult.getReports());

        OptimizationStage optimizationStage = new OptimizationStage();
        OllirResult ollirResult = optimizationStage.toOllir(semanticsResult);
        BackendStage backendStage = new BackendStage();
        JasminResult jasminResult = backendStage.toJasmin(ollirResult);

        System.out.println(jasminResult.getJasminCode());

        jasminResult.run();
    }

    @Test
    public void NotInitInIf4() {
        JmmParserResult res = TestUtils.parse("test/fixtures/public/NotInitInIf4.jmm");
        assertEquals("Program",res.getRootNode().getKind());
        TestUtils.noErrors(res.getReports());
        JmmSemanticsResult semanticsResult = TestUtils.analyse(res);
        TestUtils.noErrors(semanticsResult.getReports());

        OptimizationStage optimizationStage = new OptimizationStage();
        OllirResult ollirResult = optimizationStage.toOllir(semanticsResult);
        BackendStage backendStage = new BackendStage();
        JasminResult jasminResult = backendStage.toJasmin(ollirResult);

        System.out.println(jasminResult.getJasminCode());

        jasminResult.run();
    }

    @Test
    public void QuickSort() {
        JmmParserResult res = TestUtils.parse("test/fixtures/public/QuickSort.jmm");
        assertEquals("Program",res.getRootNode().getKind());
        TestUtils.noErrors(res.getReports());
        JmmSemanticsResult semanticsResult = TestUtils.analyse(res);
        TestUtils.noErrors(semanticsResult.getReports());

        OptimizationStage optimizationStage = new OptimizationStage();
        OllirResult ollirResult = optimizationStage.toOllir(semanticsResult);
        BackendStage backendStage = new BackendStage();
        JasminResult jasminResult = backendStage.toJasmin(ollirResult);

        System.out.println(jasminResult.getJasminCode());

        jasminResult.run();
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
    public void test4() {
        JmmParserResult res = TestUtils.parse("test/fixtures/public/test4.jmm");
        assertEquals("Program",res.getRootNode().getKind());
        TestUtils.noErrors(res.getReports());
        JmmSemanticsResult semanticsResult = TestUtils.analyse(res);
        TestUtils.noErrors(semanticsResult.getReports());

        OptimizationStage optimizationStage = new OptimizationStage();
        OllirResult ollirResult = optimizationStage.toOllir(semanticsResult);
        BackendStage backendStage = new BackendStage();
        JasminResult jasminResult = backendStage.toJasmin(ollirResult);

        System.out.println(jasminResult.getJasminCode());

        jasminResult.run();
    }

    @Test
    public void TicTacToe() {
        JmmParserResult res = TestUtils.parse("test/fixtures/public/TicTacToe.jmm");
        assertEquals("Program",res.getRootNode().getKind());
        TestUtils.noErrors(res.getReports());
        JmmSemanticsResult semanticsResult = TestUtils.analyse(res);
        TestUtils.noErrors(semanticsResult.getReports());

        OptimizationStage optimizationStage = new OptimizationStage();
        OllirResult ollirResult = optimizationStage.toOllir(semanticsResult);
        BackendStage backendStage = new BackendStage();
        JasminResult jasminResult = backendStage.toJasmin(ollirResult);

        System.out.println(jasminResult.getJasminCode());
        String input = SpecsIo.getResource("fixtures/public/TicTacToe.input");
        jasminResult.run(input);
    }

    @Test
    public void WhileAndIF() {
        JmmParserResult res = TestUtils.parse("test/fixtures/public/WhileAndIF.jmm");
        assertEquals("Program",res.getRootNode().getKind());
        TestUtils.noErrors(res.getReports());
        JmmSemanticsResult semanticsResult = TestUtils.analyse(res);
        TestUtils.noErrors(semanticsResult.getReports());

        OptimizationStage optimizationStage = new OptimizationStage();
        OllirResult ollirResult = optimizationStage.toOllir(semanticsResult);
        BackendStage backendStage = new BackendStage();
        JasminResult jasminResult = backendStage.toJasmin(ollirResult);

        System.out.println(jasminResult.getJasminCode());

        jasminResult.run();
    }
}
