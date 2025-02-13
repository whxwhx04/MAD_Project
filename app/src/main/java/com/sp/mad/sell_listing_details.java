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
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

public class sell_listing_details extends AppCompatActivity {

    private TextView listingTitle, listingPrice, listingBy, listingConditions, listingCategories, listingDescription;
    private ImageView listingImage, backBtn,btnChat;
    private FirebaseFirestore db;
    private FirebaseStorage storage;
    private String itemId,sellerId, buyerId;
    private Button editButton, deleteButton;

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
        btnChat = findViewById(R.id.btn_Chat);
        editButton = findViewById(R.id.btn_Edit);
        deleteButton = findViewById(R.id.btn_Delete);

        // Set listeners
        btnChat.setOnClickListener(v -> openChatList());
        backBtn.setOnClickListener(v -> finish());
        editButton.setOnClickListener(v -> navigateToEditListing());
        deleteButton.setOnClickListener(v -> deleteListing());

        // Initialize Firestore and FirebaseStorage
        db = FirebaseFirestore.getInstance();
        storage = FirebaseStorage.getInstance();

        // Get data from Intent
        itemId = getIntent().getStringExtra("itemId");
        buyerId = getIntent().getStringExtra("buyerId");
        sellerId = getIntent().getStringExtra("sellerId");
        if (itemId == null || itemId.isEmpty()) {
            Toast.makeText(this, "Error: No item ID found", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }
        btnChat.setOnClickListener(v -> {
            if (sellerId == null || sellerId.isEmpty()) {
                Toast.makeText(this, "Error: No seller ID found", Toast.LENGTH_SHORT).show();
                return;
            }
            Intent intent = new Intent(this, ChatListActivity.class);
            intent.putExtra("itemId", itemId);
            intent.putExtra("sellerId", sellerId);  // Ensure sellerId is assigned from Firestore
            intent.putExtra("buyerId", buyerId);
            startActivity(intent);
        });
        // Fetch data
        fetchDataFromFirestore();
    }

    private void openChatList() {
        if (itemId == null || itemId.isEmpty()) {
            Toast.makeText(this, "Error: No item ID found", Toast.LENGTH_SHORT).show();
            return;
        }
        // Navigate to ChatListActivity and pass the item ID
        Intent intent = new Intent(this, ChatListActivity.class);
        intent.putExtra("itemId", itemId);
        startActivity(intent);
    }
    private void fetchDataFromFirestore() {
        DocumentReference listingRef = db.collection("listing_items").document(itemId);
        listingRef.get().addOnCompleteListener(task -> {
            if (task.isSuccessful() && task.getResult() != null && task.getResult().exists()) {
                DocumentSnapshot document = task.getResult();

                // Retrieve data safely
                String itemTitle = document.getString("itemName");
                String itemPrice = document.getString("price");
                String itemImageUrl = document.getString("imageUrl");  // This is the image URL
                String userId = document.getString("userId");
                sellerId = document.getString("userId"); // Get seller ID here
                String itemConditions = document.getString("condition");
                String itemCategories = document.getString("school") + " - " + document.getString("course");
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
                if (sellerId != null && !sellerId.isEmpty()) {
                    fetchUsername(sellerId);
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

    private void deleteListing() {
        DocumentReference listingRef = db.collection("listing_items").document(itemId);

        // Fetch the image URL before deleting the item
        listingRef.get().addOnCompleteListener(task -> {
            if (task.isSuccessful() && task.getResult() != null) {
                DocumentSnapshot document = task.getResult();
                String itemImageUrl = document.getString("imageUrl");

                if (itemImageUrl != null && !itemImageUrl.isEmpty()) {
                    // Get reference to the image file in Firebase Storage
                    StorageReference imageRef = storage.getReferenceFromUrl(itemImageUrl);

                    // Delete the image from Firebase Storage
                    imageRef.delete().addOnCompleteListener(imageDeleteTask -> {
                        if (imageDeleteTask.isSuccessful()) {
                            // After image is deleted, delete the listing item from Firestore
                            listingRef.delete().addOnCompleteListener(deleteTask -> {
                                if (deleteTask.isSuccessful()) {
                                    // Show Toast message on success
                                    Toast.makeText(sell_listing_details.this, "Item and image deleted successfully", Toast.LENGTH_SHORT).show();

                                    // Navigate back to previous activity after deletion
                                    finish();
                                } else {
                                    // Show error message if deletion fails
                                    Toast.makeText(sell_listing_details.this, "Error deleting item", Toast.LENGTH_SHORT).show();
                                }
                            });
                        } else {
                            // Show error message if image deletion fails
                            Toast.makeText(sell_listing_details.this, "Error deleting image", Toast.LENGTH_SHORT).show();
                        }
                    });
                } else {
                    // If no image URL is found, just delete the listing from Firestore
                    listingRef.delete().addOnCompleteListener(deleteTask -> {
                        if (deleteTask.isSuccessful()) {
                            Toast.makeText(sell_listing_details.this, "Item deleted successfully", Toast.LENGTH_SHORT).show();
                            finish();
                        } else {
                            Toast.makeText(sell_listing_details.this, "Error deleting item", Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            } else
            {
                Toast.makeText(sell_listing_details.this, "Error fetching item data", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
