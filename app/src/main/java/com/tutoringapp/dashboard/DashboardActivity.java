package com.tutoringapp.dashboard;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import com.google.android.material.navigation.NavigationView;
import com.tutoringapp.R;
import com.tutoringapp.authentication.LoginActivity;
import com.tutoringapp.chatbot.ChatbotActivity;
import com.tutoringapp.database.DatabaseHelper;
import com.tutoringapp.feedback.FeedbackActivity;
import com.tutoringapp.models.Subject;
import com.tutoringapp.payments.PaymentsActivity;
import com.tutoringapp.profile.ProfileActivity;
import com.tutoringapp.session.SessionsActivity;
import com.tutoringapp.session.TutorListActivity;
import com.tutoringapp.utils.SessionManager;

import java.util.ArrayList;
import java.util.List;

/**
 * DashboardActivity displays subjects from the user's course
 * and provides navigation to other app sections.
 */
public class DashboardActivity extends AppCompatActivity 
        implements NavigationView.OnNavigationItemSelectedListener {

    private DrawerLayout drawerLayout;
    private NavigationView navigationView;
    private Toolbar toolbar;
    private GridView gridSubjects;
    private SessionManager sessionManager;
    private DatabaseHelper databaseHelper;
    private SubjectAdapter subjectAdapter;
    private List<Subject> subjectList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);

        // Initialize session manager and database helper
        sessionManager = new SessionManager(this);
        databaseHelper = new DatabaseHelper(this);

        // Check if user is logged in
        if (!sessionManager.isLoggedIn()) {
            startActivity(new Intent(this, LoginActivity.class));
            finish();
            return;
        }

        // Set up toolbar
        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Dashboard");

        // Set up navigation drawer
        drawerLayout = findViewById(R.id.drawerLayout);
        navigationView = findViewById(R.id.navigationView);
        navigationView.setNavigationItemSelectedListener(this);

        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawerLayout, toolbar, 
                R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();

        // Update navigation header with user information
        View headerView = navigationView.getHeaderView(0);
        TextView tvUserName = headerView.findViewById(R.id.tvUserName);
        TextView tvUserEmail = headerView.findViewById(R.id.tvUserEmail);
        tvUserName.setText(sessionManager.getUserFullName());
        tvUserEmail.setText(sessionManager.getUserEmail());

        // Set up grid view for subjects
        gridSubjects = findViewById(R.id.gridSubjects);
        loadSubjects();

        // Set up grid item click listener
        gridSubjects.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Subject subject = subjectList.get(position);
                Intent intent = new Intent(DashboardActivity.this, TutorListActivity.class);
                intent.putExtra("SUBJECT_ID", subject.getId());
                intent.putExtra("SUBJECT_NAME", subject.getName());
                startActivity(intent);
            }
        });

        // Set up help button for chatbot
        findViewById(R.id.fabHelp).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(DashboardActivity.this, ChatbotActivity.class));
            }
        });
    }

    /**
     * Load subjects from the database based on user's course code
     */
    private void loadSubjects() {
        String courseCode = sessionManager.getUserCourseCode();
        subjectList = databaseHelper.getSubjectsByCourse(courseCode);
        
        // If no subjects found, create some default ones for the course
        if (subjectList.isEmpty()) {
            createDefaultSubjects(courseCode);
            subjectList = databaseHelper.getSubjectsByCourse(courseCode);
        }
        
        subjectAdapter = new SubjectAdapter(this, subjectList);
        gridSubjects.setAdapter(subjectAdapter);
    }

    /**
     * Create default subjects for a course if none exist
     */
    private void createDefaultSubjects(String courseCode) {
        List<String> subjectNames = new ArrayList<>();
        
        // Add default subjects based on course code
        if (courseCode.startsWith("CS") || courseCode.startsWith("INF")) {
            // Computer Science or Informatics courses
            subjectNames.add("Programming I");
            subjectNames.add("Data Structures");
            subjectNames.add("Algorithms");
            subjectNames.add("Database Systems");
            subjectNames.add("Web Development");
            subjectNames.add("Mobile Development");
        } else if (courseCode.startsWith("ENG")) {
            // Engineering courses
            subjectNames.add("Calculus I");
            subjectNames.add("Physics I");
            subjectNames.add("Mechanics");
            subjectNames.add("Thermodynamics");
            subjectNames.add("Electrical Circuits");
            subjectNames.add("Engineering Design");
        } else if (courseCode.startsWith("BUS") || courseCode.startsWith("COM")) {
            // Business or Commerce courses
            subjectNames.add("Accounting I");
            subjectNames.add("Economics I");
            subjectNames.add("Management");
            subjectNames.add("Marketing");
            subjectNames.add("Business Law");
            subjectNames.add("Finance");
        } else {
            // Generic subjects for other courses
            subjectNames.add("Academic Writing");
            subjectNames.add("Research Methods");
            subjectNames.add("Critical Thinking");
            subjectNames.add("Communication Skills");
            subjectNames.add("Ethics");
            subjectNames.add("Professional Development");
        }
        
        // Add subjects to database
        for (String name : subjectNames) {
            Subject subject = new Subject();
            subject.setName(name);
            subject.setCourseCode(courseCode);
            databaseHelper.addSubject(subject);
        }
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.navProfile) {
            startActivity(new Intent(this, ProfileActivity.class));
        } else if (id == R.id.navSessions) {
            startActivity(new Intent(this, SessionsActivity.class));
        } else if (id == R.id.navPayments) {
            startActivity(new Intent(this, PaymentsActivity.class));
        } else if (id == R.id.navFeedback) {
            startActivity(new Intent(this, FeedbackActivity.class));
        } else if (id == R.id.navChatbot) {
            startActivity(new Intent(this, ChatbotActivity.class));
        } else if (id == R.id.navLogout) {
            sessionManager.logoutUser();
            startActivity(new Intent(this, LoginActivity.class));
            finish();
        }

        drawerLayout.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    public void onBackPressed() {
        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }
}
