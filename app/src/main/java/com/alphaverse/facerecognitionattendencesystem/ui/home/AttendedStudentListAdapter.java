package com.alphaverse.facerecognitionattendencesystem.ui.home;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.alphaverse.facerecognitionattendencesystem.R;
import com.alphaverse.facerecognitionattendencesystem.model.Student;

import java.util.List;

public class AttendedStudentListAdapter extends RecyclerView.Adapter<AttendedStudentListAdapter.ViewHolder> {
    private List<Student> students;
    private Context context;
    private LayoutInflater layoutInflater;

    public AttendedStudentListAdapter(Context context) {
        this.context = context;
        this.layoutInflater = LayoutInflater.from(context);
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = layoutInflater.inflate(R.layout.item_take_attendance, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Student current = students.get(position);
        holder.setAttendance(current);
    }


    @Override
    public int getItemCount() {
        if (students != null)
            return students.size();
        else
            return 0;
    }

    public void setStudents(List<Student> studentList) {
        this.students = studentList;
        notifyDataSetChanged();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private TextView studentDetails, attendance;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            studentDetails = itemView.findViewById(R.id.studentDetail_tv);
            attendance = itemView.findViewById(R.id.attendance_tv);
        }

        public void setAttendance(Student student) {
            studentDetails.setText(String.format("%s-%s", student.getRegNo(), student.getName()));
            attendance.setText(R.string.attendancePresent);
        }

    }
}

