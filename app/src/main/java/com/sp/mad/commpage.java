package com.sp.mad;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

public class commpage extends AppCompatActivity {
    private RecyclerView recyclerView;
    private PostAdapter postAdapter;
    private List<Post> postList;
    private String currentUserId;
    private ImageView filtrIcon, addIcon;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_commpage);

        recyclerView = findViewById(R.id.recyclerView);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(layoutManager);

        filtrIcon = findViewById(R.id.filtrIcon);
        filtrIcon.setOnClickListener(v -> {
            Intent filterIntent = new Intent(commpage.this, commfilter.class);
            startActivity(filterIntent);
        });

        addIcon = findViewById(R.id.addicon);
        addIcon.setOnClickListener(v -> {
            Intent addpostIntent = new Intent(commpage.this, create_post.class);
            startActivity(addpostIntent);
        });

        // Get current user ID
        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        currentUserId = mAuth.getCurrentUser().getUid();

        BottomNavigationView bottomNavigationView = findViewById(R.id.bottomNavigationView);
        bottomNavigationView.setOnItemSelectedListener(item -> {
            if (item.getItemId() == R.id.explore) {
                startActivity(new Intent(commpage.this, mainpage.class));
                return true;
            } else if (item.getItemId() == R.id.community) {
                startActivity(new Intent(commpage.this, commpage.class));
                return true;
            } else if (item.getItemId() == R.id.sell) {
                startActivity(new Intent(commpage.this, create_listing.class));
                return true;
            } else if (item.getItemId() == R.id.updates) {
                return true;
            } else if (item.getItemId() == R.id.profile) {
                startActivity(new Intent(commpage.this, profile_page.class));
                return true;
            }
            return false;
        });

        // Initialize the post list
        postList = new ArrayList<>();

        // Fetch posts from Firestore
        fetchPostsFromFirestore();
    }

    private void fetchPostsFromFirestore() {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("posts")
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    if (queryDocumentSnapshots != null) {
                        for (QueryDocumentSnapshot document : queryDocumentSnapshots) {
                            String postId = document.getId();
                            String userId = document.getString("userId");
                            String description = document.getString("description");
                            String imageUrl = document.getString("imageUrl");

                            // Fetch the username of the user who created the post
                            fetchUsername(userId, postId, description, imageUrl);
                        }
                    }
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(commpage.this, "Error loading posts: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }

    private void fetchUsername(String userId, String postId, String description, String imageUrl) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("users").document(userId)
                .get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        String username = documentSnapshot.getString("username");

                        // Add the post to the list with username included
                        postList.add(new Post(postId, userId, username, description, imageUrl));

                        // Set the adapter with the fetched posts once the username is added
                        postAdapter = new PostAdapter(postList, currentUserId);
                        recyclerView.setAdapter(postAdapter);
                    }
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(commpage.this, "Error loading username: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }
}
