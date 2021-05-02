import pt.up.fe.comp.jmm.JmmNode;
import pt.up.fe.comp.jmm.analysis.table.Symbol;
import pt.up.fe.comp.jmm.ast.PreorderJmmVisitor;
import pt.up.fe.comp.jmm.analysis.table.Type;
import pt.up.fe.comp.jmm.report.Report;
import pt.up.fe.comp.jmm.report.ReportType;
import pt.up.fe.comp.jmm.report.Stage;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class FillSTVisitor extends PreorderJmmVisitor<List<Report>, Boolean> {
    private SymbolTableImp symbolTableImp;
    private String methodSignature;

    public FillSTVisitor(SymbolTableImp symbolTableImp) {
        super();

        this.symbolTableImp = symbolTableImp;

        addVisit("Library", this::dealWithImports);
        addVisit("Class", this::dealWithClass);
        addVisit("MainMethod", this::dealWithMain);
        addVisit("Method", this::dealWithMethod);
        addVisit("MethodBody", this::dealWithMethodBody);
        addVisit("EqualStatement", this::dealWithAssignment);
    }

    public Boolean dealWithImports(JmmNode node, List<Report> reports) {
        this.symbolTableImp.addImport(node.get("name"));
        return true;
    }

    public Boolean dealWithClass(JmmNode node, List<Report> reports) {
        this.symbolTableImp.setClassName(node.get("name"));

        if(node.getAttributes().contains("extends"))
            this.symbolTableImp.setSuperClass(node.get("extends"));

        fillClassVar(node, reports);
        return true;
    }

    private void fillClassVar(JmmNode node, List<Report> reports) {
        List<JmmNode> children = node.getChildren();
        int i = 0;
        JmmNode current = children.get(i);

        while (current.getKind().equals("Var")) {
            Boolean unique = this.symbolTableImp.addField(getSymbol(current));
            if (!unique) {
                reports.add(new Report(ReportType.ERROR, Stage.SEMANTIC, Integer.parseInt(node.get("line")), Integer.parseInt(node.get("col")),  "Variable " + getSymbol(current).getName() + " has already been declared."));
            }
            i++;
            if (i == children.size())
                break;

            current = children.get(i);
        }
    }

    private Boolean dealWithMain(JmmNode node, List<Report> reports) {

        List<Symbol> parameters = new ArrayList<>();

        parameters.add(new Symbol(new Type("string", true), node.get("argName"))); // String [] args

        MethodTable methodTable = new MethodTable("main", new Type("void", false), parameters); // public static void main

        methodSignature = "main";

        this.symbolTableImp.addMethod("main", methodTable);

        return true;
    }

    public Boolean dealWithMethod(JmmNode node, List<Report> reports) {
        List<JmmNode> children = node.getChildren();

        List<Symbol> parameters = getParameters(children.get(1));

        MethodTable methodTable = new MethodTable(node.get("name"), getType(children.get(0)), parameters);

        for(Symbol s: parameters)
            methodTable.addLocalVariable(s, true);

        methodSignature = methodTable.getSignature();

        this.symbolTableImp.addMethod(methodTable.getSignature(), methodTable);

        return true;
    }

    public Boolean dealWithMethodBody(JmmNode node, List<Report> reports){
        List<JmmNode> children = node.getChildren();

        if(!children.isEmpty())
            fillMethodVar(node, symbolTableImp.getMethod(this.methodSignature), reports);

        return true;
    }

    public Boolean dealWithAssignment(JmmNode node, List<Report> reports) {
        String identifier = node.getChildren().get(0).get("name");

        // Searching the symbols of the local variables to see if any has the name we're looking for
        HashMap<Symbol, Boolean> localVariables = symbolTableImp.getMethod(methodSignature).getLocalVariables();
        for (Symbol i : localVariables.keySet()) {
            if (i.getName().equals(identifier)) {
                symbolTableImp.assignMethodVariable(methodSignature, i.getName());
                return true;
            }
        }

        // Searching the symbols of the class variables to see if any has the name we're looking for
        List<Symbol> classVariables = symbolTableImp.getFields();
        for (Symbol i : classVariables) {
            if (i.getName().equals(identifier)) {
                symbolTableImp.assignField(i.getName());
                return true;
            }
        }
        return false;
    }

    private List<Symbol> getParameters(JmmNode node) {
        List<JmmNode> children = node.getChildren();
        List<Symbol> symbolSet = new ArrayList<>();

        for (JmmNode argument : children) {
            if (argument.getKind().equals("Argument")) {
                symbolSet.add(getSymbol(argument));
            }
        }

        return symbolSet;
    }

    private void fillMethodVar(JmmNode node, MethodTable methodTable, List<Report> reports) {
        List<JmmNode> children = node.getChildren();
        int i = 0;
        JmmNode current = children.get(i);

        while (current.getKind().equals("Var")) {
            Boolean unique = methodTable.addLocalVariable(getSymbol(current));
            if (!unique) {
                reports.add(new Report(ReportType.ERROR, Stage.SEMANTIC, Integer.parseInt(node.get("line")), Integer.parseInt(node.get("col")),  "Variable " + getSymbol(current).getName() + " has already been declared."));
            }
            i++;
            if (i == children.size())
                break;
            current = children.get(i);
        }
    }

    private Symbol getSymbol(JmmNode var) {
        String name = var.get("name");
        return new Symbol(getType(var.getChildren().get(0)), name);
    }

    private Type getType(JmmNode typeNode) {
        String type = typeNode.get("name");
        boolean isArray = typeNode.getChildren().size() > 0;
        return new Type(type, isArray);
    }
}
