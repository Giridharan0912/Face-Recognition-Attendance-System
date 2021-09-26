package com.alphaverse.facerecognitionattendencesystem.ui.home;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.alphaverse.facerecognitionattendencesystem.R;
import com.alphaverse.facerecognitionattendencesystem.model.Student;
import com.alphaverse.facerecognitionattendencesystem.model.Subject;
import com.squareup.picasso.Picasso;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.List;

public class StudentsListAdapter extends RecyclerView.Adapter<StudentsListAdapter.ViewHolder> {
    private LayoutInflater layoutInflater;
    private Context context;
    private List<Student> studentList;

    public StudentsListAdapter(Context context) {
        this.context = context;
        this.layoutInflater = LayoutInflater.from(context);
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view=layoutInflater.inflate(R.layout.item_student_list,parent,false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Student current=studentList.get(position);
        holder.setStudent(current);

    }

    @Override
    public int getItemCount() {
        if (studentList != null) {
            return studentList.size();
        } else {
            return 0;
        }
    }
    public void setSubjectList(List<Student> students) {
        studentList = students;
        notifyDataSetChanged();
    }
    public class ViewHolder extends RecyclerView.ViewHolder {
        private ImageView imageView;
        private TextView stName,stRegNo;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            stName=itemView.findViewById(R.id.item_student_name);
            stRegNo=itemView.findViewById(R.id.item_student_reg_No);
            imageView=itemView.findViewById(R.id.item_student_imgView);
        }

        public void setStudent(Student student){
            stName.setText(student.getName());
            stRegNo.setText(student.getRegNo());
           Picasso.get().load(student.getImageUrl()).into(imageView);
        }

    }
}
