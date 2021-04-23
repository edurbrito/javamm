import pt.up.fe.comp.jmm.JmmNode;
import pt.up.fe.comp.jmm.analysis.table.Symbol;
import pt.up.fe.comp.jmm.analysis.table.Type;
import pt.up.fe.comp.jmm.ast.PreorderJmmVisitor;
import pt.up.fe.comp.jmm.report.Report;

import java.util.List;

public class OllirVisitor extends PreorderJmmVisitor<List<Report>, Boolean> {
    private SymbolTableImp symbolTableImp;
    private StringBuilder ollirCode;
    private String methodName;

    public OllirVisitor(SymbolTableImp symbolTableImp) {
        super();
        this.ollirCode = new StringBuilder("\n");

        this.symbolTableImp = symbolTableImp;

        addVisit("EqualStatement", this::dealWithEqual);
        addVisit("Class", this::dealWithClass);
        addVisit("Method", this::dealWithMethod);

    }

    private Boolean dealWithEqual(JmmNode node, List<Report> reports) {
        List<JmmNode> children = node.getChildren();
        JmmNode leftChild = children.get(0);
        JmmNode rightChild = children.get(1);

        String result = leftChild.get("name") + "." + this.symbol
    }

    private Boolean dealWithClass(JmmNode node, List<Report> reports) {

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

    private String dealWithChild(JmmNode child){
        switch (child.getKind()){
            case "Method"->{return dealWithMethod(child);}
            case "MainMethod"->{return dealWithMainMethod(child);}
            case "Var"->{return dealWithVar(child);}
            case "TwoPartExpression"->{return dealWithTwoPartExpression(child);}
        }
        return "";
    }

    private String getReturnType(JmmNode node){
        MethodTable methodTable = this.symbolTableImp.methods.get(node.get("name"));
        return getTypeOllir(methodTable.returnType);
    }

    private String getArgs(JmmNode node){
        StringBuilder result = new StringBuilder("\n");
        MethodTable methodTable=this.symbolTableImp.methods.get(node.get("name"));
        for (Symbol parameter:methodTable.parameters){
            result.append(parameter.name+getTypeOllir(parameter.type())+",");
        }
        result.deleteCharAt(result.lenght()-1);

        return result.toString();
    }

    private String dealWithMainMethod(JmmNode node) {
        StringBuilder result = new StringBuilder("\n");

        result.append(".method public" + getReturnType(node) + "static main(" + getArgs(node) + ").V {");

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

        // TODO
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
