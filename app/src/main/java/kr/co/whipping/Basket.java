package kr.co.whipping;

public class Basket {
    private int basketId;
    private String barcodeId;
    private String barcdoeType;
    private String itemName;
    private int amount;

    public Basket(int basketId, String barcodeId, String barcdoeType, String itemName, int amount) {
        this.basketId = basketId;
        this.barcodeId = barcodeId;
        this.barcdoeType = barcdoeType;
        this.itemName = itemName;
        this.amount = amount;
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
}
