import java.util.*;

import org.specs.comp.ollir.*;

import pt.up.fe.comp.jmm.jasmin.JasminBackend;
import pt.up.fe.comp.jmm.jasmin.JasminResult;
import pt.up.fe.comp.jmm.ollir.OllirResult;
import pt.up.fe.comp.jmm.report.Report;
import pt.up.fe.comp.jmm.report.Stage;
import pt.up.fe.specs.util.SpecsIo;

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

public class BackendStage implements JasminBackend {

    HashMap<ElementType, String> types = new HashMap<>() {{
        put(ElementType.INT32,"I");
        put(ElementType.BOOLEAN,"Z");
        put(ElementType.STRING,"Ljava/lang/String;");
        put(ElementType.ARRAYREF,"[");
    }};

    @Override
    public JasminResult toJasmin(OllirResult ollirResult) {

        ClassUnit ollirClass = ollirResult.getOllirClass();

        try {

            // Example of what you can do with the OLLIR class
            ollirClass.checkMethodLabels(); // check the use of labels in the OLLIR loaded
            ollirClass.buildCFGs(); // build the CFG of each method
            ollirClass.outputCFGs(); // output to .dot files the CFGs, one per method
            ollirClass.buildVarTables(); // build the table of variables for each method
            ollirClass.show(); // print to console main information about the input OLLIR

            // Convert the OLLIR to a String containing the equivalent Jasmin code

            String jasminCode = generateJasmin(ollirClass); // Convert node ...

            // More reports from this stage
            List<Report> reports = new ArrayList<>();

            return new JasminResult(ollirResult, jasminCode, reports);

        } catch (OllirErrorException e) {
            return new JasminResult(ollirClass.getClassName(), null,
                    Arrays.asList(Report.newError(Stage.GENERATION, -1, -1, "Exception during Jasmin generation", e)));
        }

    }

    public String generateJasmin(ClassUnit ollirClass) {
        String result = "";
        result += generateClassInfo(ollirClass);
        result += generateMethods(ollirClass);
        return result;
    }

    public String generateClassInfo(ClassUnit ollirClass){
        StringBuilder builder = new StringBuilder();

        builder.append("; class with syntax accepted by jasmin 2.3");
        builder.append("\n");
        builder.append("\n");
        builder.append(".class public ");
        builder.append(ollirClass.getClassName());
        builder.append("\n");
        builder.append(".super ");
        builder.append("java/lang/Object"); // super class?

        builder.append(generateFields(ollirClass));

        builder.append(generateConstructor(ollirClass));

        return builder.toString();
    }

    public String generateFields(ClassUnit ollirClass){

        StringBuilder builder = new StringBuilder();

        builder.append("\n");
        builder.append("\n");

        for(Field field : ollirClass.getFields()) {
            builder.append(".field ");
            builder.append(field.getFieldAccessModifier().toString().toLowerCase());
            builder.append(" ");
            builder.append(field.getFieldName());
            builder.append(" ");
            builder.append(types.get(field.getFieldType().getTypeOfElement())); // Está bem?
        }

        return builder.toString();
    }

    public String generateConstructor(ClassUnit ollirClass){

        StringBuilder builder = new StringBuilder();

        builder.append("\n");
        builder.append("\n");
        builder.append(";");
        builder.append("\n");
        builder.append("; standard initializer");
        builder.append("\n");
        builder.append(".method public <init>()V");
        builder.append("\n");
        builder.append("\taload_0");
        builder.append("\n");
        builder.append("\n");
        builder.append("\tinvokenonvirtual java/lang/Object/<init>()V");
        builder.append("\n");
        builder.append("\treturn");
        builder.append("\n");
        builder.append(".end method");

        return builder.toString();
    }

    public String generateMethods(ClassUnit ollirClass){

        StringBuilder builder = new StringBuilder();

        for(Method method : ollirClass.getMethods()) {
            if(method.isConstructMethod())
                continue;

            builder.append("\n");
            builder.append("\n");
            builder.append(".method ");
            builder.append(method.getMethodAccessModifier().toString().toLowerCase());
            builder.append(" ");

            if(method.isStaticMethod())
                builder.append("static ");
            if(method.isFinalMethod())
                builder.append("final ");

            builder.append(method.getMethodName());
            builder.append("(");
            for(Element element: method.getParams()) {
                builder.append(types.get(element.getType().getTypeOfElement()) + ";"); // Separação entre params?

                // Tipo de array de strings?
            }
            builder.append(")V");

            builder.append("\n");
            builder.append("\t.limit stack 99");
            builder.append("\n");
            builder.append("\t.limit locals 99");

            for(Instruction instruction : method.getInstructions()) {
                builder.append("\nINST: " + instruction.getInstType());
                String inst = generateOperation(instruction);
                builder.append(inst);
            }

            builder.append("\n");
            builder.append("\treturn");
            builder.append("\n");
            builder.append(".end method");
        }

        return builder.toString();
    }

    public String generateOperation(Instruction instruction){
        String result = "";
        System.out.println("INSTYPE: " + instruction.getInstType().toString());
        switch (instruction.getInstType()) {
            case ASSIGN:
                result = generateAssign(instruction);
                break;
            default:
                break;
        }

        return result;
    }

    public String generateAssign(Instruction instruction){
        try {
            System.out.println("SUC1: " + instruction.getSucc1().getNodeType());
        }
        catch(Exception e) {
            e.printStackTrace();
        }
        return "";
    }
}
