package com.sp.mad;

import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;

public class splash_screen extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);

        // Start the MainPage activity after 3 seconds

        MediaPlayer mediaPlayer = MediaPlayer.create(getApplicationContext(), R.raw.sample);
        mediaPlayer.setVolume(1.0f, 1.0f); // Left and Right volume
        mediaPlayer.start();

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                Intent intent = new Intent(splash_screen.this, login_page.class);
                startActivity(intent);

                // Finish SplashScreen to remove it from the back stack
                finish();
            }
        }, 3000); // Delay of 3 seconds
    }
}
