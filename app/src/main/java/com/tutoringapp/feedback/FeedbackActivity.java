package com.tutoringapp.feedback;

import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.tutoringapp.R;
import com.tutoringapp.database.DatabaseHelper;
import com.tutoringapp.models.Session;
import com.tutoringapp.utils.SessionManager;

import java.util.List;

/**
 * FeedbackActivity displays completed sessions that need feedback
 * and allows students to provide feedback for their tutoring sessions.
 */
public class FeedbackActivity extends AppCompatActivity {

    private ListView listSessions;
    private TextView tvEmptySessions;
    private DatabaseHelper databaseHelper;
    private SessionManager sessionManager;
    private FeedbackSessionAdapter sessionAdapter;
    private List<Session> sessionList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_feedback);

        // Initialize helpers
        databaseHelper = new DatabaseHelper(this);
        sessionManager = new SessionManager(this);

        // Set up toolbar
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Provide Feedback");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        // Initialize views
        listSessions = findViewById(R.id.listSessions);
        tvEmptySessions = findViewById(R.id.tvEmptySessions);

        // Load completed sessions without feedback
        loadCompletedSessions();

        // Set up list item click listener
        listSessions.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Session session = sessionList.get(position);
                showFeedbackDialog(session);
            }
        });
    }

    /**
     * Load completed sessions that need feedback
     */
    private void loadCompletedSessions() {
        int studentId = sessionManager.getUserId();
        
        // Get completed sessions without feedback
        sessionList = databaseHelper.getCompletedSessionsWithoutFeedback(studentId);
        
        if (sessionList.isEmpty()) {
            listSessions.setVisibility(View.GONE);
            tvEmptySessions.setVisibility(View.VISIBLE);
        } else {
            listSessions.setVisibility(View.VISIBLE);
            tvEmptySessions.setVisibility(View.GONE);
            sessionAdapter = new FeedbackSessionAdapter(this, sessionList);
            listSessions.setAdapter(sessionAdapter);
        }
    }

    /**
     * Show dialog to submit feedback for a session
     */
    private void showFeedbackDialog(final Session session) {
        FeedbackDialog dialog = new FeedbackDialog(this, session, databaseHelper);
        dialog.setOnFeedbackSubmittedListener(new FeedbackDialog.OnFeedbackSubmittedListener() {
            @Override
            public void onFeedbackSubmitted() {
                // Refresh the list after feedback submission
                loadCompletedSessions();
            }
        });
        dialog.show();
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
        // Refresh sessions when activity is resumed
        loadCompletedSessions();
    }
}
