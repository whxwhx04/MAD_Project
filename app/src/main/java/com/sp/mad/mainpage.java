package com.sp.mad;

import com.google.android.material.bottomnavigation.BottomNavigationView; // This is required if you're using BottomNavigationView

import android.content.Intent;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;



import java.util.ArrayList;
import java.util.List;

public class mainpage extends AppCompatActivity {

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
        itemList.add(new Item("Item 1", "Price $10", R.drawable.eg1));
        itemList.add(new Item("Item 2", "Price $20", R.drawable.eg2));

        // Set Adapter
        MyAdapter adapter = new MyAdapter(itemList);
        recyclerView.setAdapter(adapter);

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


}