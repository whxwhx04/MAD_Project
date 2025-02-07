package com.sp.mad;

import android.os.Bundle;
import android.widget.ImageView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;

public class SavedItemsActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private SavedItemsAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_saved_items);

        recyclerView = findViewById(R.id.savedItems_recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        // Get saved items
        List<Item> savedItems = SavedItemsManager.getSavedItems();

        // Set up adapter with saved items
        adapter = new SavedItemsAdapter(this, savedItems);
        recyclerView.setAdapter(adapter);

        // Back button functionality
        ImageView backBtn = findViewById(R.id.btn_BackSaved);
        backBtn.setOnClickListener(view -> finish());
    }
}
