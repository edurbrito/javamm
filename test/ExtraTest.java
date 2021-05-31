import org.junit.Test;
import pt.up.fe.comp.TestUtils;
import pt.up.fe.comp.jmm.JmmParserResult;
import pt.up.fe.comp.jmm.analysis.JmmSemanticsResult;
import pt.up.fe.comp.jmm.jasmin.JasminResult;
import pt.up.fe.comp.jmm.ollir.OllirResult;

import static org.junit.Assert.assertEquals;

public class ExtraTest {

    @Test
    public void Test1() {
        JmmParserResult res = TestUtils.parse("test/fixtures/public/extra/Test1.jmm");
        assertEquals("Program", res.getRootNode().getKind());
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
    public void Test2() {
        JmmParserResult res = TestUtils.parse("test/fixtures/public/extra/Test2.jmm");
        assertEquals("Program", res.getRootNode().getKind());
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
    public void Test3() {
        JmmParserResult res = TestUtils.parse("test/fixtures/public/extra/Test3.jmm");
        assertEquals("Program", res.getRootNode().getKind());
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
    public void Test4() {
        JmmParserResult res = TestUtils.parse("test/fixtures/public/extra/Test4.jmm");
        assertEquals("Program", res.getRootNode().getKind());
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
    public void Test5() {
        JmmParserResult res = TestUtils.parse("test/fixtures/public/extra/Test5.jmm");
        assertEquals("Program", res.getRootNode().getKind());
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

    // -----------------------------------------------------------------------------------------------------------

    @Test
    public void MethodOverloading() {
        JmmParserResult res = TestUtils.parse("test/fixtures/public/extra/MethodOverloading.jmm");
        assertEquals("Program", res.getRootNode().getKind());
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
    public void Constants() {
        JmmParserResult res = TestUtils.parse("test/fixtures/public/extra/Constants.jmm");
        assertEquals("Program", res.getRootNode().getKind());
        TestUtils.noErrors(res.getReports());
        JmmSemanticsResult semanticsResult = TestUtils.analyse(res);
        TestUtils.noErrors(semanticsResult.getReports());

        OptimizationStage optimizationStage = new OptimizationStage();
        OllirResult ollirResult = optimizationStage.toOllir(semanticsResult);
        BackendStage backendStage = new BackendStage();
        JasminResult jasminResult = backendStage.toJasmin(ollirResult);

        System.out.println("Jasmin Code: \n" + jasminResult.getJasminCode());

        var output = jasminResult.run();
        assertEquals("62103", output.trim());
    }

    @Test
    public void VarNotInit() {
        JmmParserResult res = TestUtils.parse("test/fixtures/public/extra/VarNotInit.jmm");
        TestUtils.noErrors(res.getReports());
        JmmSemanticsResult semanticsResult = TestUtils.analyse(res);
        TestUtils.noErrors(semanticsResult.getReports());
    }

    @Test
    public void HillClimbing() {
        JmmParserResult res = TestUtils.parse("test/fixtures/public/extra/HillClimbing.jmm");
        assertEquals("Program", res.getRootNode().getKind());
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
    public void FlagDraw() {
        JmmParserResult res = TestUtils.parse("test/fixtures/public/extra/FlagDraw.jmm");
        assertEquals("Program", res.getRootNode().getKind());
        TestUtils.noErrors(res.getReports());
        JmmSemanticsResult semanticsResult = TestUtils.analyse(res);
        TestUtils.noErrors(semanticsResult.getReports());

        OptimizationStage optimizationStage = new OptimizationStage();
        OllirResult ollirResult = optimizationStage.toOllir(semanticsResult);
        BackendStage backendStage = new BackendStage();
        JasminResult jasminResult = backendStage.toJasmin(ollirResult);

        System.out.println(jasminResult.getJasminCode());

        jasminResult.run("50\n50");
    }

    // -----------------------------------------------------------------------------------------------------------

    @Test
    public void ConstantPropagation() {
        JmmParserResult res = TestUtils.parse("test/fixtures/public/extra/ConstantPropagation.jmm");
        assertEquals("Program", res.getRootNode().getKind());
        TestUtils.noErrors(res.getReports());
        JmmSemanticsResult semanticsResult = TestUtils.analyse(res);
        TestUtils.noErrors(semanticsResult.getReports());

        OptimizationStage optimizationStage = new OptimizationStage();
        semanticsResult = optimizationStage.optimize(semanticsResult);
        OllirResult ollirResult = optimizationStage.toOllir(semanticsResult);

        BackendStage backendStage = new BackendStage();
        JasminResult jasminResult = backendStage.toJasmin(ollirResult);

        System.out.println(jasminResult.getJasminCode());

        jasminResult.run();
    }

    @Test
    public void ConstantFolding() {
        JmmParserResult res = TestUtils.parse("test/fixtures/public/extra/ConstantFolding.jmm");
        assertEquals("Program", res.getRootNode().getKind());
        TestUtils.noErrors(res.getReports());
        JmmSemanticsResult semanticsResult = TestUtils.analyse(res);
        TestUtils.noErrors(semanticsResult.getReports());

        OptimizationStage optimizationStage = new OptimizationStage();
        semanticsResult = optimizationStage.optimize(semanticsResult);
        OllirResult ollirResult = optimizationStage.toOllir(semanticsResult);

        BackendStage backendStage = new BackendStage();
        JasminResult jasminResult = backendStage.toJasmin(ollirResult);

        System.out.println(jasminResult.getJasminCode());

        jasminResult.run();
    }

    @Test
    public void WhileOptimize() {
        JmmParserResult res = TestUtils.parse("test/fixtures/public/extra/While.jmm");
        assertEquals("Program", res.getRootNode().getKind());
        TestUtils.noErrors(res.getReports());
        JmmSemanticsResult semanticsResult = TestUtils.analyse(res);
        TestUtils.noErrors(semanticsResult.getReports());

        OptimizationStage optimizationStage = new OptimizationStage();
        semanticsResult = optimizationStage.optimize(semanticsResult);
        OllirResult ollirResult = optimizationStage.toOllir(semanticsResult);

        BackendStage backendStage = new BackendStage();
        JasminResult jasminResult = backendStage.toJasmin(ollirResult);

        System.out.println(jasminResult.getJasminCode());

        jasminResult.run();
    }
}
