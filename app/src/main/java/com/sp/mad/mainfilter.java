package com.sp.mad;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageButton;
import android.widget.Spinner;
import androidx.appcompat.app.AppCompatActivity;
import java.util.HashMap;

public class mainfilter extends AppCompatActivity {
    private Spinner priceSpinner, schoolSpinner, courseSpinner, conditionSpinner;
    private ImageButton applyButton, resetButton, backButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mainfilter);

        // Initialize UI components
        priceSpinner = findViewById(R.id.mainfil_price);
        schoolSpinner = findViewById(R.id.mainfil_school);
        courseSpinner = findViewById(R.id.mainfil_course);
        conditionSpinner = findViewById(R.id.mainfil_condition);
        applyButton = findViewById(R.id.apply_fil);
        resetButton = findViewById(R.id.reset_fil);
        backButton = findViewById(R.id.back_main);

        // Apply filter button logic
        applyButton.setOnClickListener(v -> {
            Intent intent = new Intent();
            HashMap<String, String> filters = new HashMap<>();

            String price = priceSpinner.getSelectedItem().toString();
            String school = schoolSpinner.getSelectedItem().toString();
            String course = courseSpinner.getSelectedItem().toString();
            String condition = conditionSpinner.getSelectedItem().toString();

            if (!price.equals("Any")) filters.put("price", price);
            if (!school.equals("Any")) filters.put("school", school);
            if (!course.equals("Any")) filters.put("course", course);
            if (!condition.equals("Any")) filters.put("condition", condition);

            intent.putExtra("filters", filters);
            setResult(RESULT_OK, intent);
            finish();
        });

        // Reset filter button logic
        resetButton.setOnClickListener(v -> {
            priceSpinner.setSelection(0);
            schoolSpinner.setSelection(0);
            courseSpinner.setSelection(0);
            conditionSpinner.setSelection(0);
        });

        // Back button logic
        backButton.setOnClickListener(v -> finish());
    }
}
