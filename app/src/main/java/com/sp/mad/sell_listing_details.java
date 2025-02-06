package com.sp.mad;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

public class sell_listing_details extends AppCompatActivity {

    private TextView listingTitle, listingPrice, listingBy, listingConditions, listingCategories, listingDescription;
    private ImageView listingImage, backBtn;
    private FirebaseFirestore db;
    private String itemId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sell_listing_details);

        // Initialize views
        listingTitle = findViewById(R.id.seller_title);
        listingPrice = findViewById(R.id.seller_price);
        listingImage = findViewById(R.id.seller_pic);
        listingBy = findViewById(R.id.seller_acc); // This TextView will display the username
        listingConditions = findViewById(R.id.seller_condition);
        listingCategories = findViewById(R.id.seller_listing_categories);
        listingDescription = findViewById(R.id.seller_description);
        backBtn = findViewById(R.id.backBtn1); // Initialize backBtn

        // Set click listener for back button
        backBtn.setOnClickListener(v -> {
            // Navigate back to mainpage
            finish(); // This will close the current activity and return to the previous one
        });

        // Initialize Firestore
        db = FirebaseFirestore.getInstance();

        // Get data from Intent
        Intent intent = getIntent();
        itemId = intent.getStringExtra("itemId");

        // Fetch data from Firestore
        fetchDataFromFirestore();
    }

    private void fetchDataFromFirestore() {
        DocumentReference listingRef = db.collection("listing_items").document(itemId);
        listingRef.get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                DocumentSnapshot document = task.getResult(); // Use DocumentSnapshot here
                if (document != null && document.exists()) {
                    // Retrieve data from snapshot
                    String itemTitle = document.getString("itemName");
                    String itemPrice = document.getString("price");
                    String itemImageUrl = document.getString("imageUrl");
                    String userId = document.getString("userId"); // Fetch userId to get username
                    String itemConditions = document.getString("condition");
                    String itemCategories = document.getString("course") + " - " + document.getString("school");
                    String itemDescription = document.getString("description");

                    // Set data to views
                    listingTitle.setText(itemTitle);
                    listingPrice.setText("$" + itemPrice);
                    Glide.with(sell_listing_details.this).load(itemImageUrl).into(listingImage);
                    listingConditions.setText(itemConditions);
                    listingCategories.setText("Category: " + itemCategories);
                    listingDescription.setText(itemDescription);

                    // Fetch username using userId
                    fetchUsername(userId);
                } else {
                    Toast.makeText(sell_listing_details.this, "Item not found", Toast.LENGTH_SHORT).show();
                }
            } else {
                Toast.makeText(sell_listing_details.this, "Failed to retrieve data", Toast.LENGTH_SHORT).show();
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
                    listingBy.setText(username); // Set the username to the listingBy TextView
                } else {
                    listingBy.setText("User  not found");
                }
            } else {
                Toast.makeText(sell_listing_details.this, "Failed to retrieve username", Toast.LENGTH_SHORT).show();
            }
        });
    }
}