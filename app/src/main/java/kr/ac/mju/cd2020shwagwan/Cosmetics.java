package kr.ac.mju.cd2020shwagwan;

public class Cosmetics {
    // id
    private int id;

    private String productBrand;
    private String productName;
    private String productOpen;
    private String productExp;
    private String productKind;
    private int productInitPeriod;
    private int productAlarm;

    public Cosmetics(int id, String productBrand, String productName, String productOpen, String productExp, String productKind, int productInitPeriod, int productAlarm) {
        this.id = id;
        this.productBrand = productBrand;
        this.productName = productName;
        this.productOpen = productOpen;
        this.productExp = productExp;
        this.productKind = productKind;
        this.productInitPeriod = productInitPeriod;
        this.productAlarm = productAlarm;
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

    public String getProductKind() {
        return productKind;
    }

    public int getProductInitPeriod() {
        return productInitPeriod;
    }

    public int getProductAlarm() {
        return productAlarm;
    }

    public void setProductAlarm(int i){
        this.productAlarm = i;
    }
}