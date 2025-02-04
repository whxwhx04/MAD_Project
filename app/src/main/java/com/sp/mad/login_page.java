package com.sp.mad;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class login_page extends AppCompatActivity {

    private EditText studentIdEditText, studentEmailEditText, passwordEditText;
    private Button loginButton, guestButton;
    private ImageView loginImageView;
    private TextView forgotPasswordTextView, signUpTextView;

    private FirebaseAuth mAuth;  // Firebase Authentication instance

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login_page);

        // Initialize Firebase Authentication
        mAuth = FirebaseAuth.getInstance();

        // Initialize UI components
        studentIdEditText = findViewById(R.id.student_id2);
        studentEmailEditText = findViewById(R.id.student_email);
        passwordEditText = findViewById(R.id.password);
        loginButton = findViewById(R.id.signup_button);
        loginImageView = findViewById(R.id.login);
        forgotPasswordTextView = findViewById(R.id.log_in);
        signUpTextView = findViewById(R.id.signup);
        guestButton = findViewById(R.id.guest);

        // Login button click listener
        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loginUser();
            }
        });

        // Forgot Password navigation
        forgotPasswordTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(login_page.this, reset_pwd.class));
            }
        });

        // Sign Up navigation
        signUpTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(login_page.this, create_account.class));
            }
        });

        // Guest mode navigation
        guestButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(login_page.this, mainpage.class));
            }
        });
    }

    private void loginUser() {
        String studentId = studentIdEditText.getText().toString().trim();
        String email = studentEmailEditText.getText().toString().trim();
        String password = passwordEditText.getText().toString().trim();

        // Validation checks
        if (studentId.isEmpty()) {
            studentIdEditText.setError("Please enter your Student ID");
            return;
        }
        if (email.isEmpty()) {
            studentEmailEditText.setError("Please enter your Student Email");
            return;
        }
        if (password.isEmpty()) {
            passwordEditText.setError("Please enter your Password");
            return;
        }

        // Firebase Authentication - Sign in with email and password
        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {
                        FirebaseUser user = mAuth.getCurrentUser();
                        if (user != null) {
                            Toast.makeText(login_page.this, "Login Successful!", Toast.LENGTH_SHORT).show();
                            startActivity(new Intent(login_page.this, mainpage.class));
                            finish();
                        }
                    } else {
                        Toast.makeText(login_page.this, "Invalid Email or Password", Toast.LENGTH_SHORT).show();
                    }
                });
    }
}


