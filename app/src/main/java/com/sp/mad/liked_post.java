package com.sp.mad;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.auth.FirebaseAuth;

import java.util.ArrayList;
import java.util.List;

public class liked_post extends AppCompatActivity {

    private RecyclerView recyclerViewLikedPosts;
    private PostAdapter postAdapter;
    private List<Post> likedPostsList;
    private String currentUserId; // Store current user ID
    private ImageButton backButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_liked_post);

        // Initialize views
        recyclerViewLikedPosts = findViewById(R.id.recyclerViewLikedPosts);
        backButton = findViewById(R.id.btn_back);

        // Set up RecyclerView
        recyclerViewLikedPosts.setLayoutManager(new LinearLayoutManager(this));
        likedPostsList = new ArrayList<>();
        postAdapter = new PostAdapter(likedPostsList, currentUserId);
        recyclerViewLikedPosts.setAdapter(postAdapter);

        // Get the current user ID from FirebaseAuth
        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        currentUserId = mAuth.getCurrentUser().getUid(); // Fetch the current user's UID

        // Fetch liked posts for the current user
        fetchLikedPosts();

        // Back button functionality
        backButton.setOnClickListener(view -> {
            finish();  // Go back to the previous screen
        });
    }

    private void fetchLikedPosts() {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("users")
                .document(currentUserId)
                .collection("liked_posts")
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    if (queryDocumentSnapshots != null && !queryDocumentSnapshots.isEmpty()) {
                        // Clear the list and add new posts
                        likedPostsList.clear();
                        for (DocumentSnapshot document : queryDocumentSnapshots) {
                            // Extract postId from the liked_posts subcollection
                            String postId = document.getString("postId");
                            if (postId != null) {
                                // Fetch the post details using the postId
                                fetchPostDetails(postId);
                            }
                        }
                    } else {
                        Toast.makeText(liked_post.this, "No liked posts found.", Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(liked_post.this, "Failed to fetch liked posts.", Toast.LENGTH_SHORT).show();
                });
    }

    private void fetchPostDetails(String postId) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("posts")
                .document(postId)
                .get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        // Get the post details
                        String userId = documentSnapshot.getString("userId");
                        String description = documentSnapshot.getString("description");
                        String imageUrl = documentSnapshot.getString("imageUrl");

                        // Now, fetch the username from the "users" collection using the userId
                        fetchUsername(userId, postId, description, imageUrl);
                    }
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(liked_post.this, "Failed to fetch post details.", Toast.LENGTH_SHORT).show();
                });
    }

    private void fetchUsername(String userId, String postId, String description, String imageUrl) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("users")
                .document(userId)  // Use the userId to get the user's data
                .get()
                .addOnSuccessListener(userDocument -> {
                    if (userDocument.exists()) {
                        // Get the username from the user document
                        String username = userDocument.getString("username");

                        // Now create a Post object with all the details (including username)
                        Post post = new Post(postId, userId, username, description, imageUrl);

                        // Add the post to the list and notify the adapter
                        likedPostsList.add(post);
                        postAdapter.notifyItemInserted(likedPostsList.size() - 1);  // Notify adapter for the newly inserted post
                    }
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(liked_post.this, "Failed to fetch username.", Toast.LENGTH_SHORT).show();
                });
    }
}
