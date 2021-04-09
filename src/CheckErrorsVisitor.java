import pt.up.fe.comp.jmm.JmmNode;
import pt.up.fe.comp.jmm.analysis.table.Symbol;
import pt.up.fe.comp.jmm.ast.PreorderJmmVisitor;
import pt.up.fe.comp.jmm.analysis.table.Type;
import pt.up.fe.comp.jmm.report.*;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;

public class CheckErrorsVisitor extends PreorderJmmVisitor<List<Report>, Boolean> {
    private SymbolTableImp symbolTableImp;
    private String methodName;

    public CheckErrorsVisitor(SymbolTableImp symbolTableImp) {
        super();

        this.symbolTableImp = symbolTableImp;

        addVisit("Method", this::dealWithMethod);
        addVisit("MainMethod", this::dealWithMain);

        addVisit("Sum", this::dealWithArithmetic);
        addVisit("Sub", this::dealWithArithmetic);
        addVisit("Mult", this::dealWithArithmetic);
        addVisit("Div", this::dealWithArithmetic);
        addVisit("LessThan", this::dealWithLessThan);

        addVisit("And", this::dealWithLogicOp);

        addVisit("EqualStatement", this::dealWithEqual);
        addVisit("Not", this::dealWithNot);

        // Verifies if inside access is integer and if parent is array
        addVisit("AccessToArray", this::dealWithAccessArray);

        // Verifies if inside while and if condition is boolean
        addVisit("IfCondition", this::dealWithIfWhile);
        addVisit("WhileCondition", this::dealWithIfWhile);

        addVisit("MethodCall", this::dealWithMethodCall);



    }

    private Boolean dealWithMethodCall(JmmNode node, List<Report> reports) {

        List<JmmNode> children = node.getChildren();

        MethodTable methodTable = symbolTableImp.getMethods(node.get("name"));

        // Verifies if number of arguments is equal
        if(methodTable.getParameters().size() != children.size()){
            Report report = new Report(ReportType.ERROR, Stage.SEMANTIC, Integer.parseInt(node.get("line")), Integer.parseInt(node.get("col")), "Method call with different number of arguments");
            reports.add(report);
        }

        for (JmmNode child:children){
            if (!methodTable.getParameters().contains(getNodeType(child))){
                Report report = new Report(ReportType.ERROR, Stage.SEMANTIC, Integer.parseInt(node.get("line")), Integer.parseInt(node.get("col")), "Wrong parameter type");
                reports.add(report);
            }
        }


        return true;
    }

    private Boolean dealWithIfWhile(JmmNode node, List<Report> reports) {

        Type onlyChild = getNodeType(node.getChildren().get(0));

        if(onlyChild == null){
            Report report = new Report(ReportType.ERROR, Stage.SEMANTIC, Integer.parseInt(node.get("line")), Integer.parseInt(node.get("col")), "Variable is not declared beforehand");
            reports.add(report);
        }
        else if(!onlyChild.getName().equals("boolean")){
            Report report = new Report(ReportType.ERROR, Stage.SEMANTIC, Integer.parseInt(node.get("line")), Integer.parseInt(node.get("col")), "If or while condition: Expected boolean");
            reports.add(report);
        }
        return true;
    }

    private Boolean dealWithAccessArray(JmmNode node, List<Report> reports) {
        JmmNode parent = node.getParent();
        List<JmmNode> children = node.getChildren();

        if(getIdentifierType(parent) == null){
            Report report = new Report(ReportType.ERROR, Stage.SEMANTIC, Integer.parseInt(node.get("line")), Integer.parseInt(node.get("col")), "Variable is not declared beforehand");
            reports.add(report);
        }
        else if (!getIdentifierType(parent).isArray()) {
            Report report = new Report(ReportType.ERROR, Stage.SEMANTIC, Integer.parseInt(node.get("line")), Integer.parseInt(node.get("col")), "Trying to index non-array variable");
            reports.add(report);
        }
        if(!getNodeType(children.get(0)).getName().equals("int") && !getNodeType(children.get(0)).isArray()){
            Report report = new Report(ReportType.ERROR, Stage.SEMANTIC, Integer.parseInt(node.get("line")), Integer.parseInt(node.get("col")), "Incorrect index access value");
            reports.add(report);
        }
        return true;
    }

    private Boolean dealWithLessThan(JmmNode node, List<Report> reports) {
        List<JmmNode> children = node.getChildren();
        Type leftChild  = getNodeType(children.get(0));
        Type rightChild  = getNodeType(children.get(1));

        if (verifyNotNull(node, reports, leftChild, rightChild)) return true;

        // Check if the arith. exp. is with two arrays
        if(leftChild.isArray() || rightChild.isArray()){
            Report report = new Report(ReportType.ERROR, Stage.SEMANTIC, Integer.parseInt(node.get("line")), Integer.parseInt(node.get("col")), "Less than operation with arrays");
            reports.add(report);
        }

        // Check if arithmetic expression operands are of different types
        if(!leftChild.getName().equals("int") || !rightChild.getName().equals("int")){ //its effective because this language only deals with int and boolean
            Report report = new Report(ReportType.ERROR, Stage.SEMANTIC, Integer.parseInt(node.get("line")), Integer.parseInt(node.get("col")), "Less than operation with different types");
            reports.add(report);
        }
        return true;
    }

    private Boolean dealWithMain(JmmNode jmmNode, List<Report> reports) {
        this.methodName = "main";
        return true;
    }

    private Boolean dealWithNot(JmmNode node, List<Report> reports) {

        Type onlyChild = getNodeType(node.getChildren().get(0));

        if(onlyChild == null){
            Report report = new Report(ReportType.ERROR, Stage.SEMANTIC, Integer.parseInt(node.get("line")), Integer.parseInt(node.get("col")), "Less than operation with arrays");
            reports.add(report);
        }
        // Check if inside Not there is a boolean
        else if(!onlyChild.getName().equals("boolean")){
            Report report = new Report(ReportType.ERROR, Stage.SEMANTIC, Integer.parseInt(node.get("line")), Integer.parseInt(node.get("col")), "Miss match with operator Not: Expected boolean");
            reports.add(report);
        }

        return true;
    }

    private Boolean dealWithLogicOp(JmmNode node, List<Report> reports) {

        List<JmmNode> children = node.getChildren();
        Type leftChild  = getNodeType(children.get(0));
        Type rightChild  = getNodeType(children.get(1));

        if (verifyNotNull(node, reports, leftChild, rightChild)) return true;

        // Check if logic expression operands are of different types
        if(!leftChild.getName().equals("boolean") || !rightChild.getName().equals("boolean")){
            Report report = new Report(ReportType.ERROR, Stage.SEMANTIC, Integer.parseInt(node.get("line")), Integer.parseInt(node.get("col")), "Logic expression of different types");
            reports.add(report);
        }
        return true;
    }


    public Boolean dealWithMethod(JmmNode node, List<Report> reports){
        this.methodName = node.get("name");
        return true;
    }

    public Boolean dealWithArithmetic(JmmNode node, List<Report> reports) {

        List<JmmNode> children = node.getChildren();
        Type leftChild  = getNodeType(children.get(0));
        Type rightChild  = getNodeType(children.get(1));

        if(verifyNotNull(node,reports, leftChild, rightChild)) return true;

        // Check if the arith. exp. is with two arrays
        if(leftChild.isArray() || rightChild.isArray()){
            Report report = new Report(ReportType.ERROR, Stage.SEMANTIC, Integer.parseInt(node.get("line")), Integer.parseInt(node.get("col")), "Arithmetic operation with arrays");
            reports.add(report);
        }

        // Check if arithmetic expression operands are of different types
        if(!leftChild.getName().equals("int") || !rightChild.getName().equals("int")){ //its effective because this language only deals with int and boolean
            Report report = new Report(ReportType.ERROR, Stage.SEMANTIC, Integer.parseInt(node.get("line")), Integer.parseInt(node.get("col")), "Arithmetic expression of different types");
            reports.add(report);
        }
        return true;
    }

    public Boolean dealWithEqual(JmmNode node, List<Report> reports){
        List<JmmNode> children = node.getChildren();
        Type leftChild  = getNodeType(children.get(0));
        Type rightChild  = getNodeType(children.get(1));

        if(verifyNotNull(node, reports, leftChild, rightChild)) return true;

        //check if both variables are of same type
        if (!leftChild.getName().equals(rightChild.getName())){
            Report report = new Report(ReportType.ERROR, Stage.SEMANTIC, Integer.parseInt(node.get("line")), Integer.parseInt(node.get("col")), "Equal statement with different types");
            reports.add(report);
        }

        //check if both variables are equal in terms of being arrays

        if (leftChild.isArray() != rightChild.isArray()){
            Report report = new Report(ReportType.ERROR, Stage.SEMANTIC, Integer.parseInt(node.get("line")), Integer.parseInt(node.get("col")), "Equal statement where on side is an array and the other is not");
            reports.add(report);

        }
        return true;
    }

    private Type getIdentifierType(JmmNode node) {

        // Searching the symbols of the local variables to see if any has the name we're looking for
        HashMap<Symbol, String> localVariables = symbolTableImp.getMethods(methodName).getLocalVariables();
        for (Symbol i : localVariables.keySet()){
            if (i.getName().equals(node.get("name")))
                return i.getType();
        }

        // Searching the symbols in the function parameters
        HashSet<Symbol> parameters = symbolTableImp.getMethods(methodName).getParameters();
        for (Symbol i : parameters){
            if (i.getName().equals(node.get("name")))
                return i.getType();
        }

        // Searching the symbols of the class variables to see if any has the name we're looking for
        List <Symbol> classVariables = symbolTableImp.getFields();
        for (Symbol i:classVariables){
            if (i.getName().equals(node.get("name")))
                return i.getType();
        }
        return null;
    }


    private Type getNodeType(JmmNode node){
        String nodeKind=node.getKind();
        Type type = null;
        if (nodeKind.equals("Identifier")) {
            System.out.println("Node: " + node);

            type = getIdentifierType(node);

            System.out.println("Type: " + type);

            // If didn't find
            if(type == null){
                return null;
            }
            if (!type.isArray() || node.getChildren().size() == 0){
                return type;
            }else {
                return new Type(type.getName(), false);
            }

        }
        else if (nodeKind.equals("Integer")) {
            return new Type("int", false);
        }
        else if (nodeKind.equals("Boolean")){
            return new Type("boolean", false);
        }
        else if (nodeKind.equals("Sum") || nodeKind.equals("Sub") || nodeKind.equals("Mult") || nodeKind.equals("Div")){//if it is another expression
            /*List<JmmNode> children = node.getChildren();
            Type leftChild  = getNodeType(children.get(0));
            Type rightChild  = getNodeType(children.get(1));

            if(leftChild.getName().equals(rightChild.getName())){
                return leftChild;
            }else{
                return new Type("fail", false);
            }*/
            return new Type("int", false);

        }else if(nodeKind.equals("And") || nodeKind.equals("Not") || nodeKind.equals("LessThan")){
            return new Type("boolean", false);
        }
        return null;
    }

    private boolean verifyNotNull(JmmNode node, List<Report> reports, Type leftChild, Type rightChild) {

        if (leftChild == null || rightChild == null) {
            Report report = new Report(ReportType.ERROR, Stage.SEMANTIC, Integer.parseInt(node.get("line")), Integer.parseInt(node.get("col")), "Variable is not declared beforehand");
            reports.add(report);
            return true;
        }
        return false;
    }

}
