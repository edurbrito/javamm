import pt.up.fe.comp.jmm.analysis.table.Symbol;

public class Parameter {
    private int order;
    private Symbol symbol;

    public Parameter(int order, Symbol symbol) {
        this.order = order;
        this.symbol = symbol;
    }

    public int getOrder() {
        return order;
    }

    public void setOrder(int order) {
        this.order = order;
    }

    public Symbol getSymbol() {
        return symbol;
    }

    public void setSymbol(Symbol symbol) {
        this.symbol = symbol;
    }
}
