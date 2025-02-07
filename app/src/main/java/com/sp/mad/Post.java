package com.sp.mad;

public class Post {
    private String postId;
    private String userId;
    private String description;
    private String imageUrl;

    // Constructor
    public Post(String postId, String userId, String description, String imageUrl) {
        this.postId = postId;
        this.userId = userId;
        this.description = description;
        this.imageUrl = imageUrl;
    }

    // Getter methods
    public String getPostId() {
        return postId;
    }

    public String getUserId() {
        return userId;
    }

    public String getDescription() {
        return description;
    }

    public String getImageUrl() {
        return imageUrl;
    }
}
