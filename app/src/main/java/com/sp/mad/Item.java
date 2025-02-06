package com.sp.mad;

import java.io.Serializable;

public class Item implements Serializable {
    private String itemId;
    private String title;
    private String price;
    private String imageUrl;
    private String userId; // User ID to track item ownership

    // Default constructor (required for Firebase deserialization)
    public Item() {
    }

    // Parameterized constructor
    public Item(String itemId, String title, String price, String imageUrl, String userId) {
        this.itemId = itemId;
        this.title = title;
        this.price = price;
        this.imageUrl = imageUrl;
        this.userId = userId;
    }

    // Getter and Setter for itemId
    public String getItemId() { return itemId; }
    public void setItemId(String itemId) { this.itemId = itemId; }

    // Getter and Setter for title
    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    // Getter and Setter for price
    public String getPrice() { return price; }
    public void setPrice(String price) { this.price = price; }

    // Getter and Setter for imageUrl
    public String getImageUrl() { return imageUrl; }
    public void setImageUrl(String imageUrl) { this.imageUrl = imageUrl; }

    // Getter and Setter for userId
    public String getUserId() { return userId; }
    public void setUserId(String userId) { this.userId = userId; }
}
