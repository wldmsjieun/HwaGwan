package kr.ac.mju.cd2020shwagwan;



public class BarcodeInfo {

    private int id;

    private String bcdId;
    private String bcdBrand;
    private String bcdName;
    private String bcdVolume;

    public BarcodeInfo(int id, String bID, String bBrand, String bName, String bVolume){
        this.id =id;
        this.bcdId = bID;
        this.bcdBrand = bBrand;
        this.bcdName = bName;
        this.bcdVolume = bVolume;
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

    public String getBcdName() {
        return bcdName;
    }

    public String getBcdVolume() {
        return bcdVolume;
    }


}
