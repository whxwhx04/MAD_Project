package com.sp.mad;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;

public class CreateAccountActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_account); // Ensure this matches the layout file

        // Find the "Log In" TextView
        TextView logInTextView = findViewById(R.id.log_in);

        // Set click listener on "Log In" TextView
        logInTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Navigate to Login activity
                Intent intent = new Intent(CreateAccountActivity.this, LoginPage.class);
                startActivity(intent);
                finish(); // Optionally finish the current activity
            }
        });
    }
}
