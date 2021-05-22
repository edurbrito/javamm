
/**
 * Copyright 2021 SPeCS.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with
 * the License. You may obtain a copy of the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on
 * an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations under the License. under the License.
 */

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import pt.up.fe.comp.TestUtils;
import pt.up.fe.comp.jmm.ollir.OllirResult;
import pt.up.fe.specs.util.SpecsIo;

public class BackendTest {

    @Test
    public void testHelloWorld() {
        var result = TestUtils.backend(new OllirResult(SpecsIo.getResource("fixtures/public/ollir/HelloWorld.ollir")));
        TestUtils.noErrors(result.getReports());
        System.out.println("Jasmin Code: \n" + result.getJasminCode());
        var output = result.run();
        assertEquals("Hello, World!", output.trim());
    }

    @Test
    public void testSimple() {
        var result = TestUtils.backend(new OllirResult(SpecsIo.getResource("fixtures/public/ollir/Simple.ollir")));
        TestUtils.noErrors(result.getReports());

        System.out.println("Jasmin Code: \n" + result.getJasminCode());

        var output = result.run();
        assertEquals("30", output.trim());
    }

    @Test
    public void testClass1() {

        var result = TestUtils.backend(new OllirResult(SpecsIo.getResource("fixtures/public/ollir/myclass1.ollir")));
        TestUtils.noErrors(result.getReports());

        System.out.println("Jasmin Code: \n" + result.getJasminCode());

        var output = result.run();
        assertEquals("12", output.trim());
    }

    @Test
    public void testClass2() {
        var result = TestUtils.backend(new OllirResult(SpecsIo.getResource("fixtures/public/ollir/myclass2.ollir")));
        TestUtils.noErrors(result.getReports());

        System.out.println("Jasmin Code: \n" + result.getJasminCode());

        var output = result.run();
    }

    @Test
    public void testClass3() {
        var result = TestUtils.backend(new OllirResult(SpecsIo.getResource("fixtures/public/ollir/myclass3.ollir")));
        TestUtils.noErrors(result.getReports());

        System.out.println("Jasmin Code: \n" + result.getJasminCode());

        var output = result.run();
    }

    @Test
    public void testClass4() {
        var result = TestUtils.backend(new OllirResult(SpecsIo.getResource("fixtures/public/ollir/myclass4.ollir")));
        TestUtils.noErrors(result.getReports());

        System.out.println("Jasmin Code: \n" + result.getJasminCode());

        var output = result.run();
    }

    @Test
    public void testFac() {
        var result = TestUtils.backend(new OllirResult(SpecsIo.getResource("fixtures/public/ollir/Fac.ollir")));
        TestUtils.noErrors(result.getReports());

        System.out.println("Jasmin Code: \n" + result.getJasminCode());

        var output = result.run();
        assertEquals("3628800", output.trim());
    }

    @Test
    public void testLazySort() {
        var result = TestUtils.backend(new OllirResult(SpecsIo.getResource("fixtures/public/ollir/LazySort.ollir")));
        TestUtils.noErrors(result.getReports());

        System.out.println("Jasmin Code: \n" + result.getJasminCode());

        var output = result.run();
        //assertEquals("3628800", output.trim());
    }
}
