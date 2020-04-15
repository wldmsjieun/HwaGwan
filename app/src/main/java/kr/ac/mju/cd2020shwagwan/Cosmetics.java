package kr.ac.mju.cd2020shwagwan;

public class Cosmetics {
    // id
    private int id;

    private String productBrand;
    private String productName;

    public Cosmetics(int id, String productBrand, String productName) {
        this.id = id;
        this.productBrand = productBrand;
        this.productName = productName;
    }
    public int getId() {
        return this.id;
    }

    public String getproductBrand() {
        return this.productBrand;
    }

    public String getproductName() {
        return this.productName;
    }

}