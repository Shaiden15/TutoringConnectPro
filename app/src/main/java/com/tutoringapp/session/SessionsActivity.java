package com.tutoringapp.session;

import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.viewpager.widget.ViewPager;

import com.google.android.material.tabs.TabLayout;
import com.tutoringapp.R;
import com.tutoringapp.database.DatabaseHelper;
import com.tutoringapp.models.Session;
import com.tutoringapp.utils.SessionManager;

import java.util.List;

/**
 * SessionsActivity displays all user sessions (pending, accepted, rejected, completed).
 * For tutors, it shows sessions where they are the tutor.
 * For students, it shows sessions they have booked.
 */
public class SessionsActivity extends AppCompatActivity {

    private TabLayout tabLayout;
    private ViewPager viewPager;
    private SessionsPagerAdapter sessionsPagerAdapter;
    private SessionManager sessionManager;
    private DatabaseHelper databaseHelper;
    private TextView tvEmptySessions;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sessions);

        // Initialize session manager and database helper
        sessionManager = new SessionManager(this);
        databaseHelper = new DatabaseHelper(this);

        // Set up toolbar
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("My Sessions");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        // Initialize views
        tabLayout = findViewById(R.id.tabLayout);
        viewPager = findViewById(R.id.viewPager);
        tvEmptySessions = findViewById(R.id.tvEmptySessions);

        // Set up tab layout and view pager
        setupViewPager();
    }

    /**
     * Set up ViewPager with fragments for different session statuses
     */
    private void setupViewPager() {
        sessionsPagerAdapter = new SessionsPagerAdapter(getSupportFragmentManager());
        
        // Add tabs for different session statuses
        sessionsPagerAdapter.addFragment(SessionListFragment.newInstance("pending"), "Pending");
        sessionsPagerAdapter.addFragment(SessionListFragment.newInstance("accepted"), "Accepted");
        sessionsPagerAdapter.addFragment(SessionListFragment.newInstance("rejected"), "Rejected");
        sessionsPagerAdapter.addFragment(SessionListFragment.newInstance("completed"), "Completed");
        
        viewPager.setAdapter(sessionsPagerAdapter);
        tabLayout.setupWithViewPager(viewPager);
        
        // Check if user has any sessions
        checkForSessions();
    }

    /**
     * Check if the user has any sessions, show empty state if not
     */
    private void checkForSessions() {
        int userId = sessionManager.getUserId();
        boolean isTutor = sessionManager.isTutor();
        
        List<Session> sessions;
        if (isTutor) {
            sessions = databaseHelper.getSessionsByTutorId(userId);
        } else {
            sessions = databaseHelper.getSessionsByStudentId(userId);
        }
        
        if (sessions.isEmpty()) {
            viewPager.setVisibility(View.GONE);
            tabLayout.setVisibility(View.GONE);
            tvEmptySessions.setVisibility(View.VISIBLE);
            
            if (isTutor) {
                tvEmptySessions.setText("You don't have any tutoring sessions yet.");
            } else {
                tvEmptySessions.setText("You haven't booked any sessions yet.");
            }
        } else {
            viewPager.setVisibility(View.VISIBLE);
            tabLayout.setVisibility(View.VISIBLE);
            tvEmptySessions.setVisibility(View.GONE);
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

    @Override
    protected void onResume() {
        super.onResume();
        // Refresh sessions when coming back to this activity
        checkForSessions();
    }
}
