package com.sp.mad;

public class Item {
    private String itemId;
    private String title;
    private String price;
    private String imageUrl; // Image URL from Firebase
    private String userId; // User ID from Firebase


    // Constructor for Firestore data (ensuring correct assignments)
    public Item(String itemId, String title, String price, String imageUrl, String userId) {
        this.itemId = itemId;
        this.title = title;
        this.price = price;
        this.imageUrl = imageUrl; // Ensure correct assignment
        this.userId = userId; // Ensure correct assignment
    }

    // Getters
    public String getItemId() {
        return itemId;
    }

    public String getTitle() {
        return title;
    }

    public String getPrice() {
        return price;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public String getUserId() {
        return userId;
    }


}
