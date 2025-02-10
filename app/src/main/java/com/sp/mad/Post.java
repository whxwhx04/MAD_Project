package com.sp.mad;

public class Post {
    private String postId;
    private String userId;
    private String username;
    private String description;
    private String imageUrl;

    // Constructor
    public Post(String postId, String userId, String username, String description, String imageUrl) {
        this.postId = postId;
        this.userId = userId;
        this.username = username;
        this.description = description;
        this.imageUrl = imageUrl;
    }

    // Getter methods
    public String getPostId() { return postId; }

    public String getUserId() {
        return userId;
    }

    public String getUsername() { return username; } 

    public String getDescription() {
        return description;
    }

    public String getImageUrl() {
        return imageUrl;
    }
}
