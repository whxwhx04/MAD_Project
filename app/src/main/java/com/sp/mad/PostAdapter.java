package com.sp.mad;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
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

        // Handle like button functionality
        holder.likeButton.setOnClickListener(v -> {
            FirebaseFirestore db = FirebaseFirestore.getInstance();
            FirebaseAuth mAuth = FirebaseAuth.getInstance();
            String currentUserId = mAuth.getCurrentUser().getUid();  // Get current user ID

            String postId = post.getPostId();  // Get the postId of the post being liked

            db.collection("users").document(currentUserId)
                    .collection("liked_posts")
                    .document(postId)  // The document ID will be the postId
                    .get()
                    .addOnSuccessListener(documentSnapshot -> {
                        if (documentSnapshot.exists()) {
                            // If the post is already liked, remove it
                            db.collection("users").document(currentUserId)
                                    .collection("liked_posts")
                                    .document(postId)
                                    .delete()
                                    .addOnSuccessListener(aVoid -> {
                                        // Show a toast message indicating the post has been unliked
                                        Toast.makeText(v.getContext(), "You've unliked the post!", Toast.LENGTH_SHORT).show();
                                    });
                        } else {
                            // If the post is not liked, add it to liked_posts subcollection with field "PostId"
                            HashMap<String, Object> likedPostData = new HashMap<>();
                            likedPostData.put("postId", postId);  // Add the field "PostId" with the post ID

                            db.collection("users").document(currentUserId)
                                    .collection("liked_posts")
                                    .document(postId)  // The document ID still holds the post ID
                                    .set(likedPostData)  // Set "PostId" field in the document
                                    .addOnSuccessListener(aVoid -> {
                                        // Show a toast message indicating the post has been liked
                                        Toast.makeText(v.getContext(), "You've liked the post!", Toast.LENGTH_SHORT).show();
                                    });
                        }
                    })
                    .addOnFailureListener(e -> {
                        Toast.makeText(v.getContext(), "Error processing like action: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    });
        });
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
        ImageView likeButton;  // Like button

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            postImageView = itemView.findViewById(R.id.postImageView);
            descriptionTextView = itemView.findViewById(R.id.descriptionTextView);
            usernameTextView = itemView.findViewById(R.id.usernameTextView);  // Initialize username TextView
            likeButton = itemView.findViewById(R.id.likepost);  // Initialize like button
        }
    }
}
