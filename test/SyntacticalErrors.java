import org.junit.Test;
import pt.up.fe.comp.TestUtils;
import pt.up.fe.comp.jmm.JmmParserResult;

import static org.junit.Assert.*;

public class SyntacticalErrors {

    @Test
    public void BlowUp() {
        JmmParserResult res = TestUtils.parse("test/fixtures/public/fail/syntactical/BlowUp.jmm");
        TestUtils.mustFail(res.getReports());
    }

    @Test
    public void CompleteWhileTest() {
        JmmParserResult res = TestUtils.parse("test/fixtures/public/fail/syntactical/CompleteWhileTest.jmm");
        TestUtils.mustFail(res.getReports());
    }

    @Test
    public void LengthError() {
        JmmParserResult res = TestUtils.parse("test/fixtures/public/fail/syntactical/LengthError.jmm");
        assertEquals("Program", res.getRootNode().getKind());
        assertEquals(1, TestUtils.getNumErrors(res.getReports()));
    }

    @Test
    public void MissingRightPar() {
        JmmParserResult res = TestUtils.parse("test/fixtures/public/fail/syntactical/MissingRightPar.jmm");
        assertEquals("Program",res.getRootNode().getKind());
        assertEquals(1, TestUtils.getNumErrors(res.getReports()));
    }

    @Test
    public void MultipleSequential() {
        JmmParserResult res = TestUtils.parse("test/fixtures/public/fail/syntactical/MultipleSequential.jmm");
        assertEquals("Program",res.getRootNode().getKind());
        assertEquals(2, TestUtils.getNumErrors(res.getReports()));
    }

    @Test
    public void NestedLoop() {

        JmmParserResult res = TestUtils.parse("test/fixtures/public/fail/syntactical/NestedLoop.jmm");
        assertEquals("Program",res.getRootNode().getKind());
        assertEquals(2, TestUtils.getNumErrors(res.getReports()));
    }
}
