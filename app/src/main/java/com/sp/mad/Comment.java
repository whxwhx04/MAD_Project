package com.sp.mad;

public class Comment {
    private String userId;
    private String content;
    private long timestamp;

    public Comment(String userId, String content, long timestamp) {
        this.userId = userId;
        this.content = content;
        this.timestamp = timestamp;
    }

    public String getUserId() {
        return userId;
    }

    public String getContent() {
        return content;
    }

    public long getTimestamp() {
        return timestamp;
    }
}
