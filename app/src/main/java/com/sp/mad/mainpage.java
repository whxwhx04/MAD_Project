package com.sp.mad;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.SearchView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class mainpage extends AppCompatActivity {
    private FirebaseFirestore db;
    private FirebaseAuth auth;
    private RecyclerView recyclerView;
    private MyAdapter adapter;
    private List<Item> itemList;
    private SearchView searchView;
    private ImageView filterIcon;
    private DrawerLayout drawerLayout;
    private NavigationView navigationView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mainpage);

        db = FirebaseFirestore.getInstance();
        auth = FirebaseAuth.getInstance();

        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new GridLayoutManager(this, 2));
        itemList = new ArrayList<>();

        // Initialize Drawer Layout and Navigation View
        drawerLayout = findViewById(R.id.drawerLayout);
        navigationView = findViewById(R.id.navigationView);

        // Handle Navigation Drawer Item Clicks
        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(MenuItem item) {
                int id = item.getItemId();

                if (id == R.id.nav_all_chats) {
                    startActivity(new Intent(mainpage.this, mainpage.class));
                } else if (id == R.id.nav_created_post) {
                    startActivity(new Intent(mainpage.this, mainpage.class));
                } else if (id == R.id.nav_liked_posts) {
                    startActivity(new Intent(mainpage.this, mainpage.class));
                } else if (id == R.id.nav_saved_items) {
                    startActivity(new Intent(mainpage.this, saveditems_page.class));
                } else if (id == R.id.nav_bid_ask) {
                    startActivity(new Intent(mainpage.this, mainpage.class));
                } else if (id == R.id.nav_settings) {
                    startActivity(new Intent(mainpage.this, acc_setting.class));
                } else if (id == R.id.nav_logout) {
                    FirebaseAuth.getInstance().signOut();
                    startActivity(new Intent(mainpage.this, login_page.class));
                }

                // Close the drawer after selection
                drawerLayout.closeDrawer(GravityCompat.START);
                return true;
            }
        });

        // Open Drawer when nav_drawer icon is clicked
        ImageView navDrawerIcon = findViewById(R.id.nav_drawer);
        navDrawerIcon.setOnClickListener(v -> drawerLayout.openDrawer(GravityCompat.START));

        // Retrieve filters from intent
        Intent intent = getIntent();
        HashMap<String, String> filters = (HashMap<String, String>) intent.getSerializableExtra("filters");
        fetchListings("", filters);

        // Setup Search functionality
        searchView = findViewById(R.id.searchView);
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                fetchListings(query, filters);
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                fetchListings(newText, filters);
                return true;
            }
        });

        // Handle Filter Icon click
        filterIcon = findViewById(R.id.filtrIcon);
        filterIcon.setOnClickListener(v -> {
            Intent filterIntent = new Intent(mainpage.this, mainfilter.class);
            startActivity(filterIntent);
        });

        // Bottom Navigation Bar
        BottomNavigationView bottomNavigationView = findViewById(R.id.bottomNavigationView);
        bottomNavigationView.setOnItemSelectedListener(new BottomNavigationView.OnItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(MenuItem item) {
                if (item.getItemId() == R.id.explore) {
                    startActivity(new Intent(mainpage.this, mainpage.class));
                    return true;
                } else if (item.getItemId() == R.id.community) {
                    startActivity(new Intent(mainpage.this, commpage.class));
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
            }
        });
    }

    private void fetchListings(String searchQuery, HashMap<String, String> filters) {
        CollectionReference listingsRef = db.collection("listing_items");
        Query query = listingsRef;

        if (!searchQuery.isEmpty()) {
            query = query.whereEqualTo("itemName", searchQuery);
        }

        if (filters != null) {
            for (String key : filters.keySet()) {
                String value = filters.get(key);
                if (value != null && !value.equals("Any")) {
                    query = query.whereEqualTo(key, value);
                }
            }
        }

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
}
