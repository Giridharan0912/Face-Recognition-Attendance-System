package com.alphaverse.facerecognitionattendencesystem.ui.home;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.alphaverse.facerecognitionattendencesystem.R;
import com.alphaverse.facerecognitionattendencesystem.model.Subject;
import com.alphaverse.facerecognitionattendencesystem.util.CurrentUserAPI;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.List;

public class SubjectAttendanceFragment extends Fragment {
    private RecyclerView attendanceHistoryRecyclerView;
    private String date;
    private FirebaseFirestore firestore = FirebaseFirestore.getInstance();
    private DocumentReference documentReference;
    private Subject subject;
    ArrayList<String> strings = new ArrayList<>();
    private RecyclerView recyclerView;
    private SubjectAttendanceAdapter subjectAttendanceAdapter;
    private View view;
    private Context context;
    private TextView textView;

    public SubjectAttendanceFragment() {

    }

    public SubjectAttendanceFragment(Context context) {
        this.context = context;
    }

    public SubjectAttendanceFragment(String date, Subject subject, Context context) {
        this.date = date;
        this.subject = subject;
        this.context = context;
        documentReference = firestore.collection("users").document(CurrentUserAPI.getInstance().getCurrentUserUid()).collection("subjects")
                .document(subject.getSubjectId()).collection("attendance").document(date);
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_subject_attendance, container, false);
        recyclerView = view.findViewById(R.id.attendanceHistoryRv);
        textView = view.findViewById(R.id.textViewP);
        textView.setText(R.string.loading);
        documentReference.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @SuppressLint("DefaultLocale")
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                strings.addAll(documentSnapshot.getData().keySet());
                subjectAttendanceAdapter = new SubjectAttendanceAdapter(strings, context);
                recyclerView.setAdapter(subjectAttendanceAdapter);
                recyclerView.setLayoutManager(new LinearLayoutManager(context));
                float percentage = ((float) strings.size() / CurrentUserAPI.getInstance().getUserNoOfStudents()) * 100;
                textView.setText(String.format("%d/%d (%.1f%%)", strings.size(), CurrentUserAPI.getInstance().getUserNoOfStudents(), percentage));
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {

            }
        });
        return view;
    }

    class SubjectAttendanceAdapter extends RecyclerView.Adapter<ViewHolder> {
        private LayoutInflater layoutInflater;
        private List<String> stringList;
        private Context context;

        public SubjectAttendanceAdapter(List<String> stringList, Context context) {
            this.layoutInflater = LayoutInflater.from(context);
            this.stringList = stringList;
            this.context = context;
        }

        @NonNull
        @Override
        public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = layoutInflater.inflate(R.layout.item_take_attendance, parent, false);
            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
            String current = stringList.get(position);
            holder.textView.setText(current);
        }

        @Override
        public int getItemCount() {
            if (stringList != null)
                return stringList.size();
            else return 0;
        }
    }

    private class ViewHolder extends RecyclerView.ViewHolder {
        private TextView textView;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            textView = itemView.findViewById(R.id.studentDetail_tv);
        }

    }
}
