package com.sp.mad;

public class ChatItem {
    private String chatId, buyerId, sellerId, itemId;

    public ChatItem(String chatId, String buyerId, String sellerId, String itemId) {
        this.chatId = chatId;
        this.buyerId = buyerId;
        this.sellerId = sellerId;
        this.itemId = itemId;
    }

    public String getChatId() { return chatId; }
    public String getBuyerId() { return buyerId; }
    public String getSellerId() { return sellerId; }
    public String getItemId() { return itemId; }
}
