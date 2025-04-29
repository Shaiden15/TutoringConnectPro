package com.tutoringapp.profile;

import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.tutoringapp.R;
import com.tutoringapp.database.DatabaseHelper;
import com.tutoringapp.models.User;
import com.tutoringapp.utils.SessionManager;

/**
 * ProfileActivity allows users to view and edit their profile information.
 * Users can also toggle "Tutor Mode" to become tutors.
 */
public class ProfileActivity extends AppCompatActivity {

    private EditText etFullName, etEmail, etPassword;
    private TextView tvCourseCode, tvYearOfStudy;
    private Switch switchTutorMode;
    private Button btnSave;
    private SessionManager sessionManager;
    private DatabaseHelper databaseHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        // Initialize session manager and database helper
        sessionManager = new SessionManager(this);
        databaseHelper = new DatabaseHelper(this);

        // Set up toolbar
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Profile");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        // Initialize views
        etFullName = findViewById(R.id.etFullName);
        etEmail = findViewById(R.id.etEmail);
        etPassword = findViewById(R.id.etPassword);
        tvCourseCode = findViewById(R.id.tvCourseCode);
        tvYearOfStudy = findViewById(R.id.tvYearOfStudy);
        switchTutorMode = findViewById(R.id.switchTutorMode);
        btnSave = findViewById(R.id.btnSave);

        // Load user data
        loadUserData();

        // Set up save button click listener
        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveUserData();
            }
        });
    }

    /**
     * Load user data from the database and display it
     */
    private void loadUserData() {
        int userId = sessionManager.getUserId();
        User user = databaseHelper.getUserById(userId);

        if (user != null) {
            etFullName.setText(user.getFullName());
            etEmail.setText(user.getEmail());
            etPassword.setText(user.getPassword());
            tvCourseCode.setText(user.getCourseCode());
            tvYearOfStudy.setText(String.valueOf(user.getYearOfStudy()));
            switchTutorMode.setChecked(user.isTutor());
        }
    }

    /**
     * Save updated user data to the database
     */
    private void saveUserData() {
        String fullName = etFullName.getText().toString().trim();
        String email = etEmail.getText().toString().trim();
        String password = etPassword.getText().toString().trim();
        boolean isTutor = switchTutorMode.isChecked();

        // Validate inputs
        if (fullName.isEmpty()) {
            etFullName.setError("Full name is required");
            etFullName.requestFocus();
            return;
        }

        if (email.isEmpty()) {
            etEmail.setError("Email is required");
            etEmail.requestFocus();
            return;
        }

        if (!email.endsWith(".edu") && !email.endsWith(".ac.za")) {
            etEmail.setError("Please use a valid student email");
            etEmail.requestFocus();
            return;
        }

        if (password.isEmpty() || password.length() < 6) {
            etPassword.setError("Password should be at least 6 characters");
            etPassword.requestFocus();
            return;
        }

        // Get current user
        int userId = sessionManager.getUserId();
        User user = databaseHelper.getUserById(userId);

        // Check if email is changed and already exists
        if (!user.getEmail().equals(email) && databaseHelper.checkUserExists(email)) {
            etEmail.setError("Email already in use");
            etEmail.requestFocus();
            return;
        }

        // Update user data
        user.setFullName(fullName);
        user.setEmail(email);
        user.setPassword(password);
        user.setTutor(isTutor);

        // Save to database
        boolean success = databaseHelper.updateUser(user);
        if (success) {
            // Update session
            sessionManager.updateUserSession(
                    user.getId(),
                    user.getEmail(),
                    user.getFullName(),
                    user.getCourseCode(),
                    user.getYearOfStudy(),
                    user.isTutor()
            );
            
            Toast.makeText(this, "Profile updated successfully", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, "Failed to update profile", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
