package com.sp.mad;

import android.os.Bundle;
import android.view.MenuItem;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.auth.FirebaseAuth;

import java.util.ArrayList;
import java.util.List;

public class saved_items extends AppCompatActivity {

    private RecyclerView recyclerViewSavedItems;
    private MyAdapter myAdapter;
    private List<Item> savedItemsList;
    private String currentUserId; // Store current user ID
    private ImageButton backButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_saved_items); // Reference to your saved_items.xml layout

        // Initialize views
        recyclerViewSavedItems = findViewById(R.id.recylerViewSavedItems);
        backButton = findViewById(R.id.btn_back3);

        // Set up RecyclerView with GridLayoutManager (same as main page)
        recyclerViewSavedItems.setLayoutManager(new GridLayoutManager(this, 2));  // 2 columns for grid
        savedItemsList = new ArrayList<>();
        myAdapter = new MyAdapter(savedItemsList, currentUserId);
        recyclerViewSavedItems.setAdapter(myAdapter);

        // Get the current user ID from FirebaseAuth
        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        currentUserId = mAuth.getCurrentUser().getUid(); // Fetch the current user's UID

        // Fetch saved items for the current user
        fetchSavedItems();

        // Back button functionality
        backButton.setOnClickListener(view -> {
            finish();  // Go back to the previous screen
        });
    }

    private void fetchSavedItems() {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("users")
                .document(currentUserId)
                .collection("saved_items")
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    if (queryDocumentSnapshots != null && !queryDocumentSnapshots.isEmpty()) {
                        // Clear the list and add new saved items
                        savedItemsList.clear();
                        for (DocumentSnapshot document : queryDocumentSnapshots) {
                            // Extract itemId from the saved_items subcollection
                            String itemId = document.getString("itemId");
                            if (itemId != null) {
                                // Fetch the item details using the itemId
                                fetchItemDetails(itemId);
                            }
                        }
                    } else {
                        Toast.makeText(saved_items.this, "No saved items found.", Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(saved_items.this, "Failed to fetch saved items.", Toast.LENGTH_SHORT).show();
                });
    }

    private void fetchItemDetails(String itemId) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("listing_items")
                .document(itemId)
                .get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        // Get the item details
                        String userId = documentSnapshot.getString("userId");
                        String title = documentSnapshot.getString("itemName");
                        String price ="Price: $" + documentSnapshot.getString("price");
                        String imageUrl = documentSnapshot.getString("imageUrl");

                        // Now create an Item object with all the details
                        Item item = new Item(itemId, title, price, imageUrl, userId);

                        // Add the item to the list and notify the adapter
                        savedItemsList.add(item);
                        myAdapter.notifyItemInserted(savedItemsList.size() - 1);  // Notify adapter for the newly inserted item
                    }
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(saved_items.this, "Failed to fetch item details.", Toast.LENGTH_SHORT).show();
                });
    }
}
