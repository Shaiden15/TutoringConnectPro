package com.tutoringapp.session;

import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;

import com.tutoringapp.R;
import com.tutoringapp.database.DatabaseHelper;
import com.tutoringapp.models.Session;
import com.tutoringapp.utils.NotificationHelper;
import com.tutoringapp.utils.SessionManager;

import java.util.List;

/**
 * Fragment to display sessions filtered by status.
 */
public class SessionListFragment extends Fragment {

    private static final String ARG_STATUS = "status";
    
    private String status;
    private ListView listSessions;
    private TextView tvEmptyList;
    private SessionAdapter sessionAdapter;
    private DatabaseHelper databaseHelper;
    private SessionManager sessionManager;
    private NotificationHelper notificationHelper;
    private List<Session> sessionList;

    public SessionListFragment() {
        // Required empty public constructor
    }

    /**
     * Create a new instance of the fragment with status parameter
     */
    public static SessionListFragment newInstance(String status) {
        SessionListFragment fragment = new SessionListFragment();
        Bundle args = new Bundle();
        args.putString(ARG_STATUS, status);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            status = getArguments().getString(ARG_STATUS);
        }
        
        databaseHelper = new DatabaseHelper(getContext());
        sessionManager = new SessionManager(getContext());
        notificationHelper = new NotificationHelper(getContext());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_session_list, container, false);
        
        listSessions = view.findViewById(R.id.listSessions);
        tvEmptyList = view.findViewById(R.id.tvEmptyList);
        
        // Load sessions
        loadSessions();
        
        // Set up list item click listener for actions based on user role and session status
        listSessions.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Session session = sessionList.get(position);
                handleSessionClick(session);
            }
        });
        
        return view;
    }

    /**
     * Load sessions based on user role and selected status
     */
    private void loadSessions() {
        int userId = sessionManager.getUserId();
        boolean isTutor = sessionManager.isTutor();
        
        if (isTutor) {
            // Load sessions where the user is the tutor with the given status
            sessionList = databaseHelper.getSessionsByTutorIdAndStatus(userId, status);
        } else {
            // Load sessions where the user is the student with the given status
            sessionList = databaseHelper.getSessionsByStudentIdAndStatus(userId, status);
        }
        
        if (sessionList.isEmpty()) {
            listSessions.setVisibility(View.GONE);
            tvEmptyList.setVisibility(View.VISIBLE);
            tvEmptyList.setText("No " + status + " sessions found.");
        } else {
            listSessions.setVisibility(View.VISIBLE);
            tvEmptyList.setVisibility(View.GONE);
            sessionAdapter = new SessionAdapter(getContext(), sessionList);
            listSessions.setAdapter(sessionAdapter);
        }
    }

    /**
     * Handle actions when a session is clicked
     */
    private void handleSessionClick(final Session session) {
        boolean isTutor = sessionManager.isTutor();
        
        // Different actions based on user role and session status
        if (isTutor && "pending".equals(status)) {
            // Tutor can accept or reject pending sessions
            showTutorActionDialog(session);
        } else if (!isTutor && "completed".equals(status)) {
            // Student can provide feedback for completed sessions
            if (session.getFeedbackProvided()) {
                Toast.makeText(getContext(), "Feedback already provided", Toast.LENGTH_SHORT).show();
            } else {
                // Navigate to feedback activity
                // This will be implemented in the FeedbackActivity
                Toast.makeText(getContext(), "Please provide feedback from the Feedback tab", Toast.LENGTH_SHORT).show();
            }
        }
    }

    /**
     * Show dialog for tutor to accept or reject a session
     */
    private void showTutorActionDialog(final Session session) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setTitle("Session Action");
        builder.setMessage("Do you want to accept this tutoring session?");
        
        // Accept button
        builder.setPositiveButton("Accept", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                session.setStatus("accepted");
                databaseHelper.updateSession(session);
                
                // Send notification to student
                String message = "Your session for " + session.getSubjectName() + 
                        " has been accepted by " + sessionManager.getUserFullName();
                notificationHelper.sendNotification(session.getStudentId(), "Session Accepted", message);
                
                // Refresh the list
                loadSessions();
                Toast.makeText(getContext(), "Session accepted", Toast.LENGTH_SHORT).show();
            }
        });
        
        // Reject button
        builder.setNegativeButton("Reject", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                showRejectionReasonDialog(session);
            }
        });
        
        // Cancel button
        builder.setNeutralButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        
        builder.show();
    }

    /**
     * Show dialog to input rejection reason
     */
    private void showRejectionReasonDialog(final Session session) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setTitle("Rejection Reason");
        builder.setMessage("Please provide a reason for rejecting this session:");
        
        // Add text input for rejection reason
        final EditText input = new EditText(getContext());
        builder.setView(input);
        
        // Submit button
        builder.setPositiveButton("Submit", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String reason = input.getText().toString().trim();
                
                if (reason.isEmpty()) {
                    Toast.makeText(getContext(), "Please provide a reason", Toast.LENGTH_SHORT).show();
                    return;
                }
                
                session.setStatus("rejected");
                session.setRejectionReason(reason);
                databaseHelper.updateSession(session);
                
                // Send notification to student
                String message = "Your session for " + session.getSubjectName() + 
                        " has been rejected. Reason: " + reason;
                notificationHelper.sendNotification(session.getStudentId(), "Session Rejected", message);
                
                // Refresh the list
                loadSessions();
                Toast.makeText(getContext(), "Session rejected", Toast.LENGTH_SHORT).show();
            }
        });
        
        // Cancel button
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        
        builder.show();
    }

    @Override
    public void onResume() {
        super.onResume();
        // Refresh sessions when fragment becomes visible
        loadSessions();
    }
}
