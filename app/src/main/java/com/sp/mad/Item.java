package com.sp.mad;

public class Item {
    private String title;
    private String price;
    private int imageResId;

    public Item(String title, String price, int imageResId) {
        this.title = title;
        this.price = price;
        this.imageResId = imageResId;
    }

    public String getTitle() {
        return title;
    }

    public String getPrice() {
        return price;
    }

    public int getImageResid() {
        return imageResId;
    }
}
