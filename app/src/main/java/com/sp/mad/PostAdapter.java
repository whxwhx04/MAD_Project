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

import java.util.List;

public class PostAdapter extends RecyclerView.Adapter<PostAdapter.ViewHolder> {
    private final List<Post> postList;
    private final String currentUserId;

    public PostAdapter(List<Post> postList, String currentUserId) {
        this.postList = postList;
        this.currentUserId = currentUserId; // Passed directly from commpage
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_post, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Post post = postList.get(position);

        // Set the description and username
        holder.descriptionTextView.setText(post.getDescription());
        holder.usernameTextView.setText(post.getUsername());

        // Load the post image using Glide
        Glide.with(holder.itemView.getContext())
                .load(post.getImageUrl())
                .into(holder.postImageView);
    }

    @Override
    public int getItemCount() {
        return postList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        // Views for image, description, and username
        ImageView postImageView;
        TextView descriptionTextView;
        TextView usernameTextView;  // Added TextView for username

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            postImageView = itemView.findViewById(R.id.postImageView);
            descriptionTextView = itemView.findViewById(R.id.descriptionTextView);
            usernameTextView = itemView.findViewById(R.id.usernameTextView);  // Initialize username TextView
        }
    }

    // Fetch username from Firestore
    private void fetchUsername(String userId, final ViewHolder holder) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("users").document(userId)
                .get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        String username = documentSnapshot.getString("username");
                        holder.usernameTextView.setText(username);  // Set the fetched username
                    }
                });
    }
}
