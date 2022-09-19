package kr.co.whipping;

public class Basket {
    private int basketId;
    private String barcodeId;
    private String barcdoeType;
    private int amount;

    public Basket(int basketId, String barcodeId, String barcdoeType, int amount) {
        this.basketId = basketId;
        this.barcodeId = barcodeId;
        this.barcdoeType = barcdoeType;
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

    public int getAmount() {
        return amount;
    }
}
