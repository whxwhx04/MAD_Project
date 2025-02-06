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

public class edit_listing extends AppCompatActivity {

    private EditText itemName, itemPrice, itemDescription;
    private Spinner itemCondition, itemSchool, itemCourse;
    private Button updateListing, uploadImage;
    private ImageButton backEdit;
    private ImageView itemImage;

    private FirebaseAuth mAuth;
    private FirebaseFirestore db;
    private FirebaseStorage storage;
    private FirebaseUser currentUser;

    private Uri imageUri;
    private String listingId, imageUrl;

    private static final int PICK_IMAGE_REQUEST = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_listing);

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
        itemName = findViewById(R.id.edit_name);
        itemPrice = findViewById(R.id.edit_price);
        itemDescription = findViewById(R.id.edit_des);
        itemCondition = findViewById(R.id.edit_condi);
        itemSchool = findViewById(R.id.edit_school);
        itemCourse = findViewById(R.id.edit_course);
        updateListing = findViewById(R.id.edit_list);
        uploadImage = findViewById(R.id.update_img_up);
        backEdit = findViewById(R.id.back_main3);
        itemImage = findViewById(R.id.uploaded_image);

        // Retrieve listing data from intent
        listingId = getIntent().getStringExtra("itemId");
        imageUrl = getIntent().getStringExtra("imageUrl");

        loadListingDetails();

        // Back button to previous page
        backEdit.setOnClickListener(v -> finish());

        // Update listing details in Firestore
        updateListing.setOnClickListener(v -> updateListingInfo());

        // Upload new image
        uploadImage.setOnClickListener(v -> openFileChooser());
    }

    // Load existing listing details into UI
    private void loadListingDetails() {
        DocumentReference listingRef = db.collection("listing_items").document(listingId);
        listingRef.get().addOnSuccessListener(documentSnapshot -> {
            if (documentSnapshot.exists()) {
                itemName.setText(documentSnapshot.getString("itemName"));
                itemPrice.setText(documentSnapshot.getString("price"));
                itemDescription.setText(documentSnapshot.getString("description"));
                // Set spinners for condition, school, and course accordingly
                // Load image using Glide or Picasso (if applicable)
            }
        }).addOnFailureListener(e -> Toast.makeText(edit_listing.this, "Error loading listing", Toast.LENGTH_SHORT).show());
    }

    // Function to update listing info in Firestore
    private void updateListingInfo() {
        String name = itemName.getText().toString().trim();
        String price = itemPrice.getText().toString().trim();
        String description = itemDescription.getText().toString().trim();
        String condition = itemCondition.getSelectedItem().toString();
        String school = itemSchool.getSelectedItem().toString();
        String course = itemCourse.getSelectedItem().toString();

        if (name.isEmpty() || price.isEmpty() || description.isEmpty()) {
            Toast.makeText(this, "All fields must be filled!", Toast.LENGTH_SHORT).show();
            return;
        }

        Map<String, Object> listingData = new HashMap<>();
        listingData.put("itemName", name);
        listingData.put("price", price);
        listingData.put("description", description);
        listingData.put("condition", condition);
        listingData.put("school", school);
        listingData.put("course", course);

        db.collection("listing_items").document(listingId).update(listingData)
                .addOnSuccessListener(aVoid -> Toast.makeText(edit_listing.this, "Listing updated successfully!", Toast.LENGTH_SHORT).show())
                .addOnFailureListener(e -> Toast.makeText(edit_listing.this, "Update failed: " + e.getMessage(), Toast.LENGTH_SHORT).show());
    }

    // Open file chooser for listing image
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
            itemImage.setImageURI(imageUri);
            uploadListingImage();
        }
    }

    // Upload new image to Firebase Storage
    private void uploadListingImage() {
        if (imageUri == null) {
            Toast.makeText(this, "No image selected!", Toast.LENGTH_SHORT).show();
            return;
        }

        ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Uploading...");
        progressDialog.show();

        StorageReference fileRef = storage.getReference("listing_images/" + listingId + ".jpg");

        fileRef.putFile(imageUri)
                .addOnSuccessListener(taskSnapshot -> fileRef.getDownloadUrl().addOnSuccessListener(uri -> {
                    progressDialog.dismiss();
                    Toast.makeText(edit_listing.this, "Upload successful!", Toast.LENGTH_SHORT).show();
                    updateListingImageUri(uri.toString());
                }))
                .addOnFailureListener(e -> {
                    progressDialog.dismiss();
                    Toast.makeText(edit_listing.this, "Upload failed!", Toast.LENGTH_SHORT).show();
                });
    }

    // Update image URL in Firestore
    private void updateListingImageUri(String uri) {
        db.collection("listing_items").document(listingId).update("imageUrl", uri)
                .addOnSuccessListener(aVoid -> Toast.makeText(edit_listing.this, "Image updated!", Toast.LENGTH_SHORT).show())
                .addOnFailureListener(e -> Toast.makeText(edit_listing.this, "Failed to update image!", Toast.LENGTH_SHORT).show());
    }
}
