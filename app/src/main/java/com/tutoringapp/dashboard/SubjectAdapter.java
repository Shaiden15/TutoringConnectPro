package com.tutoringapp.dashboard;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.tutoringapp.R;
import com.tutoringapp.models.Subject;

import java.util.List;

/**
 * Adapter for displaying subjects in a grid view on the dashboard.
 */
public class SubjectAdapter extends BaseAdapter {

    private Context context;
    private List<Subject> subjectList;
    private LayoutInflater inflater;

    public SubjectAdapter(Context context, List<Subject> subjectList) {
        this.context = context;
        this.subjectList = subjectList;
        this.inflater = LayoutInflater.from(context);
    }

    @Override
    public int getCount() {
        return subjectList.size();
    }

    @Override
    public Object getItem(int position) {
        return subjectList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return subjectList.get(position).getId();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;

        if (convertView == null) {
            convertView = inflater.inflate(R.layout.item_subject, parent, false);
            
            holder = new ViewHolder();
            holder.tvSubjectName = convertView.findViewById(R.id.tvSubjectName);
            
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        Subject subject = subjectList.get(position);
        holder.tvSubjectName.setText(subject.getName());

        return convertView;
    }

    static class ViewHolder {
        TextView tvSubjectName;
    }
}
