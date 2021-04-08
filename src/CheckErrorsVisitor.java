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





    }

    private Boolean dealWithLessThan(JmmNode node, List<Report> reports) {
        List<JmmNode> children = node.getChildren();
        Type leftChild  = getNodeType(children.get(0));
        Type rightChild  = getNodeType(children.get(1));

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

        // Check if inside Not there is a boolean
        if(!onlyChild.getName().equals("boolean")){
            Report report = new Report(ReportType.ERROR, Stage.SEMANTIC, Integer.parseInt(node.get("line")), Integer.parseInt(node.get("col")), "Miss match with operator Not: Expected boolean");
            reports.add(report);
        }

        return true;
    }

    private Boolean dealWithLogicOp(JmmNode node, List<Report> reports) {

        List<JmmNode> children = node.getChildren();
        Type leftChild  = getNodeType(children.get(0));
        Type rightChild  = getNodeType(children.get(1));

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

        //check if both variables are of same type
        if (!leftChild.getName().equals(rightChild.getName())){
            Report report = new Report(ReportType.ERROR, Stage.SEMANTIC, Integer.parseInt(node.get("line")), Integer.parseInt(node.get("col")), "Equal statement with different types");
            reports.add(report);
        }

        //check if both variables are equal in terms of being arrays
        if (leftChild.isArray() != rightChild.isArray()){
            Report report = new Report(ReportType.ERROR, Stage.SEMANTIC, Integer.parseInt(node.get("line")), Integer.parseInt(node.get("col")), "Equal statement with arrays");
            reports.add(report);
        }
        return true;
    }


    private Type getNodeType(JmmNode node){
        String nodeKind=node.getKind();
        Type type;
        if (nodeKind.equals("Identifier")) {
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
        return new Type("fail", false);
    }


}
