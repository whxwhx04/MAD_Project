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
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class create_account extends AppCompatActivity {

    private EditText nameInput, studentIdInput, emailInput, passwordInput, confirmPasswordInput;
    private Button registerButton;
    private TextView LogInTextView;

    private FirebaseAuth mAuth;
    private DatabaseReference mDatabase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_account);

        // Initialize Firebase Authentication
        mAuth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance().getReference("users"); // Firebase Realtime Database reference

        // Initialize UI elements
        nameInput = findViewById(R.id.name);
        studentIdInput = findViewById(R.id.student_id2);
        emailInput = findViewById(R.id.email);
        passwordInput = findViewById(R.id.password);
        confirmPasswordInput = findViewById(R.id.cfm_password);
        registerButton = findViewById(R.id.signup_button);
        LogInTextView = findViewById(R.id.log_in);

        // Set button click listener for registration
        registerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                registerUser();
            }
        });

        // Set click listener for Login navigation
        LogInTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(create_account.this, login_page.class));
            }
        });
    }

    private void registerUser() {
        String name = nameInput.getText().toString().trim();
        String studentId = studentIdInput.getText().toString().trim();
        String email = emailInput.getText().toString().trim();
        String password = passwordInput.getText().toString().trim();
        String confirmPassword = confirmPasswordInput.getText().toString().trim();

        // Input validation
        if (name.isEmpty() || studentId.isEmpty() || email.isEmpty() || password.isEmpty() || confirmPassword.isEmpty()) {
            Toast.makeText(this, "All fields are required!", Toast.LENGTH_SHORT).show();
            return;
        }
        if (!password.equals(confirmPassword)) {
            Toast.makeText(this, "Passwords do not match!", Toast.LENGTH_SHORT).show();
            return;
        }
        if (studentId.length() != 8) {
            Toast.makeText(this, "Student ID must be 8 characters!", Toast.LENGTH_SHORT).show();
            return;
        }
        if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            Toast.makeText(this, "Enter a valid email!", Toast.LENGTH_SHORT).show();
            return;
        }
        if (password.length() < 8) {
            Toast.makeText(this, "Password must be at least 8 characters!", Toast.LENGTH_SHORT).show();
            return;
        }

        // Firebase Authentication - Create user with email and password
        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {
                        // Get Firebase user
                        FirebaseUser firebaseUser = mAuth.getCurrentUser();
                        if (firebaseUser != null) {
                            String userId = firebaseUser.getUid(); // Unique Firebase user ID

                            // Store user details in Firebase Realtime Database
                            User user = new User(name, studentId, email);
                            mDatabase.child(userId).setValue(user)
                                    .addOnCompleteListener(task1 -> {
                                        if (task1.isSuccessful()) {
                                            Toast.makeText(create_account.this, "Account created successfully!", Toast.LENGTH_LONG).show();
                                            startActivity(new Intent(create_account.this, login_page.class));
                                            finish();
                                        } else {
                                            Toast.makeText(create_account.this, "Failed to save user details.", Toast.LENGTH_SHORT).show();
                                        }
                                    });
                        }
                    } else {
                        Toast.makeText(create_account.this, "Registration failed: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }

    // User class to store user details in Firebase
    public static class User {
        public String name, studentId, email;

        public User() {
            // Default constructor required for Firebase
        }

        public User(String name, String studentId, String email) {
            this.name = name;
            this.studentId = studentId;
            this.email = email;
        }
    }
}





