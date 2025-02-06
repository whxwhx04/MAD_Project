package com.sp.mad;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.LinearLayout;

import androidx.appcompat.app.AppCompatActivity;

public class BuyerAllChats extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.chat_recycler_row); // Set the layout to chat_recycler_row.xml

        ImageView backBtn = findViewById(R.id.chat_backBtn);
        LinearLayout chatRow = findViewById(R.id.ChatRow); // Get reference to the ChatRow layout

        // Back button functionality
        backBtn.setOnClickListener(view -> {
            Intent intent = new Intent(BuyerAllChats.this, Buyer_Listings.class);
            startActivity(intent);
            finish(); // Close current activity
        });

        // Set click listener on the chat row
        chatRow.setOnClickListener(view -> {
            Intent chatIntent = new Intent(BuyerAllChats.this, BuyerChatRoomActivity.class);
            // You can pass any necessary data to BuyerChatRoomActivity here
            startActivity(chatIntent);
        });
    }
}