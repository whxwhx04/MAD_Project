package com.sp.mad;

import android.content.Intent;
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
        this.currentUserId = currentUserId;
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
        handleLikeButton(holder, post);

        // Navigate to PostDetailsActivity on post click
        handlePostClick(holder, post);
    }

    private void handleLikeButton(ViewHolder holder, Post post) {
        holder.likeButton.setOnClickListener(v -> {
            FirebaseFirestore db = FirebaseFirestore.getInstance();
            FirebaseAuth mAuth = FirebaseAuth.getInstance();
            String currentUserId = mAuth.getCurrentUser().getUid();

            String postId = post.getPostId();  // This is the correct postId from posts collection

            db.collection("users").document(currentUserId)
                    .collection("liked_posts")
                    .document(postId)
                    .get()
                    .addOnSuccessListener(documentSnapshot -> {
                        if (documentSnapshot.exists()) {
                            db.collection("users").document(currentUserId)
                                    .collection("liked_posts")
                                    .document(postId)
                                    .delete()
                                    .addOnSuccessListener(aVoid -> {
                                        Toast.makeText(v.getContext(), "You've unliked the post!", Toast.LENGTH_SHORT).show();
                                    });
                        } else {
                            HashMap<String, Object> likedPostData = new HashMap<>();
                            likedPostData.put("postId", postId);

                            db.collection("users").document(currentUserId)
                                    .collection("liked_posts")
                                    .document(postId)
                                    .set(likedPostData)
                                    .addOnSuccessListener(aVoid -> {
                                        Toast.makeText(v.getContext(), "You've liked the post!", Toast.LENGTH_SHORT).show();
                                    });
                        }
                    })
                    .addOnFailureListener(e -> {
                        Toast.makeText(v.getContext(), "Error processing like action: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    });
        });
    }

    private void handlePostClick(ViewHolder holder, Post post) {
        holder.itemView.setOnClickListener(v -> {
            // Navigate to PostDetailsActivity on post click and pass the correct postId
            Intent intent = new Intent(v.getContext(), post_details.class);
            intent.putExtra("postId", post.getPostId());  // Pass the postId from the posts collection
            v.getContext().startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return postList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView postImageView;
        TextView descriptionTextView;
        TextView usernameTextView;
        ImageView likeButton;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            postImageView = itemView.findViewById(R.id.postImageView);
            descriptionTextView = itemView.findViewById(R.id.descriptionTextView);
            usernameTextView = itemView.findViewById(R.id.usernameTextView);
            likeButton = itemView.findViewById(R.id.likepost);
        }
    }
}
