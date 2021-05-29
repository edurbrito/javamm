import pt.up.fe.comp.jmm.JmmNode;
import pt.up.fe.comp.jmm.analysis.table.Symbol;
import pt.up.fe.comp.jmm.analysis.table.Type;
import pt.up.fe.comp.jmm.ast.JmmNodeImpl;
import pt.up.fe.comp.jmm.ast.PreorderJmmVisitor;

import java.util.*;

public class ConstantPropagation extends PreorderJmmVisitor<Boolean, Boolean>{

    private HashMap<Variable, Integer> variables;
    private HashMap<Variable, Integer> fields;

    private SymbolTableImp symbolTableImp;
    private String methodSignature;

    private class Variable{

        private String identifier;
        private String methodSignature;
        private int count;

        public Variable(String identifier){
            this.identifier = identifier;
            this.methodSignature = ConstantPropagation.this.methodSignature;
            this.count = 1;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            Variable variable = (Variable) o;
            return Objects.equals(identifier, variable.identifier) && Objects.equals(methodSignature, variable.methodSignature);
        }

        @Override
        public int hashCode() {
            return Objects.hash(identifier, methodSignature);
        }

        @Override
        public String toString() {
            return "Variable{" +
                    "identifier='" + identifier + '\'' +
                    ", methodSignature='" + methodSignature + '\'' +
                    ", count=" + count +
                    '}';
        }

        public void addAssign(){
            this.count += 1;
        }

        public int getAssigns(){
            return this.count;
        }
    }

    public ConstantPropagation(SymbolTableImp symbolTableImp){
        super();

        this.symbolTableImp = symbolTableImp;

        this.variables = new HashMap<>();
        this.fields = new HashMap<>();

        addVisit("MainMethod", this::dealWithMethod);
        addVisit("Method", this::dealWithMethod);
        addVisit("EqualStatement", this::dealWithEqualStatement);
    }

    private Boolean dealWithMethod(JmmNode jmmNode, Boolean aBoolean) {
        methodSignature = this.getMethodSignature(jmmNode);
        return true;
    }

    private Boolean dealWithEqualStatement(JmmNode jmmNode, Boolean aBoolean) {
        String identifier = jmmNode.getChildren().get(0).get("name");
        JmmNode leftSide = jmmNode.getChildren().get(1);
        Variable newVariable = new Variable(identifier);

        if(this.variables.containsKey(newVariable)){
            for(Variable key : this.variables.keySet()){
                if(key.equals(newVariable)){
                    key.addAssign();
                    this.variables.put(key, this.variables.get(key));
                    break;
                }
            }
        }
        else{
            if(leftSide.getKind().equals("Integer")){
                Symbol v = new Symbol(new Type("int",false), identifier);

                if(this.symbolTableImp.getLocalVariables(this.methodSignature).contains(v) || this.symbolTableImp.getParameters(this.methodSignature).contains(v) || (!this.methodSignature.equals("main") && this.symbolTableImp.getFields().contains(v)))
                    this.variables.put(newVariable, Integer.parseInt(leftSide.get("value")));
            }
        }
        return true;
    }

    private String getMethodSignature(JmmNode jmmNode){
        if(jmmNode.getKind().equals("Method")){
            List<JmmNode> children = jmmNode.getChildren();

            List<Parameter> parameters = getParameters(children.get(1));

            MethodTable methodTable = new MethodTable(jmmNode.get("name"), getType(children.get(0)), parameters);

            for(Parameter s: parameters)
                methodTable.addLocalVariable(s.getSymbol(), true);

            return methodTable.getSignature();
        }
        else if(jmmNode.getKind().equals("MainMethod")){
            return "main";
        }

        return "";
    }

    private List<Parameter> getParameters(JmmNode node) {
        List<JmmNode> children = node.getChildren();
        List<Parameter> symbolSet = new ArrayList<>();

        int i = 1;
        for (JmmNode argument : children) {
            if (argument.getKind().equals("Argument")) {
                symbolSet.add(new Parameter(i++, getSymbol(argument)));
            }
        }

        return symbolSet;
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

    private int getIndex(List<JmmNode> children, JmmNode node){
        return children.indexOf(node);
    }

    public boolean substitute(JmmNode node){

        if(node.getKind().equals("MainMethod") || node.getKind().equals("Method")){
            methodSignature = this.getMethodSignature(node);
        }
        else if(node.getKind().equals("EqualStatement")){
            String identifier = node.getChildren().get(0).get("name");
            Variable newVariable = new Variable(identifier);

            if(this.variables.containsKey(newVariable)){
                for(Variable key : this.variables.keySet()){
                    if(key.equals(newVariable) && key.getAssigns() == 1){
                        return true;
                    }
                }
            }
        }

        if(node.getKind().equals("Identifier")){
            JmmNodeImpl parent = (JmmNodeImpl) node.getParent();

            if(!parent.getKind().equals("EqualStatement") || (parent.getKind().equals("EqualStatement") && !parent.getChildren().get(0).equals(node))){
                Variable v = new Variable(node.get("name"));
                int index = this.getIndex(parent.getChildren(), node);
                int count = 1;

                for(Variable key : this.variables.keySet()){
                    if(key.equals(v)){
                        count = key.getAssigns();
                        break;
                    }
                }

                if(this.variables.containsKey(v) && index != -1 && count == 1){
                    Integer value = this.variables.get(v);
                    JmmNodeImpl newNode = new JmmNodeImpl("Integer");
                    newNode.setParent(parent);
                    newNode.put("value", value.toString());
                    node.delete();
                    parent.add(newNode, index);
                }
            }
        }
        else{

            HashSet<JmmNode> removables = new HashSet<>();

            for (int i = 0; i < node.getNumChildren(); i++) {
                if(this.substitute(node.getChildren().get(i)))
                    removables.add(node.getChildren().get(i));
            }

            for(JmmNode remove : removables){
                node.removeChild(remove);
            }
        }

        return false;
    }
}
