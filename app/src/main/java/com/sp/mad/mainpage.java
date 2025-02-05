package com.sp.mad;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

public class mainpage extends AppCompatActivity {

    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mainpage);

        // Initialize Firestore
        db = FirebaseFirestore.getInstance();

        // Initialize RecyclerView
        RecyclerView recyclerView = findViewById(R.id.recyclerView);

        // Set GridLayoutManager with 2 columns
        GridLayoutManager gridLayoutManager = new GridLayoutManager(this, 2);
        recyclerView.setLayoutManager(gridLayoutManager);

        // Fetch listings from Firestore
        fetchListings(recyclerView);

        BottomNavigationView bottomNavigationView = findViewById(R.id.bottomNavigationView);
        bottomNavigationView.setOnItemSelectedListener(item -> {
            if (item.getItemId() == R.id.explore) {
                // Handle Explore action
                Intent intent = new Intent(mainpage.this, mainpage.class);
                startActivity(intent);
                return true;
            } else if (item.getItemId() == R.id.community) {
                // Handle Community action
                return true;
            } else if (item.getItemId() == R.id.sell) {
                // Handle Sell action
                Intent intent = new Intent(mainpage.this, create_listing.class);
                startActivity(intent);
                return true;
            } else if (item.getItemId() == R.id.updates) {
                // Handle Updates action
                return true;
            } else if (item.getItemId() == R.id.profile) {
                // Handle Profile action
                Intent intent = new Intent(mainpage.this, profile_page.class);
                startActivity(intent);
                return true;
            } else
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
                            String title = document.getString("itemName");
                            String price = "Price: " + document.getString("price");
                            String imageUrl = document.getString("imageUrl");

                            // Add the item to the list
                            itemList.add(new Item(title, price, imageUrl));
                        }

                        // Set Adapter
                        MyAdapter adapter = new MyAdapter(itemList);
                        recyclerView.setAdapter(adapter);
                    } else {
                        Toast.makeText(mainpage.this, "Error getting listings", Toast.LENGTH_SHORT).show();
                    }
                });
    }
}
