package com.sp.mad;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.drawerlayout.widget.DrawerLayout;
import com.google.android.material.navigation.NavigationView;
import androidx.core.view.GravityCompat;

import java.util.ArrayList;
import java.util.List;

public class MainPage extends AppCompatActivity {

    private DrawerLayout drawerLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mainpage);

        // Initialize RecyclerView
        RecyclerView recyclerView = findViewById(R.id.recyclerView);

        // Set GridLayoutManager with 2 columns
        GridLayoutManager gridLayoutManager = new GridLayoutManager(this, 2);
        recyclerView.setLayoutManager(gridLayoutManager);

        // Create sample data
        List<Item> itemList = new ArrayList<>();
        itemList.add(new Item("Item 1", "Price $10", R.drawable.eg1, "Ke Liang", "Brand New", "EEE", "DCPE", "BNIB"));
        itemList.add(new Item("Item 2", "Price $20", R.drawable.eg2, "John", "Heavily Used", "EEE", "DEEE", "Open for nego"));

        // Set Adapter
        MyAdapter adapter = new MyAdapter(this, itemList);
        recyclerView.setAdapter(adapter);

        // Bottom Navigation View
        BottomNavigationView bottomNavigationView = findViewById(R.id.bottomNavigationView);
        bottomNavigationView.setOnItemSelectedListener(item -> {
            if (item.getItemId() == R.id.explore) {
                // Handle Explore action
                return true;
            } else if (item.getItemId() == R.id.community) {
                // Handle Community action
                return true;
            } else if (item.getItemId() == R.id.sell) {
                // Handle Sell action
                return true;
            } else if (item.getItemId() == R.id.updates) {
                // Handle Updates action
                return true;
            } else if (item.getItemId() == R.id.profile) {
                Intent intent = new Intent(MainPage.this, Profile.class);
                startActivity(intent);
                return true;
            } else {
                return false;
            }
        });

        // Initialize DrawerLayout and NavigationView
        drawerLayout = findViewById(R.id.drawer_layout);
        NavigationView navigationView = findViewById(R.id.navigationView);

        // Set item selection listener for navigation menu
        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                int id = item.getItemId();
                if (id == R.id.nav_all_chats) {
                    // Open All Chats
                } else if (id == R.id.nav_create_post) {
                    // Open Create Post
                } else if (id == R.id.nav_liked_posts) {
                    // Open Liked Posts
                } else if (id == R.id.nav_saved_items) {
                    // Open Saved Items
                } else if (id == R.id.nav_bid_ask) {
                    // Open Bid & Ask
                } else if (id == R.id.nav_settings) {
                    // Open Settings
                } else if (id == R.id.nav_logout) {
                    logOut();
                }
                drawerLayout.closeDrawer(GravityCompat.START);
                return true;
            }
        });

        // Open the Navigation Drawer when the button is clicked
        ImageView navDrawerButton = findViewById(R.id.nav_drawer);
        navDrawerButton.setOnClickListener(v -> {
            drawerLayout.openDrawer(GravityCompat.START);
        });
    }

    // Method to handle Log Out
    private void logOut() {
        // Clear user session, remove stored credentials, etc.
        // Example: SharedPreferences.clear(), or call a logout API if necessary

        // After logout, redirect user to Login page or Splash Screen
        Intent intent = new Intent(MainPage.this, LoginPage.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);  // Ensure no back button can return to this page
        startActivity(intent);
        finish();  // Close the current activity
    }
}
