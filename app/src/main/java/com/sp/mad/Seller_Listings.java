package com.sp.mad;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

public class Seller_Listings extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_seller_listings);

        // Get references to UI elements
        ImageView SellerImage = findViewById(R.id.seller_pic);
        TextView SellerTitle = findViewById(R.id.seller_title);
        TextView SellerPrice = findViewById(R.id.seller_price);
        TextView SellerAccount = findViewById(R.id.seller_acc);
        TextView SellerCondition = findViewById(R.id.seller_condition);
        TextView SellerSchool = findViewById(R.id.seller_listing_school);
        TextView SellerCourse = findViewById(R.id.seller_listing_course);
        TextView SellerDescription = findViewById(R.id.seller_description);
        Button MarkSold = findViewById(R.id.btn_Mark_Sold);
        Button DeleteListing = findViewById(R.id.btn_Delete);
        Button EditListing = findViewById(R.id.btn_Edit);
        ImageView backBtn1 = findViewById(R.id.backBtn1);
        ImageView ChatButton = findViewById(R.id.btn_Chat);

        // Back button functionality
        backBtn1.setOnClickListener(view -> {
            Intent intent = new Intent(Seller_Listings.this, MainPage.class);
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
            SellerTitle.setText(title);
            //makeOffer.();
            SellerPrice.setText(price);
            SellerDescription.setText(description);
            SellerAccount.setText(accountSeller);
            SellerCondition.setText(condition);
            SellerSchool.setText(school);
            SellerCourse.setText(course);
            SellerImage.setImageResource(imageResId);
        }
    }
}
