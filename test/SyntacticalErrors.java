import org.junit.Test;
import pt.up.fe.comp.TestUtils;
import pt.up.fe.comp.jmm.JmmParserResult;

import static org.junit.Assert.*;

public class SyntacticalErrors {

    @Test
    public void MissingRightPar() {
        // Ler de ficheiro

        JmmParserResult res = TestUtils.parse("test/fixtures/public/fail/syntactical/MissingRightPar.jmm");
        assertEquals("Program",res.getRootNode().getKind());
        assertEquals(TestUtils.getNumErrors(res.getReports()),1);
    }

    @Test
    public void MultipleSequential() {
        // Ler de ficheiro

        JmmParserResult res = TestUtils.parse("test/fixtures/public/fail/syntactical/MultipleSequential.jmm");
        assertEquals("Program",res.getRootNode().getKind());
        assertEquals(TestUtils.getNumErrors(res.getReports()),2);
    }

    @Test
    public void NestedLoop() {
        // Ler de ficheiro

        JmmParserResult res = TestUtils.parse("test/fixtures/public/fail/syntactical/NestedLoop.jmm");
        assertEquals("Program",res.getRootNode().getKind());
        assertEquals(TestUtils.getNumErrors(res.getReports()),2);
    }
}
