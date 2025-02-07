package com.sp.mad;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageButton;
import android.widget.Spinner;
import androidx.appcompat.app.AppCompatActivity;
import java.util.HashMap;

public class commfilter extends AppCompatActivity {
    private Spinner schoolSpinner, courseSpinner;
    private ImageButton applyButton, resetButton, backButton;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_commfilter);
        schoolSpinner = findViewById(R.id.commfil_school);
        courseSpinner = findViewById(R.id.commfil_course);
        applyButton = findViewById(R.id.apply_commfil);
        resetButton = findViewById(R.id.reset_commfil);
        backButton = findViewById(R.id.back_comm2);

        // Apply filter button logic
        applyButton.setOnClickListener(v -> {
            Intent intent = new Intent();
            HashMap<String, String> filters = new HashMap<>();

            String school = schoolSpinner.getSelectedItem().toString();
            String course = courseSpinner.getSelectedItem().toString();

            if (!school.equals("Any")) filters.put("school", school);
            if (!course.equals("Any")) filters.put("course", course);

            intent.putExtra("filters", filters);
            setResult(RESULT_OK, intent);
            finish();
        });
        // Reset filter button logic
        resetButton.setOnClickListener(v -> {
            schoolSpinner.setSelection(0);
            courseSpinner.setSelection(0);
        });
        // Back button logic
        backButton.setOnClickListener(v -> finish());
    }
}