
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
import pt.up.fe.comp.jmm.JmmParserResult;
import pt.up.fe.comp.jmm.analysis.JmmSemanticsResult;

import static org.junit.Assert.assertEquals;


public class OptimizeTest {

    @Test
    public void Arithmetic() {//test arithmetic
        //SpecsIo.getResource(
        var result = TestUtils.optimize("test/fixtures/public/Arithmetic.jmm");
        //TestUtils.noErrors(result.getReports());
        System.out.println(result.getOllirClass().getClassName());
        result.getOllirClass().show();

    }

    @Test
    public void Array1() {

        var result = TestUtils.optimize("test/fixtures/public/Array1.jmm");
        //TestUtils.noErrors(result.getReports());
        System.out.println(result.getOllirClass().getClassName());
        result.getOllirClass().show();
    }

    @Test
    public void Array2() {

        var result = TestUtils.optimize("test/fixtures/public/Array2.jmm");
        //TestUtils.noErrors(result.getReports());
        System.out.println(result.getOllirClass().getClassName());
        result.getOllirClass().show();
    }

    @Test
    public void Array3() { //test basic println

        var result = TestUtils.optimize("test/fixtures/public/Array3.jmm");
        //TestUtils.noErrors(result.getReports());
        System.out.println(result.getOllirClass().getClassName());
        result.getOllirClass().show();

    }


    @Test
    public void Array4() { //test basic println
        //SpecsIo.getResource(
        var result = TestUtils.optimize("test/fixtures/public/Array4.jmm");
        //TestUtils.noErrors(result.getReports());
        System.out.println(result.getOllirClass().getClassName());
        result.getOllirClass().show();
    }

    @Test
    public void Boolean() {//test class variables

        var result = TestUtils.optimize("test/fixtures/public/Boolean.jmm");
        //TestUtils.noErrors(result.getReports());
        System.out.println(result.getOllirClass().getClassName());
        result.getOllirClass().show();

    }

    @Test
    public void Custom() { //test basic println
        //SpecsIo.getResource(
        var result = TestUtils.optimize("test/fixtures/public/Custom.jmm");
        //TestUtils.noErrors(result.getReports());
        System.out.println(result.getOllirClass().getClassName());
        result.getOllirClass().show();
    }

    @Test
    public void FindMaximum() { //test basic println
        //SpecsIo.getResource(
        var result = TestUtils.optimize("test/fixtures/public/FindMaximum.jmm");
        //TestUtils.noErrors(result.getReports());
        System.out.println(result.getOllirClass().getClassName());
        result.getOllirClass().show();
    }


    @Test
    public void GetField() { //test basic println
        //SpecsIo.getResource(
        var result = TestUtils.optimize("test/fixtures/public/GetField.jmm");
        //TestUtils.noErrors(result.getReports());
        System.out.println(result.getOllirClass().getClassName());
        result.getOllirClass().show();
    }

    @Test
    public void HelloWorld() { //test basic println
        //SpecsIo.getResource(
        var result = TestUtils.optimize("test/fixtures/public/HelloWorld.jmm");
        //TestUtils.noErrors(result.getReports());
        System.out.println(result.getOllirClass().getClassName());
        result.getOllirClass().show();

    }

    @Test
    public void Lazysort() { //test basic println
        //SpecsIo.getResource(
        var result = TestUtils.optimize("test/fixtures/public/Lazysort.jmm");
        //TestUtils.noErrors(result.getReports());
        System.out.println(result.getOllirClass().getClassName());
        result.getOllirClass().show();

    }

    @Test
    public void Length() { //test basic println
        //SpecsIo.getResource(
        var result = TestUtils.optimize("test/fixtures/public/Length.jmm");
        //TestUtils.noErrors(result.getReports());
        System.out.println(result.getOllirClass().getClassName());
        result.getOllirClass().show();

    }

    public void Life() { //test basic println
        //SpecsIo.getResource(
        var result = TestUtils.optimize("test/fixtures/public/Life.jmm");
        //TestUtils.noErrors(result.getReports());
        System.out.println(result.getOllirClass().getClassName());
        result.getOllirClass().show();

    }

    @Test
    public void GlobalAccess() {//test class variables

        var result = TestUtils.optimize("test/fixtures/public/GlobalAccess.jmm");
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
    public void MonteCarloPi() {
        JmmParserResult res = TestUtils.parse("test/fixtures/public/MonteCarloPi.jmm");
        assertEquals("Program",res.getRootNode().getKind());
        TestUtils.noErrors(res.getReports());
        JmmSemanticsResult semanticsResult = TestUtils.analyse(res);
        TestUtils.noErrors(semanticsResult.getReports());
    }

    @Test
    public void notInitInIf() {
        JmmParserResult res = TestUtils.parse("test/fixtures/public/notInitInIf.jmm");
        assertEquals("Program",res.getRootNode().getKind());
        TestUtils.noErrors(res.getReports());
        JmmSemanticsResult semanticsResult = TestUtils.analyse(res);
        TestUtils.noErrors(semanticsResult.getReports());
    }

    @Test
    public void notInitInIf2() {
        JmmParserResult res = TestUtils.parse("test/fixtures/public/notInitInIf2.jmm");
        assertEquals("Program",res.getRootNode().getKind());
        TestUtils.noErrors(res.getReports());
        JmmSemanticsResult semanticsResult = TestUtils.analyse(res);
        TestUtils.noErrors(semanticsResult.getReports());
    }

    @Test
    public void notInitInIf3() {
        JmmParserResult res = TestUtils.parse("test/fixtures/public/notInitInIf3.jmm");
        assertEquals("Program",res.getRootNode().getKind());
        TestUtils.noErrors(res.getReports());
        JmmSemanticsResult semanticsResult = TestUtils.analyse(res);
        TestUtils.noErrors(semanticsResult.getReports());
    }

    @Test
    public void NotInitInIf4() {
        JmmParserResult res = TestUtils.parse("test/fixtures/public/NotInitInIf4.jmm");
        assertEquals("Program",res.getRootNode().getKind());
        TestUtils.noErrors(res.getReports());
        JmmSemanticsResult semanticsResult = TestUtils.analyse(res);
        TestUtils.noErrors(semanticsResult.getReports());
    }

    @Test
    public void QuickSort() {
        JmmParserResult res = TestUtils.parse("test/fixtures/public/QuickSort.jmm");
        assertEquals("Program",res.getRootNode().getKind());
        TestUtils.noErrors(res.getReports());
        JmmSemanticsResult semanticsResult = TestUtils.analyse(res);
        TestUtils.noErrors(semanticsResult.getReports());
    }

    @Test
    public void Simple() {
        JmmParserResult res = TestUtils.parse("test/fixtures/public/Simple.jmm");
        assertEquals("Program",res.getRootNode().getKind());
        TestUtils.noErrors(res.getReports());
        JmmSemanticsResult semanticsResult = TestUtils.analyse(res);
        TestUtils.noErrors(semanticsResult.getReports());
    }

    @Test
    public void test2() {
        JmmParserResult res = TestUtils.parse("test/fixtures/public/test2.jmm");
        assertEquals("Program",res.getRootNode().getKind());
        TestUtils.noErrors(res.getReports());
        JmmSemanticsResult semanticsResult = TestUtils.analyse(res);
        TestUtils.noErrors(semanticsResult.getReports());
    }

    @Test
    public void test3() {
        JmmParserResult res = TestUtils.parse("test/fixtures/public/test3.jmm");
        assertEquals("Program",res.getRootNode().getKind());
        TestUtils.noErrors(res.getReports());
        JmmSemanticsResult semanticsResult = TestUtils.analyse(res);
        TestUtils.noErrors(semanticsResult.getReports());
    }

    @Test
    public void test4() {
        JmmParserResult res = TestUtils.parse("test/fixtures/public/test4.jmm");
        assertEquals("Program",res.getRootNode().getKind());
        TestUtils.noErrors(res.getReports());
        JmmSemanticsResult semanticsResult = TestUtils.analyse(res);
        TestUtils.noErrors(semanticsResult.getReports());
    }

    @Test
    public void test5() {
        JmmParserResult res = TestUtils.parse("test/fixtures/public/test5.jmm");
        assertEquals("Program",res.getRootNode().getKind());
        TestUtils.noErrors(res.getReports());
        JmmSemanticsResult semanticsResult = TestUtils.analyse(res);
        TestUtils.noErrors(semanticsResult.getReports());
    }

    @Test
    public void TicTacToe() {
        JmmParserResult res = TestUtils.parse("test/fixtures/public/TicTacToe.jmm");
        assertEquals("Program",res.getRootNode().getKind());
        TestUtils.noErrors(res.getReports());
        JmmSemanticsResult semanticsResult = TestUtils.analyse(res);
        TestUtils.noErrors(semanticsResult.getReports());
    }

    @Test
    public void WhileAndIF() {
        JmmParserResult res = TestUtils.parse("test/fixtures/public/WhileAndIF.jmm");
        assertEquals("Program",res.getRootNode().getKind());
        TestUtils.noErrors(res.getReports());
        JmmSemanticsResult semanticsResult = TestUtils.analyse(res);
        TestUtils.noErrors(semanticsResult.getReports());
    }

    @Test
    public void ifAndWhile() {//test class variables

        var result = TestUtils.optimize("test/fixtures/public/whileTest.jmm");
        //TestUtils.noErrors(result.getReports());
        System.out.println(result.getOllirClass().getClassName());
        result.getOllirClass().show();

    }


    @Test
    public void personal() {//test class variables

        var result = TestUtils.optimize("test/fixtures/public/personal.jmm");
        //TestUtils.noErrors(result.getReports());
        System.out.println(result.getOllirClass().getClassName());
        result.getOllirClass().show();

    }

}
