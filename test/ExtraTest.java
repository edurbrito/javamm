import org.junit.Test;
import pt.up.fe.comp.TestUtils;
import pt.up.fe.comp.jmm.JmmParserResult;
import pt.up.fe.comp.jmm.analysis.JmmSemanticsResult;
import pt.up.fe.comp.jmm.jasmin.JasminResult;
import pt.up.fe.comp.jmm.ollir.OllirResult;

import static org.junit.Assert.assertEquals;

public class ExtraTest {

    @Test
    public void MethodOverloading() {
        JmmParserResult res = TestUtils.parse("test/fixtures/public/extra/MethodOverloading.jmm");
        assertEquals("Program", res.getRootNode().getKind());
        TestUtils.noErrors(res.getReports());
        JmmSemanticsResult semanticsResult = TestUtils.analyse(res);
        TestUtils.noErrors(semanticsResult.getReports());

        //var ollirResult = TestUtils.optimize("test/fixtures/public/extra/MethodOverloading.jmm");
        //var jasminResult = TestUtils.backend(ollirResult);

        //System.out.println("Jasmin Code: \n" + jasminResult.getJasminCode());

        //var output = jasminResult.run();
        //assertEquals("Hello, World!", output.trim());
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
}
