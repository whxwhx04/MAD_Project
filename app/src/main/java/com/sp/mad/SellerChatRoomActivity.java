package com.sp.mad;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import java.util.ArrayList;
import java.util.List;

public class SellerChatRoomActivity extends AppCompatActivity {
    private RecyclerView recyclerView;
    private ChatAdapter chatAdapter;
    private List<Message> messageList;
    private EditText messageInput;
    private ImageView sendButton;
    private String sellerId;
    private String buyerId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.seller_chat_room);

        // Get IDs from Intent
        Intent intent = getIntent();
        sellerId = intent.getStringExtra("currentUserId");
        buyerId = intent.getStringExtra("otherUserId");

        // Initialize RecyclerView (fixing incorrect ID)
        recyclerView = findViewById(R.id.seller_recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        messageList = new ArrayList<>();
        chatAdapter = new ChatAdapter(messageList, sellerId);
        recyclerView.setAdapter(chatAdapter);

        // Initialize input fields (fixing incorrect IDs)
        messageInput = findViewById(R.id.seller_messageInput);
        sendButton = findViewById(R.id.btn_sellerSendMsg);

        // Back Button functionality
        ImageView backBtn = findViewById(R.id.sellerChat_backBtn);
        backBtn.setOnClickListener(view -> {
            finish(); // Close activity and go back
        });

        // Send message on button click
        sendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendMessage();
            }
        });
    }

    private void sendMessage() {
        String messageText = messageInput.getText().toString().trim();
        if (!messageText.isEmpty()) {
            Message message = new Message(sellerId, messageText, System.currentTimeMillis());
            messageList.add(message);
            chatAdapter.notifyItemInserted(messageList.size() - 1);
            recyclerView.scrollToPosition(messageList.size() - 1);
            messageInput.setText("");
        }
    }
}
