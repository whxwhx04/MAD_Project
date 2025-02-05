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

public class create_listing extends AppCompatActivity {

    private ImageView uploadedImage;
    private EditText listName, listDescription, listPrice;
    private Spinner catSchool, catCourse, listCondition;
    private Button uploadImageBtn, createListBtn;
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
        setContentView(R.layout.activity_create_listing);

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
        uploadedImage = findViewById(R.id.uploaded_image);
        listName = findViewById(R.id.list_name);
        listDescription = findViewById(R.id.list_des);
        listPrice = findViewById(R.id.list_price);
        catSchool = findViewById(R.id.cat_school);
        catCourse = findViewById(R.id.cat_course);
        listCondition = findViewById(R.id.list_condi);
        uploadImageBtn = findViewById(R.id.list_img_up);
        createListBtn = findViewById(R.id.create_list);
        backBtn = findViewById(R.id.back_main2);

        // Back button to previous activity
        backBtn.setOnClickListener(v -> finish());

        // Set click listeners
        uploadImageBtn.setOnClickListener(view -> pickImage());
        createListBtn.setOnClickListener(view -> {
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

    // Validates user input before creating listing
    private boolean validateInputs() {
        if (listName.getText().toString().trim().isEmpty() ||
                listDescription.getText().toString().trim().isEmpty() ||
                listPrice.getText().toString().trim().isEmpty() ||
                catSchool.getSelectedItem().toString().equals("Select School") ||
                catCourse.getSelectedItem().toString().equals("Select Course") ||
                listCondition.getSelectedItem().toString().equals("Select Condition")) {
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

        String listingId = db.collection("listing_items").document().getId(); // Generate unique listing ID
        StorageReference fileRef = storage.getReference("listing_images/" + listingId + ".jpg"); // Use listing ID as filename

        fileRef.putFile(imageUri)
                .addOnSuccessListener(taskSnapshot -> fileRef.getDownloadUrl().addOnSuccessListener(uri -> {
                    progressDialog.dismiss();
                    saveListingToFirestore(listingId, uri.toString()); // Save image URL & listing data
                }))
                .addOnFailureListener(e -> {
                    progressDialog.dismiss();
                    Toast.makeText(this, "Image upload failed: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }

    // Saves listing details to Firestore
    private void saveListingToFirestore(String listingId, String imageUrl) {
        Map<String, Object> listingData = new HashMap<>();
        listingData.put("id", listingId);
        listingData.put("userId", currentUser.getUid());
        listingData.put("itemName", listName.getText().toString().trim());
        listingData.put("description", listDescription.getText().toString().trim());
        listingData.put("price", listPrice.getText().toString().trim());
        listingData.put("school", catSchool.getSelectedItem().toString());
        listingData.put("course", catCourse.getSelectedItem().toString());
        listingData.put("condition", listCondition.getSelectedItem().toString());
        listingData.put("imageUrl", imageUrl);  // Ensure image URL is stored

        DocumentReference listingRef = db.collection("listing_items").document(listingId);
        listingRef.set(listingData)
                .addOnSuccessListener(aVoid -> {
                    Toast.makeText(this, "Listing created successfully!", Toast.LENGTH_SHORT).show();
                    finish();
                })
                .addOnFailureListener(e -> Toast.makeText(this, "Failed to create listing", Toast.LENGTH_SHORT).show());
    }
}
