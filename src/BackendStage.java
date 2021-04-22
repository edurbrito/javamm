import java.util.*;

import org.specs.comp.ollir.*;

import pt.up.fe.comp.jmm.jasmin.JasminBackend;
import pt.up.fe.comp.jmm.jasmin.JasminResult;
import pt.up.fe.comp.jmm.ollir.OllirResult;
import pt.up.fe.comp.jmm.ollir.OllirUtils;
import pt.up.fe.comp.jmm.report.Report;
import pt.up.fe.comp.jmm.report.Stage;
import pt.up.fe.specs.util.SpecsIo;

import javax.print.DocFlavor;

import static org.specs.comp.ollir.ElementType.*;

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
        put(ARRAYREF,"[");
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

        for(Field field : ollirClass.getFields()) {
            builder.append("\n");
            builder.append(".field ");
            builder.append(lower(field.getFieldAccessModifier().toString()));
            builder.append(" ");
            builder.append(field.getFieldName());
            builder.append(" ");
            builder.append(types.get(field.getFieldType().getTypeOfElement()));
        }

        return builder.toString();
    }

    public String generateConstructor(ClassUnit ollirClass) {

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

            System.out.println("Method: " + method.getMethodName());

            builder.append("\n");
            builder.append("\n");
            builder.append(".method ");
            builder.append(lower(method.getMethodAccessModifier().toString()));
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
            builder.append("\n");

            for(Instruction instruction : method.getInstructions()) {
                // builder.append("\nINST: " + instruction.getInstType());
                String inst = generateOperation(method, instruction, false);
                builder.append(inst);
            }

            builder.append("\n");
            builder.append("\treturn");
            builder.append("\n");
            builder.append(".end method");
        }

        return builder.toString();
    }

    public String generateOperation(Method method, Instruction instruction, boolean assign){
        StringBuilder builder = new StringBuilder();
        switch (instruction.getInstType()) {
            case ASSIGN:
                builder.append(generateAssign(method, (AssignInstruction) instruction));
                break;
            case GETFIELD:
                builder.append(generateGetField(method, (GetFieldInstruction) instruction));
                break;
            case PUTFIELD:
                builder.append(generatePutField(method, (PutFieldInstruction) instruction));
                break;
            case CALL:
                builder.append(generateCall(method, (CallInstruction) instruction, assign));
                break;
            case UNARYOPER:
                builder.append(generateUnaryOperation(method, (UnaryOpInstruction) instruction));
                break;
            case BINARYOPER:
                builder.append(generateBinaryOperation(method, (BinaryOpInstruction) instruction));
                break;
            case NOPER:
                builder.append(generateSingleOperation(method, (SingleOpInstruction) instruction));
                break;
            default:
                break;
        }

        return builder.toString();
    }

    public String generateAssign(Method method, AssignInstruction instruction) {
        StringBuilder builder = new StringBuilder();

        Element dest = instruction.getDest();

        Instruction rhs = instruction.getRhs();
        builder.append(generateOperation(method, rhs, true));

        builder.append("\n\t");
        if(dest.getType().getTypeOfElement().equals(INT32))
            builder.append("i");
        else
            builder.append("a");

        builder.append("store_");
        Operand operand = (Operand) dest;
        builder.append(OllirAccesser.getVarTable(method).get(operand.getName()).getVirtualReg());

        return builder.toString();
    }

    public String generateGetField(Method method, GetFieldInstruction instruction) {
        StringBuilder builder = new StringBuilder();

        Element first_element = instruction.getFirstOperand();
        Element second_element = instruction.getSecondOperand();

        builder.append("\n");
        builder.append("\taload_");
        if(first_element.getType().getTypeOfElement().equals(ElementType.THIS))
            builder.append("0");
        else {
            Operand operand = (Operand) first_element;
            builder.append(OllirAccesser.getVarTable(method).get(operand.getName()).getVirtualReg());
        }

        builder.append("\n");
        builder.append("\tgetfield ");
        builder.append(types.get(second_element.getType().getTypeOfElement()));
        builder.append(" ");
        builder.append(((Operand) second_element).getName());

        return builder.toString();
    }

    public String generatePutField(Method method, PutFieldInstruction instruction) {
        StringBuilder builder = new StringBuilder();

        Element first_element = instruction.getFirstOperand();
        Element second_element = instruction.getSecondOperand();
        Element third_element = instruction.getThirdOperand();

        builder.append("\n");

        builder.append(generateValue(method, third_element));

        builder.append("\n");
        builder.append("\tputfield ");
        builder.append(((Operand) first_element).getName());
        builder.append("/");
        builder.append(((Operand) second_element).getName());
        builder.append(" ");
        builder.append(types.get(second_element.getType().getTypeOfElement()));

        return builder.toString();
    }

    public String generateCall(Method method, CallInstruction instruction, boolean assign) {
        StringBuilder builder = new StringBuilder();

        if(assign) {

            builder.append("\n");
            if(!instruction.getFirstArg().isLiteral()){
                builder.append("\taload_");
                if(instruction.getFirstArg().getType().getTypeOfElement().equals(THIS)){
                    builder.append(0);
                }
                else {
                        Operand operand = (Operand) instruction.getFirstArg();
                        builder.append(OllirAccesser.getVarTable(method).get(operand.getName()).getVirtualReg());
                }
             }

            System.out.println("------------------ CALL --------------------");
            System.out.println("INST: " + instruction.getClass().getName());
            System.out.println("FIRST: " + instruction.getFirstArg());
            System.out.println("SECOND: " + instruction.getSecondArg());
            System.out.println("NUM OP: " + instruction.getNumOperands());
            System.out.println("RETURN TYPE: " + instruction.getReturnType());
            if(instruction.getListOfOperands() != null) {
                System.out.println("LIST OF OP: ");
                for (Element op : instruction.getListOfOperands()) {
                    System.out.println(op.toString());
                }
            }

            System.out.println("--------------------------------------");
        }

        return builder.toString();
    }

    public String generateUnaryOperation(Method method , UnaryOpInstruction instruction) {
        StringBuilder builder = new StringBuilder();

        return builder.toString();
    }

    public String generateBinaryOperation(Method method , BinaryOpInstruction instruction) {
        StringBuilder builder = new StringBuilder();

        Element leftOperand = instruction.getLeftOperand();
        Element rightOperand = instruction.getRightOperand();

        if (instruction.getUnaryOperation().getOpType().equals(OperationType.ADD)) {

        }

        return builder.toString();
    }

    public String generateSingleOperation(Method method, SingleOpInstruction instruction) {
        StringBuilder builder = new StringBuilder();

        Element element = instruction.getSingleOperand();

        builder.append("\n");

        builder.append(generateValue(method, element));

        return builder.toString();
    }

    public String generateValue(Method method, Element element) {
        StringBuilder builder = new StringBuilder();

        if(element.isLiteral()){
            int value = Integer.parseInt(((LiteralElement) element).getLiteral());

            if(value >= -1 && value <= 5)
                builder.append("\ticonst_");
            else
                builder.append("\tbipush ");

            builder.append(value);
        }
        else if (types.get(element.getType().getTypeOfElement()).equals(ARRAYREF)) { // Como saber o index para A[i] por ex?
            ArrayOperand operand = (ArrayOperand) element;
            builder.append("\t");
            if(operand.getType().getTypeOfElement().equals(INT32))
                builder.append("i");
            else
                builder.append("a");
            builder.append("aload_");
            builder.append(OllirAccesser.getVarTable(method).get(operand.getName()).getVirtualReg());
        }
        else {
            Operand operand = (Operand) element;
            builder.append("\t");
            if(operand.getType().getTypeOfElement().equals(INT32))
                builder.append("i");
            else
                builder.append("a");
            builder.append("load_");
            builder.append(OllirAccesser.getVarTable(method).get(operand.getName()).getVirtualReg());
        }

        return builder.toString();
    }

    public String lower(String s){
        return s.toLowerCase();
    }
}
