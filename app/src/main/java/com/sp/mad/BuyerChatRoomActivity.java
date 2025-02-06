package com.sp.mad;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import java.util.ArrayList;
import java.util.List;

public class BuyerChatRoomActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private ChatAdapter chatAdapter;
    private List<Message> messageList;
    private EditText messageInput;
    private ImageView sendButton, uploadImageButton;
    private String currentUserId = "buyer_id"; // Replace with actual buyer ID
    private String sellerId = "seller_id"; // Replace with actual seller ID
    private static final int PICK_IMAGE_REQUEST = 1; // Request code for picking an image
    private ImageView chatroom_backBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.buyer_chat_room);

        recyclerView = findViewById(R.id.recycler_view);
        messageInput = findViewById(R.id.messageInput);
        sendButton = findViewById(R.id.btn_sendMsg);
        uploadImageButton = findViewById(R.id.btn_uploadImage);
        chatroom_backBtn = findViewById(R.id.chatroom_backBtn);

        // Initialize the message list
        messageList = new ArrayList<>();
        chatAdapter = new ChatAdapter(messageList, currentUserId);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(chatAdapter);

        // Get the current user ID (Buyer) and the seller ID
        Intent intent = getIntent();
        if (intent != null) {
            currentUserId = intent.getStringExtra("currentUserId");
            sellerId = intent.getStringExtra("otherUserId");
        }

        // Set up the send button click listener
        sendButton.setOnClickListener(v -> sendMessage());

        // Set up the upload image button click listener
        uploadImageButton.setOnClickListener(v -> openImagePicker());

        // Set up the back button click listener
        chatroom_backBtn.setOnClickListener(v -> finish());
    }

    private void sendMessage() {
        String messageText = messageInput.getText().toString().trim();
        if (!messageText.isEmpty()) {
            long timestamp = System.currentTimeMillis();
            Message message = new Message(currentUserId, messageText, timestamp);
            messageList.add(message); // Add message to the in-memory list
            chatAdapter.notifyItemInserted(messageList.size() - 1); // Notify adapter of new message
            recyclerView.scrollToPosition(messageList.size() - 1); // Scroll to the latest message
            messageInput.setText(""); // Clear the input field
        }
    }

    private void openImagePicker() {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        intent.setType("image/*");
        startActivityForResult(intent, PICK_IMAGE_REQUEST); // Start image picker intent
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null) {
            Uri imageUri = data.getData(); // Get the image URI

            if (imageUri != null) {
                // Do something with the image, like uploading or displaying it in the chat
                // For now, we just show a Toast to indicate the image has been selected
                Toast.makeText(this, "Image selected: " + imageUri, Toast.LENGTH_SHORT).show();

                // You can send the image in the chat by converting it to base64 or using a file upload method
            }
        }
    }
}
