package kr.ac.mju.cd2020shwagwan.ui.AdditionalInformation.lowest.repository;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class ResponseItem {
    @SerializedName("title")
    @Expose
    private String title;
    @SerializedName("link")
    @Expose
    private String link;
    @SerializedName("image")
    @Expose
    private String image;
    @SerializedName("lprice")
    @Expose
    private String lprice;
    @SerializedName("hprice")
    @Expose
    private String hprice;
    @SerializedName("mallName")
    @Expose
    private String mallName;
    @SerializedName("productId")
    @Expose
    private String productId;
    @SerializedName("productType")
    @Expose
    private String productType;
    @SerializedName("brand")
    @Expose
    private String brand;
    @SerializedName("maker")
    @Expose
    private String maker;
    @SerializedName("category1")
    @Expose
    private String category1;
    @SerializedName("category2")
    @Expose
    private String category2;
    @SerializedName("category3")
    @Expose
    private String category3;
    @SerializedName("category4")
    @Expose
    private String category4;

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getLink() {
        return link;
    }

    public void setLink(String link) {
        this.link = link;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getLprice() {
        return lprice;
    }

    public void setLprice(String lprice) {
        this.lprice = lprice;
    }

    public String getHprice() {
        return hprice;
    }

    public void setHprice(String hprice) {
        this.hprice = hprice;
    }

    public String getMallName() {
        return mallName;
    }

    public void setMallName(String mallName) {
        this.mallName = mallName;
    }

    public String getProductId() {
        return productId;
    }

    public void setProductId(String productId) {
        this.productId = productId;
    }

    public String getProductType() {
        return productType;
    }

    public void setProductType(String productType) {
        this.productType = productType;
    }

    public String getBrand() {
        return brand;
    }

    public void setBrand(String brand) {
        this.brand = brand;
    }

    public String getMaker() {
        return maker;
    }

    public void setMaker(String maker) {
        this.maker = maker;
    }

    public String getCategory1() {
        return category1;
    }

    public void setCategory1(String category1) {
        this.category1 = category1;
    }

    public String getCategory2() {
        return category2;
    }

    public void setCategory2(String category2) {
        this.category2 = category2;
    }

    public String getCategory3() {
        return category3;
    }

    public void setCategory3(String category3) {
        this.category3 = category3;
    }

    public String getCategory4() {
        return category4;
    }

    public void setCategory4(String category4) {
        this.category4 = category4;
    }


}
