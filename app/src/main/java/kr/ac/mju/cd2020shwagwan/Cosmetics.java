package kr.ac.mju.cd2020shwagwan;

public class Cosmetics {

    private String itemBrand;
    private String itemName;

    public Cosmetics(int id, String itemBrand, String itemName) {

        this.itemBrand = itemBrand;
        this.itemName = itemName;
    }

    public String getitemBrand() {
        return this.itemBrand;
    }

    public String getitemName() {
        return this.itemName;
    }

}
