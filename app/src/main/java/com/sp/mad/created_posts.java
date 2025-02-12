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

public class created_posts extends AppCompatActivity {

    private RecyclerView recyclerViewCreatedPosts;
    private PostAdapter postAdapter;
    private List<Post> createdPostsList;
    private String currentUserId; // Store current user ID
    private ImageButton backButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_created_posts);  // Use the layout XML you provided earlier

        // Initialize views
        recyclerViewCreatedPosts = findViewById(R.id.recyclerViewCreatedPosts);
        backButton = findViewById(R.id.btn_back2);

        // Get the current user ID from FirebaseAuth
        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        currentUserId = mAuth.getCurrentUser().getUid(); // Fetch the current user's UID

        // Set up RecyclerView
        recyclerViewCreatedPosts.setLayoutManager(new LinearLayoutManager(this));
        createdPostsList = new ArrayList<>();

        // Pass the currentUserId to the PostAdapter
        postAdapter = new PostAdapter(createdPostsList, currentUserId);
        recyclerViewCreatedPosts.setAdapter(postAdapter);

        // Fetch created posts for the current user
        fetchCreatedPosts();

        // Back button functionality
        backButton.setOnClickListener(view -> {
            finish();  // Go back to the previous screen
        });
    }

    private void fetchCreatedPosts() {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("posts")  // Query the posts collection
                .whereEqualTo("userId", currentUserId)  // Filter by userId
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    if (queryDocumentSnapshots != null && !queryDocumentSnapshots.isEmpty()) {
                        // Clear the list and add new posts
                        createdPostsList.clear();
                        for (DocumentSnapshot document : queryDocumentSnapshots) {
                            // Extract post data
                            String postId = document.getId();  // Use document ID as postId
                            String userId = document.getString("userId");
                            String description = document.getString("description");
                            String imageUrl = document.getString("imageUrl");

                            // Fetch the username based on userId
                            fetchUsernameAndUpdatePost(postId, userId, description, imageUrl);
                        }
                    } else {
                        Toast.makeText(created_posts.this, "No created posts found.", Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(created_posts.this, "Failed to fetch created posts.", Toast.LENGTH_SHORT).show();
                });
    }

    private void fetchUsernameAndUpdatePost(String postId, String userId, String description, String imageUrl) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("users")  // Query the users collection
                .document(userId)
                .get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        String username = documentSnapshot.getString("username");  // Fetch username

                        // Create a new Post object with the username
                        Post post = new Post(postId, userId, username, description, imageUrl);

                        // Add the post to the list and notify the adapter
                        createdPostsList.add(post);
                        postAdapter.notifyItemInserted(createdPostsList.size() - 1);  // Notify adapter for the newly inserted post
                    }
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(created_posts.this, "Failed to fetch username.", Toast.LENGTH_SHORT).show();
                });
    }
}
