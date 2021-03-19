import org.junit.Test;
import pt.up.fe.comp.TestUtils;
import pt.up.fe.comp.jmm.JmmParserResult;

import static org.junit.Assert.*;

public class SyntacticalErrors {

    @Test
    public void BlowUp() {
        JmmParserResult res = TestUtils.parse("test/fixtures/public/fail/syntactical/BlowUp.jmm");
        assertEquals("Program",res.getRootNode().getKind());
        assertEquals(4, TestUtils.getNumErrors(res.getReports()));
    }

    @Test
    public void CompleteWhileTest() {
        JmmParserResult res = TestUtils.parse("test/fixtures/public/fail/syntactical/CompleteWhileTest.jmm");
        assertEquals("Program",res.getRootNode().getKind());
        assertEquals(4, TestUtils.getNumErrors(res.getReports()));          // Não sei qual o numero
    }

    @Test
    public void MissingRightPar() {
        JmmParserResult res = TestUtils.parse("test/fixtures/public/fail/syntactical/MissingRightPar.jmm");
        assertEquals("Program",res.getRootNode().getKind());
        assertEquals(1, TestUtils.getNumErrors(res.getReports()));
    }
}
