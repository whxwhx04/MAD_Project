package com.sp.mad;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.squareup.picasso.Picasso;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class updates_page extends AppCompatActivity {

    private RecyclerView recyclerView;
    private String currentUserId;
    private FirebaseFirestore db;
    private List<Update> updatesList;
    private UpdatesAdapter updatesAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_updates_page);

        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        currentUserId = mAuth.getCurrentUser().getUid();

        db = FirebaseFirestore.getInstance();

        recyclerView = findViewById(R.id.recyclerView3);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        updatesList = new ArrayList<>();
        updatesAdapter = new UpdatesAdapter(updatesList);
        recyclerView.setAdapter(updatesAdapter);

        // Fetch updates from the user's updates subcollection
        fetchUserUpdates();

        BottomNavigationView bottomNavigationView = findViewById(R.id.bottomNavigationView);
        bottomNavigationView.setOnItemSelectedListener(item -> {
            if (item.getItemId() == R.id.explore) {
                startActivity(new Intent(updates_page.this, mainpage.class));
                return true;
            } else if (item.getItemId() == R.id.community) {
                startActivity(new Intent(updates_page.this, commpage.class));
                return true;
            } else if (item.getItemId() == R.id.sell) {
                startActivity(new Intent(updates_page.this, create_listing.class));
                return true;
            } else if (item.getItemId() == R.id.updates) {
                startActivity(new Intent(updates_page.this, updates_page.class));
                return true;
            } else if (item.getItemId() == R.id.profile) {
                startActivity(new Intent(updates_page.this, profile_page.class));
                return true;
            }
            return false;
        });
    }

    // Fetch updates from Firestore
    private void fetchUserUpdates() {
        db.collection("users").document(currentUserId)
                .collection("updates")
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    if (!queryDocumentSnapshots.isEmpty()) {
                        for (DocumentSnapshot documentSnapshot : queryDocumentSnapshots) {
                            String updateId = documentSnapshot.getId();
                            long timestamp = documentSnapshot.getLong("timestamp");

                            // Check if it's a listingId or postId
                            if (documentSnapshot.contains("listingId")) {
                                String listingId = documentSnapshot.getString("listingId");
                                fetchListingUpdate(listingId, timestamp);
                            } else if (documentSnapshot.contains("postId")) {
                                String postId = documentSnapshot.getString("postId");
                                fetchPostUpdate(postId, timestamp);
                            }
                        }
                    }
                })
                .addOnFailureListener(e -> Toast.makeText(updates_page.this, "Failed to load updates", Toast.LENGTH_SHORT).show());
    }

    // Fetch details for a listing update
    private void fetchListingUpdate(String listingId, long timestamp) {
        db.collection("listing_items").document(listingId)
                .get()
                .addOnSuccessListener(documentSnapshot -> {
                    String imageUrl = documentSnapshot.getString("imageUrl");
                    String formattedTime = formatTimestamp(timestamp);
                    Update update = new Update(imageUrl, "You have listed a new item!", formattedTime, listingId);
                    updatesList.add(update);
                    updatesAdapter.notifyDataSetChanged();
                })
                .addOnFailureListener(e -> Toast.makeText(updates_page.this, "Failed to load listing details", Toast.LENGTH_SHORT).show());
    }

    // Fetch details for a post update
    private void fetchPostUpdate(String postId, long timestamp) {
        db.collection("posts").document(postId)
                .get()
                .addOnSuccessListener(documentSnapshot -> {
                    String imageUrl = documentSnapshot.getString("imageUrl");  // Assuming image URL is stored in posts collection
                    String formattedTime = formatTimestamp(timestamp);
                    Update update = new Update(imageUrl, "You have made a new post!", formattedTime, postId);
                    updatesList.add(update);
                    updatesAdapter.notifyDataSetChanged();
                })
                .addOnFailureListener(e -> Toast.makeText(updates_page.this, "Failed to load post details", Toast.LENGTH_SHORT).show());
    }

    // Format the timestamp to the desired format (yyyy/MM/dd 23:59)
    private String formatTimestamp(long timestamp) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd HH:mm", Locale.getDefault());
        Date date = new Date(timestamp);
        return sdf.format(date);
    }

    // Remove update
    public void removeUpdate(String updateId) {
        db.collection("users").document(currentUserId)
                .collection("updates")
                .document(updateId)
                .delete()
                .addOnSuccessListener(aVoid -> {
                    Toast.makeText(updates_page.this, "Update removed", Toast.LENGTH_SHORT).show();
                    // Refresh the updates
                    fetchUserUpdates();
                })
                .addOnFailureListener(e -> Toast.makeText(updates_page.this, "Failed to remove update", Toast.LENGTH_SHORT).show());
    }
}
