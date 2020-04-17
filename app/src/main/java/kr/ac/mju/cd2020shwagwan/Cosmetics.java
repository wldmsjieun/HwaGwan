package kr.ac.mju.cd2020shwagwan;

public class Cosmetics {
    // id
    private int id;

    private String productBrand;
    private String productName;
    private String productOpen;
    private String productExp;

    public Cosmetics(int id, String productBrand, String productName, String productOpen, String productExp) {
        this.id = id;
        this.productBrand = productBrand;
        this.productName = productName;
        this.productOpen = productOpen;
        this.productExp = productExp;
    }
    public int getId() {
        return this.id;
    }

    public String getProductBrand() { return this.productBrand; }

    public String getProductName() {
        return this.productName;
    }

    public String getProductOpen() {
        return this.productOpen;
    }

    public String getProductExp() {
        return this.productExp;
    }

}