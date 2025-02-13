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
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.DocumentSnapshot;
import java.util.List;

public class MessageAdapter extends RecyclerView.Adapter<MessageAdapter.MessageViewHolder> {

    private Context context;
    private List<Message> messageList;
    private String currentUserId; // The current user's ID (buyer or seller)
    private FirebaseFirestore db; // Firestore instance to retrieve profile pictures

    public MessageAdapter(Context context, List<Message> messageList, String currentUserId) {
        this.context = context;
        this.messageList = messageList;
        this.currentUserId = currentUserId;
        this.db = FirebaseFirestore.getInstance();
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

        // Get the sender (buyer or seller)
        String senderId = message.getSenderId();

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

        // Fetch profile picture for the sender (buyer or seller)
        fetchProfilePicture(senderId, holder);

        // Fetch and set the profile picture for the opposite party (buyer or seller) if needed
        if (message.getBuyerId().equals(currentUserId)) {
            fetchProfilePicture(message.getSellerId(), holder);
        } else {
            fetchProfilePicture(message.getBuyerId(), holder);
        }
    }

    @Override
    public int getItemCount() {
        return messageList.size();
    }

    @Override
    public int getItemViewType(int position) {
        Message message = messageList.get(position);
        return (message.getSenderId() != null && message.getSenderId().equals(currentUserId)) ? 1 : 2;
    }

    private void fetchProfilePicture(String userId, MessageViewHolder holder) {
        db.collection("users").document(userId).get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        String profilePictureUrl = documentSnapshot.getString("profilePicture");
                        if (profilePictureUrl != null && !profilePictureUrl.isEmpty()) {
                            // If the profile picture URL exists, load it using Glide
                            if (userId.equals(currentUserId)) {
                                Glide.with(context).load(profilePictureUrl).into(holder.buyerChatPic);
                            } else {
                                Glide.with(context).load(profilePictureUrl).into(holder.sellerChatPic);
                            }
                        }
                    }
                });
    }

    public static class MessageViewHolder extends RecyclerView.ViewHolder {
        TextView messageText;
        ImageView messageImage;
        ImageView buyerChatPic;
        ImageView sellerChatPic;

        public MessageViewHolder(View itemView) {
            super(itemView);
            messageText = itemView.findViewById(R.id.message_seller);
            messageImage = itemView.findViewById(R.id.sellerChat_pic);
            buyerChatPic = itemView.findViewById(R.id.buyerChat_pic);
            sellerChatPic = itemView.findViewById(R.id.sellerChat_pic);
        }
    }
}
