package com.sp.mad;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.squareup.picasso.Picasso;

public class profile_page extends AppCompatActivity {
    private TextView accountName, userSchool, userCourse;
    private ImageView profilePicture, accSet;
    private FirebaseAuth auth;
    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile_page);

        // Initialize Firebase
        auth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        // Initialize UI components
        accountName = findViewById(R.id.accountName);
        userSchool = findViewById(R.id.user_school);
        userCourse = findViewById(R.id.user_course);
        profilePicture = findViewById(R.id.profilePicture);
        accSet = findViewById(R.id.acc_set);

        // Load user information
        loadUserInfo();

        // Navigate to Account Settings Page
        accSet.setOnClickListener(v -> {
            Intent intent = new Intent(profile_page.this, acc_setting.class);
            startActivity(intent);
        });

        // Bottom Navigation
        BottomNavigationView bottomNavigationView = findViewById(R.id.bottomNavigationView);
        bottomNavigationView.setOnItemSelectedListener(item -> {
            if (item.getItemId() == R.id.explore) {
                startActivity(new Intent(profile_page.this, mainpage.class));
                return true;
            } else if (item.getItemId() == R.id.community) {
                return true;
            } else if (item.getItemId() == R.id.sell) {
                startActivity(new Intent(profile_page.this, create_listing.class));
                return true;
            } else if (item.getItemId() == R.id.updates) {
                return true;
            } else if (item.getItemId() == R.id.profile) {
                startActivity(new Intent(profile_page.this, profile_page.class));
                return true;
            } else {
                return false;
            }
        });
    }

    private void loadUserInfo() {
        FirebaseUser user = auth.getCurrentUser();
        if (user != null) {
            String userId = user.getUid();
            DocumentReference userRef = db.collection("users").document(userId);
            userRef.get().addOnSuccessListener(documentSnapshot -> {
                if (documentSnapshot.exists()) {
                    String username = documentSnapshot.getString("username");
                    String school = documentSnapshot.getString("school");
                    String course = documentSnapshot.getString("course");
                    String profilePicUrl = documentSnapshot.getString("profilePicture");

                    accountName.setText(username);
                    userSchool.setText(school);
                    userCourse.setText(course);
                    if (profilePicUrl != null && !profilePicUrl.isEmpty()) {
                        Picasso.get().load(profilePicUrl).into(profilePicture);
                    }
                }
            });
        }
    }
}
