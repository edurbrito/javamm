
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

import org.junit.Test;

import pt.up.fe.comp.TestUtils;
import pt.up.fe.comp.jmm.analysis.JmmSemanticsResult;
import pt.up.fe.specs.util.SpecsIo;

public class OptimizeTest {


    @Test
    public void testHelloWorld() { //test basic println
        //SpecsIo.getResource(
        var result = TestUtils.optimize("test/fixtures/public/HelloWorld.jmm");
        //TestUtils.noErrors(result.getReports());
        System.out.println(result.getOllirClass().getClassName());
        result.getOllirClass().show();

    }


    @Test
    public void arithmetic() {//test arithmetic
        //SpecsIo.getResource(
        var result = TestUtils.optimize("test/fixtures/public/Arithmetic.jmm");
        //TestUtils.noErrors(result.getReports());
        System.out.println(result.getOllirClass().getClassName());
        result.getOllirClass().show();

    }

    @Test
    public void simple() {//test create class and invoque methods

        var result = TestUtils.optimize("test/fixtures/public/Simple.jmm");
        //TestUtils.noErrors(result.getReports());
        System.out.println(result.getOllirClass().getClassName());
        result.getOllirClass().show();

    }

    @Test
    public void globals() {//test class variables

        var result = TestUtils.optimize("test/fixtures/public/global_access.jmm");
        //TestUtils.noErrors(result.getReports());
        System.out.println(result.getOllirClass().getClassName());
        result.getOllirClass().show();

    }
    @Test
    public void ifs() {//test class variables

        var result = TestUtils.optimize("test/fixtures/public/ifs.jmm");
        //TestUtils.noErrors(result.getReports());
        System.out.println(result.getOllirClass().getClassName());
        result.getOllirClass().show();

    }

    @Test
    public void array1() {

        var result = TestUtils.optimize("test/fixtures/public/Array1.jmm");
        //TestUtils.noErrors(result.getReports());
        System.out.println(result.getOllirClass().getClassName());
        result.getOllirClass().show();
    }

    @Test
    public void array2() {

        var result = TestUtils.optimize("test/fixtures/public/Array2.jmm");
        //TestUtils.noErrors(result.getReports());
        System.out.println(result.getOllirClass().getClassName());
        result.getOllirClass().show();
    }

    @Test
    public void array3() { //test basic println

        var result = TestUtils.optimize("test/fixtures/public/Array2.jmm");
        //TestUtils.noErrors(result.getReports());
        System.out.println(result.getOllirClass().getClassName());
        result.getOllirClass().show();

    }


    @Test
    public void array4() { //test basic println
        //SpecsIo.getResource(
        var result = TestUtils.optimize("test/fixtures/public/Array4.jmm");
        //TestUtils.noErrors(result.getReports());
        System.out.println(result.getOllirClass().getClassName());
        result.getOllirClass().show();

    }

    @Test
    public void ifAndWhile() {//test class variables

        var result = TestUtils.optimize("test/fixtures/public/whileTest.jmm");
        //TestUtils.noErrors(result.getReports());
        System.out.println(result.getOllirClass().getClassName());
        result.getOllirClass().show();

    }


}
