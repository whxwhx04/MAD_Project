package com.sp.mad;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;

public class login_page extends AppCompatActivity {

    // UI Components
    private EditText studentIdEditText;
    private EditText passwordEditText;
    private Button loginButton;
    private ImageView loginImageView;
    private TextView rectangleTextView;
    private TextView forgotPasswordTextView;
    private TextView signUpTextView;
    private Button guestButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login_page); // Ensure this matches your XML file name

        // Initialize UI components
        studentIdEditText = findViewById(R.id.email);
        passwordEditText = findViewById(R.id.password);
        loginButton = findViewById(R.id.reset_pwd_button);
        loginImageView = findViewById(R.id.login);
        rectangleTextView = findViewById(R.id.rectangle);
        forgotPasswordTextView = findViewById(R.id.log_in); // Ensure ID matches your XML
        signUpTextView = findViewById(R.id.signup); // Ensure ID matches your XML
        guestButton = findViewById(R.id.guest);

        // Set an onClickListener for the login button
        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Get the input values
                String studentId = studentIdEditText.getText().toString().trim();
                String password = passwordEditText.getText().toString().trim();

                // Simple validation check
                if (studentId.isEmpty()) {
                    studentIdEditText.setError("Please enter your Student ID");
                } else if (password.isEmpty()) {
                    passwordEditText.setError("Please enter your Password");
                } else {
                    // Add login logic here
                    // Navigate to MainPage activity after successful login
                    // Intent intent = new Intent(LoginPage.this, MainPage.class);
                    // startActivity(intent);
                    // finish(); // Optionally finish the login activity
                }
            }
        });

        // Set an onClickListener for the "Forgot Password" text
        forgotPasswordTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Navigate to the Reset Password page
                Intent intent = new Intent(login_page.this, reset_pwd.class);
                startActivity(intent);
            }
        });

        // Set an onClickListener for the "Sign Up" text
        signUpTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Navigate to the Create Account page
                Intent intent = new Intent(login_page.this, create_account.class);
                startActivity(intent);
            }
        });

        // Set an onClickListener for the "Guest Mode" button
        guestButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Navigate to the Create Account page
                Intent intent = new Intent(login_page.this, mainpage.class);
                startActivity(intent);
            }
        });
    }
}
