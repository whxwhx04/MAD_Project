package com.sp.mad;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import android.view.MenuItem;
import java.util.ArrayList;
import java.util.List;

public class Profile extends AppCompatActivity {
    private RecyclerView recyclerViewListings;
    private MyAdapter adapter;
    private List<Item> itemList;

    private TextView accountName, userSchool, userCourse;
    private ImageView profilePicture, settingsIcon, dmIcon;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        // Initialize Profile UI Elements
        accountName = findViewById(R.id.accountName);
        userSchool = findViewById(R.id.user_school);
        userCourse = findViewById(R.id.user_course);
        profilePicture = findViewById(R.id.profilePicture);
        settingsIcon = findViewById(R.id.imageView7);
        dmIcon = findViewById(R.id.dmIcon);

        // Load user profile data
        accountName.setText("John Doe");
        userSchool.setText("EEE");
        userCourse.setText("DCPE");
        profilePicture.setImageResource(R.drawable.logo);

        // Handle settings icon click
        settingsIcon.setOnClickListener(v -> {
            Intent intent = new Intent(Profile.this, MainPage.class);
            startActivity(intent);
        });

        // Handle DM icon click
        dmIcon.setOnClickListener(v -> {
            Intent intent = new Intent(Profile.this, MainPage.class);
            startActivity(intent);
        });

        // Initialize RecyclerView
        recyclerViewListings = findViewById(R.id.recyclerViewListings);
        recyclerViewListings.setLayoutManager(new GridLayoutManager(this, 2));

        // Sample data
        itemList = new ArrayList<>();
        itemList.add(new Item("Laptop", "$500", R.drawable.eg1, "Mike", "High-performance laptop", "Likely Used", "EEE", "DEB"));
        itemList.add(new Item("Phone", "$300", R.drawable.eg2, "Alice", "Brand new smartphone", "New", "EEE", "DCPE"));

        adapter = new MyAdapter(this, itemList);
        recyclerViewListings.setAdapter(adapter);

        // Ensure MyAdapter has this method
        adapter.setOnItemClickListener(position -> {
            Item selectedItem = itemList.get(position);
            Intent intent = new Intent(Profile.this, Buyer_Listings.class);
            intent.putExtra("title", selectedItem.getTitle());
            intent.putExtra("price", selectedItem.getPrice());
            intent.putExtra("imageResId", selectedItem.getImageResid()); // Ensure method exists
            intent.putExtra("description", selectedItem.getDescription());
            intent.putExtra("accountSeller", selectedItem.getAccountSeller());
            intent.putExtra("condition", selectedItem.getCondition());
            intent.putExtra("school", selectedItem.getSchool());
            intent.putExtra("course", selectedItem.getCourse());
            startActivity(intent);
        });

        // Setup Bottom Navigation
        BottomNavigationView bottomNavigationView = findViewById(R.id.bottomNavigationView);
        bottomNavigationView.setSelectedItemId(R.id.profile);

        bottomNavigationView.setOnItemSelectedListener(new BottomNavigationView.OnItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                if (item.getItemId() == R.id.explore) {
                    startActivity(new Intent(Profile.this, MainPage.class));
                    overridePendingTransition(0, 0);
                    return true;
                } else if (item.getItemId() == R.id.community) {
                    startActivity(new Intent(Profile.this, LoginPage.class)); // Change if needed
                    overridePendingTransition(0, 0);
                    return true;
                }
                return false;
            }
        });
    }
}
