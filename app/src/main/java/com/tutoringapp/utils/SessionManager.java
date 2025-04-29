package com.tutoringapp.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;

/**
 * SessionManager handles user session management using SharedPreferences.
 * It stores and retrieves user login data and session state.
 */
public class SessionManager {
    
    // Shared Preferences
    private SharedPreferences pref;
    private Editor editor;
    private Context context;
    
    // Shared preferences file name
    private static final String PREF_NAME = "TutoringAppPref";
    
    // Shared preferences keys
    private static final String IS_LOGGED_IN = "IsLoggedIn";
    private static final String KEY_ID = "id";
    private static final String KEY_EMAIL = "email";
    private static final String KEY_FULL_NAME = "fullName";
    private static final String KEY_COURSE_CODE = "courseCode";
    private static final String KEY_YEAR_OF_STUDY = "yearOfStudy";
    private static final String KEY_IS_TUTOR = "isTutor";
    
    public SessionManager(Context context) {
        this.context = context;
        pref = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        editor = pref.edit();
    }
    
    /**
     * Create login session
     */
    public void createLoginSession(int id, String email, String fullName, String courseCode, int yearOfStudy, boolean isTutor) {
        // Storing login value as TRUE
        editor.putBoolean(IS_LOGGED_IN, true);
        
        // Store user data
        editor.putInt(KEY_ID, id);
        editor.putString(KEY_EMAIL, email);
        editor.putString(KEY_FULL_NAME, fullName);
        editor.putString(KEY_COURSE_CODE, courseCode);
        editor.putInt(KEY_YEAR_OF_STUDY, yearOfStudy);
        editor.putBoolean(KEY_IS_TUTOR, isTutor);
        
        // Commit changes
        editor.commit();
    }
    
    /**
     * Update user session with new data
     */
    public void updateUserSession(int id, String email, String fullName, String courseCode, int yearOfStudy, boolean isTutor) {
        // Store user data
        editor.putInt(KEY_ID, id);
        editor.putString(KEY_EMAIL, email);
        editor.putString(KEY_FULL_NAME, fullName);
        editor.putString(KEY_COURSE_CODE, courseCode);
        editor.putInt(KEY_YEAR_OF_STUDY, yearOfStudy);
        editor.putBoolean(KEY_IS_TUTOR, isTutor);
        
        // Commit changes
        editor.commit();
    }
    
    /**
     * Check login method will check user login status
     * If not logged in, redirect to LoginActivity
     */
    public boolean isLoggedIn() {
        return pref.getBoolean(IS_LOGGED_IN, false);
    }
    
    /**
     * Clear session details and log out user
     */
    public void logoutUser() {
        // Clear all data from Shared Preferences
        editor.clear();
        editor.commit();
    }
    
    /**
     * Get stored session data - User ID
     */
    public int getUserId() {
        return pref.getInt(KEY_ID, -1);
    }
    
    /**
     * Get stored session data - Email
     */
    public String getUserEmail() {
        return pref.getString(KEY_EMAIL, null);
    }
    
    /**
     * Get stored session data - Full Name
     */
    public String getUserFullName() {
        return pref.getString(KEY_FULL_NAME, null);
    }
    
    /**
     * Get stored session data - Course Code
     */
    public String getUserCourseCode() {
        return pref.getString(KEY_COURSE_CODE, null);
    }
    
    /**
     * Get stored session data - Year of Study
     */
    public int getUserYearOfStudy() {
        return pref.getInt(KEY_YEAR_OF_STUDY, 0);
    }
    
    /**
     * Get stored session data - Is Tutor
     */
    public boolean isTutor() {
        return pref.getBoolean(KEY_IS_TUTOR, false);
    }
}
