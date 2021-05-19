import pt.up.fe.comp.jmm.JmmNode;
import pt.up.fe.comp.jmm.analysis.table.Symbol;
import pt.up.fe.comp.jmm.analysis.table.Type;
import pt.up.fe.comp.jmm.ast.PreorderJmmVisitor;
import pt.up.fe.comp.jmm.report.Report;

import java.util.*;

public class OllirVisitor extends PreorderJmmVisitor<List<Report>, Boolean> {
    private final SymbolTableImp symbolTableImp;
    private final StringBuilder ollirCode = new StringBuilder("\n");
    private String methodName, methodKey;
    //private int tempsCount = 1;
    //private int tempsCount2 = 1;
    private int whileCounter = 1;
    private int tempsCount = 1, tempIf = 0;
    private boolean hasReturn = false;

    public OllirVisitor(SymbolTableImp symbolTableImp) {
        super();

        this.symbolTableImp = symbolTableImp;

        addVisit("Class", this::dealWithClass);

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
        StringJoiner res = new StringJoiner("\n");

        if(op.getKind().equals("LessThan")){
            cond = dealWithArithmetic(op);
        }else {
            cond = dealWithBoolOp(op);
        }

        String pre = cond.get(0), inCond = cond.get(1);

        res.add(pre);

        res.add("if ( " + inCond.replace("<", ">=") + " ) goto else" + countIf +";");

        // IfElse Body
        JmmNode ifBody = ifElse.getChildren().get(1);

        for(JmmNode bodyExp : ifBody.getChildren()){
            res.add(join(dealWithChild(bodyExp)));
        }
        res.add("goto endif" + countIf + ";");

        // IfElse else
        res.add("else" + countIf +":");

        JmmNode ifElseBody = ifElse.getChildren().get(2);
        for(JmmNode bodyExp : ifElseBody.getChildren()){
            res.add(join(dealWithChild(bodyExp)));
        }

        res.add("endif" + countIf + ":\n");

        List<String> finalList = new ArrayList<>();
        finalList.add(res.toString());
        return finalList;
    }

    private String join(List<String> in){
        StringBuilder res = new StringBuilder();

        if(in.size() == 1)
            return in.get(0);

        for(String str : in){
            if(str.length()>0)
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
                    result.append(dealWithWhileCondition(child, whileNumber));
                    continue;
                }
                case "WhileBody": {
                    result.append(dealWithWhileBody(child, whileNumber));
                    continue;
                }
            }
        }
        result.append("EndLoop" + whileNumber + ":\n");
        return result.toString();
    }

    private String dealWithWhileCondition(JmmNode node, int whileNumber) {
        StringBuilder result = new StringBuilder("Loop" + whileNumber + ":\n");

        JmmNode ConditionNode = node.getChildren().get(0);
        for (String i : dealWithChild(ConditionNode)) {
            result.append(i).append('\n');
        }


        result.deleteCharAt(result.lastIndexOf("\n")); //necessary to locate where to put if condition
        result.insert(result.lastIndexOf("\n") + 1, "if (");
        if (result.lastIndexOf(";") == result.length() - 1)//remove any ; that should not exist
            result.deleteCharAt(result.lastIndexOf(";"));
        result.append(") goto Body" + whileNumber + ";\n");

        result.append("goto EndLoop" + whileNumber + ";\n");
        return result.toString();
    }

    private String dealWithWhileBody(JmmNode node, int whileNumber) {
        StringBuilder result = new StringBuilder("Body" + whileNumber + ":\n");
        for (JmmNode child : node.getChildren()) {
            for (String i : dealWithChild(child)) {
                result.append(i.replace("\n", "")).append('\n');
            }
        }
        result.append("goto Loop" + whileNumber + ";\n");

        return result.toString();
    }


    private List<String> dealWithAllocationExpression(JmmNode child) {
        StringBuilder result = new StringBuilder();

        List<String> finalList = new ArrayList<>();
        boolean putfield = isPutfield(child.getParent().getChildren());


        if (child.getChildren().get(0).getKind().equals("Object")) {
            result.append("new (").append(child.getChildren().get(0).get("name")).append(")").append(".");
            result.append(child.getChildren().get(0).get("name")).append(";\n");
            result.append("invokespecial(").append(child.getParent().getChildren().get(0).get("name")).append(".").append(child.getChildren().get(0).get("name")).append(",\"<init>\").V");
            finalList.add(result.toString());
            return finalList;
        }
        else {
            JmmNode lengthNode = child.getChildren().get(0).getChildren().get(0); // Get the nodes that are responsible for the array length
            String length;
            StringBuilder pre = new StringBuilder("");

            if(lengthNode.getKind().equals("Integer")) {    // If length is an Integer
                length = dealWithInteger(lengthNode);
            }else if(lengthNode.getKind().equals("Identifier")) {   // If length is an identifier
                length = dealWithIdentifier(lengthNode).get(0);
            }else{                                                  // If length is an arithmetic op
                List<String> tempRes = dealWithArithmetic(lengthNode);
                pre.append(tempRes.get(0)).append("\n");
                length = tempRes.get(1);
            }

            result.append("new(array, ").append(length).append(")").append(".array.i32");
            if(putfield){
                pre.append("a1.array.i32 :=.array.i32 ").append(result.toString()).append(";\n");
                result = new StringBuilder();
                result.append("a1.array.i32");
            }
            finalList.add(pre.toString());
            finalList.add(result.toString());
            return finalList;
        }
    }


    private boolean dealWithClass(JmmNode node, List<Report> reports) {

        //class constructor
        ollirCode.append(node.get("name") + " {\n");
        for (JmmNode child : node.getChildren()) {
            if (child.getKind().equals("Var")) {
                ollirCode.append(dealWithVar(child) + '\n');
            }
        }
        ollirCode.append(".construct " + node.get("name") + "().V {\n");
        ollirCode.append("" + "invokespecial(this, \"<init>\").V;\n");
        ollirCode.append("" + "}\n");

        for (JmmNode child:node.getChildren()){
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
                            if (child2.getKind().equals("Type"))
                                methodKey.append(child2.get("name"));
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
            result.append(parameter.getSymbol().getName() + "." + getTypeOllir(parameter.getSymbol().getType()) + ",");
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
        result.append("." + getTypeOllir(symbolTableImp.methods.get(methodKey).returnType));
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

        String typeOllir = getTypeOllir(type);

        result.append(typeOllir + ";");

        return result.toString();
    }


    private List<String> dealWithTwoPart(JmmNode node){

        if(node.getChildren().get(1).getKind().equals("AccessToArray")) {
            List<String> res = new ArrayList<>();
            List<String> resAccess = dealWithArithmetic(node.getChildren().get(1).getChildren().get(0));
            res.add(resAccess.get(0));
            res.add(node.getChildren().get(0).get("name") + "[" + resAccess.get(1) + "]" +".i32");

            return res;
        }

        StringBuilder result = new StringBuilder("t" + this.tempsCount);

        JmmNode leftChild = node.getChildren().get(0);
        JmmNode rightChild = node.getChildren().get(1);

        if (leftChild.getKind().equals("This")) {
            String expressionResult = dealWithThisExpression(node);
            String type = ("." + expressionResult.split("\\.")[expressionResult.split("\\.").length - 1]);
            result.append(type+" := "+ type+ " "+expressionResult+";"+"\n");

            return Collections.singletonList(result.toString());
        }

        String objectName = leftChild.get("name");
        Symbol classSym = this.symbolTableImp.getMethod(this.methodKey).getVariable(objectName);
        if (classSym==null) {
            String before = "";
            if (this.symbolTableImp.getImports().contains(objectName)) {
                JmmNode methodCall = rightChild.getChildren().get(0);
                List<JmmNode> identifiers = methodCall.getChildren();


                StringBuilder key = new StringBuilder();
                for (JmmNode child : identifiers) {

                    key.append(",");
                    if(child.getKind().equals("TwoPartExpression")){

                        List<String> res = mountIdentifierOLLIR(child.getChildren().get(0), child.getChildren().get(1));
                        before = res.get(0) + "\n" + "t1.i32 :=.i32 " + res.get(1) + ";";
                        key.append(" t1.i32 ");

                    }else
                        key.append(dealWithChild(child).get(0));

                    for(String i:dealWithChild(child)){
                        if(i.length()>0)
                            result.append(i).append('\n');
                    }
                }

                result = new StringBuilder();
                result.append(before + "\n");
                result.append("invokestatic(" + objectName + ", \"" + methodCall.get("name")+"\"");
                result.append(key.toString());


                result.append(").V;\n");
            }
            return Collections.singletonList(result.toString());
        }

        String className = classSym.getType().getName();

        String callName = "";


        callName = rightChild.getChildren().get(0).get("name");



        StringBuilder key= new StringBuilder(callName);

        for (JmmNode callArgs : rightChild.getChildren().get(0).getChildren()) {
            result.append(",");
            for(String i:dealWithChild(callArgs)){
                if(i.length()>0)
                    result.append(i).append('\n');
            }
//            result.append(dealWithChild(callArgs).get(0));
            if(dealWithChild(callArgs).get(0).split("\\.")[1].equals("i32")){
                key.append("int");
            }else if (dealWithChild(callArgs).get(0).split("\\.")[1].equals("bool")){
                key.append("bool");
            }else{
                key.append(dealWithChild(callArgs).get(0).split("\\.")[1]);
            }
        }
        result = new StringBuilder();


        result.append("invokevirtual(" + objectName + "." + className + ",\"" + callName + "\"");

        result.append(")");

        result.append("." + getTypeOllir(symbolTableImp.methods.get(key.toString()).returnType));


        return Collections.singletonList(result.toString());
    }


    private String dealWithThisExpression(JmmNode TwoPartNode) {
        StringBuilder result=new StringBuilder();
        JmmNode rightChild = TwoPartNode.getChildren().get(1);
        if (rightChild.getChildren().get(0).getKind().equals("MethodCall")) {
            JmmNode methodCall = rightChild.getChildren().get(0);

            result.append("invokevirtual(this,");
            result.append("\""+methodCall.get("name") + "\"");
            StringBuilder key=new StringBuilder(methodCall.get("name"));

            for (JmmNode callArgs : methodCall.getChildren()) {
                result.append(",");
                for(String i:dealWithChild(callArgs)){
                    if(i.length()>0)
                        result.append(i).append('\n');
                }
//                result.append(dealWithChild(callArgs).get(0));
                if(dealWithChild(callArgs).get(0).split("\\.")[1].equals("i32")){
                    key.append("int");
                }else if (dealWithChild(callArgs).get(0).split("\\.")[1].equals("bool")){
                    key.append("bool");
                }else{
                    key.append(dealWithChild(callArgs).get(0).split("\\.")[1]);
                }
            }
            result.append(")");
            result.append("." + getTypeOllir(symbolTableImp.methods.get(key.toString()).returnType));
        }

        return result.toString();
    }


    private String dealWithEqualStatement(JmmNode equalNode){
        List<JmmNode> children = equalNode.getChildren();

        // Get OLLIR type of operation
        Type leftType = getIdentifierType(children.get(0));

        String type = getTypeOllir(leftType);
        if(leftType.isArray() && children.get(0).getChildren().size() == 0){
            type = "array." + type;
        }

        boolean putfield = isPutfield(children);

        String leftEqual, rightEqual, preEqual = "";
        List<String> resLeft = dealWithChild(children.get(0));
        List<String> resRight = dealWithChild(children.get(1));

        if(resLeft.size() > 1){
            if(resLeft.get(0).length() > 0){
                preEqual = resLeft.get(0) + "\n";
            }
            leftEqual = resLeft.get(1);
        }else{
            leftEqual = resLeft.get(0);
        }

        if(resRight.size() > 1){
            if(resRight.get(0).length() > 0){
                preEqual += resRight.get(0) + "\n";
            }
            rightEqual = resRight.get(1);
        }else{
            rightEqual = resRight.get(0);
        }



/*
        // Checks if the right child is a compost operation (arithmetic or boolean)
        if(rightEqual.equals("")){
            List<String> res;
            String rightNodeKind = children.get(1).getKind();


            if(rightNodeKind.equals("Not") || rightNodeKind.equals("And")){     // Boolean operation
                res = dealWithBoolOp(children.get(1));
            }else { // Arithmetic operation
                res = dealWithArithmetic(children.get(1));
            }

            preEqual = res.get(0);           // the OLLIR code needed before the operation
            rightEqual = res.get(1);         // the right side of the op
        }

        if(children.get(1).getKind().equals("AllocationExpression")){
            preEqual += "t1.array.i32 :=.array.i32 " + rightEqual + ";\n" ;
            rightEqual = "t1.array.i32";
        }


        if(leftEqual.contains("!!")){
            String[] res = leftEqual.split("!!");
            preEqual += res[0] + "\n";
            leftEqual = res[1];
        }
        if(rightEqual.contains("!!")){
            String[] res = leftEqual.split("!!");
            preEqual += res[0] + "\n";
            rightEqual = res[1];
        }

*/


        if(preEqual.equals("")){
            if (putfield)
                return "putfield(this," + leftEqual + "," + rightEqual + ").V;\n";
            return leftEqual + " :=."  + type + " " + rightEqual  + ";\n";
        }else{
            if (putfield)
                return preEqual + '\n' + "putfield(this," + leftEqual + "," + rightEqual + ").V;\n";
            return preEqual + "\n" + leftEqual + " :=." + type + " " + rightEqual + ";\n";
        }
    }


    private boolean isPutfield(List<JmmNode> children) {
        boolean putfield = false;
        if (children.get(0).getKind().equals("Identifier")){
            JmmNode identifierNode = children.get(0);


            List<Symbol> classVariables = symbolTableImp.getFields();
            for (Symbol i : classVariables) {
                if (i.getName().equals(children.get(0).get("name"))) {
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

        }
        return putfield;
    }


    private List<String> dealWithBoolOp(JmmNode booleanNode) {

        List<String> finalList = new ArrayList<>();
        StringBuilder result = new StringBuilder();
        List<JmmNode> children = booleanNode.getChildren();

        if(booleanNode.getKind().equals("Boolean")){
            finalList.add("");      // There are no Temps
            finalList.add(booleanNode.get("value")+".bool" + " &&.bool " + booleanNode.get("value") + ".bool");
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
            pre = "t1.i32 :=.i32 " + arithmeticNode.get("value") + ".i32;";
            finalList.add(pre);
            finalList.add("t1.i32");
            return finalList;
        }



        List<String> temps = dealWithTemp(children, "i32");         // Checks if temporary variables are needed
        left = temps.get(0); right = temps.get(1); pre = temps.get(2);
        if (right.contains(":=")) {
            pre=right;
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


    private List<String> dealWithTemp(List<JmmNode> children, String type){

        StringBuilder pre = new StringBuilder();

        StringBuilder leftTemp = new StringBuilder();
        for (String i : dealWithChild(children.get(0))){
            if(i.length() > 0)
                leftTemp.append('\n').append(i);
        }
        leftTemp.deleteCharAt(0);
        String left = leftTemp.toString(), right = "";

        List<String> opsAr = Arrays.asList("Sum", "Sub", "Mult", "Div", "And", "LessThan");
        List<String> opsBo = Arrays.asList("And");


        // Checks if temporary variables are needed
        if(opsAr.contains(children.get(0).getKind()) || opsBo.contains(children.get(0).getKind())){

            List<String> res;
            if(opsAr.contains(children.get(0).getKind())){         // it uses recursion to determine the ollir code of the operands
                res = dealWithArithmetic(children.get(0));
            }else{
                res = dealWithBoolOp(children.get(0));
            }

            left = "t1." + type;
            pre.append(res.get(0) + "\n");
            pre.append("t1." + type + " :=." + type + " ");
            pre.append(res.get(1) + ";\n");
        }

        StringBuilder rightTemp = new StringBuilder();
        for (String i:dealWithChild(children.get(1))){
            if(i.length() > 0)
                rightTemp.append('\n').append(i);
        }
        rightTemp.deleteCharAt(0);
        right=rightTemp.toString();
        if(children.size() > 1 && (opsAr.contains(children.get(1).getKind()) || opsBo.contains(children.get(1).getKind()))){
            right = "u1." + type;

            List<String> res;
            if(opsAr.contains(children.get(0).getKind())){
                res = dealWithArithmetic(children.get(1));
            }else{
                res = dealWithBoolOp(children.get(1));
            }

            pre.append(res.get(0) + "\n");
            pre.append("u1." + type + " :=." + type + " ");
            pre.append(res.get(1) + ";\n");
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
        result.append("ret.");
        result.append(getTypeOllir(symbolTableImp.methods.get(this.methodKey).returnType));
        result.append(" ");
        for (JmmNode child:node.getChildren())
            result.append(join(dealWithChild(child)));

//            result.append(dealWithChild(child).get(0));
        result.append(";\n");
        return result.toString();
    }


    private List<String> dealWithIdentifier(JmmNode child) {
        String identifierName = child.get("name");
        String pre = "";
        Parameter parameter = this.symbolTableImp.getMethod(this.methodKey).getParameter(identifierName);
        boolean accessToArray = false;

        if(parameter != null)
            pre =  "$" + Integer.toString(parameter.getOrder()) + ".";

        StringBuilder before = new StringBuilder();
        String array = "";
        if(child.getChildren().size() > 0 && child.getChildren().get(0).getKind().equals("AccessToArray")){     //if is accessing an array
            accessToArray = true;
            JmmNode indexNode = child.getChildren().get(0).getChildren().get(0);

            //List<String> res =  dealWithArithmetic(indexNode);
            List<String> res =  dealWithArithmetic(indexNode);
            before.append(res.get(0)).append("\n");

            String betwPar;
            if(!indexNode.getKind().equals("Integer")){
                before.append("t1.i32 :=.i32 ").append(res.get(1)).append(";\n");
                betwPar = "t1.i32";
            }else {
                betwPar = res.get(1);
            }
            array = "[" +  betwPar + "]";


        }
        Type identifierType = getIdentifierType(child);
        if(identifierType.isArray() && child.getChildren().size() == 0)
            array += ".array";

        List<String> finalString = new ArrayList<>();
        boolean putfield = isPutfield(child.getParent().getChildren());

        if(putfield && accessToArray){
            before.append("t1.i32 :=.i32 ").append(pre).append(identifierName).append(array).append(".").append(getTypeOllir(identifierType)).append(";");
        }

        if(!before.isEmpty())
            finalString.add(before.toString());

        if(putfield && accessToArray)
            finalString.add("t1.i32");
        else
            finalString.add(pre + identifierName + array + "." + getTypeOllir(identifierType));

        return finalString;
    }


    private List<String> mountIdentifierOLLIR(JmmNode identifierNode, JmmNode accessNode){

        String identifierName = identifierNode.get("name");
        String pre = "";
        Parameter parameter = this.symbolTableImp.getMethod(this.methodKey).getParameter(identifierName);

        if(parameter != null)
            pre =  "$" + Integer.toString(parameter.getOrder()) + ".";

        String array = "", before = "";

        JmmNode indexNode = accessNode.getChildren().get(0);

        List<String> res =  dealWithArithmetic(indexNode);
        before += "\n" + res.get(0);

        String betwPar;
        if(!indexNode.getKind().equals("Integer")){
            before += "\n"  + "t1.i32 :=.i32 " + res.get(1) +";";
            betwPar = "t1.i32";
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

        StringBuilder result = new StringBuilder();
        //if(type.isArray()) result.append("array.");

        String typeOllir;

        if(type.getName().equals("int")) typeOllir = "i32";
        else if(type.getName().equals("boolean")) typeOllir = "bool";
        else if(type.getName().equals("void")) typeOllir = "V";
        else typeOllir = type.getName();

        result.append(typeOllir);

        return result.toString();
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
        return child.get("value") + ".bool";
    }



}
