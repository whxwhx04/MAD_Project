package com.sp.mad;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class BuyerChatRoomActivity extends AppCompatActivity {

    private static final int PICK_IMAGE_REQUEST = 1;

    private RecyclerView recyclerView;
    private ChatAdapter chatAdapter;
    private List<Message> messagesList;
    private FirebaseFirestore db;
    private FirebaseStorage storage;
    private String buyerId, sellerId, itemId, currentUserId, chatId;
    private EditText messageInput;
    private ImageView btnSendMessage, btnUploadImage, itemImageView, btnBack;
    private Uri imageUri;
    private TextView sellerNameTextView, sellerSchoolTextView, sellerCourseTextView, itemPriceTextView, itemConditionTextView, itemNameTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.buyer_chat_room);

        // Initialize UI components
        recyclerView = findViewById(R.id.recycler_view);
        messageInput = findViewById(R.id.messageInput);
        btnSendMessage = findViewById(R.id.btn_sendMsg);
        btnUploadImage = findViewById(R.id.btn_uploadImage);
        btnBack = findViewById(R.id.chatroom_backBtn);
        sellerNameTextView = findViewById(R.id.seller_account_name);
        itemNameTextView = findViewById(R.id.item_name);
        itemConditionTextView = findViewById(R.id.item_condition);
        itemPriceTextView = findViewById(R.id.item_price);
        sellerSchoolTextView = findViewById(R.id.seller_school);
        sellerCourseTextView = findViewById(R.id.seller_course);
        itemImageView = findViewById(R.id.item_pic);

        db = FirebaseFirestore.getInstance();
        storage = FirebaseStorage.getInstance();
        messagesList = new ArrayList<>();

        // Get data from Intent
        buyerId = getIntent().getStringExtra("buyerId");
        sellerId = getIntent().getStringExtra("sellerId");
        itemId = getIntent().getStringExtra("itemId");

        currentUserId = buyerId; // Buyer is the sender

        // Fetch and update seller name
        fetchSellerDetails(sellerId);
        fetchItemDetails(itemId);

        getOrCreateChatId(); // Fetch or create chat_id

        btnBack.setOnClickListener(v -> finish());

        btnSendMessage.setOnClickListener(v -> sendMessage());

        btnUploadImage.setOnClickListener(v -> openFileChooser());
    }

    private void fetchItemDetails(String itemId) {
        db.collection("listing_items").document(itemId).get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        String itemName = documentSnapshot.getString("itemName");
                        String itemCondition = documentSnapshot.getString("condition");
                        String itemPrice = documentSnapshot.getString("price");
                        String sellerSchool = documentSnapshot.getString("school");
                        String sellerCourse = documentSnapshot.getString("course");
                        String itemImageUrl = documentSnapshot.getString("imageUrl");
                        sellerId = documentSnapshot.getString("userId");

                        // Update UI with fetched data
                        itemNameTextView.setText(itemName != null ? itemName : "N/A");
                        itemConditionTextView.setText(itemCondition != null ? itemCondition : "N/A");
                        itemPriceTextView.setText(itemPrice != null ? "$" + itemPrice : "$0");
                        sellerSchoolTextView.setText(sellerSchool != null ? sellerSchool + " - " : "N/A - ");
                        sellerCourseTextView.setText(sellerCourse != null ? sellerCourse : "N/A");

                        // Load item image
                        if (itemImageUrl != null && !itemImageUrl.isEmpty()) {
                            Glide.with(this).load(itemImageUrl).into(itemImageView);
                        } else {
                            itemImageView.setImageResource(R.drawable.placeholder_image);
                        }

                        // Fetch seller details
                        if (sellerId != null) {
                            fetchSellerDetails(sellerId);
                        }
                    } else {
                        Toast.makeText(this, "Item details not found", Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(this, "Failed to fetch item details: " + e.getMessage(), Toast.LENGTH_LONG).show();
                });
    }

    private void fetchSellerDetails(String sellerId) {
        if (sellerId == null || sellerId.isEmpty()) {
            Toast.makeText(this, "Seller ID is missing", Toast.LENGTH_SHORT).show();
            return;
        }

        db.collection("users").document(sellerId).get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        // Use "username" instead of "name"
                        String sellerName = documentSnapshot.getString("username");

                        if (sellerName != null && !sellerName.isEmpty()) {
                            sellerNameTextView.setText(sellerName);
                        } else {
                            sellerNameTextView.setText("Seller Name Not Found");
                        }
                    } else {
                        sellerNameTextView.setText("Seller Not Found");
                    }
                })
                .addOnFailureListener(e -> {
                    sellerNameTextView.setText("Error Fetching Seller");
                    Toast.makeText(this, "Failed to fetch seller: " + e.getMessage(), Toast.LENGTH_LONG).show();
                });
    }



    private void setupChat() {
        chatAdapter = new ChatAdapter(this, messagesList, currentUserId);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(chatAdapter);
        loadMessages();
    }

    private void loadMessages() {
        db.collection("chats").document(chatId)
                .collection("messages")
                .orderBy("timestamp", Query.Direction.ASCENDING)
                .addSnapshotListener((snapshots, e) -> {
                    if (e != null) {
                        Toast.makeText(BuyerChatRoomActivity.this, "Failed to load messages", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    messagesList.clear();
                    if (snapshots != null) {
                        for (DocumentSnapshot document : snapshots.getDocuments()) {
                            Message message = document.toObject(Message.class);
                            messagesList.add(message);
                        }
                    }

                    chatAdapter.notifyDataSetChanged();
                    if (!messagesList.isEmpty()) {
                        recyclerView.smoothScrollToPosition(messagesList.size() - 1);
                    }
                });
    }

    private void getOrCreateChatId() {
        db.collection("listing_items").document(itemId)
                .collection("chat_id")
                .whereEqualTo("buyerId", buyerId)
                .whereEqualTo("sellerId", sellerId)
                .limit(1) // Check if the chat already exists
                .get()
                .addOnSuccessListener(querySnapshot -> {
                    if (!querySnapshot.isEmpty()) {
                        // Use existing chat_id
                        chatId = querySnapshot.getDocuments().get(0).getId();
                    } else {
                        // Create a new chat_id
                        chatId = db.collection("chats").document().getId();

                        // Store the chat_id in listing_items/{itemId}/chat_id/{chatId}
                        HashMap<String, Object> chatMetadata = new HashMap<>();
                        chatMetadata.put("buyerId", buyerId);
                        chatMetadata.put("sellerId", sellerId);
                        chatMetadata.put("itemId", itemId);

                        db.collection("listing_items").document(itemId)
                                .collection("chat_id")
                                .document(chatId)
                                .set(chatMetadata) // Store buyerId, sellerId, and itemId in listing_items as well
                                .addOnFailureListener(e ->
                                        Toast.makeText(this, "Failed to store chat metadata", Toast.LENGTH_SHORT).show()
                                );

                        // Store the same metadata inside chats/{chatId}
                        db.collection("chats").document(chatId)
                                .set(chatMetadata) // Ensure chat document stores buyerId, sellerId, and itemId
                                .addOnFailureListener(e ->
                                        Toast.makeText(this, "Failed to initialize chat", Toast.LENGTH_SHORT).show()
                                );
                    }
                    setupChat(); // Proceed with chat setup once chatId is ready
                })
                .addOnFailureListener(e ->
                        Toast.makeText(this, "Failed to fetch chat ID", Toast.LENGTH_SHORT).show()
                );
    }



    private void sendMessage() {
        String text = messageInput.getText().toString().trim();
        if (text.isEmpty()) return;

        Message message = new Message(
                buyerId, sellerId, text, buyerId, "", System.currentTimeMillis(), itemId
        );

        db.collection("chats").document(chatId)
                .collection("messages")
                .add(message)
                .addOnSuccessListener(documentReference -> messageInput.setText(""))
                .addOnFailureListener(e -> Toast.makeText(this, "Failed to send message", Toast.LENGTH_SHORT).show());
    }

    private void openFileChooser() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select Image"), PICK_IMAGE_REQUEST);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == Activity.RESULT_OK && data != null && data.getData() != null) {
            imageUri = data.getData();
            uploadImageToFirebase();
        }
    }

    private void uploadImageToFirebase() {
        if (imageUri == null) return;

        StorageReference storageRef = storage.getReference("chat_images/" + System.currentTimeMillis() + ".jpg");
        storageRef.putFile(imageUri)
                .addOnSuccessListener(taskSnapshot -> storageRef.getDownloadUrl().addOnSuccessListener(uri -> {
                    sendImageMessage(uri.toString());
                }))
                .addOnFailureListener(e -> Toast.makeText(this, "Image upload failed", Toast.LENGTH_SHORT).show());
    }

    private void sendImageMessage(String imageUrl) {
        Message message = new Message(
                buyerId, sellerId, "", buyerId, imageUrl, System.currentTimeMillis(), itemId
        );

        db.collection("chats").document(chatId)
                .collection("messages")
                .add(message)
                .addOnSuccessListener(documentReference -> Toast.makeText(this, "Image sent", Toast.LENGTH_SHORT).show())
                .addOnFailureListener(e -> Toast.makeText(this, "Failed to send image", Toast.LENGTH_SHORT).show());
    }
}
