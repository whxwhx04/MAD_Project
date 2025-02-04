package com.sp.mad;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;

public class reset_pwd extends AppCompatActivity {

    private EditText emailInput;
    private Button sendResetLinkButton;
    private TextView logInTextView;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reset_pwd);

        // Initialize Firebase Auth
        mAuth = FirebaseAuth.getInstance();

        // Link UI elements
        emailInput = findViewById(R.id.email);
        sendResetLinkButton = findViewById(R.id.signup_button); // Send reset link button
        logInTextView = findViewById(R.id.log_in); // Navigate back to login

        // Send reset link when button is clicked
        sendResetLinkButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendPasswordReset();
            }
        });

        // Navigate to login page when "Already Have Account? Log In" is clicked
        logInTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(reset_pwd.this, login_page.class));
                finish(); // Close this activity
            }
        });
    }

    private void sendPasswordReset() {
        String email = emailInput.getText().toString().trim();

        if (email.isEmpty()) {
            Toast.makeText(this, "Enter your student email!", Toast.LENGTH_SHORT).show();
            return;
        }

        mAuth.sendPasswordResetEmail(email)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        Toast.makeText(reset_pwd.this, "Reset link sent to your email!", Toast.LENGTH_LONG).show();
                        startActivity(new Intent(reset_pwd.this, login_page.class)); // Redirect to login after sending
                        finish();
                    } else {
                        Toast.makeText(reset_pwd.this, "Failed to send reset email!", Toast.LENGTH_SHORT).show();
                    }
                });
    }
}

