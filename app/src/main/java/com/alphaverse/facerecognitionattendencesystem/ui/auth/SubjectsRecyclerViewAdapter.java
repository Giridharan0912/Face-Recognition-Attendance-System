package com.alphaverse.facerecognitionattendencesystem.ui.auth;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.alphaverse.facerecognitionattendencesystem.R;
import com.alphaverse.facerecognitionattendencesystem.model.Subject;

import java.util.List;

public class SubjectsRecyclerViewAdapter extends RecyclerView.Adapter<SubjectsRecyclerViewAdapter.ViewHolder> {
    private final LayoutInflater layoutInflater;
    private List<Subject> subjectArrayList;
    private final Context context;
    private final int totalSubjects;
    private OnClickedAttendance onClickedAttendance;


    public SubjectsRecyclerViewAdapter(Context context, int totalSubjects, OnClickedAttendance clickedAttendance) {
        this.context = context;
        this.totalSubjects = totalSubjects;
        this.layoutInflater = LayoutInflater.from(context);
        this.onClickedAttendance = clickedAttendance;

    }

    public SubjectsRecyclerViewAdapter(Context context, int totalSubjects) {
        this.context = context;
        this.totalSubjects = totalSubjects;
        this.layoutInflater = LayoutInflater.from(context);

    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = layoutInflater.inflate(R.layout.item_create_subject, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Subject current = subjectArrayList.get(position);
        holder.subIdTv.setText(current.getSubjectId());
        holder.subNameTv.setText(current.getSubjectName());

    }


    @Override
    public int getItemCount() {
        if (subjectArrayList != null) {
            return subjectArrayList.size();
        } else return 0;
    }

    public void setSubjectArrayList(List<Subject> subjects) {
        subjectArrayList = subjects;
        notifyDataSetChanged();
    }

    public interface OnClickedAttendance {
        void onCLickedSubjectItem(Subject subject);
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private CardView cardView;
        private TextView subIdTv, subNameTv;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            cardView = itemView.findViewById(R.id.create_subject_cardView);
            subIdTv = itemView.findViewById(R.id.subject_code);
            subNameTv = itemView.findViewById(R.id.subject_name);
            cardView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            if (v.getId() == R.id.create_subject_cardView) {
                onClickedAttendance.onCLickedSubjectItem(subjectArrayList.get(getAdapterPosition()));
            }
        }
    }
}
