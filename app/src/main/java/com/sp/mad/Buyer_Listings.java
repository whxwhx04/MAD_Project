package com.sp.mad;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class Buyer_Listings extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_buyer_listings);

        // Get references to UI elements
        ImageView itemImage = findViewById(R.id.listing_pic);
        TextView listingTitle = findViewById(R.id.listing_title);
        TextView listingPrice = findViewById(R.id.listing_price);
        TextView listingAccount = findViewById(R.id.listing_acc);
        TextView listingCondition = findViewById(R.id.buyer_conditions);
        TextView listingSchool = findViewById(R.id.listing_school);
        TextView listingCourse = findViewById(R.id.listing_course);
        TextView listingDescription = findViewById(R.id.listing_description);
        Button makeOffer = findViewById(R.id.btn_offer);
        Button bidask = findViewById(R.id.btn_bidask);
        ImageView backBtn = findViewById(R.id.backBtn);
        ImageView btnSaved = findViewById(R.id.btn_saved);
        ImageView btnTextSeller = findViewById(R.id.btn_textseller);

        // Back button functionality
        backBtn.setOnClickListener(view -> {
            Intent intent = new Intent(Buyer_Listings.this, MainPage.class);
            startActivity(intent);
            finish(); // Close current activity
        });

        // Get data from Intent
        Intent intent = getIntent();
        if (intent != null) {
            String title = intent.getStringExtra("title");
            String price = intent.getStringExtra("price");
            int imageResId = intent.getIntExtra("imageResId", 0);
            String description = intent.getStringExtra("description");
            String accountSeller = intent.getStringExtra("accountSeller");
            String condition = intent.getStringExtra("condition");
            String school = intent.getStringExtra("school");
            String course = intent.getStringExtra("course");

            // Set data to UI elements
            listingTitle.setText(title);
            listingPrice.setText(price);
            listingDescription.setText(description);
            listingAccount.setText(accountSeller);
            listingCondition.setText(condition);
            listingSchool.setText(school);
            listingCourse.setText(course);
            itemImage.setImageResource(imageResId);
        }

        // Text Seller button functionality
        btnTextSeller.setOnClickListener(view -> {
            // Pass buyer_id as current user and seller_id as the other user
            Intent chatIntent = new Intent(Buyer_Listings.this, BuyerAllChats.class);
            String currentUserId = "buyer_id"; // Replace with actual buyer ID
            String sellerId = intent.getStringExtra("accountSeller"); // Get seller ID from Intent
            chatIntent.putExtra("currentUserId", currentUserId);  // Send current user (buyer) ID
            chatIntent.putExtra("otherUserId", sellerId);  // Send the seller's ID
            startActivity(chatIntent);
        });

        btnSaved.setOnClickListener(view -> {
            // Get item details from the already declared intent
            String title = intent.getStringExtra("title");
            String price = intent.getStringExtra("price");
            int imageResId = intent.getIntExtra("imageResId", 0);
            String description = intent.getStringExtra("description");
            String accountSeller = intent.getStringExtra("accountSeller");
            String condition = intent.getStringExtra("condition");
            String school = intent.getStringExtra("school");
            String course = intent.getStringExtra("course");

            if (title == null || title.isEmpty() || price == null || price.isEmpty()) {
                System.out.println("⚠ Empty item detected! Not adding to saved items.");
                return; // Prevent adding empty items
            }

            // Create an Item object
            Item savedItem = new Item(title, price, imageResId, description, accountSeller, condition, school, course);

            // Check if item is already saved
            if (SavedItemsManager.isItemAlreadySaved(savedItem)) {
                Toast.makeText(Buyer_Listings.this, "Item already saved!", Toast.LENGTH_SHORT).show();
                return;
            }

            // Add the item to saved items
            SavedItemsManager.addItem(savedItem);

            // Show confirmation message
            Toast.makeText(Buyer_Listings.this, "Item saved!", Toast.LENGTH_SHORT).show();
        });




        // "Make Offer" button functionality
        makeOffer.setOnClickListener(view -> {
            Intent chatIntent = new Intent(Buyer_Listings.this, BuyerChatRoomActivity.class);
            String currentUserId = "buyer_id"; // Set buyer ID as the current user
            String sellerId = intent.getStringExtra("accountSeller"); // Get seller ID from Intent
            chatIntent.putExtra("currentUserId", currentUserId);  // Send current user (buyer) ID
            chatIntent.putExtra("otherUserId", sellerId);  // Send the seller's ID
            startActivity(chatIntent);
        });
    }
}