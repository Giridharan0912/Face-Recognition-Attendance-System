package com.alphaverse.facerecognitionattendencesystem.ui.home;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.alphaverse.facerecognitionattendencesystem.R;
import com.alphaverse.facerecognitionattendencesystem.model.Subject;
import com.alphaverse.facerecognitionattendencesystem.ui.auth.AuthActivity;
import com.alphaverse.facerecognitionattendencesystem.ui.auth.SubjectsRecyclerViewAdapter;
import com.alphaverse.facerecognitionattendencesystem.util.CurrentUserAPI;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.List;


public class SubjectFragment extends Fragment implements SubjectsRecyclerViewAdapter.OnClickedAttendance {
    private List<Subject> subjectArrayList;
    private Context context;
    private FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
    private FirebaseUser firebaseUser;
    private HomeActionListener homeActionListener;
    private RecyclerView subjectRv;
    private View subjectListView;
    private SubjectsRecyclerViewAdapter.OnClickedAttendance onClickedAttendance = this::onCLickedSubjectItem;

    public SubjectFragment() {
        // Required empty public constructor
    }

    public SubjectFragment(Context context) {
        this.context = context;
        this.subjectArrayList = CurrentUserAPI.getInstance().getSubjectList();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        firebaseUser = firebaseAuth.getCurrentUser();

    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        subjectListView = inflater.inflate(R.layout.fragment_subject, container, false);
        Toolbar toolbar = subjectListView.findViewById(R.id.toolbar_subject);
        ((AppCompatActivity) getActivity()).setSupportActionBar(toolbar);
        subjectRv = subjectListView.findViewById(R.id.subjects_rv);
        return subjectListView;
    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        inflater.inflate(R.menu.main_menu, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_students:
                homeActionListener.studentsListClicked();

                break;
            case R.id.action_logout_main:
                if (firebaseUser != null && firebaseAuth != null) {
                    firebaseAuth.signOut();
                    startActivity(new Intent(getActivity(), AuthActivity.class));
                    getActivity().finish();

                }

                break;

        }
        return super.onOptionsItemSelected(item);
    }

    public void setHomeActionListenerSubjectFrag(Context context) {
        this.homeActionListener = (HomeActionListener) context;
    }

    private void setSubjectRv() {
        SubjectsRecyclerViewAdapter subjectsRecyclerViewAdapter = new SubjectsRecyclerViewAdapter(context, CurrentUserAPI.getInstance().getUserTotalSubjects(), onClickedAttendance);
        subjectRv.setAdapter(subjectsRecyclerViewAdapter);
        subjectRv.setLayoutManager(new LinearLayoutManager(getContext()));
        subjectsRecyclerViewAdapter.setSubjectArrayList(subjectArrayList);
    }

    @Override
    public void onCLickedSubjectItem(Subject subject) {
        homeActionListener.attendanceCLicked(subject);
    }

    @Override
    public void onResume() {
        super.onResume();
        setSubjectRv();
    }
}