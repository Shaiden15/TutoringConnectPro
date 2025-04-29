package com.tutoringapp.session;

import android.content.Intent;
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
import com.tutoringapp.models.User;
import com.tutoringapp.utils.SessionManager;

import java.util.List;

/**
 * TutorListActivity displays a list of available tutors for a selected subject.
 */
public class TutorListActivity extends AppCompatActivity {

    private ListView listTutors;
    private TextView tvEmptyTutors;
    private DatabaseHelper databaseHelper;
    private SessionManager sessionManager;
    private TutorAdapter tutorAdapter;
    private int subjectId;
    private String subjectName;
    private List<User> tutorList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tutor_list);

        // Get subject details from intent
        subjectId = getIntent().getIntExtra("SUBJECT_ID", -1);
        subjectName = getIntent().getStringExtra("SUBJECT_NAME");

        if (subjectId == -1) {
            finish();
            return;
        }

        // Initialize database helper and session manager
        databaseHelper = new DatabaseHelper(this);
        sessionManager = new SessionManager(this);

        // Set up toolbar
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Tutors for " + subjectName);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        // Initialize views
        listTutors = findViewById(R.id.listTutors);
        tvEmptyTutors = findViewById(R.id.tvEmptyTutors);

        // Load tutors
        loadTutors();

        // Set up list item click listener
        listTutors.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                User tutor = tutorList.get(position);
                
                // Navigate to book session activity
                Intent intent = new Intent(TutorListActivity.this, BookSessionActivity.class);
                intent.putExtra("TUTOR_ID", tutor.getId());
                intent.putExtra("TUTOR_NAME", tutor.getFullName());
                intent.putExtra("SUBJECT_ID", subjectId);
                intent.putExtra("SUBJECT_NAME", subjectName);
                startActivity(intent);
            }
        });
    }

    /**
     * Load tutors who are available for the selected subject
     */
    private void loadTutors() {
        int studentId = sessionManager.getUserId();
        tutorList = databaseHelper.getTutorsForSubject(subjectId, studentId);
        
        if (tutorList.isEmpty()) {
            listTutors.setVisibility(View.GONE);
            tvEmptyTutors.setVisibility(View.VISIBLE);
        } else {
            listTutors.setVisibility(View.VISIBLE);
            tvEmptyTutors.setVisibility(View.GONE);
            tutorAdapter = new TutorAdapter(this, tutorList);
            listTutors.setAdapter(tutorAdapter);
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
