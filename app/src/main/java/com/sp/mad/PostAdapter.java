package com.sp.mad;

import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.sp.mad.R;

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
        // Use the existing item layout from commpage.xml (item_post.xml should be in your layout folder)
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_post, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Post post = postList.get(position);
        holder.descriptionTextView.setText(post.getDescription());

        // Load the post image using Glide
        Glide.with(holder.itemView.getContext())
                .load(post.getImageUrl())
                .into(holder.postImageView);

        // Set click listener for each post item
        /*holder.itemView.setOnClickListener(v -> {
            Intent intent = new Intent(v.getContext(), PostDetailActivity.class);
            intent.putExtra("postId", post.getPostId());
            intent.putExtra("postDescription", post.getDescription());
            intent.putExtra("postImageUrl", post.getImageUrl());
            intent.putExtra("userId", post.getUserId());
            v.getContext().startActivity(intent);
        });*/
    }

    @Override
    public int getItemCount() {
        return postList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        // Views for image and description
        ImageView postImageView;
        TextView descriptionTextView;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            postImageView = itemView.findViewById(R.id.postImageView);
            descriptionTextView = itemView.findViewById(R.id.descriptionTextView);
        }
    }
}
