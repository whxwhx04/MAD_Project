package com.sp.mad;

import java.io.Serializable;

public class Item implements Serializable {
    private String itemId;
    private String title;
    private String price;
    private String imageUrl;

    // Default constructor for Firebase
    public Item() {
    }

    // Constructor with itemId
    public Item(String itemId, String title, String price, String imageUrl) {
        this.itemId = itemId;
        this.title = title;
        this.price = price;
        this.imageUrl = imageUrl;
    }

    // Getter for itemId
    public String getItemId() {
        return itemId;
    }

    // Setter for itemId
    public void setItemId(String itemId) {
        this.itemId = itemId;
    }

    // Getter for title
    public String getTitle() {
        return title;
    }

    // Setter for title
    public void setTitle(String title) {
        this.title = title;
    }

    // Getter for price
    public String getPrice() {
        return price;
    }

    // Setter for price
    public void setPrice(String price) {
        this.price = price;
    }

    // Getter for imageUrl
    public String getImageUrl() {
        return imageUrl;
    }

    // Setter for imageUrl
    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }
}
