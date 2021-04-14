import pt.up.fe.comp.jmm.JmmNode;
import pt.up.fe.comp.jmm.analysis.table.Symbol;
import pt.up.fe.comp.jmm.ast.PreorderJmmVisitor;
import pt.up.fe.comp.jmm.analysis.table.Type;
import pt.up.fe.comp.jmm.report.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class CheckErrorsVisitor extends PreorderJmmVisitor<List<Report>, Boolean> {
    private SymbolTableImp symbolTableImp;
    private String methodSignature;

    public CheckErrorsVisitor(SymbolTableImp symbolTableImp) {
        super();

        this.symbolTableImp = symbolTableImp;

        addVisit("MainMethod", this::dealWithMain);
        addVisit("Method", this::dealWithMethod);
        addVisit("Var", this::dealWithVar);

        addVisit("Sum", this::dealWithExpression);
        addVisit("Sub", this::dealWithExpression);
        addVisit("Mult", this::dealWithExpression);
        addVisit("Div", this::dealWithExpression);
        addVisit("LessThan", this::dealWithExpression);

        addVisit("And", this::dealWithLogicOp);

        addVisit("EqualStatement", this::dealWithEqual);
        addVisit("Not", this::dealWithNot);

        addVisit("AccessToArray", this::dealWithAccessArray);

        addVisit("IfCondition", this::dealWithIfWhile);
        addVisit("WhileCondition", this::dealWithIfWhile);

        addVisit("MethodCall", this::dealWithMethodCall);
    }

    private Boolean dealWithMain(JmmNode jmmNode, List<Report> reports) {
        this.methodSignature = "main";
        return true;
    }

    private Boolean dealWithMethod(JmmNode node, List<Report> reports) {
        List<JmmNode> arguments = node.getChildren().get(1).getChildren();
        List<Symbol> symbols = new ArrayList<>();

        for (JmmNode argument : arguments) {
            if (argument.getKind().equals("Argument")) {
                symbols.add(new Symbol(new Type(argument.getChildren().get(0).get("name"), argument.getNumChildren() > 0), null));
            }
        }

        this.methodSignature = (new MethodTable(node.get("name"), null, symbols)).getSignature();
        return true;
    }

    private Boolean dealWithVar(JmmNode node, List<Report> reports){
        JmmNode typeChild = node.getChildren().get(0);
        String name = typeChild.get("name");

        if(!name.equals("int") && !name.equals("boolean")){
            if(!name.equals(symbolTableImp.getClassName()) && symbolTableImp.imports.isEmpty()){
                reports.add(new Report(ReportType.ERROR, Stage.SEMANTIC, Integer.parseInt(node.get("line")), Integer.parseInt(node.get("col")),  "Variable type " + name + " does not exist."));
            }
        }

        return true;
    }

    private Boolean dealWithExpression(JmmNode node, List<Report> reports) {
        List<JmmNode> children = node.getChildren();
        Type leftChild = getNodeType(children.get(0));
        Type rightChild = getNodeType(children.get(1));

        if (verifyNotNull(node, reports, leftChild, rightChild)) return true;

        // Check if any of the expression operands is an array
        if (leftChild.isArray() || rightChild.isArray()) {
            Report report = new Report(ReportType.ERROR, Stage.SEMANTIC, Integer.parseInt(node.get("line")), Integer.parseInt(node.get("col")),  node.getKind() + " operation with arrays");
            reports.add(report);
        }

        // Check if any of the expression operands is not an integer
        if (!leftChild.getName().equals("int") || !rightChild.getName().equals("int")) { // its effective because this language only deals with int and boolean
            Report report = new Report(ReportType.ERROR, Stage.SEMANTIC, Integer.parseInt(node.get("line")), Integer.parseInt(node.get("col")), node.getKind() + " only supports integer operands");
            reports.add(report);
        }

        // Check if variables are both initialized
        if ((children.get(0).getKind().equals("Identifier") && !symbolTableImp.isAssigned(methodSignature, children.get(0).get("name"))) || (children.get(1).getKind().equals("Identifier") && !symbolTableImp.isAssigned(methodSignature, children.get(1).get("name")))) {
            Report report = new Report(ReportType.ERROR, Stage.SEMANTIC, Integer.parseInt(node.get("line")), Integer.parseInt(node.get("col")), "Assignment with variables not yet initialized ");
            reports.add(report);
        }

        return true;
    }

    private Boolean dealWithLogicOp(JmmNode node, List<Report> reports) {
        List<JmmNode> children = node.getChildren();
        Type leftChild = getNodeType(children.get(0));
        Type rightChild = getNodeType(children.get(1));

        if (verifyNotNull(node, reports, leftChild, rightChild)) return true;

        // Check if any of the logic expression operands is not a boolean
        if (!leftChild.getName().equals("boolean") || !rightChild.getName().equals("boolean")) {
            Report report = new Report(ReportType.ERROR, Stage.SEMANTIC, Integer.parseInt(node.get("line")), Integer.parseInt(node.get("col")), "Logic expression only supports integer operands");
            reports.add(report);
        }
        return true;
    }

    private Boolean dealWithEqual(JmmNode node, List<Report> reports) {
        List<JmmNode> children = node.getChildren();
        Type leftChild = getNodeType(children.get(0));
        Type rightChild = getNodeType(children.get(1));

        if (verifyNotNull(node, reports, leftChild, rightChild)){
            return true;
        }

        // If it is a method call that we dont know the return type, returns any
        if(leftChild.getName().equals("any") || rightChild.getName().equals("any")) {
            return true;
        }

        // Check if both variables are of same type
        if (!leftChild.equals(rightChild)) {
            Report report = new Report(ReportType.ERROR, Stage.SEMANTIC, Integer.parseInt(node.get("line")), Integer.parseInt(node.get("col")), "Equal statement with different types");
            reports.add(report);
        }

        return true;
    }

    private Boolean dealWithNot(JmmNode node, List<Report> reports) {
        Type onlyChild = getNodeType(node.getChildren().get(0));

        if (onlyChild == null) {
            Report report = new Report(ReportType.ERROR, Stage.SEMANTIC, Integer.parseInt(node.get("line")), Integer.parseInt(node.get("col")), "NOT operation operand is null");
            reports.add(report);
        }

        // Check if inside NOT there is a boolean
        else if (!onlyChild.getName().equals("boolean")) {
            Report report = new Report(ReportType.ERROR, Stage.SEMANTIC, Integer.parseInt(node.get("line")), Integer.parseInt(node.get("col")), "Miss match with operator Not: Expected boolean");
            reports.add(report);
        }
        return true;
    }

    private Boolean dealWithAccessArray(JmmNode node, List<Report> reports) {
        JmmNode nodeIdentifier = node.getParent().getChildren().get(0);
        List<JmmNode> children = node.getChildren();

        if (!node.getParent().getKind().equals("AllocationExpression") && !node.getParent().getKind().equals("Identifier")) {
            if (getIdentifierType(nodeIdentifier) == null) {
                Report report = new Report(ReportType.ERROR, Stage.SEMANTIC, Integer.parseInt(node.get("line")), Integer.parseInt(node.get("col")), "Variable is not declared beforehand");
                reports.add(report);
            } else if (!getIdentifierType(nodeIdentifier).isArray()) {
                Report report = new Report(ReportType.ERROR, Stage.SEMANTIC, Integer.parseInt(node.get("line")), Integer.parseInt(node.get("col")), "Trying to index non-array variable");
                reports.add(report);
            }
        }

        if (!getNodeType(children.get(0)).getName().equals("int") && !getNodeType(children.get(0)).isArray()) {
            Report report = new Report(ReportType.ERROR, Stage.SEMANTIC, Integer.parseInt(node.get("line")), Integer.parseInt(node.get("col")), "Incorrect index access value");
            reports.add(report);
        }
        return true;
    }

    private Boolean dealWithIfWhile(JmmNode node, List<Report> reports) {
        Type onlyChild = getNodeType(node.getChildren().get(0));

        // If it is a method call that we dont know, returns any
        if (onlyChild.getName().equals("any")) {
            return true;
        }

        if (onlyChild == null) {
            Report report = new Report(ReportType.ERROR, Stage.SEMANTIC, Integer.parseInt(node.get("line")), Integer.parseInt(node.get("col")), "Condition is empty");
            reports.add(report);
        } else if (!onlyChild.getName().equals("boolean")) {
            Report report = new Report(ReportType.ERROR, Stage.SEMANTIC, Integer.parseInt(node.get("line")), Integer.parseInt(node.get("col")), node.getKind() + ": Expected boolean");
            reports.add(report);
        }
        return true;
    }

    private Boolean dealWithMethodCall(JmmNode nodeMethodCall, List<Report> reports) {
        JmmNode partExpression = nodeMethodCall.getParent().getParent(); // two parts expression node
        JmmNode objectCaller = partExpression.getChildren().get(0);

        if (objectCaller.getKind().equals("This") || objectCaller.getKind().equals("AllocationExpression")) {
            if (checkLocalFunction(nodeMethodCall, reports))
                return true;

            if(this.symbolTableImp.hasSuperClass()){
                return true;
            }

            // Function does not exist and class does not have super class
            Report report = new Report(ReportType.ERROR, Stage.SEMANTIC, Integer.parseInt(nodeMethodCall.get("line")), Integer.parseInt(nodeMethodCall.get("col")), "Method does not exist");
            reports.add(report);

            return true;
        }
        else if ((objectCaller.getKind().equals("Identifier") && this.symbolTableImp.hasImport(objectCaller.get("name"))) || (objectCaller.getNumChildren() > 0 && this.symbolTableImp.hasImport(objectCaller.getChildren().get(0).get("name")))) {
            return true;
        }
        else {
            Type calleeType = getIdentifierType(objectCaller);
            if (calleeType == null || calleeType.getName().equals("int") || calleeType.getName().equals("boolean")) {
                Report report = new Report(ReportType.ERROR, Stage.SEMANTIC, Integer.parseInt(nodeMethodCall.get("line")), Integer.parseInt(nodeMethodCall.get("col")), "Method call not recognized");
                reports.add(report);
            }
        }
        return true;
    }

    private Boolean checkLocalFunction(JmmNode nodeMethodCall, List<Report> reports) {
        String methodSignature = getMethodSignature(nodeMethodCall);

        // Gets method table associated to function
        MethodTable methodTable = symbolTableImp.getMethod(methodSignature);
        List<MethodTable> overloadMethods = new ArrayList<>();

        // Verifies if function exists
        if (methodTable == null) {
            for (String sig : symbolTableImp.getMethods()) {
                MethodTable m = symbolTableImp.getMethod(sig);
                if (m.getName().equals(nodeMethodCall.get("name"))) {
                    overloadMethods.add(m);
                    break;
                }
            }

            if(overloadMethods.isEmpty())
                return false;

            if(this.symbolTableImp.hasSuperClass()){
                return true;
            }

        }

        // Get parameters
        List<JmmNode>children = nodeMethodCall.getChildren();

        // Verifies if number of arguments is equal
        for (MethodTable overload : overloadMethods) {
            if (overload.getParameters().size() != children.size()) {
                Report report = new Report(ReportType.ERROR, Stage.SEMANTIC, Integer.parseInt(nodeMethodCall.get("line")), Integer.parseInt(nodeMethodCall.get("col")), "No method with " + children.size() + " arguments. Expecting " + overload.getParameters().size() + " arguments.");
                reports.add(report);
                continue;
            }

            // Verify if parameters are of the correct type
            for (int i = 0; i < overload.getParameters().size(); i++) {
                if (!overload.getParameters().get(i).getType().equals(getNodeType(children.get(i)))) {
                    Report report = new Report(ReportType.ERROR, Stage.SEMANTIC, Integer.parseInt(nodeMethodCall.get("line")), Integer.parseInt(nodeMethodCall.get("col")), "Wrong object type in parameter " + i + ". Expecting " + overload.getParameters().get(i).getType() + " but got " + getNodeType(children.get(i)));
                    reports.add(report);
                    continue;
                }
            }
        }

        return true;
    }

    private Type getNodeType(JmmNode node) {
        String nodeKind = node.getKind();
        Type type;

        if (nodeKind.equals("Identifier")) {
            type = getIdentifierType(node);

            // If didn't find
            if (type == null) {
                return null;
            }
            if (!type.isArray() || node.getChildren().size() == 0) {
                return type;
            } else {
                return new Type(type.getName(), false);
            }

        } else if (nodeKind.equals("Integer")) {
            return new Type("int", false);
        } else if (nodeKind.equals("Boolean")) {
            return new Type("boolean", false);
        } else if (nodeKind.equals("Sum") || nodeKind.equals("Sub") || nodeKind.equals("Mult") || nodeKind.equals("Div")) { // if it is another expression
            return new Type("int", false);
        } else if (nodeKind.equals("And") || nodeKind.equals("Not") || nodeKind.equals("LessThan")) {
            return new Type("boolean", false);
        } else if(nodeKind.equals("TwoPartExpression")) {
            if (node.getChildren().get(1).getKind().equals("AccessToArray")) { // Array access
                return getArrayType(node);
            }
            else if (node.getChildren().get(1).getKind().equals("DotExpression")){
                JmmNode dotExpression = node.getChildren().get(1);
                if(node.getChildren().get(0).getKind().equals("Identifier") && dotExpression.getChildren().get(0).getKind().equals("Length")){
                    return new Type("int", false);
                }
                else { // Method Call
                    return getMethodReturnType(node);
                }
            }

        } else if (nodeKind.equals("AllocationExpression")) {
            String childKind = node.getChildren().get(0).getKind();
            if(childKind.equals("AccessToArray"))
                return new Type("int", true);
            else
                return new Type(node.getChildren().get(0).get("name"), false);
        }
        return null;
    }

    // If we are doing an array access get node type shall return the type of the array and that it is not an array because it is just an access to it
    private Type getArrayType(JmmNode nodeTwoPartsExp) {
        JmmNode identifier = nodeTwoPartsExp.getChildren().get(0);
        return new Type(getIdentifierType(identifier).getName(), false);
    }

    private Type getMethodReturnType(JmmNode nodeTwoPartExp) {
        JmmNode objectCaller = nodeTwoPartExp.getChildren().get(0);

        // If is this node or a class object
        if (objectCaller.getKind().equals("This") || (objectCaller.getKind().equals("Identifier") && getIdentifierType(objectCaller) != null && getIdentifierType(objectCaller).getName().equals(symbolTableImp.getClassName()))) {
            Type returnType = getLocalFunctionReturn(nodeTwoPartExp.getChildren().get(1).getChildren().get(0));

            if (returnType != null )
                return returnType;

            if (this.symbolTableImp.hasSuperClass())
                return new Type("any", false);

            return null;
        }

        if (!objectCaller.getKind().equals("Identifier") && !objectCaller.getKind().equals("AllocationExpression")) {
            return null;
        }

        if (objectCaller.getKind().equals("AllocationExpression") && objectCaller.getChildren().get(0).get("name").equals(symbolTableImp.getClassName())) {
            return getLocalFunctionReturn(nodeTwoPartExp.getChildren().get(1).getChildren().get(0));
        }

        if (this.symbolTableImp.hasImport(objectCaller.get("name")) || (objectCaller.getKind().equals("Identifier") && this.symbolTableImp.hasImport(getIdentifierType(objectCaller).getName()))) {
            return new Type("any", false);
        }

        return null;
    }

    private Type getLocalFunctionReturn(JmmNode nodeMethodCall) {
        String methodSignature = getMethodSignature(nodeMethodCall);

        // Gets method table associated to function
        MethodTable methodTable = symbolTableImp.getMethod(methodSignature);

        // Verify if method exists
        if(methodTable == null)
            return null;

        return methodTable.getReturnType();
    }

    private Type getIdentifierType(JmmNode node) {

        if(!node.getKind().equals("Identifier"))
            return null;

        // Searching the symbols of the local variables to see if any has the name we're looking for
        HashMap<Symbol, Boolean> localVariables = symbolTableImp.getMethod(methodSignature).getLocalVariables();
        for (Symbol i : localVariables.keySet()) {
            if (i.getName().equals(node.get("name")))
                return i.getType();
        }

        // Searching the symbols in the function parameters
        List<Symbol> parameters = symbolTableImp.getMethod(methodSignature).getParameters();
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

    private String getMethodSignature(JmmNode nodeMethodCall) {
        if (nodeMethodCall.getNumChildren() == 0) {
            return (new MethodTable(nodeMethodCall.get("name"), null, new ArrayList<>())).getSignature();
        }

        List<JmmNode> arguments = nodeMethodCall.getChildren();

        List<Symbol> symbols = new ArrayList<>();

        for (JmmNode argument : arguments) {
            symbols.add(new Symbol(getNodeType(argument), null));
        }

        return (new MethodTable(nodeMethodCall.get("name"), null, symbols)).getSignature();
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
