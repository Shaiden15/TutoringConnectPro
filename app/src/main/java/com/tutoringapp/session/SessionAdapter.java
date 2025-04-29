package com.tutoringapp.session;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.tutoringapp.R;
import com.tutoringapp.database.DatabaseHelper;
import com.tutoringapp.models.Session;
import com.tutoringapp.models.User;
import com.tutoringapp.utils.SessionManager;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

/**
 * Adapter for displaying session items in a list.
 */
public class SessionAdapter extends ArrayAdapter<Session> {

    private Context context;
    private List<Session> sessionList;
    private DatabaseHelper databaseHelper;
    private SessionManager sessionManager;
    private SimpleDateFormat dateFormat;

    public SessionAdapter(Context context, List<Session> sessionList) {
        super(context, 0, sessionList);
        this.context = context;
        this.sessionList = sessionList;
        this.databaseHelper = new DatabaseHelper(context);
        this.sessionManager = new SessionManager(context);
        this.dateFormat = new SimpleDateFormat("EEE, MMM d, yyyy 'at' h:mm a", Locale.getDefault());
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.item_session, parent, false);
        }

        Session session = sessionList.get(position);

        TextView tvSubjectName = convertView.findViewById(R.id.tvSubjectName);
        TextView tvSessionDate = convertView.findViewById(R.id.tvSessionDate);
        TextView tvSessionType = convertView.findViewById(R.id.tvSessionType);
        TextView tvSessionWith = convertView.findViewById(R.id.tvSessionWith);
        TextView tvSessionStatus = convertView.findViewById(R.id.tvSessionStatus);
        TextView tvRejectionReason = convertView.findViewById(R.id.tvRejectionReason);

        // Set session details
        tvSubjectName.setText(session.getSubjectName());
        
        // Format and set session date
        try {
            Date date = new Date(session.getSessionDate());
            tvSessionDate.setText(dateFormat.format(date));
        } catch (Exception e) {
            tvSessionDate.setText(String.valueOf(session.getSessionDate()));
        }
        
        // Set session type (online/in person)
        tvSessionType.setText(session.isOnlineSession() ? "Online (MS Teams)" : "In Person (Steve-Biko Library)");
        
        // Set the name of the other person (tutor or student)
        boolean isTutor = sessionManager.isTutor();
        int userId = sessionManager.getUserId();
        
        if (isTutor && session.getTutorId() == userId) {
            // Show student name to tutor
            User student = databaseHelper.getUserById(session.getStudentId());
            if (student != null) {
                tvSessionWith.setText("Session with: " + student.getFullName());
            }
        } else {
            // Show tutor name to student
            User tutor = databaseHelper.getUserById(session.getTutorId());
            if (tutor != null) {
                tvSessionWith.setText("Session with: " + tutor.getFullName());
            }
        }
        
        // Set status with appropriate color
        tvSessionStatus.setText(session.getStatus());
        switch (session.getStatus()) {
            case "pending":
                tvSessionStatus.setTextColor(context.getResources().getColor(android.R.color.holo_blue_dark));
                break;
            case "accepted":
                tvSessionStatus.setTextColor(context.getResources().getColor(android.R.color.holo_green_dark));
                break;
            case "rejected":
                tvSessionStatus.setTextColor(context.getResources().getColor(android.R.color.holo_red_dark));
                break;
            case "completed":
                tvSessionStatus.setTextColor(context.getResources().getColor(android.R.color.darker_gray));
                break;
        }
        
        // Show rejection reason if applicable
        if ("rejected".equals(session.getStatus()) && session.getRejectionReason() != null) {
            tvRejectionReason.setVisibility(View.VISIBLE);
            tvRejectionReason.setText("Reason: " + session.getRejectionReason());
        } else {
            tvRejectionReason.setVisibility(View.GONE);
        }

        return convertView;
    }
}
