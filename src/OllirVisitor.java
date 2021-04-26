import pt.up.fe.comp.jmm.JmmNode;
import pt.up.fe.comp.jmm.analysis.table.Symbol;
import pt.up.fe.comp.jmm.analysis.table.Type;
import pt.up.fe.comp.jmm.ast.PreorderJmmVisitor;
import pt.up.fe.comp.jmm.report.Report;

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

    private String dealWithChild(JmmNode child){
        switch (child.getKind()){
            case "Method"->{return dealWithMethod(child);}
            case "MainMethod"->{return dealWithMainMethod(child);}
            case "Var"->{return dealWithVar(child);}
            case "TwoPartExpression"->{return dealWithTwoPart(child);}
            case "EqualStatement"-> {return dealWithEqualStatement(child);}
            case "Sum"->{return dealWithArithmetic(child);}
            case "Sub"->{return dealWithArithmetic(child);}
            case "Mul"->{return dealWithArithmetic(child);}
            case "Div"->{return dealWithArithmetic(child);}
            //case "AllocationExpression"->{return dealWithAllocationExpression(child);}
        }
        return "";
    }


    private boolean dealWithClass(JmmNode node, List<Report> reports) {


        //class constructor
        ollirCode.append(node.get("name")+" {");
        ollirCode.append("\t.construct "+node.get("name")+"().V {");
        ollirCode.append("\t\t"+"invokespecial(this, \"<init>\").V;");
        ollirCode.append("\t"+"}");

        for (JmmNode child:node.getChildren()){
            ollirCode.append(dealWithChild(child));
        }
        ollirCode.append("}");

        return true;
    }

    public String getOllirCode(){return this.ollirCode.toString();}

    private String getReturnType(JmmNode node){
        MethodTable methodTable = this.symbolTableImp.methods.get(node.get("name"));
        return getTypeOllir(methodTable.returnType);
    }

    private String getArgs(JmmNode node){
        StringBuilder result = new StringBuilder("\n");
        System.out.println(this.symbolTableImp);
        MethodTable methodTable = this.symbolTableImp.getMethod(node.get("name"));

        for (Symbol parameter : methodTable.parameters){
            result.append(parameter.getName()+getTypeOllir(parameter.getType())+",");
        }
        result.deleteCharAt(result.length()-1);

        return result.toString();
    }

    private String dealWithMainMethod(JmmNode node) {
        StringBuilder result = new StringBuilder("\n");

        result.append(".method public static main(" + node.get("argName") + ".array.String).V {");

        for (JmmNode child:node.getChildren()){
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
        StringBuilder result = new StringBuilder();
        List<JmmNode> children = equalNode.getChildren();
        result.append(dealWithChild(children.get(0)) +" := "+ dealWithChild(children.get(1)));

        return result.toString();

    }

    private String dealWithArithmetic(JmmNode arithmeticNode){
        StringBuilder result = new StringBuilder();
        List<JmmNode> children = arithmeticNode.getChildren();
        result.append(dealWithChild(children.get(0)));
        switch (arithmeticNode.getKind()){
            case "Sum"->{result.append(" + ");}
            case "Sub"->{result.append(" - ");}
            case "Mul"->{result.append(" * ");}
            case "Div"->{result.append(" / ");}
        }
        result.append(dealWithChild(children.get(1)));

        return result.toString();
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

}
