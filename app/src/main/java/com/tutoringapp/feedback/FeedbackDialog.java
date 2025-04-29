package com.tutoringapp.feedback;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.tutoringapp.R;
import com.tutoringapp.database.DatabaseHelper;
import com.tutoringapp.models.Feedback;
import com.tutoringapp.models.Session;
import com.tutoringapp.models.User;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * Dialog for submitting feedback for a tutoring session.
 */
public class FeedbackDialog extends Dialog {

    private Session session;
    private DatabaseHelper databaseHelper;
    private TextView tvSubjectName, tvSessionDate, tvTutorName;
    private RatingBar ratingBar;
    private EditText etComments;
    private Button btnSubmit, btnCancel;
    private SimpleDateFormat dateFormat;
    private OnFeedbackSubmittedListener listener;

    public interface OnFeedbackSubmittedListener {
        void onFeedbackSubmitted();
    }

    public FeedbackDialog(Context context, Session session, DatabaseHelper databaseHelper) {
        super(context);
        this.session = session;
        this.databaseHelper = databaseHelper;
        this.dateFormat = new SimpleDateFormat("EEE, MMM d, yyyy 'at' h:mm a", Locale.getDefault());
    }

    public void setOnFeedbackSubmittedListener(OnFeedbackSubmittedListener listener) {
        this.listener = listener;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.dialog_feedback);

        // Initialize views
        tvSubjectName = findViewById(R.id.tvSubjectName);
        tvSessionDate = findViewById(R.id.tvSessionDate);
        tvTutorName = findViewById(R.id.tvTutorName);
        ratingBar = findViewById(R.id.ratingBar);
        etComments = findViewById(R.id.etComments);
        btnSubmit = findViewById(R.id.btnSubmit);
        btnCancel = findViewById(R.id.btnCancel);

        // Set session details
        tvSubjectName.setText(session.getSubjectName());
        
        try {
            Date date = new Date(session.getSessionDate());
            tvSessionDate.setText(dateFormat.format(date));
        } catch (Exception e) {
            tvSessionDate.setText(String.valueOf(session.getSessionDate()));
        }
        
        // Get tutor name
        User tutor = databaseHelper.getUserById(session.getTutorId());
        if (tutor != null) {
            tvTutorName.setText("Tutor: " + tutor.getFullName());
        } else {
            tvTutorName.setText("Tutor: Unknown");
        }

        // Set up button click listeners
        btnSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                submitFeedback();
            }
        });

        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });
    }

    /**
     * Submit feedback for the session
     */
    private void submitFeedback() {
        float rating = ratingBar.getRating();
        String comments = etComments.getText().toString().trim();
        
        if (rating == 0) {
            Toast.makeText(getContext(), "Please provide a rating", Toast.LENGTH_SHORT).show();
            return;
        }
        
        if (comments.isEmpty()) {
            Toast.makeText(getContext(), "Please provide comments", Toast.LENGTH_SHORT).show();
            return;
        }
        
        // Create new feedback
        Feedback feedback = new Feedback();
        feedback.setSessionId(session.getId());
        feedback.setStudentId(session.getStudentId());
        feedback.setTutorId(session.getTutorId());
        feedback.setRating(rating);
        feedback.setComments(comments);
        feedback.setFeedbackDate(System.currentTimeMillis());
        
        // Save feedback to database
        long feedbackId = databaseHelper.addFeedback(feedback);
        
        if (feedbackId > 0) {
            // Update session's feedback status
            session.setFeedbackProvided(true);
            databaseHelper.updateSession(session);
            
            Toast.makeText(getContext(), "Feedback submitted successfully", Toast.LENGTH_SHORT).show();
            
            // Notify listener
            if (listener != null) {
                listener.onFeedbackSubmitted();
            }
            
            dismiss();
        } else {
            Toast.makeText(getContext(), "Failed to submit feedback", Toast.LENGTH_SHORT).show();
        }
    }
}
