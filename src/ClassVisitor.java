import pt.up.fe.comp.jmm.JmmNode;
import pt.up.fe.comp.jmm.ast.PreorderJmmVisitor;
import pt.up.fe.comp.jmm.analysis.table.Type;

import java.util.List;

public class ClassVisitor extends PreorderJmmVisitor<Boolean, Boolean> {
    private SymbolTableImp symbolTableImp;

    public ClassVisitor(SymbolTableImp symbolTableImp) {
        super();

        this.symbolTableImp = symbolTableImp;

        addVisit("Library", this::dealWithImports);
        addVisit("Class", this::dealWithClass);
        addVisit("Var", this::dealWithClassFields);

    }


    public Boolean dealWithImports(JmmNode node, Boolean dummy) {
        this.symbolTableImp.addImport(node.get("name"));
        return true;
    }

    public Boolean dealWithClass(JmmNode node, Boolean dummy) {
        this.symbolTableImp.setClassName(node.get("name"));
        this.symbolTableImp.setSuperClass(node.get("extends"));
        return true;
    }

    public Boolean dealWithClassFields(JmmNode node, Boolean dummy) {
        JmmNode child = node.getChildren().get(0);
        List<JmmNode> grandChild = child.getChildren();
        Type type = new Type(child.get("name"), !grandChild.isEmpty());
        this.symbolTableImp.addField(type, node.get("name"));
        return true;
    }

}
