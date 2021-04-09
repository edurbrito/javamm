import pt.up.fe.comp.jmm.analysis.table.Symbol;
import pt.up.fe.comp.jmm.analysis.table.Type;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;

public class MethodTable {
    List<Symbol> parameters;
    HashMap<Symbol, String> localVariables = new HashMap<>();
    Type returnType;

    public MethodTable(Type returnType, List<Symbol> parameters) {
        this.returnType = returnType;
        this.parameters = parameters;
    }

    public void addParameter(Symbol parameter) {
        this.parameters.add(parameter);
    }

    public List<Symbol> getParameters() {
        return this.parameters;
    }

    public void setParameters(List<Symbol> parameters) {
        this.parameters = parameters;
    }

    public HashMap<Symbol, String> getLocalVariables() {
        return localVariables;
    }

    public void setLocalVariables(HashMap<Symbol, String> localVariables) {
        this.localVariables = localVariables;
    }

    public void addLocalVariable(Symbol variable) {
        this.localVariables.put(variable, "");
    }

    public void addLocalVariable(Symbol variable, String value) {
        this.localVariables.put(variable, value);
    }

    public void assignVariable(String variableName, String value) {
        for (Symbol symbol : localVariables.keySet()) {
            if (symbol.getName().equals(variableName))
                localVariables.put(symbol, value);
        }
    }

    public Type getReturnType() {
        return returnType;
    }

    public void setReturnType(Type returnType) {
        this.returnType = returnType;
    }
}
