package com.sp.mad;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.List;

public class SellerChatRoomActivity extends AppCompatActivity {

    private static final int PICK_IMAGE_REQUEST = 1;
    private Uri imageUri;
    private FirebaseStorage storage;
    private RecyclerView recyclerView;
    private ChatAdapter chatAdapter;
    private List<Message> messageList;
    private EditText messageInput;
    private ImageView sendButton, itemImageView, backBtn, btnUploadImage;

    private FirebaseFirestore db;
    private String chatId, itemId, sellerId, buyerId;
    private TextView BuyerNameTextView, sellerSchoolTextView, sellerCourseTextView,
            itemPriceTextView, itemConditionTextView, itemNameTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.seller_chat_room);

        // Retrieve chat ID and item ID from intent
        chatId = getIntent().getStringExtra("chatId");
        itemId = getIntent().getStringExtra("itemId");
        buyerId = getIntent().getStringExtra("buyerId");

        if (chatId == null) {
            Toast.makeText(this, "Error: Missing chat details", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        // Initialize Firebase and UI elements
        db = FirebaseFirestore.getInstance();
        storage = FirebaseStorage.getInstance();
        recyclerView = findViewById(R.id.seller_recycler_view);
        messageInput = findViewById(R.id.seller_messageInput);
        sendButton = findViewById(R.id.btn_sellerSendMsg);
        btnUploadImage = findViewById(R.id.btn_sellerUploadImage);
        backBtn = findViewById(R.id.sellerChat_backBtn);
        BuyerNameTextView = findViewById(R.id.buyer_account_name);
        itemNameTextView = findViewById(R.id.seller_item_name);
        itemConditionTextView = findViewById(R.id.seller_item_condition);
        itemPriceTextView = findViewById(R.id.seller_item_price);
        sellerSchoolTextView = findViewById(R.id.lister_school);
        sellerCourseTextView = findViewById(R.id.lister_course);
        itemImageView = findViewById(R.id.seller_item_pic);

        // Load seller ID from FirebaseAuth
        sellerId = getSellerId();

        messageList = new ArrayList<>();
        chatAdapter = new ChatAdapter(this, messageList, sellerId);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(chatAdapter);

        loadMessages();

        fetchBuyerName(buyerId);
        backBtn.setOnClickListener(v -> finish());

        // Send text message
        sendButton.setOnClickListener(v -> sendMessage());

        btnUploadImage.setOnClickListener(v -> openFileChooser());
    }

    private String getSellerId() {
        return FirebaseAuth.getInstance().getCurrentUser() != null
                ? FirebaseAuth.getInstance().getCurrentUser().getUid()
                : "seller123"; // Fallback dummy ID
    }

    private void fetchBuyerName(String buyerId) {
        if (buyerId == null || buyerId.isEmpty()) {
            BuyerNameTextView.setText("Unknown Buyer");
            return;
        }

        db.collection("users").document(buyerId)
                .get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        String buyerName = documentSnapshot.getString("username");
                        BuyerNameTextView.setText(buyerName != null ? buyerName : "Unknown Buyer");
                    } else {
                        BuyerNameTextView.setText("Unknown Buyer");
                    }
                })
                .addOnFailureListener(e -> Log.e("ChatDebug", "Failed to fetch buyer name", e));
    }


    private void loadMessages() {
        CollectionReference messagesRef = db.collection("chats").document(chatId).collection("messages");
        messagesRef.orderBy("timestamp", Query.Direction.ASCENDING)
                .addSnapshotListener((queryDocumentSnapshots, e) -> {
                    if (e != null) {
                        Toast.makeText(SellerChatRoomActivity.this, "Failed to load messages", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    messageList.clear();
                    for (QueryDocumentSnapshot doc : queryDocumentSnapshots) {
                        Message message = doc.toObject(Message.class);
                        messageList.add(message);
                    }
                    chatAdapter.notifyDataSetChanged();
                    recyclerView.scrollToPosition(messageList.size() - 1);
                });
    }

    private void sendMessage() {
        String text = messageInput.getText().toString().trim();
        if (text.isEmpty()) return;

        String sellerId = FirebaseAuth.getInstance().getCurrentUser().getUid();

        db.collection("chats").document(chatId)
                .get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        String buyerId = documentSnapshot.getString("buyerId");
                        if (buyerId == null || buyerId.isEmpty()) {
                            Toast.makeText(this, "Error: Buyer ID missing", Toast.LENGTH_SHORT).show();
                            return;
                        }

                        Message message = new Message(buyerId, sellerId, text, sellerId, "", System.currentTimeMillis(), itemId);

                        db.collection("chats").document(chatId)
                                .collection("messages")
                                .add(message)
                                .addOnSuccessListener(documentReference -> messageInput.setText(""))
                                .addOnFailureListener(e -> Toast.makeText(SellerChatRoomActivity.this, "Failed to send message", Toast.LENGTH_SHORT).show());
                    }
                });
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

