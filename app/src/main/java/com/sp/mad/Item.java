package com.sp.mad;

public class Item {
    private String title;
    private String price;
    private String imageUrl; // Use String for storing the image URL

    // Constructor
    public Item(String title, String price, String imageUrl) {
        this.title = title;
        this.price = price;
        this.imageUrl = imageUrl;
    }

    // Getter for the title
    public String getTitle() {
        return title;
    }

    // Getter for the price
    public String getPrice() {
        return price;
    }

    // Getter for the imageUrl
    public String getImageUrl() {
        return imageUrl;
    }
}

