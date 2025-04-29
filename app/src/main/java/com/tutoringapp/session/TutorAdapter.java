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
import com.tutoringapp.models.User;

import java.util.List;

/**
 * Adapter for displaying tutor items in a list.
 */
public class TutorAdapter extends ArrayAdapter<User> {

    private Context context;
    private List<User> tutorList;

    public TutorAdapter(Context context, List<User> tutorList) {
        super(context, 0, tutorList);
        this.context = context;
        this.tutorList = tutorList;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.item_tutor, parent, false);
        }

        User tutor = tutorList.get(position);

        TextView tvTutorName = convertView.findViewById(R.id.tvTutorName);
        TextView tvTutorCourse = convertView.findViewById(R.id.tvTutorCourse);
        TextView tvTutorYear = convertView.findViewById(R.id.tvTutorYear);

        tvTutorName.setText(tutor.getFullName());
        tvTutorCourse.setText("Course: " + tutor.getCourseCode());
        tvTutorYear.setText("Year: " + tutor.getYearOfStudy());

        return convertView;
    }
}
