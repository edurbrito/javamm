
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
import pt.up.fe.comp.jmm.ollir.OllirUtils;
import pt.up.fe.specs.util.SpecsIo;

import java.util.ArrayList;

public class BackendTest {

    @Test
    public void testHelloWorld() {
        var result = TestUtils.backend(SpecsIo.getResource("fixtures/public/HelloWorld.jmm"));
        TestUtils.noErrors(result.getReports());

        var output = result.run();
        assertEquals("Hello, World!", output.trim());
    }

    @Test
    public void testClass1() {

        var result = TestUtils.backend(new OllirResult(OllirUtils.parse(SpecsIo.getResource("fixtures/public/ollir/myclass1.ollir")), null, new ArrayList<>()));
        TestUtils.noErrors(result.getReports());

        //var result = TestUtils.backend(SpecsIo.getResource("fixtures/public/ollir/myclass1.ollir"));
        //TestUtils.noErrors(result.getReports());

        //var output = result.run();
        //assertEquals("Hello, World!", output.trim());

        System.out.println("Jasmin Code: \n" + result.getJasminCode());
    }

    @Test
    public void testClass2() {
        var result = TestUtils.backend(SpecsIo.getResource("fixtures/public/ollir/myclass2.ollir"));
        TestUtils.noErrors(result.getReports());

        var output = result.run();
        assertEquals("Hello, World!", output.trim());
    }

    @Test
    public void testClass3() {
        var result = TestUtils.backend(SpecsIo.getResource("fixtures/public/ollir/myclass3.ollir"));
        TestUtils.noErrors(result.getReports());

        var output = result.run();
        assertEquals("Hello, World!", output.trim());
    }

    @Test
    public void testClass4() {
        var result = TestUtils.backend(SpecsIo.getResource("fixtures/public/ollir/myclass4.ollir"));
        TestUtils.noErrors(result.getReports());

        var output = result.run();
        assertEquals("Hello, World!", output.trim());
    }
}
