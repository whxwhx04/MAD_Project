package com.sp.mad;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class ChatListAdapter extends RecyclerView.Adapter<ChatListAdapter.ViewHolder> {
    private Context context;
    private List<ChatItem> chatList;

    public ChatListAdapter(Context context, List<ChatItem> chatList) {
        this.context = context;
        this.chatList = chatList;
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

        // Display chat ID and buyer ID for reference
        holder.chatName.setText("Chat ID: " + chat.getItemId());
        holder.buyerId.setText("Buyer ID: " + chat.getBuyerId());

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

    @Override
    public int getItemCount() {
        return chatList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView chatName, buyerId;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            chatName = itemView.findViewById(R.id.user_name_text); // Update in chat_recycler_row.xml
            buyerId = itemView.findViewById(R.id.item_listing_name);
        }
    }
}
