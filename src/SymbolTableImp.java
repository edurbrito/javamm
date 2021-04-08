import pt.up.fe.comp.jmm.analysis.table.Symbol;
import pt.up.fe.comp.jmm.analysis.table.SymbolTable;
import pt.up.fe.comp.jmm.analysis.table.Type;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;

public class SymbolTableImp implements SymbolTable {
    List<String> imports = new ArrayList();
    String className = "";
    String superClass = "";
    HashMap<Symbol, String> fields = new HashMap<>();   // String is the value of the variable
    HashMap<String, MethodTable> methods = new HashMap<>();

    @Override
    public List<String> getImports() {
        return imports;
    }

    @Override
    public String getClassName() {
        return className;
    }

    @Override
    public String getSuper() {
        return superClass;
    }

    @Override
    public List<Symbol> getFields() {
        return new ArrayList<>(fields.keySet());
    }

    @Override
    public List<String> getMethods() {
        return new ArrayList<>(methods.keySet());
    }
    public MethodTable getMethods(String methodName) {
        return methods.get(methodName);
    }

    @Override
    public Type getReturnType(String methodName) {
        return methods.get(methodName).getReturnType();
    }

    @Override
    public List<Symbol> getParameters(String methodName) {
        return new ArrayList<>(methods.get(methodName).getParameters());
    }

    @Override
    public List<Symbol> getLocalVariables(String methodName) {
        return new ArrayList<>(methods.get(methodName).getLocalVariables().keySet());
    }


    public void addImport(String _import){
        this.imports.add(_import);
    }

    public void setClassName(String className) {
        this.className = className;
    }

    public void setSuperClass(String superClass) {
        this.superClass = superClass;
    }

    public void addField(Type type, String name) {
        fields.put(new Symbol(type, name), "");
    }

    public void addField(Symbol symbol){fields.put(symbol,"");}

    public void addField(Type type, String name, String value) {
        fields.put(new Symbol(type, name), value);
    }

    public void addFieldValue(String name, String value) {
        for(Symbol symbol : fields.keySet()) {
            if(symbol.getName().equals(name))
                fields.put(symbol,value);
        }
    }

    public void addMethod(String methodName, MethodTable methodTable){this.methods.put(methodName, methodTable);}

    public void addMethod(String methodName, Type returnType, HashSet<Symbol> parameters) {
        methods.put(methodName, new MethodTable(returnType, parameters));
    }

    public void addMethodParameter(String methodName, Symbol parameter) {
        methods.get(methodName).addParameter(parameter);
    }

    public void addMethodVariable(String methodName, Symbol variable) {
        methods.get(methodName).addLocalVariable(variable);
    }

    public void addMethodVariable(String methodName, Symbol variable, String value) {
        methods.get(methodName).addLocalVariable(variable, value);
    }

    public void assignMethodVariable(String methodName, String variableName, String value) {
        methods.get(methodName).assignVariable(variableName, value);
    }

    @Override
    public String toString() {
        return "SymbolTableImp{" +
                "imports=" + imports +
                ", className='" + className + '\'' +
                ", superClass='" + superClass + '\'' +
                ", fields=" + fields +
                ", methods=" + methods +
                '}';
    }
}
