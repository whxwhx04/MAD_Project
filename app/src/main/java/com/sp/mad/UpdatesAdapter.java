package com.sp.mad;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.Picasso;

import java.util.List;

public class UpdatesAdapter extends RecyclerView.Adapter<UpdatesAdapter.ViewHolder> {

    private List<Update> updatesList;

    public UpdatesAdapter(List<Update> updatesList) {
        this.updatesList = updatesList;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        // Inflate the layout for each update item (items_updates.xml)
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.items_updates, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        Update update = updatesList.get(position);

        // Set the image from URL using Picasso
        Picasso.get().load(update.getImageUrl()).into(holder.updateImageView);

        // Set the update text (description)
        holder.updateInfoTextView.setText(update.getUpdateInfo());

        // Set the formatted timestamp
        holder.updateTimeTextView.setText(update.getTimestamp());

        // Set a click listener for the remove button
        holder.removeUpdateImageView.setOnClickListener(v -> {
            // Call remove update method in the activity
            ((updates_page) holder.itemView.getContext()).removeUpdate(update.getUpdateId());
        });
    }

    @Override
    public int getItemCount() {
        return updatesList.size();
    }

    // ViewHolder to hold views for each update
    public static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView updateImageView;
        TextView updateInfoTextView;
        TextView updateTimeTextView;
        ImageView removeUpdateImageView;

        public ViewHolder(View itemView) {
            super(itemView);

            // Initialize views from the layout
            updateImageView = itemView.findViewById(R.id.update_img);
            updateInfoTextView = itemView.findViewById(R.id.update_info);
            updateTimeTextView = itemView.findViewById(R.id.update_time);
            removeUpdateImageView = itemView.findViewById(R.id.remove_update);
        }
    }
}
