package com.sp.mad;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;

public class SavedItemsAdapter extends RecyclerView.Adapter<SavedItemsAdapter.ViewHolder> {
    private List<Item> savedItemsList;
    private Context context;

    public SavedItemsAdapter(Context context, List<Item> savedItemsList) {
        this.context = context;
        this.savedItemsList = savedItemsList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.saved_items_layout, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Item item = savedItemsList.get(position);
        holder.itemTitle.setText(item.getTitle());
        holder.itemPrice.setText(item.getPrice());
        holder.itemImage.setImageResource(item.getImageResid());

        // Navigate to Buyer_Listings when an item is clicked
        holder.itemView.setOnClickListener(view -> {
            Intent intent = new Intent(context, Buyer_Listings.class);
            intent.putExtra("title", item.getTitle());
            intent.putExtra("price", item.getPrice());
            intent.putExtra("imageResId", item.getImageResid());
            intent.putExtra("description", item.getDescription());
            intent.putExtra("accountSeller", item.getAccountSeller());
            intent.putExtra("condition", item.getCondition());
            intent.putExtra("school", item.getSchool());
            intent.putExtra("course", item.getCourse());

            context.startActivity(intent);
        });

        // Remove item when delete button is clicked
        holder.removeSavedItem.setOnClickListener(view -> {
            savedItemsList.remove(position);
            notifyItemRemoved(position);
            notifyItemRangeChanged(position, savedItemsList.size());
        });
    }


    @Override
    public int getItemCount() {
        return savedItemsList.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView itemTitle, itemPrice;
        ImageView itemImage, removeSavedItem;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            itemImage = itemView.findViewById(R.id.savedItemImage);
            itemTitle = itemView.findViewById(R.id.savedItemTitle);
            itemPrice = itemView.findViewById(R.id.savedItemPrice);
            removeSavedItem = itemView.findViewById(R.id.removeSavedItem);
        }
    }
}
