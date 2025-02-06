package com.sp.mad;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.SearchView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.List;

public class mainpage extends AppCompatActivity {

    private FirebaseFirestore db;
    private FirebaseAuth auth;
    private RecyclerView recyclerView;
    private MyAdapter adapter;
    private List<Item> itemList;
    private SearchView searchView;
    private ImageView filterIcon;  // Added filter icon

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mainpage);

        // Initialize Firebase
        db = FirebaseFirestore.getInstance();
        auth = FirebaseAuth.getInstance();

        // Initialize RecyclerView
        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new GridLayoutManager(this, 2));
        itemList = new ArrayList<>();

        // Fetch Listings
        fetchListings("");

        // Initialize SearchView
        searchView = findViewById(R.id.searchView);
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                searchListings(query);
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                searchListings(newText);
                return true;
            }
        });

        // Initialize Filter Icon and Set OnClick Listener
        filterIcon = findViewById(R.id.filterIcon);
        filterIcon.setOnClickListener(v -> {
            Intent intent = new Intent(mainpage.this, mainfilter.class);
            startActivity(intent);
        });

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

    private void fetchListings(String searchQuery) {
        CollectionReference listingsRef = db.collection("listing_items");
        Query query = searchQuery.isEmpty() ? listingsRef : listingsRef.whereEqualTo("itemName", searchQuery);

        query.get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                itemList.clear();
                for (QueryDocumentSnapshot document : task.getResult()) {
                    String itemId = document.getId();
                    String title = document.getString("itemName");
                    String price = "Price: " + document.getString("price");
                    String imageUrl = document.getString("imageUrl");
                    String itemUserId = document.getString("userId");

                    itemList.add(new Item(itemId, title, price, imageUrl, itemUserId));
                }

                String currentUserId = auth.getCurrentUser() != null ? auth.getCurrentUser().getUid() : null;

                if (adapter == null) {
                    adapter = new MyAdapter(itemList, currentUserId);
                    recyclerView.setAdapter(adapter);
                } else {
                    adapter.notifyDataSetChanged();
                }
            } else {
                Toast.makeText(mainpage.this, "Error getting listings", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void searchListings(String query) {
        fetchListings(query);
    }
}
