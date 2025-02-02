package com.sp.mad;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import androidx.appcompat.app.AppCompatActivity;

public class create_listing extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_listing);

        ImageButton backMainButton = findViewById(R.id.back_main2);
        backMainButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Navigate back to MainPage
                Intent intent = new Intent(create_listing.this, mainpage.class);
                startActivity(intent);
                // Optional: If you want to close the current activity
                finish();
            }
        });
    }
}
