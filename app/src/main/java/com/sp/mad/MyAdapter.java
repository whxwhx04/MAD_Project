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
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.List;

public class MyAdapter extends RecyclerView.Adapter<MyAdapter.ViewHolder> {
    private final List<Item> itemList;
    private final String currentUserId;

    public MyAdapter(List<Item> itemList, String currentUserId) {
        this.itemList = itemList;
        this.currentUserId = currentUserId; // Passed directly from profile_page
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.activity_post_listing, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Item item = itemList.get(position);
        holder.itemTitle.setText(item.getTitle());
        holder.itemPrice.setText(item.getPrice());

        Glide.with(holder.itemImage.getContext())
                .load(item.getImageUrl())
                .into(holder.itemImage);

        // Set click listener to navigate to respective details page
        holder.itemView.setOnClickListener(v -> {
            Intent intent;
            if (item.getUserId().equals(currentUserId)) {
                // Navigate to sell_listing_details if the item belongs to the user
                intent = new Intent(v.getContext(), sell_listing_details.class);
            } else {
                // Navigate to buy_listing_details if the item belongs to another user
                intent = new Intent(v.getContext(), buy_listing_details.class);
            }
            intent.putExtra("itemId", item.getItemId());
            intent.putExtra("itemTitle", item.getTitle());
            intent.putExtra("itemPrice", item.getPrice());
            intent.putExtra("itemImageUrl", item.getImageUrl());
            v.getContext().startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return itemList.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView itemTitle;
        TextView itemPrice;
        ImageView itemImage;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            itemImage = itemView.findViewById(R.id.itemImage);
            itemTitle = itemView.findViewById(R.id.itemTitle);
            itemPrice = itemView.findViewById(R.id.itemPrice);
        }
    }
}
