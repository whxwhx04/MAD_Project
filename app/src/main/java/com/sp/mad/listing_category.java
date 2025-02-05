package com.sp.mad;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.Spinner;
import androidx.appcompat.app.AppCompatActivity;

public class listing_category extends AppCompatActivity {
    private Spinner catSchool, catCourse;
    private ImageButton resetCat, applyCat, backList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_listing_category);

        catSchool = findViewById(R.id.cat_school);
        catCourse = findViewById(R.id.cat_course);
        resetCat = findViewById(R.id.reset_cat);
        applyCat = findViewById(R.id.apply_cat);
        backList = findViewById(R.id.back_list);

        // Back button to return to create_listing
        backList.setOnClickListener(v -> finish());

        // Reset button clears selections
        resetCat.setOnClickListener(v -> {
            catSchool.setSelection(0);
            catCourse.setSelection(0);
        });

        // Apply button saves selections and returns to create_listing
        applyCat.setOnClickListener(v -> {
            String selectedSchool = catSchool.getSelectedItem().toString();
            String selectedCourse = catCourse.getSelectedItem().toString();

            Intent returnIntent = new Intent(listing_category.this, create_listing.class);
            returnIntent.putExtra("selectedSchool", selectedSchool);
            returnIntent.putExtra("selectedCourse", selectedCourse);
            startActivity(returnIntent);
        });
    }
}