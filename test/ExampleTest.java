
import org.junit.Test;
import pt.up.fe.comp.TestUtils;

import static org.junit.Assert.assertEquals;

public class ExampleTest {

    @Test
    public void testExpression() {
        // Ler de ficheiro
        assertEquals("Parse", TestUtils.parse("test/fixtures/public/Array1.jmm").getRootNode().getKind());
        assertEquals("Parse", TestUtils.parse("test/fixtures/public/Array2.jmm").getRootNode().getKind());
        assertEquals("Parse", TestUtils.parse("test/fixtures/public/Array3.jmm").getRootNode().getKind());
        assertEquals("Parse", TestUtils.parse("test/fixtures/public/Custom.jmm").getRootNode().getKind());
        assertEquals("Parse", TestUtils.parse("test/fixtures/public/FindMaximum.jmm").getRootNode().getKind());
         assertEquals("Parse", TestUtils.parse("test/fixtures/public/HelloWorld.jmm").getRootNode().getKind());
         assertEquals("Parse", TestUtils.parse("test/fixtures/public/Lazysort.jmm").getRootNode().getKind());
         assertEquals("Parse", TestUtils.parse("test/fixtures/public/Lif.jmm").getRootNode().getKind());
         assertEquals("Parse", TestUtils.parse("test/fixtures/public/MonteCarloPi.jmm").getRootNode().getKind());
         assertEquals("Parse", TestUtils.parse("test/fixtures/public/notInitInIf.jmm").getRootNode().getKind());
         assertEquals("Parse", TestUtils.parse("test/fixtures/public/notInitInIf2.jmm").getRootNode().getKind());
         assertEquals("Parse", TestUtils.parse("test/fixtures/public/notInitInIf3.jmm").getRootNode().getKind());
         assertEquals("Parse", TestUtils.parse("test/fixtures/public/notInitInIf4.jmm").getRootNode().getKind());
         assertEquals("Parse", TestUtils.parse("test/fixtures/public/QuickSort.jmm").getRootNode().getKind());
         assertEquals("Parse", TestUtils.parse("test/fixtures/public/Simple.jmm").getRootNode().getKind());
         assertEquals("Parse", TestUtils.parse("test/fixtures/public/test2.jmm").getRootNode().getKind());
         assertEquals("Parse", TestUtils.parse("test/fixtures/public/test3.jmm").getRootNode().getKind());
         assertEquals("Parse", TestUtils.parse("test/fixtures/public/test4.jmm").getRootNode().getKind());
         assertEquals("Parse", TestUtils.parse("test/fixtures/public/test5.jmm").getRootNode().getKind());
         assertEquals("Parse", TestUtils.parse("test/fixtures/public/TicTacToe.jmm").getRootNode().getKind());
         assertEquals("Parse", TestUtils.parse("test/fixtures/public/varNotInit.jmm").getRootNode().getKind());
         assertEquals("Parse", TestUtils.parse("test/fixtures/public/WhileAndIF.jmm").getRootNode().getKind());
	}
}
