package com.sp.mad;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

public class mainpage extends AppCompatActivity {

    private FirebaseFirestore db;
    private FirebaseAuth auth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mainpage);

        // Initialize Firebase
        db = FirebaseFirestore.getInstance();
        auth = FirebaseAuth.getInstance();

        // Initialize RecyclerView
        RecyclerView recyclerView = findViewById(R.id.recyclerView);
        GridLayoutManager gridLayoutManager = new GridLayoutManager(this, 2);
        recyclerView.setLayoutManager(gridLayoutManager);

        // Fetch listings from Firestore
        fetchListings(recyclerView);

        // Bottom Navigation
        BottomNavigationView bottomNavigationView = findViewById(R.id.bottomNavigationView);
        bottomNavigationView.setOnItemSelectedListener(item -> {
            if (item.getItemId() == R.id.explore) {
                startActivity(new Intent(mainpage.this, mainpage.class));
                return true;
            } else if (item.getItemId() == R.id.community) {
                return true;
            } else if (item.getItemId() == R.id.sell) {
                startActivity(new Intent(mainpage.this, create_listing.class));
                return true;
            } else if (item.getItemId() == R.id.updates) {
                return true;
            } else if (item.getItemId() == R.id.profile) {
                startActivity(new Intent(mainpage.this, profile_page.class));
                return true;
            }
            return false;
        });
    }

    private void fetchListings(RecyclerView recyclerView) {
        CollectionReference listingsRef = db.collection("listing_items");

        listingsRef.get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        List<Item> itemList = new ArrayList<>();
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            String itemId = document.getId(); // Fetch document ID
                            String title = document.getString("itemName");
                            String price = "Price: " + document.getString("price");
                            String imageUrl = document.getString("imageUrl");
                            String itemUserId = document.getString("userId"); // Fetch the userId for each item

                            itemList.add(new Item(itemId, title, price, imageUrl, itemUserId)); // Include userId

                        }

                        // Get the current user's ID
                        String currentUserId = auth.getCurrentUser() != null ? auth.getCurrentUser().getUid() : null;

                        // Set Adapter with the current user's ID
                        MyAdapter adapter = new MyAdapter(itemList, currentUserId);
                        recyclerView.setAdapter(adapter);
                    } else {
                        Toast.makeText(mainpage.this, "Error getting listings", Toast.LENGTH_SHORT).show();
                    }
                });
    }
}
