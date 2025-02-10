package com.sp.mad;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.DocumentSnapshot;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class CommentAdapter extends RecyclerView.Adapter<CommentAdapter.ViewHolder> {
    private final List<Comment> commentList;

    public CommentAdapter(List<Comment> commentList) {
        this.commentList = commentList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_comment, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Comment comment = commentList.get(position);

        // Fetch username and profile picture from the users collection
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("users").document(comment.getUserId())
                .get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        String username = documentSnapshot.getString("username");
                        String profileImageUrl = documentSnapshot.getString("profilePicture"); // Changed to correct field name

                        // Set the username in the TextView
                        holder.usernameTextView.setText(username);

                        // Set the profile image using Glide
                        Glide.with(holder.itemView.getContext()).load(profileImageUrl)
                                .into(holder.profileImageView);
                    }
                });

        // Set the comment content
        holder.commentContent.setText(comment.getContent());

        // Format the timestamp and set it
        long timestamp = comment.getTimestamp();
        String formattedTimestamp = formatTimestamp(timestamp);
        holder.timestamp.setText(formattedTimestamp);
    }

    @Override
    public int getItemCount() {
        return commentList.size();
    }

    // Method to format the timestamp
    private String formatTimestamp(long timestamp) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd HH:mm", Locale.getDefault());
        Date date = new Date(timestamp); // Convert timestamp to Date object
        return sdf.format(date); // Return the formatted string
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView commentContent, timestamp, usernameTextView;
        ImageView profileImageView;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            commentContent = itemView.findViewById(R.id.commentContent);
            timestamp = itemView.findViewById(R.id.timestamp);
            usernameTextView = itemView.findViewById(R.id.usernameTextView); // Add this line
            profileImageView = itemView.findViewById(R.id.profileImageView);
        }
    }
}
