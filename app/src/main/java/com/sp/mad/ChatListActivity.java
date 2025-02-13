package com.sp.mad;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import java.util.ArrayList;
import java.util.List;
public class ChatListActivity extends AppCompatActivity {
    private RecyclerView recyclerView;
    private ChatListAdapter chatListAdapter;
    private List<ChatItem> chatList;
    private FirebaseFirestore db;
    private String itemId; // The item ID passed from seller's listing
    private ImageView backBtn;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat_list);
        backBtn = findViewById(R.id.chat_backBtn);
        recyclerView = findViewById(R.id.recyclerViewChatList);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        db = FirebaseFirestore.getInstance();
        chatList = new ArrayList<>();
        chatListAdapter = new ChatListAdapter(this, chatList);
        recyclerView.setAdapter(chatListAdapter);
        // Get itemId from intent
        itemId = getIntent().getStringExtra("itemId");
        if (itemId != null) {
            fetchChatIdsForItem(itemId);
        } else {
            Toast.makeText(this, "Item ID not found", Toast.LENGTH_SHORT).show();
            finish();
        }
        backBtn.setOnClickListener(v -> finish());
    }
    private void fetchChatIdsForItem(String itemId) {
        CollectionReference chatIdRef = db.collection("listing_items")
                .document(itemId)
                .collection("chat_id");
        chatIdRef.get().addOnCompleteListener(task -> {
            if (task.isSuccessful() && task.getResult() != null) {
                List<String> chatIds = new ArrayList<>();
                for (QueryDocumentSnapshot document : task.getResult()) {
                    chatIds.add(document.getId()); // Collect all chatId values
                }
                if (!chatIds.isEmpty()) {
                    fetchChats(chatIds); // Fetch chat details from "chats" collection
                } else {
                    Toast.makeText(ChatListActivity.this, "No chats found for this item", Toast.LENGTH_SHORT).show();
                }
            } else {
                Log.e("ChatListActivity", "Error getting chat IDs", task.getException());
            }
        });
    }
    private void fetchChats(List<String> chatIds) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        FirebaseAuth auth = FirebaseAuth.getInstance();
        String currentUserId = auth.getCurrentUser().getUid(); // Seller's ID
        chatList.clear(); // Clear previous data
        for (String chatId : chatIds) {
            db.collection("chats").document(chatId)
                    .get()
                    .addOnSuccessListener(chatSnapshot -> {
                        if (chatSnapshot.exists()) {
                            String buyerId = chatSnapshot.getString("buyerId");
                            String itemId = chatSnapshot.getString("item_id");
                            // 🔥 Log retrieved data
                            Log.d("ChatListActivity", "chatId: " + chatId);
                            Log.d("ChatListActivity", "buyerId: " + buyerId);
                            Log.d("ChatListActivity", "itemId: " + itemId);
                            // 🛑 Prevent null values from removing chats
                            if (buyerId == null) {
                                Log.e("ChatListActivity", "Warning: buyerId is null for chatId: " + chatId);
                                buyerId = "Unknown"; // Set default value
                            }
                            if (itemId == null) {
                                Log.e("ChatListActivity", "Warning: itemId is null for chatId: " + chatId);
                                itemId = "Unknown"; // Set default value
                            }
                            // ✅ Ensure valid chat is added
                            ChatItem chatItem = new ChatItem(chatId, buyerId, currentUserId, itemId);
                            chatList.add(chatItem);
                            chatListAdapter.notifyDataSetChanged();
                        } else {
                            Log.e("ChatListActivity", "Error: Chat document " + chatId + " does not exist!");
                        }
                    })
                    .addOnFailureListener(e -> Log.e("ChatListActivity", "Error fetching chat details", e));
        }
    }
}