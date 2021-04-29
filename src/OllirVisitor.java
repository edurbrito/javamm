import pt.up.fe.comp.jmm.JmmNode;
import pt.up.fe.comp.jmm.analysis.table.Symbol;
import pt.up.fe.comp.jmm.analysis.table.Type;
import pt.up.fe.comp.jmm.ast.PreorderJmmVisitor;
import pt.up.fe.comp.jmm.report.Report;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class OllirVisitor extends PreorderJmmVisitor<List<Report>, Boolean> {
    private SymbolTableImp symbolTableImp;
    private StringBuilder ollirCode = new StringBuilder("\n");
    private String methodName;

    public OllirVisitor(SymbolTableImp symbolTableImp) {
        super();


        this.symbolTableImp = symbolTableImp;

        addVisit("Class", this::dealWithClass);

    }
    public String getOllirCode(){return this.ollirCode.toString();}

    private String dealWithChild(JmmNode child){
        switch (child.getKind()){
            case "Method"->{return dealWithMethod(child);}
            case "MainMethod"->{return dealWithMainMethod(child);}
            case "TwoPartExpression"->{return dealWithTwoPart(child);}
            case "EqualStatement"-> {return dealWithEqualStatement(child);}
            case "Identifier" ->{ return dealWithIdentifier(child);}
            case "Integer" ->{ return dealWithInteger(child);}
            //case "Var"->{ollirCode.append(dealWithVar(child));}
            //case "AllocationExpression"->{return dealWithAllocationExpression(child);}
        }
        return "";
    }



    private boolean dealWithClass(JmmNode node, List<Report> reports) {

        //class constructor
        ollirCode.append(node.get("name") + " {\n");
        ollirCode.append(".construct "+node.get("name")+"().V {\n");
        ollirCode.append("invokespecial(this, \"<init>\").V;\n");
        ollirCode.append("}\n");

        for (JmmNode child:node.getChildren()){
            dealWithChild(child);
        }
        ollirCode.append("}");

        return true;
    }



    private String getReturnType(JmmNode node){
        MethodTable methodTable = null;
        for (String i: this.symbolTableImp.methods.keySet()){
            if (this.symbolTableImp.methods.get(i).getName().equals(node.get("name"))){
                methodTable=this.symbolTableImp.methods.get(i);
                break;
            }
        }

        assert methodTable != null;
        return getTypeOllir(methodTable.returnType);
    }


    private String getArgs(JmmNode node){
        StringBuilder result = new StringBuilder("\n");
        System.out.println(this.symbolTableImp);
        MethodTable methodTable = null;
        for (String i: this.symbolTableImp.methods.keySet()){
            if (this.symbolTableImp.methods.get(i).getName().equals(node.get("name"))){
                methodTable=this.symbolTableImp.methods.get(i);
                break;
            }
        }

        assert methodTable != null;

        for (Symbol parameter : methodTable.parameters){
            result.append(parameter.getName()+getTypeOllir(parameter.getType())+",");
        }
        result.deleteCharAt(result.length()-1);

        return result.toString();
    }

    private String dealWithMainMethod(JmmNode node) {
        this.methodName = "main";
        StringBuilder result = new StringBuilder("\n");
        JmmNode methodBody = node.getChildren().get(0);

        result.append(".method public static main(" + node.get("argName") + ".array.String).V {\n");

        for (JmmNode child:methodBody.getChildren()){
            result.append(dealWithChild(child));
        }
        result.append("}");
        return result.toString();
    }


    private String dealWithMethod(JmmNode node) {
        this.methodName = node.get("name");

        StringBuilder result = new StringBuilder();
        result.append(".method public "+ node.get("name")+"("+getArgs(node)+").V {");

        for (JmmNode child:node.getChildren()){
            result.append(dealWithChild(child));
        }

        result.append("}");
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

    private String dealWithTwoPart(JmmNode node){
        JmmNode leftChild = node.getChildren().get(0);
        JmmNode rightChild = node.getChildren().get(0);

        String objectName = leftChild.get("name");
        Symbol classSym = this.symbolTableImp.getMethod(this.methodName).getVariable(objectName);
        String className = classSym.getType().getName();

        String callName = rightChild.getChildren().get(0).get("name");

        String res = "invokevirtual(" + objectName + "." + className + ",\"" + callName + "\").V";

        return res;
    }

    private String dealWithEqualStatement(JmmNode equalNode){
        List<JmmNode> children = equalNode.getChildren();

        // Get OLLIR type of operation
        String type = getTypeOllir(getIdentifierType(children.get(0)));

        String left, right, pre = "";
        left = dealWithChild(children.get(0));

        right = dealWithChild(children.get(1));
        if(right.equals("")){
            List<String> res =  dealWithArithmetic(children.get(1));
            pre = res.get(0);
            right = res.get(1);
        }


        if(pre.equals("")){
            System.out.println("OIAA");
            return left + " :=." + type + " " + right + "\n";
        }else{
            return pre + "\n" + left + " :=." + type + " " + right + "\n";
        }

    }

    private List<String> dealWithArithmetic(JmmNode arithmeticNode){
        List<String> finalList = new ArrayList<>();
        StringBuilder result = new StringBuilder();
        List<JmmNode> children = arithmeticNode.getChildren();


        String left, right, pre;
        List<String> temps = dealWithTemp(children);
        System.out.println("RES: " + temps);
        left = temps.get(0); right = temps.get(1); pre = temps.get(2);
        result.append(left);

        switch (arithmeticNode.getKind()){
            case "Sum"->{result.append(" + ");}
            case "Sub"->{result.append(" - ");}
            case "Mult"->{result.append(" * ");}
            case "Div"->{result.append(" / ");}
        }
        result.append(right);

        finalList.add(pre);
        finalList.add(result.toString());

        return finalList;
    }

    private List<String> dealWithTemp(List<JmmNode> children){

        StringBuilder pre = new StringBuilder();

        String left = dealWithChild(children.get(0)), right = dealWithChild(children.get(1));

        if(left.equals("")){
            left = "t1.i32";
            List<String> res = dealWithArithmetic(children.get(0));

            pre.append(res.get(0) + "\n");
            pre.append("t1.i32 :=.i32 ");
            pre.append(res.get(1) + "\n");
        }

        if(right.equals("")){
            right = "u1.i32";
            List<String> res = dealWithArithmetic(children.get(1));

            pre.append(res.get(0) + "\n");
            pre.append("u1.i32 :=.i32 ");
            pre.append(res.get(1) + "\n");
        }

        List<String> res = new ArrayList<>();
        res.add(left);
        res.add(right);
        if(pre.isEmpty())
            res.add("");
        else
            res.add(pre.toString());

        return res;
    }







    private String dealWithIdentifier(JmmNode child) {
        String identifierName = child.get("name");

        return identifierName + "." + getTypeOllir(getIdentifierType(child));
    }

    private String dealWithInteger(JmmNode child) {

        return child.get("value") + ".i32";
    }


    private String getTypeOllir(Type type) {
        StringBuilder result = new StringBuilder();
        if(type.isArray()) result.append("array.");

        String typeOllir;

        if(type.getName().equals("int")) typeOllir = "i32";
        else if(type.getName().equals("boolean")) typeOllir = "bool";
        else typeOllir = type.getName();

        result.append(typeOllir);

        return result.toString();
    }


    private Type getIdentifierType(JmmNode node) {

        if(!node.getKind().equals("Identifier"))
            return null;

        // Searching the symbols of the local variables to see if any has the name we're looking for
        HashMap<Symbol, Boolean> localVariables = symbolTableImp.getMethodByName(this.methodName).getLocalVariables();
        for (Symbol i : localVariables.keySet()) {
            if (i.getName().equals(node.get("name")))
                return i.getType();
        }

        // Searching the symbols in the function parameters
        List<Symbol> parameters = symbolTableImp.getMethodByName(this.methodName).getParameters();
        for (Symbol i : parameters) {
            if (i.getName().equals(node.get("name")))
                return i.getType();
        }

        // Searching the symbols of the class variables to see if any has the name we're looking for
        List<Symbol> classVariables = symbolTableImp.getFields();
        for (Symbol i : classVariables) {
            if (i.getName().equals(node.get("name")))
                return i.getType();
        }

        return null;
    }


}
