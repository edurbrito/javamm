import org.junit.Test;
import pt.up.fe.comp.TestUtils;
import pt.up.fe.comp.jmm.analysis.JmmSemanticsResult;

import static org.junit.Assert.*;

public class SemanticErrors {
    @Test
    public void ArrIndexNotInt() {
        JmmSemanticsResult res = TestUtils.analyse("test/fixtures/public/fail/semantic/arr_index_not_int.jmm");
        TestUtils.mustFail(res.getReports());
        assertEquals(1, TestUtils.getNumErrors(res.getReports()));
    }

    @Test
    public void ArrSizeNotInt() {
        JmmSemanticsResult res = TestUtils.analyse("test/fixtures/public/fail/semantic/arr_size_not_int.jmm");
        TestUtils.mustFail(res.getReports());
        assertEquals(1, TestUtils.getNumErrors(res.getReports()));
    }

    @Test
    public void BadArguments() {
        JmmSemanticsResult res = TestUtils.analyse("test/fixtures/public/fail/semantic/badArguments.jmm");
        TestUtils.mustFail(res.getReports());
        assertEquals(3, TestUtils.getNumErrors(res.getReports()));
    }

    @Test
    public void BinopIncomp() {
        JmmSemanticsResult res = TestUtils.analyse("test/fixtures/public/fail/semantic/binop_incomp.jmm");
        TestUtils.mustFail(res.getReports());
        assertEquals(1, TestUtils.getNumErrors(res.getReports()));
    }

    @Test
    public void FuncNotFound() {
        JmmSemanticsResult res = TestUtils.analyse("test/fixtures/public/fail/semantic/funcNotFound.jmm");
        TestUtils.mustFail(res.getReports());
        assertEquals(2, TestUtils.getNumErrors(res.getReports()));
    }

    @Test
    public void SimpleLength() {
        JmmSemanticsResult res = TestUtils.analyse("test/fixtures/public/fail/semantic/simple_length.jmm");
        TestUtils.mustFail(res.getReports());
        assertEquals(1, TestUtils.getNumErrors(res.getReports()));
    }

    @Test
    public void VarExpIncomp() {
        JmmSemanticsResult res = TestUtils.analyse("test/fixtures/public/fail/semantic/var_exp_incomp.jmm");
        TestUtils.mustFail(res.getReports());
        assertEquals(1, TestUtils.getNumErrors(res.getReports()));
    }

    @Test
    public void VarLitIncomp() {
        JmmSemanticsResult res = TestUtils.analyse("test/fixtures/public/fail/semantic/var_lit_incomp.jmm");
        TestUtils.mustFail(res.getReports());
        assertEquals(1, TestUtils.getNumErrors(res.getReports()));
    }

    @Test
    public void VarUndef() {
        JmmSemanticsResult res = TestUtils.analyse("test/fixtures/public/fail/semantic/var_undef.jmm");
        TestUtils.mustFail(res.getReports());
        assertEquals(1, TestUtils.getNumErrors(res.getReports()));
    }
/*
    @Test
    public void MissType() {
        JmmSemanticsResult res = TestUtils.analyse("test/fixtures/public/fail/semantic/extra/miss_type.jmm");
        TestUtils.mustFail(res.getReports());
        assertEquals(1, TestUtils.getNumErrors(res.getReports()));
    }
}
