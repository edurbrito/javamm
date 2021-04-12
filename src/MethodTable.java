import pt.up.fe.comp.jmm.analysis.table.Symbol;
import pt.up.fe.comp.jmm.analysis.table.Type;

import java.util.HashMap;
import java.util.List;

public class MethodTable {
    String name;
    String signature;
    List<Symbol> parameters;
    HashMap<Symbol, Boolean> localVariables = new HashMap<>();   // Boolean is true if the variable has been assigned
    Type returnType;

    public MethodTable(String name, Type returnType, List<Symbol> parameters) {
        this.name = name;
        this.returnType = returnType;
        this.parameters = parameters;
        StringBuilder sigBuilder = new StringBuilder(this.name);
        for (Symbol parameter : parameters) {
            sigBuilder.append(parameter.getType().getName());
        }
        this.signature = sigBuilder.toString();
    }

    public String getName(){ return this.name; }

    public String getSignature() {
        return signature;
    }

    public List<Symbol> getParameters() {
        return this.parameters;
    }

    public HashMap<Symbol, Boolean> getLocalVariables() {
        return localVariables;
    }

    public void addLocalVariable(Symbol variable) {
        this.localVariables.put(variable, false);
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
