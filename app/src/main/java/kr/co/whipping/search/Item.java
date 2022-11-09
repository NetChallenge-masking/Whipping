package kr.co.whipping.search;

public class Item {
    public String item_name;
    public String item_location;

    public Item(String item_name, String item_location) {
        this.item_name = item_name;
        this.item_location = item_location;
    }

    public String getItem_name() {
        return item_name;
    }

    public String getItem_location() {
        return item_location;
    }
}
