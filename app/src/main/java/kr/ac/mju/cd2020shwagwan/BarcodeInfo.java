package kr.ac.mju.cd2020shwagwan;

public class BarcodeInfo {

    private int id;
    private String bcdId;
    private String bcdBrand;
    private String bcdProduct;
    private String bcdVolume;

    public BarcodeInfo(int id, String bcdID, String bcdBrand, String bcdProduct, String bcdVolume){
        this.id =id;
        this.bcdId = bcdID;
        this.bcdBrand = bcdBrand;
        this.bcdProduct = bcdProduct;
        this.bcdVolume = bcdVolume;
    }

    public int getId() {
        return id;
    }

    public String getBcdId() {
        return bcdId;
    }

    public String getBcdBrand() {
        return bcdBrand;
    }

    public String getBcdProduct() {
        return bcdProduct;
    }

    public String getBcdVolume() {
        return bcdVolume;
    }


}
