import pt.up.fe.comp.jmm.JmmNode;
import pt.up.fe.comp.jmm.analysis.table.Symbol;
import pt.up.fe.comp.jmm.ast.PreorderJmmVisitor;
import pt.up.fe.comp.jmm.analysis.table.Type;

import java.util.HashSet;
import java.util.List;

public class FillSTVisitor extends PreorderJmmVisitor<Boolean, Boolean> {
    private SymbolTableImp symbolTableImp;
    public FillSTVisitor(SymbolTableImp symbolTableImp) {
        super();

        this.symbolTableImp = symbolTableImp;

        addVisit("Library", this::dealWithImports);
        addVisit("Class", this::dealWithClass);
        addVisit("Method", this::dealWithMethod);
        addVisit("MainMethod", this::dealWithMain);

    }

    public Boolean dealWithImports(JmmNode node, Boolean dummy) {

        this.symbolTableImp.addImport(node.get("name"));
        return true;
    }

    public Boolean dealWithClass(JmmNode node, Boolean dummy) {
        this.symbolTableImp.setClassName(node.get("name"));
        this.symbolTableImp.setSuperClass(node.get("extends"));
        fillClassVar(node);
        return true;
    }

    private void fillClassVar(JmmNode node) {
        List<JmmNode> children = node.getChildren();
        int i = 0;
        JmmNode current = children.get(i);

        while(current.getKind().equals("Var")){
            this.symbolTableImp.addField(getSymbol(current));
            i++;
            if(i == children.size())
                break;

            current = children.get(i);
        }


    }

    private Boolean dealWithMain(JmmNode node, Boolean aBoolean) {

        List<JmmNode> children = node.getChildren();

        HashSet<Symbol> parameters = new HashSet<>();

        parameters.add(new Symbol(new Type("string", true), node.get("argName")));

        MethodTable methodTable = new MethodTable(new Type("void", false), parameters);

        fillMethodVar(children.get(0), methodTable);

        this.symbolTableImp.addMethod("main", methodTable);

        return true;
    }

    public Boolean dealWithMethod(JmmNode node, Boolean dummy){      // Node has kind Method

        List<JmmNode> children = node.getChildren();


        HashSet<Symbol> parameters = getParameters(children.get(1));

        MethodTable methodTable = new MethodTable(getType(children.get(0)), parameters);

        fillMethodVar(children.get(2), methodTable);

        this.symbolTableImp.addMethod(node.get("name"), methodTable);

        return true;
    }

    private HashSet<Symbol> getParameters(JmmNode node){
        List<JmmNode> children = node.getChildren();
        HashSet<Symbol>symbolSet = new HashSet<>();

        for (JmmNode argument:children){
            if (argument.getKind().equals("Argument")){
                symbolSet.add(getSymbol(argument));
            }
        }

        return symbolSet;
    }

    private void fillMethodVar(JmmNode node, MethodTable methodTable) {     // Node has kind MethodBody
        List<JmmNode> children = node.getChildren();
        int i = 0;
        JmmNode current = children.get(i);

        while(current.getKind().equals("Var")){
            methodTable.addLocalVariable(getSymbol(current));
            i++;
            if(i == children.size())
                break;
            current = children.get(i);
        }
    }

    private Symbol getSymbol(JmmNode var){
        String name = var.get("name");
        return new Symbol(getType(var.getChildren().get(0)), name);
    }

    private Type getType(JmmNode typeNode){
        String type = typeNode.get("name");
        boolean isArray = typeNode.getChildren().size() > 0;
        return new Type(type, isArray);
    }

}
