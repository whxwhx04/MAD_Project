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
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.HashMap;
import java.util.Map;

public class create_post extends AppCompatActivity {
    private ImageView uploadedImage;
    private EditText postDescription;
    private Button uploadImageBtn, createPostBtn;
    private Spinner postSchool, postCourse;
    private ImageButton backBtn;
    private Uri imageUri;
    private FirebaseAuth mAuth;
    private FirebaseFirestore db;
    private FirebaseStorage storage;
    private FirebaseUser currentUser;

    private static final int IMAGE_PICK_CODE = 1000;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_post);
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
        // Initialize UI components
        uploadedImage = findViewById(R.id.uploaded_img);
        postDescription = findViewById(R.id.post_des);
        postSchool = findViewById(R.id.post_sch);
        postCourse = findViewById(R.id.post_course);
        uploadImageBtn = findViewById(R.id.post_img_up);
        createPostBtn = findViewById(R.id.create_post);
        backBtn = findViewById(R.id.back_comm);
        // Back button logic
        backBtn.setOnClickListener(v -> finish());
        // Set click listeners
        uploadImageBtn.setOnClickListener(view -> pickImage());
        createPostBtn.setOnClickListener(view -> {
            if (validateInputs()) {
                uploadImageAndSaveListing();
            }
        });
    }

    // Opens gallery to pick an image
    private void pickImage() {
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType("image/*");
        startActivityForResult(intent, IMAGE_PICK_CODE);
    }

    // Handles image selection result
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == IMAGE_PICK_CODE && resultCode == RESULT_OK && data != null) {
            imageUri = data.getData();
            uploadedImage.setImageURI(imageUri);
        }
    }
    private boolean validateInputs() {
        if (postDescription.getText().toString().trim().isEmpty() ||
                postSchool.getSelectedItem().toString().equals("Select School") ||
                postCourse.getSelectedItem().toString().equals("Select Course")) {
            Toast.makeText(this, "Please fill in all fields", Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }
    // Uploads image first, then stores listing data
    private void uploadImageAndSaveListing() {
        if (imageUri == null) {
            Toast.makeText(this, "Please upload an image", Toast.LENGTH_SHORT).show();
            return;
        }

        ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Uploading...");
        progressDialog.show();

        String postId = db.collection("posts").document().getId(); // Generate unique listing ID
        StorageReference fileRef = storage.getReference("post_images/" + postId + ".jpg"); // Use listing ID as filename

        fileRef.putFile(imageUri)
                .addOnSuccessListener(taskSnapshot -> fileRef.getDownloadUrl().addOnSuccessListener(uri -> {
                    progressDialog.dismiss();
                    savePostToFirestore(postId, uri.toString()); // Save image URL & listing data
                }))
                .addOnFailureListener(e -> {
                    progressDialog.dismiss();
                    Toast.makeText(this, "Image upload failed: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }
        // Saves post details to Firestore
        private void savePostToFirestore(String postId, String imageUrl) {
            Map<String, Object> listingData = new HashMap<>();
            listingData.put("id", postId);
            listingData.put("userId", currentUser.getUid());
            listingData.put("description", postDescription.getText().toString().trim());
            listingData.put("school", postSchool.getSelectedItem().toString());
            listingData.put("course", postCourse.getSelectedItem().toString());
            listingData.put("imageUrl", imageUrl);  // Ensure image URL is stored

            DocumentReference listingRef = db.collection("posts").document(postId);
            listingRef.set(listingData)
                    .addOnSuccessListener(aVoid -> {
                        Toast.makeText(this, "Post created successfully!", Toast.LENGTH_SHORT).show();
                        finish();
                    })
                    .addOnFailureListener(e -> Toast.makeText(this, "Failed to create post", Toast.LENGTH_SHORT).show());
    }
}