import pt.up.fe.comp.jmm.JmmNode;
import pt.up.fe.comp.jmm.ast.JmmNodeImpl;
import pt.up.fe.comp.jmm.ast.PreorderJmmVisitor;

import java.util.List;

public class ConstantFolding  extends PreorderJmmVisitor<Boolean, Boolean> {

    public ConstantFolding() {
        super();
    }

    private boolean isOperation(JmmNode node){
        switch (node.getKind()){
            case "Sub":
            case "Sum":
            case "Mult":
            case "Div":
            case "LessThan":
                return true;
            default:
                return false;
        }
    }

    public boolean canFold(JmmNode node) {
        if(this.isOperation(node)) {

            boolean canFold = true;

            for (int i = 0; i < node.getNumChildren(); i++) {
                canFold = canFold && this.canFold(node.getChildren().get(i));
            }

            return canFold;
        }
        else if(node.getKind().equals("Integer")) {
            return true;
        }

        return false;
    }

    public int calculateValue(JmmNode node) {

        if(node.getKind().equals("Integer")) {
            return Integer.parseInt(node.get("value"));
        }

        JmmNode leftChild = node.getChildren().get(0);
        JmmNode rigthChild = node.getChildren().get(1);

        int leftValue = this.calculateValue(leftChild);
        int rigthValue = this.calculateValue(rigthChild);
        int result = 0;

        switch(node.getKind()) {
            case "Sum":
                result = leftValue + rigthValue;
                break;
            case "Sub":
                result = leftValue - rigthValue;
                break;
            case "Mult":
                result = leftValue * rigthValue;
                break;
            case "Div":
                result = leftValue / rigthValue;
                break;
            case "LessThan":
                result = leftValue < rigthValue ? 1 : 0;
                break;
        }

        return result;
    }

    private int getIndex(List<JmmNode> children, JmmNode node){
        return children.indexOf(node);
    }

    public boolean fold(JmmNode node) {

        if (this.isOperation(node)) {
            boolean canFold = this.canFold(node);
            if(canFold) {
                int result = this.calculateValue(node);
                JmmNodeImpl parent = (JmmNodeImpl) node.getParent();
                int index = this.getIndex(parent.getChildren(), node);
                JmmNodeImpl newNode = new JmmNodeImpl("Integer");
                newNode.setParent(parent);
                newNode.put("value", Integer.toString(result));
                node.delete();
                parent.add(newNode, index);
            }
        }

        for (int i = 0; i < node.getNumChildren(); i++) {
            this.fold(node.getChildren().get(i));
        }

        return false;
    }
}
