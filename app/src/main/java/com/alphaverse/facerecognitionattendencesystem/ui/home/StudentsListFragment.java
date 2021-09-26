package com.alphaverse.facerecognitionattendencesystem.ui.home;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.alphaverse.facerecognitionattendencesystem.R;
import com.alphaverse.facerecognitionattendencesystem.model.Student;
import com.alphaverse.facerecognitionattendencesystem.util.CurrentUserAPI;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;


public class StudentsListFragment extends Fragment {
    private View studentsListView;
    private HomeActionListener homeActionListener;
    private FirebaseFirestore firebaseFirestore = FirebaseFirestore.getInstance();
    private CollectionReference collectionReference;
    private RecyclerView studentsListRv;
    private StudentsListAdapter studentsListAdapter;
    private ProgressBar progressBar;
    private Context context;
    private TextView textView;

    public StudentsListFragment(Context context) {
        // Required empty public constructor
        this.context = context;
    }

    public StudentsListFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        studentsListView = inflater.inflate(R.layout.fragment_students_list, container, false);
        Toolbar toolbar = studentsListView.findViewById(R.id.students_toolbar);
        ((AppCompatActivity) getActivity()).setSupportActionBar(toolbar);
        studentsListRv = studentsListView.findViewById(R.id.studentList_rv);
        textView = studentsListView.findViewById(R.id.toolbar_subtitle);
        progressBar = studentsListView.findViewById(R.id.editStudentsListProgress);
        progressBar.setVisibility(View.VISIBLE);
        collectionReference = firebaseFirestore.collection("users").document(CurrentUserAPI.getInstance().getCurrentUserUid())
                .collection("students");
        collectionReference.get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                if (!queryDocumentSnapshots.isEmpty()) {
                    ArrayList<Student> studentsArrayList = new ArrayList<>();
                    for (QueryDocumentSnapshot snapshot : queryDocumentSnapshots) {
                        Student student = snapshot.toObject(Student.class);
                        studentsArrayList.add(student);
                    }
                    setStudentsListView(studentsArrayList);

                    textView.setText(studentsArrayList.size() + " out of " + CurrentUserAPI.getInstance().getUserNoOfStudents() + " students are added");
                    textView.setVisibility(View.VISIBLE);
                    progressBar.setVisibility(View.INVISIBLE);
                }

            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {

            }
        });
        return studentsListView;
    }

    public void setHomeActionListener(Context context) {
        this.homeActionListener = (HomeActionListener) context;
    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        inflater.inflate(R.menu.home_menu, menu);
        super.onCreateOptionsMenu(menu, inflater);


    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_edit_home:
                homeActionListener.createStudentClicked();
                break;
            case R.id.action_logout_home:
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    private void setStudentsListView(List<Student> studentsListView) {
        studentsListAdapter = new StudentsListAdapter(context);
        studentsListRv.setAdapter(studentsListAdapter);
        studentsListRv.setLayoutManager(new LinearLayoutManager(context));
        studentsListAdapter.setSubjectList(studentsListView);
    }

    @Override
    public void onResume() {
        super.onResume();

    }
}