package com.tutoringapp.authentication;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.tutoringapp.R;
import com.tutoringapp.database.DatabaseHelper;
import com.tutoringapp.models.User;

/**
 * RegisterActivity allows new users to create an account.
 * Users must provide their email, password, full name, course code, and year of study.
 */
public class RegisterActivity extends AppCompatActivity {

    private EditText etEmail, etPassword, etConfirmPassword, etFullName, etCourseCode, etYearOfStudy;
    private Button btnRegister;
    private TextView tvLogin;
    private DatabaseHelper databaseHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        // Initialize database helper
        databaseHelper = new DatabaseHelper(this);

        // Initialize views
        etEmail = findViewById(R.id.etEmail);
        etPassword = findViewById(R.id.etPassword);
        etConfirmPassword = findViewById(R.id.etConfirmPassword);
        etFullName = findViewById(R.id.etFullName);
        etCourseCode = findViewById(R.id.etCourseCode);
        etYearOfStudy = findViewById(R.id.etYearOfStudy);
        btnRegister = findViewById(R.id.btnRegister);
        tvLogin = findViewById(R.id.tvLogin);

        // Set up register button click listener
        btnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                registerUser();
            }
        });

        // Set up login text click listener
        tvLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish(); // Go back to login activity
            }
        });
    }

    /**
     * Validate user inputs and register the user if valid
     */
    private void registerUser() {
        String email = etEmail.getText().toString().trim();
        String password = etPassword.getText().toString().trim();
        String confirmPassword = etConfirmPassword.getText().toString().trim();
        String fullName = etFullName.getText().toString().trim();
        String courseCode = etCourseCode.getText().toString().trim();
        String yearOfStudyStr = etYearOfStudy.getText().toString().trim();

        // Validate inputs
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

        if (password.isEmpty()) {
            etPassword.setError("Password is required");
            etPassword.requestFocus();
            return;
        }

        if (password.length() < 6) {
            etPassword.setError("Password should be at least 6 characters");
            etPassword.requestFocus();
            return;
        }

        if (!password.equals(confirmPassword)) {
            etConfirmPassword.setError("Passwords do not match");
            etConfirmPassword.requestFocus();
            return;
        }

        if (fullName.isEmpty()) {
            etFullName.setError("Full name is required");
            etFullName.requestFocus();
            return;
        }

        if (courseCode.isEmpty()) {
            etCourseCode.setError("Course code is required");
            etCourseCode.requestFocus();
            return;
        }

        if (yearOfStudyStr.isEmpty()) {
            etYearOfStudy.setError("Year of study is required");
            etYearOfStudy.requestFocus();
            return;
        }

        int yearOfStudy;
        try {
            yearOfStudy = Integer.parseInt(yearOfStudyStr);
            if (yearOfStudy < 1 || yearOfStudy > 7) {
                etYearOfStudy.setError("Please enter a valid year (1-7)");
                etYearOfStudy.requestFocus();
                return;
            }
        } catch (NumberFormatException e) {
            etYearOfStudy.setError("Please enter a valid year");
            etYearOfStudy.requestFocus();
            return;
        }

        // Check if email already exists
        if (databaseHelper.checkUserExists(email)) {
            etEmail.setError("Email already registered");
            etEmail.requestFocus();
            return;
        }

        // Create new user
        User user = new User();
        user.setEmail(email);
        user.setPassword(password);
        user.setFullName(fullName);
        user.setCourseCode(courseCode);
        user.setYearOfStudy(yearOfStudy);
        user.setTutor(false); // New users are not tutors by default

        // Insert user into database
        long userId = databaseHelper.addUser(user);
        if (userId > 0) {
            Toast.makeText(this, "Registration successful", Toast.LENGTH_SHORT).show();
            startActivity(new Intent(RegisterActivity.this, LoginActivity.class));
            finish();
        } else {
            Toast.makeText(this, "Registration failed", Toast.LENGTH_SHORT).show();
        }
    }
}
