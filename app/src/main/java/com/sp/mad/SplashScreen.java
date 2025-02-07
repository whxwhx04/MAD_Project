package com.sp.mad;

import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

public class SplashScreen extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);

        // Start the MainPage activity after 3 seconds
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                // Start MainPage activity
                Intent intent = new Intent(SplashScreen.this, LoginPage.class);
                startActivity(intent);

                // Finish SplashScreen to remove it from the back stack
                finish();
            }
        }, 3000); // Delay of 3 seconds
    }
}
