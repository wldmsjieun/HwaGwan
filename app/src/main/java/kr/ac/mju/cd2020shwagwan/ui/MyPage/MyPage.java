package kr.ac.mju.cd2020shwagwan.ui.MyPage;

public class MyPage {
    private int id;
    private String brandName;
    private String productName;
    private String dtOpen;
    private String dtExp;
    private String kind;
    private String volume;
    private String additionalContent;

    public MyPage(int id, String brandName, String productName, String dtOpen, String dtExp, String kind, String volume, String additionalContent) {
        this.id = id;
        this.brandName = brandName;
        this.productName = productName;
        this.dtOpen = dtOpen;
        this.dtExp = dtExp;
        this.kind = kind;
        this.volume = volume;
        this.additionalContent = additionalContent;
    }

    public int getId() {
        return id;
    }

    public String getBrandName() {
        return brandName;
    }

    public String getProductName() {
        return productName;
    }

    public String getDtOpen() {
        return dtOpen;
    }

    public String getDtExp() {
        return dtExp;
    }

    public String getKind() {
        return kind;
    }

    public String getVolume() {
        return volume;
    }

    public String getAdditionalContent() {
        return additionalContent;
    }
}
