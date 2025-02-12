package com.sp.mad;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;

public class buy_listing_details extends AppCompatActivity {

    private TextView listingTitle, listingPrice, listingBy, listingConditions, listingCategories, listingDescription;
    private ImageView listingImage, backBtn, btnSave;
    private FirebaseFirestore db;
    private String itemId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_buy_listing_details);

        // Initialize views
        listingTitle = findViewById(R.id.listing_title);
        listingPrice = findViewById(R.id.listing_price);
        listingImage = findViewById(R.id.listing_pic);
        listingBy = findViewById(R.id.listing_acc);
        listingConditions = findViewById(R.id.buyer_conditions);
        listingCategories = findViewById(R.id.listing_categories);
        listingDescription = findViewById(R.id.listing_description);
        backBtn = findViewById(R.id.backBtn); // Initialize backBtn
        btnSave = findViewById(R.id.btn_save); // Initialize the save button

        // Set click listener for back button
        backBtn.setOnClickListener(v -> {
            finish(); // Close the current activity and return to the previous one
        });

        // Initialize Firestore
        db = FirebaseFirestore.getInstance();

        // Get data from Intent
        Intent intent = getIntent();
        itemId = intent.getStringExtra("itemId");

        // Fetch data from Firestore
        fetchDataFromFirestore();

        // Handle save button functionality
        handleSaveButton();
    }

    private void fetchDataFromFirestore() {
        DocumentReference listingRef = db.collection("listing_items").document(itemId);
        listingRef.get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                DocumentSnapshot document = task.getResult();
                if (document != null && document.exists()) {
                    String itemTitle = document.getString("itemName");
                    String itemPrice = document.getString("price");
                    String itemImageUrl = document.getString("imageUrl");
                    String userId = document.getString("userId");
                    String itemConditions = document.getString("condition");
                    String itemCategories = document.getString("school") + " - " + document.getString("course");
                    String itemDescription = document.getString("description");

                    // Set data to views
                    listingTitle.setText(itemTitle);
                    listingPrice.setText("$" + itemPrice);
                    Glide.with(buy_listing_details.this).load(itemImageUrl).into(listingImage);
                    listingConditions.setText(itemConditions);
                    listingCategories.setText("Category: " + itemCategories);
                    listingDescription.setText(itemDescription);

                    // Fetch username using userId
                    fetchUsername(userId);
                } else {
                    Toast.makeText(buy_listing_details.this, "Item not found", Toast.LENGTH_SHORT).show();
                }
            } else {
                Toast.makeText(buy_listing_details.this, "Failed to retrieve data", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void fetchUsername(String userId) {
        DocumentReference userRef = db.collection("users").document(userId);
        userRef.get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                DocumentSnapshot userDocument = task.getResult();
                if (userDocument != null && userDocument.exists()) {
                    String username = userDocument.getString("username");
                    listingBy.setText(username);
                } else {
                    listingBy.setText("User not found");
                }
            } else {
                Toast.makeText(buy_listing_details.this, "Failed to retrieve username", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void handleSaveButton() {
        btnSave.setOnClickListener(v -> {
            FirebaseAuth mAuth = FirebaseAuth.getInstance();
            String currentUserId = mAuth.getCurrentUser().getUid();

            FirebaseFirestore db = FirebaseFirestore.getInstance();
            db.collection("users").document(currentUserId)
                    .collection("saved_items")
                    .document(itemId)
                    .get()
                    .addOnSuccessListener(documentSnapshot -> {
                        if (documentSnapshot.exists()) {
                            // If the item is already saved, unsave it
                            db.collection("users").document(currentUserId)
                                    .collection("saved_items")
                                    .document(itemId)
                                    .delete()
                                    .addOnSuccessListener(aVoid -> {
                                        Toast.makeText(buy_listing_details.this, "Item unsaved!", Toast.LENGTH_SHORT).show();
                                    })
                                    .addOnFailureListener(e -> {
                                        Toast.makeText(buy_listing_details.this, "Error unsaving item: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                                    });
                        } else {
                            // If the item is not saved, save it
                            HashMap<String, Object> savedItemData = new HashMap<>();
                            savedItemData.put("itemId", itemId);

                            db.collection("users").document(currentUserId)
                                    .collection("saved_items")
                                    .document(itemId)
                                    .set(savedItemData)
                                    .addOnSuccessListener(aVoid -> {
                                        Toast.makeText(buy_listing_details.this, "Item saved!", Toast.LENGTH_SHORT).show();
                                    })
                                    .addOnFailureListener(e -> {
                                        Toast.makeText(buy_listing_details.this, "Error saving item: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                                    });
                        }
                    })
                    .addOnFailureListener(e -> {
                        Toast.makeText(buy_listing_details.this, "Error processing save action: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    });
        });
    }
}
