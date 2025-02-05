package com.sp.mad;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
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
        ImageView btnLiked = findViewById(R.id.btn_liked);

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

        // Make Offer button functionality
        makeOffer.setOnClickListener(view -> {
            // Handle the "Make Offer" button click
            // You can navigate to another activity or show a dialog, etc.
            // For example:
            //Intent intentOffer = new Intent(Buyer_Listings.this, MakeOfferActivity.class);
            //startActivity(intentOffer);
        });

        // Bid and Ask button functionality
        bidask.setOnClickListener(view -> {
            // Handle the "BID N ASK" button click
            // You can open a new activity or show a dialog, etc.
            // For example:
            //Intent intentBid = new Intent(Buyer_Listings.this, BidAskActivity.class);
            //startActivity(intentBid);
        });

        // Liked button functionality (optional)
        btnLiked.setOnClickListener(view -> {
            // Handle the "Liked" button click (toggle or show some UI feedback)
        });
    }
}
