package kr.co.whipping;

import java.sql.Blob;

public class Basket {
    private int basketId;
    private String barcodeId;
    private String barcdoeType;
    private String itemName;
    private int amount;
    private int price;
    private byte[] img;

    public Basket(int basketId, String barcodeId, String barcdoeType, String itemName, int amount, int price, byte[] img) {
        this.basketId = basketId;
        this.barcodeId = barcodeId;
        this.barcdoeType = barcdoeType;
        this.itemName = itemName;
        this.amount = amount;
        this.price = price;
        this.img = img;
    }

    public int getBasketId() {
        return basketId;
    }

    public String getBarcodeId() {
        return barcodeId;
    }

    public String getBarcdoeType() {
        return barcdoeType;
    }

    public String getItemName() {
        return itemName;
    }

    public int getAmount() {
        return amount;
    }

    public int getPrice() {
        return price;
    }

    public byte[] getImg() {
        return img;
    }
}
