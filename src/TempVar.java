public class TempVar {
    private String name;
    private int nr = 1;

    public TempVar(String name) {
        this.name = name;
    }

    public void increment(){
        this.nr++;
    }

    public String getVar(){
        return name + nr;
    }

}
