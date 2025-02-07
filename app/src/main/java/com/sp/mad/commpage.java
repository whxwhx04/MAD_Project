package com.sp.mad;

import androidx.appcompat.app.AppCompatActivity;
import android.widget.ImageView;
import android.content.Intent;
import android.os.Bundle;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class commpage extends AppCompatActivity {

    private ImageView filtrIcon,addIcon;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_commpage);

        filtrIcon = findViewById(R.id.filtrIcon);
        filtrIcon.setOnClickListener(v -> {
            Intent filterIntent = new Intent(commpage.this, commfilter.class);
            startActivity(filterIntent);
        });

        addIcon = findViewById(R.id.addicon);
        addIcon.setOnClickListener(v -> {
            Intent addpostIntent = new Intent(commpage.this, create_post.class);
            startActivity(addpostIntent);
        });

        BottomNavigationView bottomNavigationView = findViewById(R.id.bottomNavigationView);
        bottomNavigationView.setOnItemSelectedListener(item -> {
            if (item.getItemId() == R.id.explore) {
                startActivity(new Intent(commpage.this, mainpage.class));
                return true;
            } else if (item.getItemId() == R.id.community) {
                startActivity(new Intent(commpage.this, commpage.class));
                return true;
            } else if (item.getItemId() == R.id.sell) {
                startActivity(new Intent(commpage.this, create_listing.class));
                return true;
            } else if (item.getItemId() == R.id.updates) {
                return true;
            } else if (item.getItemId() == R.id.profile) {
                startActivity(new Intent(commpage.this, profile_page.class));
                return true;
            }
            return false;
        });
    }
}