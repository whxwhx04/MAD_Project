package com.sp.mad;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.List;

public class ChatListAdapter extends RecyclerView.Adapter<ChatListAdapter.ViewHolder> {

    private Context context;
    private List<ChatItem> chatList;
    private FirebaseFirestore db;

    public ChatListAdapter(Context context, List<ChatItem> chatList) {
        this.context = context;
        this.chatList = chatList;
        this.db = FirebaseFirestore.getInstance();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.chat_recycler_row, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        ChatItem chat = chatList.get(position);

        // Fetch and display the item name from the listing_items collection
        String itemId = chat.getItemId();
        if (itemId != null && !itemId.isEmpty()) {
            db.collection("listing_items").document(itemId).get()
                    .addOnSuccessListener(documentSnapshot -> {
                        if (documentSnapshot.exists()) {
                            String itemName = documentSnapshot.getString("itemName");
                            holder.chatName.setText(itemName != null ? itemName : "Item Name Not Found");
                        } else {
                            // If the item is not found, try fetching it using the chats collection
                            fetchItemIdFromChats(chat.getBuyerId(), chat.getSellerId(), holder);
                        }
                    })
                    .addOnFailureListener(e -> {
                        holder.chatName.setText("Error fetching item name");
                    });
        } else {
            // If itemId is missing, try fetching it from the chats collection
            fetchItemIdFromChats(chat.getBuyerId(), chat.getSellerId(), holder);
        }

        // Fetch and display the buyer name from the users collection using buyerId
        String buyerId = chat.getBuyerId();
        db.collection("users").document(buyerId).get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        String buyerName ="Buyer:"+documentSnapshot.getString("username");
                        holder.buyerId.setText(buyerName != null ? buyerName : "Buyer Not Found");
                    } else {
                        holder.buyerId.setText("Buyer Not Found");
                    }
                })
                .addOnFailureListener(e -> {
                    holder.buyerId.setText("Error fetching buyer name");
                });

        // Handle chat click event
        holder.itemView.setOnClickListener(v -> {
            Intent intent = new Intent(context, SellerChatRoomActivity.class);
            intent.putExtra("chatId", chat.getChatId());
            intent.putExtra("buyerId", chat.getBuyerId());
            intent.putExtra("sellerId", chat.getSellerId());
            intent.putExtra("itemId", chat.getItemId()); // Pass item ID for reference
            context.startActivity(intent);
        });
    }

    private void fetchItemIdFromChats(String buyerId, String sellerId, ViewHolder holder) {
        db.collection("chats")
                .whereEqualTo("buyerId", buyerId)
                .whereEqualTo("sellerId", sellerId)
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    if (!queryDocumentSnapshots.isEmpty()) {
                        for (DocumentSnapshot document : queryDocumentSnapshots) {
                            String fetchedItemId = document.getString("itemId");
                            if (fetchedItemId != null && !fetchedItemId.isEmpty()) {
                                // Fetch the item details using the fetched itemId
                                fetchItemName(fetchedItemId, holder);
                                break;
                            }
                        }
                    } else {
                        holder.chatName.setText("Item ID Not Found in Chats");
                    }
                })
                .addOnFailureListener(e -> {
                    holder.chatName.setText("Error fetching Item ID from Chats");
                });
    }

    private void fetchItemName(String itemId, ViewHolder holder) {
        db.collection("listing_items").document(itemId).get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        String itemName ="Item:"+ documentSnapshot.getString("itemName");
                        holder.chatName.setText(itemName != null ? itemName : "Item Name Not Found");
                    } else {
                        holder.chatName.setText("Item Not Found");
                    }
                })
                .addOnFailureListener(e -> {
                    holder.chatName.setText("Error fetching item name");
                });
    }

    @Override
    public int getItemCount() {
        return chatList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        TextView chatName, buyerId;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            chatName = itemView.findViewById(R.id.user_name_text);
            buyerId = itemView.findViewById(R.id.item_listing_name);
        }
    }
}
