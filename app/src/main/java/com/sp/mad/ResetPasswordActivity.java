package com.sp.mad;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;

public class ResetPasswordActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reset_pwd); // Ensure this matches your XML layout file name

        // Find the "Log In" TextView
        TextView logInTextView = findViewById(R.id.log_in);

        // Set an onClickListener for navigation to the LoginPage activity
        logInTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Navigate to the LoginPage activity
                Intent intent = new Intent(ResetPasswordActivity.this, LoginPage.class);
                startActivity(intent);
                finish(); // Optionally close the ResetPasswordActivity
            }
        });
    }
}
