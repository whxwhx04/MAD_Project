package com.sp.mad;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

public class buy_listing_details extends AppCompatActivity {

    private TextView listingTitle, listingPrice, listingBy, listingConditions, listingCategories, listingDescription;
    private ImageView listingImage, backBtn;
    private Button makeOfferBtn;
    private FirebaseFirestore db;
    private String itemId, sellerId, buyerId;

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
        backBtn = findViewById(R.id.backBtn);
        makeOfferBtn = findViewById(R.id.btn_offer);

        // Set click listener for back button
        backBtn.setOnClickListener(v -> finish());

        // Initialize Firestore
        db = FirebaseFirestore.getInstance();

        // Get data from Intent
        Intent intent = getIntent();
        itemId = intent.getStringExtra("itemId");

        // Get current user ID (buyer)
        buyerId = FirebaseAuth.getInstance().getCurrentUser().getUid();

        // Fetch data from Firestore
        fetchDataFromFirestore();

        // Set click listener for "Make Offer" button
        makeOfferBtn.setOnClickListener(v -> {
            if (sellerId == null) {
                Toast.makeText(buy_listing_details.this, "Error: Seller ID not available", Toast.LENGTH_SHORT).show();
                return;
            }
            Intent chatIntent = new Intent(buy_listing_details.this, BuyerChatRoomActivity.class);
            chatIntent.putExtra("buyerId", buyerId);
            chatIntent.putExtra("sellerId", sellerId);
            chatIntent.putExtra("itemId", itemId);
            startActivity(chatIntent);
        });
    }

    private void fetchDataFromFirestore() {
        DocumentReference listingRef = db.collection("listing_items").document(itemId);
        listingRef.get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                DocumentSnapshot document = task.getResult();
                if (document != null && document.exists()) {
                    // Retrieve data from snapshot
                    String itemTitle = document.getString("itemName");
                    String itemPrice = document.getString("price");
                    String itemImageUrl = document.getString("imageUrl");
                    sellerId = document.getString("userId"); // Fetch sellerId
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

                    // Fetch username using sellerId
                    fetchUsername(sellerId);
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
}
