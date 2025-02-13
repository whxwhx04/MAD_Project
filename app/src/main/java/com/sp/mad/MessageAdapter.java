package com.sp.mad;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Glide;
import java.util.List;

public class MessageAdapter extends RecyclerView.Adapter<MessageAdapter.MessageViewHolder> {

    private Context context;
    private List<Message> messageList;
    private String currentUserId; // The current user's ID (buyer or seller)

    public MessageAdapter(Context context, List<Message> messageList, String currentUserId) {
        this.context = context;
        this.messageList = messageList;
        this.currentUserId = currentUserId;
    }

    @NonNull
    @Override
    public MessageViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view;
        if (viewType == 1) {
            view = LayoutInflater.from(context).inflate(R.layout.chat_message_sent, parent, false);
        } else {
            view = LayoutInflater.from(context).inflate(R.layout.chat_message_received, parent, false);
        }
        return new MessageViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MessageViewHolder holder, int position) {
        Message message = messageList.get(position);

        // Handle Image Messages
        if (message.getImageUrl() != null && !message.getImageUrl().isEmpty()) {
            holder.messageImage.setVisibility(View.VISIBLE);
            holder.messageText.setVisibility(View.GONE);
            Glide.with(context).load(message.getImageUrl()).into(holder.messageImage);
        } else {
            holder.messageText.setVisibility(View.VISIBLE);
            holder.messageImage.setVisibility(View.GONE);
            holder.messageText.setText(message.getMessageText());
        }
    }

    @Override
    public int getItemCount() {
        return messageList.size();
    }

    @Override
    public int getItemViewType(int position) {
        Message message = messageList.get(position);
        return (message.getBuyerId() != null && message.getBuyerId().equals(currentUserId)) ? 1 : 2;
    }

    public static class MessageViewHolder extends RecyclerView.ViewHolder {
        TextView messageText;
        ImageView messageImage;

        public MessageViewHolder(View itemView) {
            super(itemView);
            messageText = itemView.findViewById(R.id.message_seller);  // Same for both layouts
            messageImage = itemView.findViewById(R.id.sellerChat_pic); // Same for both layouts
        }
    }
}