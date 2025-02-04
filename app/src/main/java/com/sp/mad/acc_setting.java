package com.sp.mad;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.SetOptions;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.HashMap;
import java.util.Map;

public class acc_setting extends AppCompatActivity {

    private EditText userName, userPwd, confirmPwd, accCourse;
    private Spinner accSch;
    private Button updateAccount, logout, uploadPfp, changePwd;
    private ImageButton backAcc;
    private ImageView profileImage;

    private FirebaseAuth mAuth;
    private FirebaseFirestore db;
    private FirebaseStorage storage;
    private FirebaseUser currentUser;

    private static final int PICK_IMAGE_REQUEST = 1;
    private Uri imageUri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_acc_setting);

        // Initialize Firebase
        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
        storage = FirebaseStorage.getInstance();
        currentUser = mAuth.getCurrentUser();

        if (currentUser == null) {
            Toast.makeText(this, "User not logged in!", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        // Bind UI components
        userName = findViewById(R.id.user_name);
        userPwd = findViewById(R.id.user_email);
        accSch = findViewById(R.id.acc_sch);
        accCourse = findViewById(R.id.acc_course);
        updateAccount = findViewById(R.id.acc_update);
        logout = findViewById(R.id.log_out);
        uploadPfp = findViewById(R.id.pfp_up);
        backAcc = findViewById(R.id.back_acc);
        profileImage = findViewById(R.id.acc_set);
        changePwd = findViewById(R.id.change_pwd);

        // Back button to Profile Page
        backAcc.setOnClickListener(v -> finish());

        // Update account details in Firestore
        updateAccount.setOnClickListener(v -> updateAccountInfo());

        // Change password in Firebase Authentication
        changePwd.setOnClickListener(v -> sendPasswordResetEmail());

        // Upload Profile Picture
        uploadPfp.setOnClickListener(v -> openFileChooser());

        // Log out user
        logout.setOnClickListener(v -> logoutUser());
    }

    // Function to update user info in Firestore
    private void updateAccountInfo() {
        String name = userName.getText().toString().trim();
        String school = accSch.getSelectedItem().toString();
        String course = accCourse.getText().toString().trim();

        if (name.isEmpty() || school.isEmpty() || course.isEmpty()) {
            Toast.makeText(this, "All fields must be filled!", Toast.LENGTH_SHORT).show();
            return;
        }

        String userId = currentUser.getUid();
        DocumentReference userRef = db.collection("users").document(userId);

        Map<String, Object> userInfo = new HashMap<>();
        userInfo.put("username", name);
        userInfo.put("school", school);
        userInfo.put("course", course);

        // Use set() with merge to avoid "No document to update" errors
        userRef.set(userInfo, SetOptions.merge())
                .addOnSuccessListener(aVoid -> {
                    Toast.makeText(acc_setting.this, "Account updated successfully!", Toast.LENGTH_SHORT).show();

                    // Update Firebase Authentication display name
                    UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
                            .setDisplayName(name)
                            .build();
                    currentUser.updateProfile(profileUpdates);
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(acc_setting.this, "Update failed: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }

    // Function to send password reset email
    private void sendPasswordResetEmail() {
        String email = userPwd.getText().toString().trim();

        if (email.isEmpty()) {
            Toast.makeText(this, "Please enter your email address", Toast.LENGTH_SHORT).show();
            return;
        }

        mAuth.sendPasswordResetEmail(email)
                .addOnSuccessListener(aVoid -> Toast.makeText(acc_setting.this, "Password reset email sent!", Toast.LENGTH_SHORT).show())
                .addOnFailureListener(e -> Toast.makeText(acc_setting.this, "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show());
    }

    // Open file chooser for profile picture
    private void openFileChooser() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(intent, PICK_IMAGE_REQUEST);
    }

    // Handle selected image
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {
            imageUri = data.getData();
            profileImage.setImageURI(imageUri);
            uploadProfilePicture();
        }
    }

    // Upload profile picture to Firebase Storage
    private void uploadProfilePicture() {
        if (imageUri == null) {
            Toast.makeText(this, "No image selected!", Toast.LENGTH_SHORT).show();
            return;
        }

        ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Uploading...");
        progressDialog.show();

        String userId = currentUser.getUid();
        StorageReference fileRef = storage.getReference("profile_pictures/" + userId + ".jpg");

        fileRef.putFile(imageUri)
                .addOnSuccessListener(taskSnapshot -> fileRef.getDownloadUrl().addOnSuccessListener(uri -> {
                    progressDialog.dismiss();
                    Toast.makeText(acc_setting.this, "Upload successful!", Toast.LENGTH_SHORT).show();
                    saveProfilePictureUri(uri.toString());
                }))
                .addOnFailureListener(e -> {
                    progressDialog.dismiss();
                    Toast.makeText(acc_setting.this, "Upload failed!", Toast.LENGTH_SHORT).show();
                });
    }

    // Save profile picture URL in Firestore
    private void saveProfilePictureUri(String uri) {
        String userId = currentUser.getUid();
        DocumentReference userRef = db.collection("users").document(userId);
        userRef.update("profilePicture", uri)
                .addOnSuccessListener(aVoid -> Toast.makeText(acc_setting.this, "Profile picture updated!", Toast.LENGTH_SHORT).show())
                .addOnFailureListener(e -> Toast.makeText(acc_setting.this, "Failed to update profile picture!", Toast.LENGTH_SHORT).show());
    }

    // Log out user
    private void logoutUser() {
        mAuth.signOut();
        startActivity(new Intent(acc_setting.this, login_page.class));
        finish();
    }
}


