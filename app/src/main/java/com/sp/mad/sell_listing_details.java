package com.sp.mad;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

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
    private Button editButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sell_listing_details);

        // Initialize views
        listingTitle = findViewById(R.id.seller_title);
        listingPrice = findViewById(R.id.seller_price);
        listingImage = findViewById(R.id.seller_pic);
        listingBy = findViewById(R.id.seller_acc);
        listingConditions = findViewById(R.id.seller_condition);
        listingCategories = findViewById(R.id.seller_listing_categories);
        listingDescription = findViewById(R.id.seller_description);
        backBtn = findViewById(R.id.backBtn1);
        editButton = findViewById(R.id.btn_Edit);

        // Set listeners
        backBtn.setOnClickListener(v -> finish());
        editButton.setOnClickListener(v -> navigateToEditListing());

        // Initialize Firestore
        db = FirebaseFirestore.getInstance();

        // Get data from Intent
        itemId = getIntent().getStringExtra("itemId");
        if (itemId == null || itemId.isEmpty()) {
            Toast.makeText(this, "Error: No item ID found", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        // Fetch data
        fetchDataFromFirestore();
    }

    private void fetchDataFromFirestore() {
        DocumentReference listingRef = db.collection("listing_items").document(itemId);
        listingRef.get().addOnCompleteListener(task -> {
            if (task.isSuccessful() && task.getResult() != null && task.getResult().exists()) {
                DocumentSnapshot document = task.getResult();

                // Retrieve data safely
                String itemTitle = document.getString("itemName");
                String itemPrice = document.getString("price");
                String itemImageUrl = document.getString("imageUrl");
                String userId = document.getString("userId");
                String itemConditions = document.getString("condition");
                String itemCategories = document.getString("course") + " - " + document.getString("school");
                String itemDescription = document.getString("description");

                // Set data with null checks
                listingTitle.setText(itemTitle != null ? itemTitle : "No Title");
                listingPrice.setText(itemPrice != null ? "$" + itemPrice : "Price Unavailable");
                listingConditions.setText(itemConditions != null ? itemConditions : "No Condition Info");
                listingCategories.setText(itemCategories != null ? "Category: " + itemCategories : "No Category Info");
                listingDescription.setText(itemDescription != null ? itemDescription : "No Description Available");

                // Load image safely
                if (itemImageUrl != null && !itemImageUrl.isEmpty()) {
                    Glide.with(this).load(itemImageUrl).into(listingImage);
                } else {
                    listingImage.setImageResource(R.drawable.placeholder_image); // Ensure you have a placeholder image
                }

                // Fetch seller username
                if (userId != null && !userId.isEmpty()) {
                    fetchUsername(userId);
                } else {
                    listingBy.setText("Unknown Seller");
                }
            } else {
                Toast.makeText(this, "Item not found", Toast.LENGTH_SHORT).show();
                finish();
            }
        });
    }

    private void fetchUsername(String userId) {
        DocumentReference userRef = db.collection("users").document(userId);
        userRef.get().addOnCompleteListener(task -> {
            if (task.isSuccessful() && task.getResult() != null && task.getResult().exists()) {
                String username = task.getResult().getString("username");
                listingBy.setText(username != null ? username : "Unknown User");
            } else {
                listingBy.setText("User not found");
            }
        });
    }

    private void navigateToEditListing() {
        Intent intent = new Intent(this, edit_listing.class);
        intent.putExtra("itemId", itemId);
        startActivity(intent);
    }
}
