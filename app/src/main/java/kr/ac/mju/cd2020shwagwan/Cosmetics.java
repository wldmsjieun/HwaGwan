package kr.ac.mju.cd2020shwagwan;

public class Cosmetics {
    // id
    private int id;

    private String itemBrand;
    private String itemName;

    public Cosmetics(int id, String itemBrand, String itemName) {
        this.id = id;
        this.itemBrand = itemBrand;
        this.itemName = itemName;
    }
    public int getId() {
        return this.id;
    }

    public String getitemBrand() {
        return this.itemBrand;
    }

    public String getitemName() {
        return this.itemName;
    }

}
