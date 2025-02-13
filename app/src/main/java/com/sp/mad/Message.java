package com.sp.mad;
public class Message {
    private String buyerId;
    private String sellerId;
    private String messageText;
    private String senderId; // ✅ Add senderId
    private String imageUrl;
    private long timestamp;
    private String itemId; // ✅ Add itemId
    // Empty constructor for Firestore deserialization
    public Message() {}
    // Constructor
    public Message(String buyerId, String sellerId, String messageText, String senderId, String imageUrl, long timestamp, String itemId) {
        this.buyerId = buyerId;
        this.sellerId = sellerId;
        this.messageText = messageText;
        this.senderId = senderId;
        this.imageUrl = imageUrl;
        this.timestamp = timestamp;
        this.itemId = itemId;
    }
    // ✅ Method to get sender ID
    public String getSenderId() { return senderId; }
    public String getBuyerId() { return buyerId; }
    public String getSellerId() { return sellerId; }
    public String getMessageText() { return messageText; }
    public long getTimestamp() { return timestamp; }
    public String getImageUrl() { return imageUrl; }
    public String getItemId() { return itemId; }
}