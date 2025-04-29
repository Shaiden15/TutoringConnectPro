package com.tutoringapp.feedback;

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

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

/**
 * Adapter for displaying sessions that need feedback.
 */
public class FeedbackSessionAdapter extends ArrayAdapter<Session> {

    private Context context;
    private List<Session> sessionList;
    private DatabaseHelper databaseHelper;
    private SimpleDateFormat dateFormat;

    public FeedbackSessionAdapter(Context context, List<Session> sessionList) {
        super(context, 0, sessionList);
        this.context = context;
        this.sessionList = sessionList;
        this.databaseHelper = new DatabaseHelper(context);
        this.dateFormat = new SimpleDateFormat("EEE, MMM d, yyyy 'at' h:mm a", Locale.getDefault());
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.item_feedback_session, parent, false);
        }

        Session session = sessionList.get(position);

        TextView tvSubjectName = convertView.findViewById(R.id.tvSubjectName);
        TextView tvSessionDate = convertView.findViewById(R.id.tvSessionDate);
        TextView tvTutorName = convertView.findViewById(R.id.tvTutorName);
        TextView tvSessionType = convertView.findViewById(R.id.tvSessionType);

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
        
        // Set session type
        tvSessionType.setText(session.isOnlineSession() ? "Online Session" : "In-Person Session");

        return convertView;
    }
}
