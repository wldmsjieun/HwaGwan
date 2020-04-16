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
        return id;
    }

    public String getProductBrand() {
        return productBrand;
    }

    public String getProductName() {
        return productName;
    }

    public String getProductOpen() {
        return productOpen;
    }

    public String getProductExp() {
        return productExp;
    }
}