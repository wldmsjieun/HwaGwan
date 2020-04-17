package kr.ac.mju.cd2020shwagwan;

public class Cosmetics {
    // id
    private int id;

    private String productBrand;
    private String productName;
    private String productOpen;
    private String productExp;

    public Cosmetics(int id, String productBrand, String productName) {
        this.id = id;
        this.productBrand = productBrand;
        this.productName = productName;
//        this.productOpen = productOpen;
//        this.productExp = productExp;
    }
    public int getId() {
        return this.id;
    }

    public String getproductBrand() { return this.productBrand; }

    public String getproductName() {
        return this.productName;
    }

    public String productOpen() {
        return this.productOpen;
    }

    public String productExp() {
        return this.productExp;
    }

}