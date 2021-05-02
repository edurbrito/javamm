import java.util.*;

import org.specs.comp.ollir.*;

import pt.up.fe.comp.jmm.jasmin.JasminBackend;
import pt.up.fe.comp.jmm.jasmin.JasminResult;
import pt.up.fe.comp.jmm.ollir.OllirResult;
import pt.up.fe.comp.jmm.report.Report;
import pt.up.fe.comp.jmm.report.Stage;

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
        put(ElementType.VOID,"V");
        put(ElementType.INT32,"I");
        put(ElementType.BOOLEAN,"Z");
        put(ElementType.STRING,"Ljava/lang/String;");
        put(ARRAYREF,"[");
    }};

    String superClass = "";
    int lessThanCounter = 1;

    @Override
    public JasminResult toJasmin(OllirResult ollirResult) {

        ClassUnit ollirClass = ollirResult.getOllirClass();
        superClass = ollirClass.getSuperClass();
        try {

            // Example of what you can do with the OLLIR class
            ollirClass.checkMethodLabels(); // check the use of labels in the OLLIR loaded
            ollirClass.buildCFGs(); // build the CFG of each method
            ollirClass.outputCFGs(); // output to .dot files the CFGs, one per method
            ollirClass.buildVarTables(); // build the table of variables for each method
            ollirClass.show(); // print to console main information about the input OLLIR

            // Convert the OLLIR to a String containing the equivalent Jasmin code

            superClass = ollirClass.getSuperClass() == null ? "java/lang/Object" : ollirClass.getSuperClass();

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

    public String generateClassInfo(ClassUnit ollirClass) {
        StringBuilder builder = new StringBuilder();

        builder.append("; class with syntax accepted by jasmin 2.3");
        builder.append("\n");
        builder.append("\n");
        builder.append(".class public ");
        builder.append(ollirClass.getClassName());
        builder.append("\n");
        builder.append(".super ");
        builder.append(superClass);

        builder.append(generateFields(ollirClass));

        return builder.toString();
    }

    public String generateFields(ClassUnit ollirClass) {

        StringBuilder builder = new StringBuilder();

        builder.append("\n");

        for(Field field : ollirClass.getFields()) {
            builder.append("\n");
            builder.append(".field ");
            builder.append(lower(field.getFieldAccessModifier().toString()));
            builder.append(" ");
            builder.append(field.getFieldName());
            builder.append(" ");
            builder.append(getType(field.getFieldType()));
        }

        return builder.toString();
    }

    public String generateMethods(ClassUnit ollirClass) {

        StringBuilder builder = new StringBuilder();

        for(Method method : ollirClass.getMethods()) {
            if(method.isConstructMethod()) {
                builder.append("\n");
                builder.append("\n");
                builder.append(".method public <init>");
            }
            else {
                builder.append("\n");
                builder.append("\n");
                builder.append(".method ");
                builder.append(lower(method.getMethodAccessModifier().toString()));
                builder.append(" ");

                if (method.isStaticMethod())
                    builder.append("static ");
                if (method.isFinalMethod())
                    builder.append("final ");

                builder.append(method.getMethodName());
            }

            builder.append("(");
            for(Element element: method.getParams()) {
                builder.append(getType(element.getType()));
            }
            builder.append(")");
            builder.append(getType(method.getReturnType()));

            builder.append("\n");
            builder.append("\t.limit stack 99");
            builder.append("\n");
            builder.append("\t.limit locals 99");

            for(Instruction instruction : method.getInstructions()) {
                String inst = generateOperation(method, instruction);
                builder.append(inst);
            }

            if(!method.getInstructions().get(method.getInstructions().size() - 1).getInstType().equals(InstructionType.RETURN) && method.getReturnType().getTypeOfElement().equals(VOID)) {
                builder.append("\n");
                builder.append("\t");
                builder.append("\n");
                builder.append("\t");
                builder.append("return");
            }

            builder.append("\n");
            builder.append(".end method");
        }

        return builder.toString();
    }

    public String generateOperation(Method method, Instruction instruction) {
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
                builder.append(generateCall(method, (CallInstruction) instruction));
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
            case RETURN:
                builder.append(generateReturnOperation(method, (ReturnInstruction) instruction));
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

        if (dest instanceof ArrayOperand)
            builder.append(generateDest(method, dest));

        builder.append(generateOperation(method, rhs));

        if (dest instanceof ArrayOperand) {
            builder.append("\n");
            builder.append("\t");
            if(dest.getType().getTypeOfElement().equals(INT32) || dest.getType().getTypeOfElement().equals(BOOLEAN))
                builder.append("i");
            else
                builder.append("a");
            builder.append("astore");
        }
        else if(!(rhs instanceof CallInstruction && OllirAccesser.getCallInvocation((CallInstruction) rhs).equals(CallType.NEW))) {
            Operand operand = (Operand) dest;
            builder.append("\n");
            builder.append("\t");
            if(operand.getType().getTypeOfElement().equals(INT32) || operand.getType().getTypeOfElement().equals(BOOLEAN))
                builder.append("i");
            else
                builder.append("a");
            builder.append("store_");
            builder.append(OllirAccesser.getVarTable(method).get(operand.getName()).getVirtualReg());
        }

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
        builder.append(((ClassType) first_element.getType()).getName() + "/" + ((Operand) second_element).getName());
        builder.append(" ");
        builder.append(getType(second_element.getType()));

        return builder.toString();
    }

    public String generatePutField(Method method, PutFieldInstruction instruction) {
        StringBuilder builder = new StringBuilder();

        Element first_element = instruction.getFirstOperand();
        Element second_element = instruction.getSecondOperand();
        Element third_element = instruction.getThirdOperand();

        builder.append("\n");
        builder.append("\n");
        builder.append("\taload_");
        if(first_element.getType().getTypeOfElement().equals(ElementType.THIS))
            builder.append("0");
        else {
            Operand operand = (Operand) first_element;
            builder.append(OllirAccesser.getVarTable(method).get(operand.getName()).getVirtualReg());
        }

        builder.append(generateValue(method, third_element));

        builder.append("\n");
        builder.append("\tputfield ");
        builder.append(((ClassType) first_element.getType()).getName() + "/");
        builder.append(((Operand) second_element).getName());
        builder.append(" ");
        builder.append(getType(second_element.getType()));

        return builder.toString();
    }

    public String generateCall(Method method, CallInstruction instruction) {
        StringBuilder builder = new StringBuilder();

        builder.append("\n");

        Element objectInstance = instruction.getFirstArg();
        LiteralElement literal = (LiteralElement) instruction.getSecondArg();

        switch (OllirAccesser.getCallInvocation(instruction)) {
            case NEW:
                if (objectInstance.getType() instanceof ClassType) {
                    builder.append("\n");
                    builder.append("\t");
                    builder.append("new ");
                    builder.append(((ClassType) objectInstance.getType()).getName());
                    builder.append("\n");
                    builder.append("\t");
                    builder.append("dup");
                }
                else if (objectInstance.getType() instanceof ArrayType) {
                    Operand operand = (Operand) objectInstance;
                    for (Element arg : instruction.getListOfOperands()) {
                        builder.append(generateValue(method, arg));
                    }
                    builder.append("\n");
                    builder.append("\t");
                    builder.append("newarray ");
                    // if(((ArrayType) instruction.getReturnType()).getTypeOfElements().equals(INT32)) TODO OTHER CLASSES
                    builder.append("int");

                    builder.append("\n");
                    builder.append("\t");
                    builder.append("astore_");
                    builder.append(OllirAccesser.getVarTable(method).get(operand.getName()).getVirtualReg());
                }
                break;
            case invokevirtual:
                builder.append("\n");
                builder.append("\t");
                builder.append("aload_");
                if(objectInstance.getType().getTypeOfElement().equals(ElementType.THIS))
                    builder.append("0");
                else {
                    Operand operand = (Operand) objectInstance;
                    builder.append(OllirAccesser.getVarTable(method).get(operand.getName()).getVirtualReg());
                }

                for (Element arg : instruction.getListOfOperands()) {
                    builder.append(generateValue(method, arg));
                }

                builder.append("\n");
                builder.append("\t");
                builder.append("invokevirtual ");

                builder.append(((ClassType) objectInstance.getType()).getName());

                builder.append("/").append(literal.getLiteral().replace("\"","")).append("(");
                for (Element arg : instruction.getListOfOperands()) {
                    builder.append(getType(arg.getType()));
                }
                builder.append(")");
                builder.append(getType(instruction.getReturnType()));
                break;
            case invokeinterface:
                break;
            case invokespecial:
                if(objectInstance.getType().getTypeOfElement().equals(ElementType.THIS)) {
                    builder.append("\n");
                    builder.append("\t");
                    builder.append("aload_0");
                }

                for (Element arg : instruction.getListOfOperands()) {
                    builder.append(generateValue(method, arg));
                }

                builder.append("\n");
                builder.append("\t");
                builder.append("invokespecial ");
                if(objectInstance.getType().getTypeOfElement().equals(ElementType.THIS))
                    builder.append(superClass);
                else
                    builder.append(((ClassType) objectInstance.getType()).getName());
                builder.append("/<init>(");
                for (Element arg : instruction.getListOfOperands()) {
                    builder.append(getType(arg.getType()));
                }
                builder.append(")V");
                if(!objectInstance.getType().getTypeOfElement().equals(ElementType.THIS)) {
                    builder.append("\n");
                    builder.append("\t");
                    builder.append("astore_");
                    Operand operand = (Operand) objectInstance;
                    builder.append(OllirAccesser.getVarTable(method).get(operand.getName()).getVirtualReg());
                }
                break;
            case invokestatic:
                for (Element arg : instruction.getListOfOperands()) {
                    builder.append(generateValue(method, arg));
                }

                builder.append("\n\tinvokestatic ");

                builder.append(((Operand) objectInstance).getName());

                builder.append("/").append(literal.getLiteral().replace("\"","")).append("(");
                for (Element arg : instruction.getListOfOperands()) {
                    builder.append(getType(arg.getType()));
                }
                builder.append(")");
                builder.append(getType(instruction.getReturnType()));
                break;
            case arraylength:
                builder.append(generateValue(method, objectInstance));

                builder.append("\n");
                builder.append("\t");
                builder.append("arraylength ");
                break;
            case ldc:
                builder.append("\n\tldc " + ((LiteralElement) objectInstance).getLiteral());
                break;
            default:
                break;
        }

        return builder.toString();
    }

    public String generateUnaryOperation(Method method , UnaryOpInstruction instruction) {
        StringBuilder builder = new StringBuilder();

        Element operand = instruction.getRightOperand();

        builder.append("\n");
        builder.append("\t");

        if(instruction.getUnaryOperation().getOpType().equals(OperationType.NOT)) {
            builder.append(generateValue(method, operand));
            builder.append("\n");
            builder.append("\t");
            builder.append("iconst_1");
            builder.append("\n");
            builder.append("\t");
            builder.append("ixor");
        }

        return builder.toString();
    }

    public String generateBinaryOperation(Method method , BinaryOpInstruction instruction) {
        StringBuilder builder = new StringBuilder();

        Element leftOperand = instruction.getLeftOperand();
        Element rightOperand = instruction.getRightOperand();

        builder.append("\n");
        builder.append("\t");
        builder.append(generateValue(method, leftOperand));
        builder.append(generateValue(method, rightOperand));
        builder.append("\n");
        builder.append("\t");

        if (instruction.getUnaryOperation().getOpType().equals(OperationType.ADD)) {
            builder.append("iadd");
        }
        else if (instruction.getUnaryOperation().getOpType().equals(OperationType.SUB)) {
            builder.append("isub");
        }
        else if (instruction.getUnaryOperation().getOpType().equals(OperationType.MUL)) {
            builder.append("imul");
        }
        else if (instruction.getUnaryOperation().getOpType().equals(OperationType.DIV)) {
            builder.append("idiv");
        }
        else if (instruction.getUnaryOperation().getOpType().equals(OperationType.LTH)) {
            builder.append("if_icmplt LT_True_");
            builder.append(lessThanCounter);
            builder.append("\n");
            builder.append("\t");
            builder.append("iconst_0");
            builder.append("\n");
            builder.append("\t");
            builder.append("goto LT_End_");
            builder.append(lessThanCounter);
            builder.append("\n");
            builder.append("\t");
            builder.append("LT_True_");
            builder.append(lessThanCounter).append(":");
            builder.append("\n");
            builder.append("\t");
            builder.append("iconst_1");
            builder.append("\n");
            builder.append("\t");
            builder.append("LT_End_");
            builder.append(lessThanCounter).append(":");
            lessThanCounter++;
        }
        else if (instruction.getUnaryOperation().getOpType().equals(OperationType.AND)) {
            builder.append("iand");
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

    public String generateReturnOperation(Method method, ReturnInstruction instruction) {
        StringBuilder builder = new StringBuilder();

        builder.append("\n");
        builder.append("\t");

        if (instruction.hasReturnValue()) {

            Element operand = instruction.getOperand();
            builder.append(generateValue(method, operand));

            builder.append("\n");
            builder.append("\t");

            if(operand.getType().getTypeOfElement().equals(INT32) || operand.getType().getTypeOfElement().equals(BOOLEAN))
                builder.append("i");
            else
                builder.append("a");
        }
        else {
            builder.append("\n");
            builder.append("\t");
        }

        builder.append("return");
        return builder.toString();
    }

    public String generateDest(Method method, Element element) {
        StringBuilder builder = new StringBuilder();

        builder.append("\n");
        generateArray(method, (ArrayOperand) element, builder);

        return builder.toString();
    }

    public String generateValue(Method method, Element element) {
        StringBuilder builder = new StringBuilder();

        builder.append("\n");
        builder.append("\t");

        if(element.isLiteral()) {
            LiteralElement literalElement = ((LiteralElement) element);

            if (literalElement.getType().getTypeOfElement().equals(INT32) || literalElement.getType().getTypeOfElement().equals(BOOLEAN)) {
                int value = Integer.parseInt(literalElement.getLiteral());
                if(value >= -1 && value <= 5)
                    builder.append("iconst_");
                else if(value >= -128 && value <= 127)
                    builder.append("bipush ");
                else if(value >= -32768 && value <= 32767)
                    builder.append("sipush ");
                else
                    builder.append("ldc ");

                builder.append(value);
            }
            else {
                builder.append("ldc ");
                builder.append(literalElement.getLiteral());
            }
        }
        else if (element instanceof ArrayOperand) {
            generateArray(method, (ArrayOperand) element, builder);

            if(element.getType().getTypeOfElement().equals(INT32) || element.getType().getTypeOfElement().equals(BOOLEAN))
                builder.append("i");
            else
                builder.append("a");
            builder.append("aload");
        }
        else {
            Operand operand = (Operand) element;

            if(operand.getType().getTypeOfElement().equals(INT32) || operand.getType().getTypeOfElement().equals(BOOLEAN))
                builder.append("i");
            else
                builder.append("a");
            builder.append("load_");
            builder.append(OllirAccesser.getVarTable(method).get(operand.getName()).getVirtualReg());
        }

        return builder.toString();
    }

    private void generateArray(Method method, ArrayOperand operand, StringBuilder builder) {
        builder.append("\t");
        builder.append("aload_");
        builder.append(OllirAccesser.getVarTable(method).get(operand.getName()).getVirtualReg());

        builder.append("\n");
        builder.append("\t");
        for (Element index : operand.getIndexOperands()) {
            if(index.isLiteral()) {
                int value = Integer.parseInt(((LiteralElement) index).getLiteral());

                if(value >= -1 && value <= 5)
                    builder.append("iconst_");
                else
                    builder.append("bipush ");

                builder.append(value);
            }
            else {
                Operand index_operand = (Operand) index;
                builder.append("iload_");
                builder.append(OllirAccesser.getVarTable(method).get(index_operand.getName()).getVirtualReg());
            }
        }
    }

    public String lower(String s){
        return s.toLowerCase();
    }

    public String getType(Type type){
        if(type.getTypeOfElement().equals(OBJECTREF)) {
            return "L" + ((ClassType) type).getName() + ";";
        }
        else if (type.getTypeOfElement().equals(ARRAYREF))
        {
            ArrayType arrayType = (ArrayType) type;
            return types.get(type.getTypeOfElement()) + types.get(arrayType.getTypeOfElements());
        }
        else
        {
            return types.get(type.getTypeOfElement());
        }
    }
}
