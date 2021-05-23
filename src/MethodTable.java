import pt.up.fe.comp.jmm.analysis.table.Symbol;
import pt.up.fe.comp.jmm.analysis.table.Type;

import java.util.HashMap;
import java.util.List;

public class MethodTable {
    String name;
    String signature;
    List<Parameter> parameters;
    HashMap<Symbol, Boolean> localVariables = new HashMap<>();   // Boolean is true if the variable has been assigned
    Type returnType;

    public MethodTable(String name, Type returnType, List<Parameter> parameters) {
        this.name = name;
        this.returnType = returnType;
        this.parameters = parameters;
        StringBuilder sigBuilder = new StringBuilder(this.name);
        for (Parameter parameter : parameters) {
            String array = parameter.getSymbol().getType().isArray() ? "array" : "";
            sigBuilder.append(array + parameter.getSymbol().getType().getName());
        }
        this.signature = sigBuilder.toString();
    }

    public String getName(){ return this.name; }

    public String getSignature() {
        return signature;
    }

    public List<Parameter> getParameters() {
        return this.parameters;
    }

    public Parameter getParameter(String name){
        for(Parameter p : this.parameters){
            if(p.getSymbol().getName().equals(name)){
                return p;
            }
        }
        return null;
    }

    public HashMap<Symbol, Boolean> getLocalVariables() {
        return localVariables;
    }

    public Symbol getVariable(String variableName){

        for(Symbol s : this.localVariables.keySet()){
            if(s.getName().equals(variableName))
                return s;
        }

        for(Parameter s : this.parameters){
            if(s.getSymbol().getName().equals(variableName))
                return s.getSymbol();
        }

        return null;
    }

    public Boolean addLocalVariable(Symbol variable) {
        // Check if a variable with the same name is already declared
        for (Symbol symbol : localVariables.keySet()) {
            if (symbol.getName().equals(variable.getName()))
                return false;
        }
        localVariables.put(variable, false);
        return true;
    }

    public void addLocalVariable(Symbol variable, Boolean assign) {
        this.localVariables.put(variable, assign);
    }

    public void assignVariable(String variableName) {
        for (Symbol symbol : localVariables.keySet()) {
            if (symbol.getName().equals(variableName))
                localVariables.put(symbol, true);
        }
    }

    public Type getReturnType() {
        return returnType;
    }
}
