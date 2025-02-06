package com.sp.mad;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Button;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import java.util.ArrayList;
import java.util.List;

public class Seller_Listings extends AppCompatActivity {
    private ImageView backBtn1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_seller_listings);

        // Initialize UI elements
        backBtn1 = findViewById(R.id.backBtn1);

        // Back button functionality
        backBtn1.setOnClickListener(view -> {
            Intent intent = new Intent(Seller_Listings.this, MainPage.class);
            startActivity(intent);
            finish();
        });

        // Get data from Intent for listing details
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
            TextView sellerTitle = findViewById(R.id.seller_title);
            TextView sellerPrice = findViewById(R.id.seller_price);
            TextView sellerDescription = findViewById(R.id.seller_description);
            TextView sellerAccount = findViewById(R.id.seller_acc);
            TextView sellerCondition = findViewById(R.id.seller_condition);
            TextView sellerSchool = findViewById(R.id.seller_listing_school);
            TextView sellerCourse = findViewById(R.id.seller_listing_course);
            ImageView sellerImage = findViewById(R.id.seller_pic);

            sellerTitle.setText(title);
            sellerPrice.setText(price);
            sellerDescription.setText(description);
            sellerAccount.setText(accountSeller);
            sellerCondition.setText(condition);
            sellerSchool.setText(school);
            sellerCourse.setText(course);
            sellerImage.setImageResource(imageResId);
        }
        // Text Buyer button functionality
        ImageView btnTextBuyer = findViewById(R.id.btn_Chat);
        btnTextBuyer.setOnClickListener(view -> {
            // Pass seller_id as current user and buyer_id as the other user
            Intent chatIntent = new Intent(Seller_Listings.this, SellerAllChats.class);
            String currentUserId = "seller_id";  // Replace with actual seller ID
            String buyerId = intent.getStringExtra("buyerId"); // Get buyer ID from Intent
            chatIntent.putExtra("currentUserId", currentUserId);  // Send current user (seller) ID
            chatIntent.putExtra("otherUserId", buyerId);  // Send the buyer's ID
            startActivity(chatIntent);
        });
    }
}
