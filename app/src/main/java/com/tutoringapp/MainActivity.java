package com.tutoringapp;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

import androidx.appcompat.app.AppCompatActivity;

import com.tutoringapp.authentication.LoginActivity;
import com.tutoringapp.utils.SessionManager;

/**
 * MainActivity serves as the splash screen and entry point of the application.
 * It checks if the user is already logged in and redirects accordingly.
 */
public class MainActivity extends AppCompatActivity {

    private static final int SPLASH_TIMEOUT = 2000; // 2 seconds
    private SessionManager sessionManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        sessionManager = new SessionManager(this);

        new Handler().postDelayed(() -> {
            // Check if user is already logged in
            if (sessionManager.isLoggedIn()) {
                startActivity(new Intent(MainActivity.this, com.tutoringapp.dashboard.DashboardActivity.class));
            } else {
                startActivity(new Intent(MainActivity.this, LoginActivity.class));
            }
            finish();
        }, SPLASH_TIMEOUT);
    }
}
