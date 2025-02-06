package com.sp.mad;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;

public class ChatAdapter extends RecyclerView.Adapter<ChatAdapter.ChatViewHolder> {
    private List<Message> messages;
    private String currentUserId;

    public ChatAdapter(List<Message> messages, String currentUserId) {
        this.messages = messages;
        this.currentUserId = currentUserId;
    }

    @NonNull
    @Override
    public ChatViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.chat_message_row, parent, false);
        return new ChatViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ChatViewHolder holder, int position) {
        Message message = messages.get(position);
        holder.messageTextView.setText(message.getMessage());

        // Align messages based on sender
        if (message.getSenderId() != null && message.getSenderId().equals(currentUserId)) {
            // Current user (buyer) message -> align right
            holder.messageTextView.setBackgroundResource(R.drawable.bg_message_sent);
            holder.messageTextView.setTextAlignment(View.TEXT_ALIGNMENT_VIEW_END);
        } else {
            // Other user (seller) message -> align left
            holder.messageTextView.setBackgroundResource(R.drawable.bg_message_received);
            holder.messageTextView.setTextAlignment(View.TEXT_ALIGNMENT_VIEW_START);
        }

    }


    @Override
    public int getItemCount() {
        return messages.size();
    }

    static class ChatViewHolder extends RecyclerView.ViewHolder {
        TextView messageTextView;

        ChatViewHolder(View itemView) {
            super(itemView);
            messageTextView = itemView.findViewById(R.id.message_text);
        }
    }
}
