package com.sp.mad;

public class Update {

    private String imageUrl;
    private String updateInfo;
    private String timestamp;
    private String updateId;

    // Constructor
    public Update(String imageUrl, String updateInfo, String timestamp, String updateId) {
        this.imageUrl = imageUrl;
        this.updateInfo = updateInfo;
        this.timestamp = timestamp;
        this.updateId = updateId;
    }

    // Getter methods
    public String getImageUrl() {
        return imageUrl;
    }

    public String getUpdateInfo() {
        return updateInfo;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public String getUpdateId() {
        return updateId;
    }
}
