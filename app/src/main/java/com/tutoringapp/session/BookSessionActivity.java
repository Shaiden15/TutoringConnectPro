package com.tutoringapp.session;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.tutoringapp.R;
import com.tutoringapp.database.DatabaseHelper;
import com.tutoringapp.models.Session;
import com.tutoringapp.utils.NotificationHelper;
import com.tutoringapp.utils.SessionManager;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

/**
 * BookSessionActivity allows students to book tutoring sessions.
 */
public class BookSessionActivity extends AppCompatActivity {

    private TextView tvTutorName, tvSubjectName, tvSelectedDate;
    private RadioGroup rgSessionType;
    private RadioButton rbOnline, rbInPerson;
    private Button btnSelectDate, btnBookSession;
    
    private int tutorId;
    private String tutorName;
    private int subjectId;
    private String subjectName;
    
    private Calendar selectedDateTime;
    private boolean isOnlineSession = true;
    
    private DatabaseHelper databaseHelper;
    private SessionManager sessionManager;
    private NotificationHelper notificationHelper;
    private SimpleDateFormat dateTimeFormat;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_book_session);

        // Get data from intent
        tutorId = getIntent().getIntExtra("TUTOR_ID", -1);
        tutorName = getIntent().getStringExtra("TUTOR_NAME");
        subjectId = getIntent().getIntExtra("SUBJECT_ID", -1);
        subjectName = getIntent().getStringExtra("SUBJECT_NAME");

        if (tutorId == -1 || subjectId == -1) {
            finish();
            return;
        }

        // Initialize helpers
        databaseHelper = new DatabaseHelper(this);
        sessionManager = new SessionManager(this);
        notificationHelper = new NotificationHelper(this);
        dateTimeFormat = new SimpleDateFormat("EEE, MMM d, yyyy 'at' h:mm a", Locale.getDefault());
        selectedDateTime = Calendar.getInstance();
        
        // Set future time (add 1 day)
        selectedDateTime.add(Calendar.DAY_OF_MONTH, 1);

        // Set up toolbar
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Book Session");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        // Initialize views
        tvTutorName = findViewById(R.id.tvTutorName);
        tvSubjectName = findViewById(R.id.tvSubjectName);
        tvSelectedDate = findViewById(R.id.tvSelectedDate);
        rgSessionType = findViewById(R.id.rgSessionType);
        rbOnline = findViewById(R.id.rbOnline);
        rbInPerson = findViewById(R.id.rbInPerson);
        btnSelectDate = findViewById(R.id.btnSelectDate);
        btnBookSession = findViewById(R.id.btnBookSession);

        // Set initial values
        tvTutorName.setText("Tutor: " + tutorName);
        tvSubjectName.setText("Subject: " + subjectName);
        updateSelectedDateText();

        // Set up radio group listener
        rgSessionType.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                isOnlineSession = (checkedId == R.id.rbOnline);
            }
        });

        // Set up date selection button
        btnSelectDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDateTimePicker();
            }
        });

        // Set up book session button
        btnBookSession.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                bookSession();
            }
        });
    }

    /**
     * Update the text showing the selected date and time
     */
    private void updateSelectedDateText() {
        tvSelectedDate.setText("Selected: " + dateTimeFormat.format(selectedDateTime.getTime()));
    }

    /**
     * Show date picker followed by time picker
     */
    private void showDateTimePicker() {
        // Get current values
        final Calendar currentDate = Calendar.getInstance();
        int year = selectedDateTime.get(Calendar.YEAR);
        int month = selectedDateTime.get(Calendar.MONTH);
        int day = selectedDateTime.get(Calendar.DAY_OF_MONTH);

        // Date picker
        DatePickerDialog datePickerDialog = new DatePickerDialog(
                this, new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                selectedDateTime.set(Calendar.YEAR, year);
                selectedDateTime.set(Calendar.MONTH, month);
                selectedDateTime.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                
                // After date is set, show time picker
                showTimePicker();
            }
        }, year, month, day);

        // Only allow future dates
        datePickerDialog.getDatePicker().setMinDate(currentDate.getTimeInMillis());
        datePickerDialog.show();
    }

    /**
     * Show time picker dialog
     */
    private void showTimePicker() {
        int hour = selectedDateTime.get(Calendar.HOUR_OF_DAY);
        int minute = selectedDateTime.get(Calendar.MINUTE);

        TimePickerDialog timePickerDialog = new TimePickerDialog(
                this, new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                selectedDateTime.set(Calendar.HOUR_OF_DAY, hourOfDay);
                selectedDateTime.set(Calendar.MINUTE, minute);
                
                // Update displayed date
                updateSelectedDateText();
            }
        }, hour, minute, false);

        timePickerDialog.show();
    }

    /**
     * Book a session with the selected tutor
     */
    private void bookSession() {
        // Ensure selected time is in the future
        if (selectedDateTime.getTimeInMillis() <= System.currentTimeMillis()) {
            Toast.makeText(this, "Please select a future date and time", Toast.LENGTH_SHORT).show();
            return;
        }

        // Create new session
        Session session = new Session();
        session.setStudentId(sessionManager.getUserId());
        session.setTutorId(tutorId);
        session.setSubjectId(subjectId);
        session.setSubjectName(subjectName);
        session.setSessionDate(selectedDateTime.getTimeInMillis());
        session.setOnlineSession(isOnlineSession);
        session.setStatus("pending");
        session.setBookingDate(System.currentTimeMillis());
        session.setFeedbackProvided(false);

        // Save to database
        long sessionId = databaseHelper.addSession(session);
        if (sessionId > 0) {
            // Send notification to tutor
            String studentName = sessionManager.getUserFullName();
            String message = studentName + " has requested a tutoring session for " + 
                    subjectName + " on " + dateTimeFormat.format(selectedDateTime.getTime());
            notificationHelper.sendNotification(tutorId, "New Session Request", message);
            
            Toast.makeText(this, "Session booked successfully", Toast.LENGTH_SHORT).show();
            finish();
        } else {
            Toast.makeText(this, "Failed to book session", Toast.LENGTH_SHORT).show();
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
