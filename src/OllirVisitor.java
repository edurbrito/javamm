import pt.up.fe.comp.jmm.JmmNode;
import pt.up.fe.comp.jmm.analysis.table.Symbol;
import pt.up.fe.comp.jmm.analysis.table.Type;
import pt.up.fe.comp.jmm.ast.PreorderJmmVisitor;
import pt.up.fe.comp.jmm.report.Report;
import pt.up.fe.specs.util.utilities.StringList;

import java.util.*;
import java.util.regex.Pattern;

public class OllirVisitor extends PreorderJmmVisitor<List<Report>, Boolean> {
    private final SymbolTableImp symbolTableImp;
    private final StringBuilder ollirCode = new StringBuilder("\n");
    private String methodName, methodKey;
    private int whileCounter = 1;
    private int tempsCount = 0, tempIf = 0;
    private boolean hasReturn = false;
    private boolean optimizedWhiles = false;

    public OllirVisitor(SymbolTableImp symbolTableImp) {
        super();

        this.symbolTableImp = symbolTableImp;

        addVisit("Class", this::dealWithClass);
    }

    public void setOptimizedWhiles(boolean optimizedWhiles){
        this.optimizedWhiles = optimizedWhiles;
    }

    public String getOllirCode(){return this.ollirCode.toString();}

    private List<String> dealWithChild(JmmNode child){
        switch (child.getKind()){
            case "Method": {return Collections.singletonList(dealWithMethod(child));}
            case "MainMethod": {return Collections.singletonList(dealWithMainMethod(child));}
            case "TwoPartExpression":{return dealWithTwoPart(child);}
            case "EqualStatement": {return Collections.singletonList(dealWithEqualStatement(child));}
            case "Identifier" :{ return dealWithIdentifier(child);}
            case "Integer" :{ return Collections.singletonList(dealWithInteger(child));}
            case "Boolean":{ return Collections.singletonList(dealWithBoolean(child));}
            case "AllocationExpression":{return dealWithAllocationExpression(child);}
            case "MethodBody":{return Collections.singletonList(dealWithMethodBody(child));}
            case "Return":{return Collections.singletonList(dealWithReturn(child));}
            case "While":{return Collections.singletonList(dealWithWhile(child));}
            case "Sum":
            case "Sub":
            case "Mult":
            case "Div":
            case "LessThan": {return dealWithArithmetic(child);}
            case "Not":
            case "And": {return dealWithBoolOp(child);}
            case "IfElse":{return dealWithIfElse(child);}

        }
        return Collections.singletonList("");
    }



    private List<String> dealWithIfElse(JmmNode ifElse) {

        int countIf = this.tempIf++;

        // IfElse Condition
        JmmNode ifCondition = ifElse.getChildren().get(0);
        JmmNode op = ifCondition.getChildren().get(0);
        List<String> cond;
        StringBuilder res = new StringBuilder("\n");

        cond = dealWithChild(op);
        String pre = "", inCond = "";
        if(cond.size() > 1){
            pre = cond.get(0);
            inCond = cond.get(1);
        }else{
            inCond = cond.get(0);
        }


        if(!inCond.contains("&&") && !inCond.contains("<") && !inCond.contains("!")){
            inCond += " &&.bool 1.bool";
        }

        res.append(pre).append("\n");

        res.append("if ( " + inCond + " ) goto bodyIf" + countIf +";\n");

        // IfElse else
        JmmNode ifBody = ifElse.getChildren().get(1);

        JmmNode ifElseBody = ifElse.getChildren().get(2);
        for(JmmNode bodyExp : ifElseBody.getChildren()){
            res.append(join(dealWithChild(bodyExp))).append("\n");
        }
        res.append("goto endif" + countIf + ";\n");

        // IfElse body
        res.append("bodyIf" + countIf +": \n");

        for(JmmNode bodyExp : ifBody.getChildren()){
            res.append(join(dealWithChild(bodyExp))).append("\n");
        }


        res.append("endif" + countIf + ":\n");

        List<String> finalList = new ArrayList<>();
        finalList.add(res.toString());
        return finalList;
    }

    private String join(List<String> in){
        StringBuilder res = new StringBuilder();

        if(in.size() == 1)
            return in.get(0);

        for(String str : in){
            if(str.length()>0 && str.length() > 8)
                res.append(str + "\n");
        }
        return res.toString();
    }

    private String dealWithWhile(JmmNode node) {
        int whileNumber = whileCounter;
        whileCounter++;
        StringBuilder result = new StringBuilder();
        for (JmmNode child : node.getChildren()) {
            switch (child.getKind()) {
                case "WhileCondition": {
                    if(this.optimizedWhiles){
                        result.append("goto Test" + whileNumber + ";\n");
                        result.append("Loop" + whileNumber + ":\n");
                    }
                    else{
                        result.append(dealWithWhileCondition(child, whileNumber));
                    }
                    continue;
                }
                case "WhileBody": {
                    if(this.optimizedWhiles){
                        result.append(dealWithOptimizedWhileBody(child, whileNumber));
                        result.append(dealWithOptimizedWhileCondition(child.getParent().getChildren().get(0), whileNumber));
                    }
                    else{
                        result.append(dealWithWhileBody(child, whileNumber));
                    }
                    continue;
                }
            }
        }
        if (!this.optimizedWhiles)
            result.append("EndLoop" + whileNumber + ":\n");
        return result.toString();
    }

    private String dealWithWhileCondition(JmmNode node, int whileNumber) {
        StringBuilder result = new StringBuilder("Loop" + whileNumber + ":\n");
        JmmNode ConditionNode = node.getChildren().get(0);

        List<String> cond = dealWithChild(ConditionNode);
        String pre = "", inCond = "";
        if(cond.size() > 1){
            pre = cond.get(0);
            inCond = cond.get(1);
        }else{
            inCond = cond.get(0);
        }

        if(!inCond.contains("&&") && !inCond.contains("<") && !inCond.contains("!")){
            inCond += " &&.bool 1.bool";
        }

        result.append(pre).append("\n");

        result.append("if ( " + inCond + ") goto Body" + whileNumber + ";\n");
        result.append("goto EndLoop" + whileNumber + ";\n");
        return result.toString();
    }

    private String dealWithOptimizedWhileCondition(JmmNode node, int whileNumber) {
        StringBuilder result = new StringBuilder();
        JmmNode ConditionNode = node.getChildren().get(0);

        List<String> cond = dealWithChild(ConditionNode);
        String pre = "", inCond = "";
        if(cond.size() > 1){
            pre = cond.get(0);
            inCond = cond.get(1);
        }else{
            inCond = cond.get(0);
        }

        if(!inCond.contains("&&") && !inCond.contains("<") && !inCond.contains("!")){
            inCond += " &&.bool 1.bool";
        }

        result.append(pre).append("\n");

        result.append("Test" + whileNumber + ":\n");
        result.append("if ( " + inCond + ") goto Loop" + whileNumber + ";\n");
        return result.toString();
    }

    private String dealWithWhileBody(JmmNode node, int whileNumber) {
        StringBuilder result = new StringBuilder("Body" + whileNumber + ":\n");
        result.append(dealWithBodyInstructions(node, whileNumber));
        result.append("goto Loop" + whileNumber + ";\n");
        return result.toString();
    }

    private String dealWithOptimizedWhileBody(JmmNode node, int whileNumber) {
        return dealWithBodyInstructions(node, whileNumber);
    }

    private String dealWithBodyInstructions(JmmNode node, int whileNumber) {
        StringBuilder result = new StringBuilder();
        for (JmmNode child : node.getChildren()) {
            for (String i : dealWithChild(child)) {
                if (!Pattern.matches("[tu][ib][0-9]+\\..{3,4}",i))
                    result.append(i.replace("\n\n", "\n")).append('\n');
            }
        }
        return result.toString();
    }

    private List<String> dealWithAllocationExpression(JmmNode child) {
        StringBuilder result = new StringBuilder();
        StringBuilder pre = new StringBuilder();
        List<String> finalList = new ArrayList<>();
        boolean putfield = isPutfield(child.getParent().getChildren());

        String type=null;
        if(child.getParent().getChildren().get(0).getKind().equals("Identifier")) {
            type = getTypeOllir(getIdentifierType(child.getParent().getChildren().get(0)), !getIdentifierType(child.getParent().getChildren().get(0)).isArray());
        }

        if (child.getChildren().get(0).getKind().equals("Object")) {
            String tempVar = getTempVar(child.getChildren().get(0).get("name"), true);

            pre.append(tempVar).append(" :=.").append(child.getChildren().get(0).get("name"))
                    .append(" new (").append(child.getChildren().get(0).get("name")).append(")").append(".");
            pre.append(child.getChildren().get(0).get("name")).append(";\n");
            pre.append("invokespecial (" + tempVar + ",\"<init>\").V;");

            result.append(tempVar);
            finalList.add(pre.toString());
            finalList.add(result.toString());
            return finalList;
        }
        else {
            JmmNode lengthNode = child.getChildren().get(0).getChildren().get(0); // Get the nodes that are responsible for the array length
            String length;

            if(lengthNode.getKind().equals("Integer")) {    // If length is an Integer
                length = dealWithInteger(lengthNode);

            }else if(lengthNode.getKind().equals("Identifier")) {   // If length is an identifier
                length = dealWithIdentifier(lengthNode).get(0);

            }else if(lengthNode.getKind().equals("TwoPartExpression")){
                List<String> tempRes = dealWithTwoPart(lengthNode);
                pre.append(tempRes.get(0)).append("\n");
                length = tempRes.get(1);

            }else{                                                  // If length is an arithmetic op
                List<String> tempRes = dealWithArithmetic(lengthNode);
                pre.append(tempRes.get(0)).append("\n");
                length = tempRes.get(1);
            }

            result.append("new(array, ").append(length).append(")").append(".array.i32");
            if(putfield){
                String tempVar = getTempVar(type, true);
                pre.append( tempVar + " :=." + type + " ").append(result.toString()).append(";\n");
                result = new StringBuilder();
                result.append(tempVar);
            }
            finalList.add(pre.toString());
            finalList.add(result.toString());
            return finalList;
        }
    }


    private boolean dealWithClass(JmmNode node, List<Report> reports) {

        //class constructor
        ollirCode.append(node.get("name")+" ");
        try{
            ollirCode.append("extends "+node.get("extends"));
        }catch (Exception e){

        }
        ollirCode.append(" {\n");
        for (JmmNode child : node.getChildren()) {
            if (child.getKind().equals("Var")) {
                ollirCode.append(dealWithVar(child) + '\n');
            }
        }
        ollirCode.append(".construct " + node.get("name") + "().V {\n");
        ollirCode.append("" + "invokespecial (this, \"<init>\").V;\n");
        ollirCode.append("" + "}\n");

        for (JmmNode child:node.getChildren()){
            this.tempsCount=0;
            switch (child.getKind()){
                case "Method": {ollirCode.append(dealWithMethod(child)); break;}
                case "MainMethod": {ollirCode.append(dealWithMainMethod(child)); break;}
            }
        }
        ollirCode.append("}\n");

        return true;
    }


    private String getArgs(JmmNode node) {
        StringBuilder methodKey = new StringBuilder(this.methodName);
        for (JmmNode child : node.getChildren()) {
            if (child.getKind().equals("MethodArguments")) {
                for (JmmNode child1 : child.getChildren())
                    if (child1.getKind().equals("Argument")) {
                        for (JmmNode child2 : child1.getChildren()) {
                            if (child2.getKind().equals("Type")){
                                if(child2.getNumChildren() > 0){
                                    methodKey.append("array");
                                }
                                methodKey.append(child2.get("name"));
                            }
                        }
                    }

            }
        }
        this.methodKey = methodKey.toString();


        StringBuilder result = new StringBuilder();
        //System.out.println(this.symbolTableImp);
        MethodTable methodTable = null;

        methodTable = this.symbolTableImp.methods.get(this.methodKey);

        assert methodTable != null;

        for (Parameter parameter : methodTable.parameters) {
            result.append(parameter.getSymbol().getName() + "." + getTypeOllir(parameter.getSymbol().getType(),false) + ",");
        }
        if (result.length() > 0)
            result.deleteCharAt(result.length() - 1);

        return result.toString();
    }


    private String dealWithMethodBody(JmmNode node){
        StringBuilder result = new StringBuilder("\n");

        for (JmmNode child:node.getChildren()){
            result.append(join(dealWithChild(child)));
            if(child.getKind().equals("Identifier")){
                result.append(";");
            }
        }

        return result.toString();
    }


    private String dealWithMainMethod(JmmNode node) {
        this.methodName = "main";
        this.methodKey = "main";
        StringBuilder result = new StringBuilder("\n");
        JmmNode methodBody = node.getChildren().get(0);

        result.append(".method public static main(" + node.get("argName") + ".array.String).V {\n");

        result.append(dealWithMethodBody(methodBody));

        result.append("ret.V;\n}\n");



        return result.toString();
    }


    private String dealWithMethod(JmmNode node) {
        this.methodName = node.get("name");
        this.hasReturn = false;

        StringBuilder result = new StringBuilder();
        result.append(".method public " + node.get("name") + "(" + getArgs(node) + ")");
        result.append("." + getTypeOllir(symbolTableImp.methods.get(methodKey).returnType, !symbolTableImp.methods.get(methodKey).returnType.isArray()));
        result.append(" {");

        for (JmmNode child : node.getChildren()){
            result.append(dealWithChild(child).get(0));
        }

        if (!hasReturn) {
            result.append("ret.V;\n");
        }
        result.append("}\n");

        return result.toString();
    }


    private String dealWithVar(JmmNode child) {
        StringBuilder result = new StringBuilder();
        String name = child.get("name");
        Type type = this.symbolTableImp.getFieldType(name);

        result.append(".field private " + name + ".");          // Grammar only accepts private fields

        String typeOllir = getTypeOllir(type, child.getChildren().get(0).getChildren().size() == 0);

        result.append(typeOllir + ";");

        return result.toString();
    }
    private String getMethodKey(List<JmmNode> children){
        StringBuilder key = new StringBuilder();
        key.append(children.get(0).getParent().get("name"));
        String res = "";
        for (JmmNode callArgs : children) {

            List<String> stringList = dealWithChild(callArgs);

            if(stringList.size() > 1){
                res = stringList.get(1);
            }else{
                res = stringList.get(0);
            }

            if(res.contains("i32") || res.contains("array")){
                String array = res.contains("array") ? "array" : "";
                key.append(array + "int");
            }else if (res.contains("bool")){
                key.append("bool");
            }else{
                key.append(res.split("\\.")[1]);
            }
        }
        return key.toString();
    }

    private List<String> getOllirArgs(List<JmmNode> children){
        List<String> finalList = new ArrayList<>();
        StringBuilder pre = new StringBuilder();
        String res = "";
        StringBuilder args = new StringBuilder();

        for (JmmNode callArgs : children) {

            List<String> stringList = dealWithChild(callArgs);

            if(stringList.size() > 1){
                pre.append(stringList.get(0)).append("\n");
                res = stringList.get(1);
            }else{
                res = stringList.get(0);
            }
            if (res.matches(".*[+|-|/|*|invoke].*")){
                List<String> aux = createTemp(res);
                pre.append(aux.get(0) + ";\n");
                args.append(", " + aux.get(1));
            }else {
                args.append(", " + res);
            }

        }
        finalList.add(pre.toString());
        finalList.add(args.toString());
        return finalList;
    }

    private List<String> dealWithTwoPart(JmmNode node){

        if(node.getChildren().get(1).getKind().equals("AccessToArray")) {
            List<String> res = new ArrayList<>();
            List<String> resAccess = dealWithArithmetic(node.getChildren().get(1).getChildren().get(0));
            if(resAccess.size() > 1){
                StringBuilder before = new StringBuilder();
                before.append(resAccess.get(0)).append("\n");
                String identifierName = node.getChildren().get(0).get("name");

                if(isGetfield(node.getChildren().get(0))){
                    Type type = getIdentifierType(node.getChildren().get(0));
                    String typeStr = getTypeOllir(type, !type.isArray());
                    String tempVar = getTempVar(typeStr, true);
                    before.append(tempVar).append(" :=.").append(typeStr).append(" getfield(this, ")
                            .append(identifierName).append(".").append(typeStr).append(").").append(typeStr).append(";\n");
                    identifierName = tempVar.split("\\.")[0];
                }
                res.add(before.toString());
                if (!Pattern.matches("[tu][ib][0-9]+\\..{3,4}",resAccess.get(1)))
                    res.add(identifierName + "[" + resAccess.get(1).split(":=")[0] + "]" +".i32");
                else
                    res.add(identifierName + "[" + resAccess.get(1) + "]" +".i32");
            }else {
                res.add(node.getChildren().get(0).get("name") + "[" + resAccess.get(0) + "]" +".i32");
            }

            return res;
        }

        StringBuilder result = new StringBuilder();


        JmmNode leftChild = node.getChildren().get(0);
        JmmNode rightChild = node.getChildren().get(1);

        if (leftChild.getKind().equals("This")) {
            List<String> listRes = dealWithThisExpression(node);
            String functionType = "";

            if (listRes.get(1).split("\\.")[listRes.get(1).split("\\.").length-2].contains("array") && !listRes.get(1).split("\\.")[listRes.get(1).split("\\.").length-1].contains("bool"))
                functionType="array.";
            if(listRes.get(1).split("\\.")[listRes.get(1).split("\\.").length-1].contains("bool")){
                functionType += "bool";
            }else if (listRes.get(1).split("\\.")[listRes.get(1).split("\\.").length-1].contains("i32")){
                functionType += "i32";
            }else{
                functionType+=listRes.get(1).split("\\.")[listRes.get(1).split("\\.").length-1];
            }

            String tempVar = getTempVar(functionType, true);

            String expressionResult = listRes.get(1);
            result.append(" " + expressionResult + ";" + "\n");
            List<String> finalL = new ArrayList<>();
            finalL.add(listRes.get(0) + "\n" + tempVar + " :=." + functionType + " " + result.toString() + "\n");
            finalL.add(tempVar);

            return finalL;

        }else if(rightChild.getKind().equals("DotExpression") && leftChild.getKind().equals("Identifier") && getIdentifierType(leftChild) != null && getIdentifierType(leftChild).isArray()){
            List<String> resLength = new ArrayList<>();
            List<String> parameterTempVar = dealWithChild(leftChild);
            String tempVar = getTempVar("i32", true);
            if (leftChild.getKind().equals("Identifier")&&parameterTempVar.size()>1) {
                resLength.add(parameterTempVar.get(0)+tempVar + " :=.i32 arraylength(" + getParameterSig(leftChild.get("name")) + parameterTempVar.get(1) + ".array.i32" + ").i32;");

            }else {

                resLength.add(tempVar + " :=.i32 arraylength(" + getParameterSig(leftChild.get("name")) + leftChild.get("name") + ".array.i32" + ").i32;");
            }
            resLength.add(tempVar);
            return resLength;
        }


        String objectName;
        if (leftChild.getKind().equals("AllocationExpression")){
            objectName=leftChild.getChildren().get(0).get("name");
        }else {
            objectName = leftChild.get("name");
        }
        Symbol classSym = this.symbolTableImp.getMethod(this.methodKey).getVariable(objectName);
        if (classSym==null) {
            result.append("t" + this.tempsCount++);
            StringBuilder before = new StringBuilder();
            if (this.symbolTableImp.getImports().contains(objectName)) {
                JmmNode methodCall = rightChild.getChildren().get(0);
                List<JmmNode> identifiers = methodCall.getChildren();


                List<String> key = new ArrayList<>();
                for (JmmNode child : identifiers) {


                    if(child.getKind().equals("TwoPartExpression") ){
                        if(child.getChildren().get(1).getKind().equals("DotExpression")){
                            List<String> res = dealWithMethodCall(child.getChildren().get(0), child.getChildren().get(1));
                            String typeString = res.get(2);
                            String tempVar = getTempVar(typeString,true);
                            before.append(res.get(0) + "\n" + tempVar + " :=." +typeString + " " + res.get(1) + ";\n");
                            key.add(tempVar);
                        }else{
                            List<String> res = mountIdentifierOLLIR(child.getChildren().get(0), child.getChildren().get(1));
                            String tempVar = getTempVar("i32", true);
                            before.append(res.get(0) + "\n" + tempVar + " :=.i32 " + res.get(1) + ";\n");
                            key.add(tempVar);
                        }


                    }else{
                        List<String> res = dealWithChild(child);
                        if(res.size() > 1){
                            before.append(res.get(0)).append("\n");
                            key.add(res.get(1));
                        }else {
                            key.add(res.get(0));

                        }
                    }


                    List<String> dealResult = dealWithChild(child);
                    List<String> opsAr = Arrays.asList("Sum", "Sub", "Mult", "Div", "And", "LessThan");
                    List<String> opsBo = Arrays.asList("And", "Not");

                    if (opsAr.contains(child.getKind()) || opsBo.contains(child.getKind())){
                        List<String> resTemp = createTemp(dealResult.get(1));
                        before.append(resTemp.get(0));
                        key.set(key.size()-1, resTemp.get(1));
                        continue;
                    }

                    for(String i:dealWithChild(child)){

                        if(i.length()>0)
                            result.append(i).append('\n');
                    }
                }

                result = new StringBuilder();
                //result.append(before.toString());
                result.append("invokestatic (" + objectName + ", \"" + methodCall.get("name")+"\"");
                for (String i :key){
                    result.append(", "+i);
                }
                //result.append(","+key.toString());


                result.append(")");
                Type returnStatic = getIdentifierType(rightChild.getParent().getParent().getChildren().get(0));
                if (returnStatic != null && returnStatic.isArray()){
                    if(rightChild.getParent().getParent().getChildren().get(0).getChildren().size()>0){
                        if (rightChild.getParent().getParent().getChildren().get(0).getChildren().get(0).getKind().equals("AccessToArray"))
                            returnStatic=new Type(returnStatic.getName(),false);
                    }
                }
                if(returnStatic != null)
                    result.append("." + getTypeOllir(returnStatic, !returnStatic.isArray()));
                else
                    result.append(".V");
                result.append(";\n");
            }
            else if (this.symbolTableImp.className.equals(objectName)){
                List<String> temp0 = dealWithChild(leftChild);
                before.append(temp0.get(0)).append(";\n");
                result = new StringBuilder();

                if(rightChild.getKind().equals("DotExpression")){
                    var callArgs = rightChild.getChildren().get(0).getChildren();
                    Type returnType = this.symbolTableImp.getMethod(getMethodKey(callArgs)).returnType;
                    String className = temp0.get(1).split("\\.")[1];
                    String tempVar = getTempVar(getTypeOllir(returnType), true);
                    String callName = rightChild.getChildren().get(0).get("name");
                    List<String> argsRes = getOllirArgs(callArgs);
                    before.append(argsRes.get(0)).append("\n");
                    before.append(tempVar).append(" :=.").append(getTypeOllir(returnType)).append(" ");
                    before.append("invokevirtual (").append(temp0.get(1))
                            .append(",\"").append(callName).append("\"");
                    before.append(argsRes.get(1));
                    before.append(").").append(getTypeOllir(returnType)).append(";\n");
                    result.append(tempVar);
                }else{
                    result.append(temp0.get(1));
                }




            }else if (this.symbolTableImp.superClass.equals(objectName)){

            }
            return Arrays.asList(before.toString(),result.toString());
        }

        String className = classSym.getType().getName();

        String callName = "";


        callName = rightChild.getChildren().get(0).get("name");

        StringBuilder key= new StringBuilder(callName);
        StringBuilder pre = new StringBuilder();
        StringBuilder args = new StringBuilder();
        StringBuilder before= new StringBuilder();
        result = new StringBuilder();
        String res="";

        for (JmmNode callArgs : rightChild.getChildren().get(0).getChildren()) {

            List<String> stringList = dealWithChild(callArgs);

            if(stringList.size() > 1){
                pre.append(stringList.get(0)).append("\n");
                res = stringList.get(1);
            }else{
                res = stringList.get(0);
            }
            if (res.matches(".*[+|-|/|*|invoke].*")){
                List<String> aux = createTemp(res);
                before.append(aux.get(0) + ";\n");
                args.append(", " + aux.get(1));
            }else {
                args.append(", " + res);
            }

//            result.append(dealWithChild(callArgs).get(0));
            if(res.contains("i32") || res.contains("array")){
                String array = res.contains("array") ? "array" : "";
                key.append(array + "int");
            }else if (res.contains("bool")){
                key.append("bool");
            }else{
                key.append(res.split("\\.")[1]);
            }
        }



        result.append(pre).append("invokevirtual (").append(objectName).append(".").append(className).append(",\"").append(callName).append("\"");
        result.append(args.toString());
        result.append(")");

        try{
            result.append("." + getTypeOllir(symbolTableImp.methods.get(key.toString()).returnType,false));
        }catch (NullPointerException e){//if function is from extended superclass
            result.append("." + "i32");
        }

        result.append(";");
        return Arrays.asList(before.toString(),result.toString());
    }



    private List<String> createTemp(String operation) {
        List<String> type;
        if (operation.contains("i32")){
            if(operation.contains("array"))
                type=Arrays.asList("int",".array.i32");
            else
                type=Arrays.asList("int",".i32");
        }else{
            if(operation.contains("array"))
                type = Arrays.asList("bool", ".array.bool");
            else
                type = Arrays.asList("bool", ".bool");
        }
        String temp="t"+ type.get(0).charAt(0)+tempsCount+type.get(1)+" :="+type.get(1)+" "+operation+";";
        String finalOp="t"+ type.get(0).charAt(0)+tempsCount+type.get(1);
        tempsCount++;
        return Arrays.asList(temp,finalOp);
    }


    private List<String> dealWithThisExpression(JmmNode TwoPartNode) {
        StringBuilder result = new StringBuilder();
        JmmNode rightChild = TwoPartNode.getChildren().get(1);
        List<String> finalList = new ArrayList<>();

        if (rightChild.getChildren().get(0).getKind().equals("MethodCall")) {
            JmmNode methodCall = rightChild.getChildren().get(0);


            StringBuilder key = new StringBuilder(methodCall.get("name"));

            StringBuilder args = new StringBuilder();
            StringBuilder pre = new StringBuilder();

            for (JmmNode callArgs : methodCall.getChildren()) {
                args.append(",");
                String res = "";
                List<String> stringList = dealWithChild(callArgs);

                List<String> opsAr = Arrays.asList("Sum", "Sub", "Mult", "Div", "And", "LessThan");
                List<String> opsBo = Arrays.asList("And", "Not");


                if(stringList.size() > 1){
                    pre.append(stringList.get(0)).append("\n");
                    if (callArgs.getKind().equals("TwoPartExpression")){
                        Type type = getIdentifierType(callArgs.getChildren().get(0));
                        String typeString = getTypeOllir(type, callArgs.getChildren().get(1).getKind().equals("AccessToArray"));
                        res = getTempVar(typeString,true);
                        pre.append(res+" :=."+typeString+" "+stringList.get(1)+";");
                        args.append(res);
                    }else if(opsAr.contains(callArgs.getKind())){
                        String tempVar = getTempVar("i32", true);
                        pre.append(tempVar).append(" :=.i32 ").append(stringList.get(1)).append(";\n");
                        args.append(tempVar);
                        res = tempVar;
                    }else if(opsBo.contains(callArgs.getKind())) {
                        String tempVar = getTempVar("bool", true);
                        pre.append(tempVar + " :=.bool ").append(stringList.get(1)).append(";\n");
                        args.append(tempVar);
                        res = tempVar;
                    }else{
                        args.append(stringList.get(1));
                        res = stringList.get(1);
                    }


                }else{
                    //if (callArgs.getKind().equals("AllocationExpression"))
                    args.append(stringList.get(0));
                    res = stringList.get(0);
                }

                if(res.contains("i32") || res.contains("array")){
                    String array = res.contains("array") ? "array" : "";
                    key.append(array + "int");
                }else if (res.contains("bool")){
                    key.append("bool");
                }else{
                    key.append(res.split("\\.")[1]);
                }
            }

            result.append("invokevirtual (this,");
            result.append("\""+methodCall.get("name") + "\"").append(args.toString());

            result.append(")");

            try{
                result.append("." + getTypeOllir(symbolTableImp.methods.get(key.toString()).returnType, !symbolTableImp.methods.get(key.toString()).returnType.isArray()));
                }catch (NullPointerException e){//if function is from extended superclass
                result.append("." + "i32");
            }
            finalList.add(pre.toString());
            finalList.add(result.toString());
        }


        return finalList;
    }


    private String dealWithEqualStatement(JmmNode equalNode){
        List<JmmNode> children = equalNode.getChildren();

        // Get OLLIR type of operation
        Type leftType = getIdentifierType(children.get(0));


        String type = getTypeOllir(leftType, children.get(0).getNumChildren() > 0);
        boolean putfield = isPutfield(children);

        String leftEqual, rightEqual, preEqual = "";

        List<String> resLeft = dealWithChild(children.get(0));
        List<String> resRight = dealWithChild(children.get(1));

        if(resLeft.size() > 1){

            preEqual = resLeft.get(0) + "\n";

            leftEqual = resLeft.get(1);
        }else{
            leftEqual = resLeft.get(0);
        }

        if(resRight.size() > 1){
            preEqual += resRight.get(0) + "\n";
            if(children.get(1).getKind().equals("TwoPartExpression")){
                String tempVar =  getTempVar(type, true);

                preEqual += tempVar + " :=." + type + " " + resRight.get(1) + ";\n";
                rightEqual = tempVar;
            }else{

                rightEqual = resRight.get(1);
            }


        }else{
            rightEqual = resRight.get(0);
            String rightType=rightEqual.split("\\.")[rightEqual.split("\\.").length-1];
            String StringleftType=leftEqual.split("\\.")[leftEqual.split("\\.").length-1];
            if (!rightType.equals(StringleftType)){
                int start = rightEqual.lastIndexOf(rightType);
                StringBuilder builder = new StringBuilder();
                builder.append(rightEqual.substring(0, start));
                builder.append(StringleftType);
                builder.append(rightEqual.substring(start + rightType.length()));
                rightEqual=builder.toString();
            }
        }


        if (putfield) {
            if (Arrays.asList("Sum","Sub","Mult","Div").contains(children.get(1).getKind())){
                String tempVar = getTempVar("i32", true);
                preEqual += preEqual + tempVar +" :=.i32 " + rightEqual + ";\n";
                rightEqual = tempVar;
            }
            if(children.get(0).getKind().equals("Identifier"))
                leftEqual=children.get(0).get("name")+"."+type;
            return preEqual + '\n' + "putfield (this," + leftEqual + "," + rightEqual + ").V;\n";
        }
        if (rightEqual.contains("i32")) {
            if(!rightEqual.contains("array"))
                return preEqual + "\n" + leftEqual + " :=." + "i32" + " " + rightEqual + ";\n";
            return preEqual + "\n" + leftEqual + " :=." + "array.i32" + " " + rightEqual + ";\n";
        }
        if (rightEqual.contains("bool")) {
            if(!rightEqual.contains("array"))
                return preEqual + "\n" + leftEqual + " :=." + "bool" + " " + rightEqual + ";\n";
            return preEqual + "\n" + leftEqual + " :=." + "array.bool" + " " + rightEqual + ";\n";
        }
        return preEqual + "\n" + leftEqual + " :=." + type + " " + rightEqual + ";\n";

    }


    private boolean isPutfield(List<JmmNode> children) {
        boolean putfield = false;
        if (children.get(0).getKind().equals("Identifier")){
            JmmNode identifierNode = children.get(0);


            putfield = verifySymbolTab(identifierNode);

        }
        return putfield;
    }


    private boolean isGetfield(JmmNode identifierNode) {
        boolean getField = false;
        if (identifierNode.getKind().equals("Identifier")){


            getField = verifySymbolTab(identifierNode);

        }
        return getField;
    }


    private boolean verifySymbolTab(JmmNode identifierNode){
        boolean putfield = false;
        List<Symbol> classVariables = symbolTableImp.getFields();
        for (Symbol i : classVariables) {
            if (i.getName().equals(identifierNode.get("name"))) {
                putfield = true;
            }
        }

        // Searching the symbols in the function parameters
        List<Parameter> parameters = symbolTableImp.getMethod(this.methodKey).getParameters();
        for (Parameter i : parameters) {
            if (i.getSymbol().getName().equals(identifierNode.get("name")))
                putfield = false;
        }


        HashMap<Symbol, Boolean> localVariables = symbolTableImp.getMethod(this.methodKey).getLocalVariables();
        for (Symbol i : localVariables.keySet()) {
            if (i.getName().equals(identifierNode.get("name")))
                putfield = false;
        }
        return putfield;
    }


    private List<String> dealWithBoolOp(JmmNode booleanNode) {

        List<String> finalList = new ArrayList<>();
        StringBuilder result = new StringBuilder();
        List<JmmNode> children = booleanNode.getChildren();

        if(booleanNode.getKind().equals("Boolean")){            // for if(true)
            finalList.add("");      // There are no Temps
            finalList.add(dealWithBoolean(booleanNode) + " &&.bool " + dealWithBoolean(booleanNode));
            return finalList;
        }

        if(children.size() == 0){
            finalList.add("");      // There are no Temps
            finalList.add(booleanNode.get("name")+".bool" + " &&.bool " + "true" + ".bool");
            return finalList;
        }


        String left, right, pre;        // the left side, right side (of the operation) and what precedes it
        List<String> temps = dealWithTemp(children, "bool");    // Checks if temporary variables are needed
        left = temps.get(0); right = temps.get(1); pre = temps.get(2);
        result.append(left);

        switch (booleanNode.getKind()){
            case "Not":{
                result.append(" !.bool ");
                result.append(left);
                break;
            }
            case "And":{
                result.append(" &&.bool ");
                result.append(right);
                break;
            }
        }

        finalList.add(pre);
        finalList.add(result.toString());


        return finalList;
    }


    private List<String> dealWithArithmetic(JmmNode arithmeticNode){


        List<String> finalList = new ArrayList<>();
        StringBuilder result = new StringBuilder();
        List<JmmNode> children = arithmeticNode.getChildren();

        String left, right, pre;            // the left side, right side (of the operation) and what precedes it

        // For access to array
        if(arithmeticNode.getKind().equals("Integer")){
            String tempVar = getTempVar("i32", true);
            pre = tempVar + " :=.i32 " + arithmeticNode.get("value") + ".i32;";
            finalList.add(pre);
            finalList.add(tempVar);
            return finalList;
        }else if(arithmeticNode.getKind().equals("Identifier")){
            List<String> res = dealWithIdentifier(arithmeticNode);

            if(res.size() > 1){
                finalList.add(res.get(0));
            }else{
                finalList.add("");
            }
            finalList.add(res.get(0));

            return finalList;
        }


        if(arithmeticNode.getChildren().size() > 0 && arithmeticNode.getChildren().get(1).getKind().equals("TwoPartsExpression")){
            List<String> res = dealWithTwoPart(arithmeticNode.getChildren().get(1));

            if(res.size() > 1){
                finalList.add(res.get(0));
            }else{
                finalList.add("");
            }
            finalList.add(res.get(1));

            return finalList;
        }



        List<String> temps = dealWithTemp(children, "i32");         // Checks if temporary variables are needed
        left = temps.get(0); right = temps.get(1); pre = temps.get(2);
        if (right.contains(":=")) {
            pre += right;
            right=right.split("\\.")[0]+"."+right.split("\\.")[right.split("\\.").length-1].replace("\n","").replace(";","");//temp variable
        }
        result.append(left);

        switch (arithmeticNode.getKind()){
            case "Sum":{result.append(" +.i32 "); break;}
            case "Sub":{result.append(" -.i32 "); break;}
            case "Mult":{result.append(" *.i32 "); break;}
            case "Div":{result.append(" /.i32 "); break;}
            case "LessThan":{result.append(" <.i32 "); break;}
        }

        result.append(right);

        finalList.add(pre);
        finalList.add(result.toString());


        return finalList;
    }

    private String getTempVar(String type, boolean left){
        int tempN = this.tempsCount++;

        if(left)

            if (type.contains("bool")){
                return "tb" + tempN + ".bool";
        }else {
                return "ti" + tempN + "." + type;
            }
        return type.contains("bool") ? "ub" + tempN + ".bool" : "ui" + tempN + "." + type;
    }

    private List<String> dealWithTemp(List<JmmNode> children, String type){

        StringBuilder pre = new StringBuilder();

        List<String> leftPart = dealWithChild(children.get(0));
        String left = "";
        if(leftPart.size() > 1){

            String[] parts = leftPart.get(1).split("\n");
            if(parts.length > 1){
                for(int i = 0; i < parts.length - 1; i++){
                    pre.append(parts[i]).append("\n");
                }
                left = parts[parts.length - 1];
                pre.append(leftPart.get(0)).append("\n");
            }else{
                pre.append(leftPart.get(0)).append("\n");
                left = leftPart.get(1);
            }


        }else{
            left = leftPart.get(0);
        }

        String right = "";

        List<String> opsAr = Arrays.asList("Sum", "Sub", "Mult", "Div", "And", "LessThan");
        List<String> opsBo = Arrays.asList("And", "Not");

        String leftKind = children.get(0).getKind();
        // Checks if temporary variables are needed
        if(opsAr.contains(leftKind) || opsBo.contains(leftKind) || leftKind.equals("TwoPartExpression")){

            String tempVar = getTempVar(type, true);
            pre.append(tempVar + " :=." + type + " ");
            pre.append(left + ";\n");

            left = tempVar;

        }

        StringBuilder rightTemp = new StringBuilder();

        if(children.size() > 1){
            List<String> rightPart = dealWithChild(children.get(1));
            if(rightPart.size() > 1){
                pre.append(rightPart.get(0)).append("\n");
                right = rightPart.get(1);
            }else{
                right = rightPart.get(0);
            }
        }



        if(children.size() > 1 && (opsAr.contains(children.get(1).getKind()) || opsBo.contains(children.get(1).getKind()) || children.get(1).getKind().equals("TwoPartExpression"))){

            String tempVar = getTempVar(type, false);
            pre.append(tempVar).append(" :=.").append(type).append(" ");
            pre.append(right).append(";\n");
            right = tempVar;

        }

        List<String> res = new ArrayList<>();
        res.add(left);
        res.add(right);
        if(pre.length() == 0)
            res.add("");
        else
            res.add(pre.toString());

        return res;
    }


    private String dealWithReturn(JmmNode node){
        this.hasReturn = true;
        StringBuilder result = new StringBuilder();
        StringBuilder retResult = new StringBuilder();


        for (JmmNode child : node.getChildren()){
            List<String> res = dealWithChild(child);
            if(res.size() > 1){
                result.append(res.get(0)).append("\n");
                retResult.append(res.get(1));
            }else{
                retResult.append(res.get(0));
            }

        }

        String type = getTypeOllir(symbolTableImp.methods.get(this.methodKey).returnType, !symbolTableImp.methods.get(this.methodKey).returnType.isArray());

        int tempcount=tempsCount++;
        String tempname;
        if(type.equals("bool"))
            tempname="t"+"b"+tempcount;
        else
            tempname="t"+"i"+tempcount;

        result.append(tempname+"."+type);result.append(" :=."+type+" " + retResult );result.append(";\n");

        result.append("ret.");
        result.append(type);
        result.append(" ");
        result.append(tempname+"."+type);

        /*if(retResult.toString().contains("invoke")){
            //result.insert(0, createTemp(retResult.toString()).get(0));
            //result.append(createTemp(retResult.toString()));
        }else if(retResult.toString().matches(".*[+|-|/|\\\\*].*")){
            List<String> temp = createTemp(retResult.toString());
            for (String i :temp.subList(0,temp.size()-2))
                result.insert(0,i+"\n");
            result.append(temp.get(temp.size()-1));
        }
        else {
            result.append(retResult.toString());
        }*/

//            result.append(dealWithChild(child).get(0));
        result.append(";\n");
        return result.toString();
    }

    private String getParameterSig(String identifierName){
        String res = "";
        Parameter parameter = this.symbolTableImp.getMethod(this.methodKey).getParameter(identifierName);

        if(parameter != null)
            res =  "$" + Integer.toString(parameter.getOrder()) + ".";
        return res;
    }


    private List<String> dealWithIdentifier(JmmNode child) {
        String identifierName = child.get("name");
        String parameterStr = "";
        Parameter parameter = this.symbolTableImp.getMethod(this.methodKey).getParameter(identifierName);
        if(parameter != null)
            parameterStr =  "$" + Integer.toString(parameter.getOrder()) + ".";


        boolean accessToArray = false;


        StringBuilder before = new StringBuilder();
        String array = "";
        Type identifierType = getIdentifierType(child);
        String typeStr = getTypeOllir(identifierType, child.getChildren().size() != 0);

        if(child.getChildren().size() > 0 && child.getChildren().get(0).getKind().equals("AccessToArray")){     //if is accessing an array
            accessToArray = true;
            JmmNode indexNode = child.getChildren().get(0).getChildren().get(0);

            List<String> res = dealWithChild(indexNode);
            String  inline = "";
            if(res.size() > 1){
                before.append(res.get(0)).append("\n");
                inline = res.get(1);
            }else{
                inline = res.get(0);
            }

            String betwPar;
            String tempVar = getTempVar("i32", true);
            before.append(tempVar + ":=.i32 ").append(inline).append(";\n");
            if(isGetfield(child)){
                String getFTempVar = getTempVar(typeStr, true);
                before.append(getFTempVar.split("\\.")[0] + ".array.i32").append(" :=.").append("array.").append(typeStr).append(" getfield(this, ")
                        .append(identifierName).append(".").append("array.").append(typeStr).append(").").append(typeStr).append(";\n");
                identifierName = getFTempVar.split("\\.")[0];
            }
            betwPar = tempVar;

            array = "[" +  betwPar + "]";
        }


        List<String> finalList = new ArrayList<>();
        boolean putfield = isPutfield(child.getParent().getChildren());

        String tempVar = "";
        if((putfield && accessToArray) || isGetfield(child)){
            tempVar = getTempVar(typeStr, true);
        }


        if(putfield && accessToArray)
            before.append(tempVar).append(" :=.").append(typeStr).append(" ")
                    .append(parameterStr).append(identifierName).append(array).append(".").append(getTypeOllir(identifierType)).append(";\n");

        if(isGetfield(child) ){//&& !child.getParent().getKind().equals("EqualStatement")){
            before.append(tempVar).append(" :=.").append(typeStr).append(" getfield(this, ")
                            .append(identifierName).append(".").append(typeStr).append(").").append(typeStr).append(";\n");
        }

        if(before.length() > 0)
            finalList.add(before.toString());

        if((putfield && accessToArray) || (isGetfield(child)))
            finalList.add(tempVar);
        else
            finalList.add(parameterStr + identifierName + array + "." + typeStr);

        return finalList;
    }


    private List<String> mountIdentifierOLLIR(JmmNode identifierNode, JmmNode accessNode){

        String identifierName = identifierNode.get("name");
        String pre = "";
        Parameter parameter = this.symbolTableImp.getMethod(this.methodKey).getParameter(identifierName);

        if(parameter != null)
            pre =  "$" + Integer.toString(parameter.getOrder()) + ".";

        String array = "", before = "";

        JmmNode indexNode = accessNode.getChildren().get(0);

        List<String> res;
        if(indexNode.getKind().equals("MethodCall"))
            res=dealWithChild(accessNode.getParent());
        else
            res =  dealWithArithmetic(indexNode);
        before += "\n" + res.get(0);

        String betwPar;
        if(!indexNode.getKind().equals("Integer")){
            String tempVar = getTempVar("i32", true);
            before += "\n"  + tempVar +" :=.i32 " + res.get(1) +";";
            betwPar = tempVar;
        }else {
            betwPar = res.get(1);
        }
        array = "[" +  betwPar + "]";



        Type identifierType = getIdentifierType(identifierNode);

        List<String> finalList = new ArrayList<>();
        finalList.add(before);
        finalList.add(pre + identifierName + array + "." + getTypeOllir(identifierType));

        return finalList;
    }




    private String dealWithInteger(JmmNode child) {

        return child.get("value") + ".i32";
    }


    /**
     * Returns the ollir type of integer and boolean
     * @param type information about the variable type
     * @return  the ollir type, in a string
     */
    private String getTypeOllir(Type type) {

        return getTypeOllir(type, true);
    }

    /**
     * Returns the ollir type of integer and boolean
     * @param type information about the variable type
     * @return  the ollir type, in a string
     */
    private String getTypeOllir(Type type, boolean access) {


        String typeOllir = "";

        if(type.isArray() && !access)
            typeOllir += "array.";

        if(type.getName().equals("int")) typeOllir += "i32";
        else if(type.getName().equals("boolean")) typeOllir += "bool";
        else if(type.getName().equals("void")) typeOllir += "V";
        else typeOllir += type.getName();



        return typeOllir;
    }


    private Type getIdentifierType(JmmNode node) {

        if(!node.getKind().equals("Identifier"))
            return null;

        // Searching the symbols of the local variables to see if any has the name we're looking for
        HashMap<Symbol, Boolean> localVariables = symbolTableImp.getMethod(this.methodKey).getLocalVariables();
        for (Symbol i : localVariables.keySet()) {
            if (i.getName().equals(node.get("name")))
                return i.getType();
        }

        // Searching the symbols in the function parameters
        List<Parameter> parameters = symbolTableImp.getMethod(this.methodKey).getParameters();
        for (Parameter i : parameters) {
            if (i.getSymbol().getName().equals(node.get("name")))
                return i.getSymbol().getType();
        }

        // Searching the symbols of the class variables to see if any has the name we're looking for
        List<Symbol> classVariables = symbolTableImp.getFields();
        for (Symbol i : classVariables) {
            if (i.getName().equals(node.get("name")))
                return i.getType();
        }

        return null;
    }


    private String dealWithBoolean(JmmNode child) {

        return child.get("value").equals("true") ? "1.bool" : "0.bool";
    }

    private List<String> dealWithMethodCall(JmmNode identifierNode, JmmNode dotExpression) {

        StringBuilder result = new StringBuilder();

        List<String> finalList = new ArrayList<>();

        if (dotExpression.getChildren().get(0).getKind().equals("MethodCall")) {
            JmmNode methodCall = dotExpression.getChildren().get(0);


            StringBuilder key = new StringBuilder(methodCall.get("name"));

            StringBuilder args = new StringBuilder();
            StringBuilder pre = new StringBuilder();

            for (JmmNode callArgs : methodCall.getChildren()) {
                args.append(",");
                String res = "";
                List<String> stringList = dealWithChild(callArgs);

                List<String> opsAr = Arrays.asList("Sum", "Sub", "Mult", "Div", "And", "LessThan");
                List<String> opsBo = Arrays.asList("And", "Not");


                if (stringList.size() > 1) {
                    pre.append(stringList.get(0)).append("\n");
                    if (callArgs.getKind().equals("TwoPartExpression")) {
                        Type type = getIdentifierType(identifierNode);
                        String typeString = getTypeOllir(type, callArgs.getChildren().get(1).getKind().equals("AccessToArray"));
                        res = getTempVar(typeString, true);
                        pre.append(res + " :=." + typeString + " " + stringList.get(1) + ";");
                        args.append(res);
                    } else if (opsAr.contains(callArgs.getKind())) {
                        String tempVar = getTempVar("i32", true);
                        pre.append(tempVar).append(" :=.i32 ").append(stringList.get(1)).append(";\n");
                        args.append(tempVar);
                        res = tempVar;
                    } else if (opsBo.contains(callArgs.getKind())) {
                        String tempVar = getTempVar("bool", true);
                        pre.append(tempVar + " :=.bool ").append(stringList.get(1)).append(";\n");
                        args.append(tempVar);
                        res = tempVar;
                    } else {
                        args.append(stringList.get(1));
                        res = stringList.get(1);
                    }


                } else {
                    //if (callArgs.getKind().equals("AllocationExpression"))
                    args.append(stringList.get(0));
                    res = stringList.get(0);
                }

                if (res.contains("i32") || res.contains("array")) {
                    String array = res.contains("array") ? "array" : "";
                    key.append(array + "int");
                } else if (res.contains("bool")) {
                    key.append("bool");
                } else {
                    key.append(res.split("\\.")[1]);
                }
            }

            result.append("invokevirtual (this,");
            result.append("\"" + methodCall.get("name") + "\"").append(args.toString());

            result.append(")");

            try {
                result.append("." + getTypeOllir(symbolTableImp.methods.get(key.toString()).returnType, !symbolTableImp.methods.get(key.toString()).returnType.isArray()));
            } catch (NullPointerException e) {//if function is from extended superclass
                result.append("." + "i32");
            }
            finalList.add(pre.toString());
            finalList.add(result.toString());
            finalList.add(getTypeOllir(symbolTableImp.methods.get(key.toString()).returnType, !symbolTableImp.methods.get(key.toString()).returnType.isArray()));
            }

        return finalList;
        }
}
