package com.example.androidgreenplate.views;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import com.example.androidgreenplate.R;


public class WelcomeActivity extends AppCompatActivity {
    private TextView textViewWelcomeTitle;
    private TextView textViewSummary;
    private TextView textViewSlogan;

    private Handler handler = new Handler(); // Use the Handler to post a delayed task

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Set the content view to your welcome layout
        setContentView(R.layout.welcome);

        // Initialize TextViews
        textViewWelcomeTitle = findViewById(R.id.textViewWelcomeTitle);
        textViewSummary = findViewById(R.id.textViewSummary);
        textViewSlogan = findViewById(R.id.textViewSlogan);

        // Post a Runnable that will start the LoginActivity after 3 seconds
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                // Create an Intent to start the LoginActivity
                Intent intent = new Intent(WelcomeActivity.this, LoginActivity.class);
                startActivity(intent);
                finish(); // Finish the current activity so the user can't return to it
            }
        }, 3000); // 3000 milliseconds delay, or 3 secs
    }
}
