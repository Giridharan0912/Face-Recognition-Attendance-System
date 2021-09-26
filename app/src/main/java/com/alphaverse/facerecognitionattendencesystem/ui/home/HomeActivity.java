package com.alphaverse.facerecognitionattendencesystem.ui.home;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.alphaverse.facerecognitionattendencesystem.R;
import com.alphaverse.facerecognitionattendencesystem.model.Subject;

public class HomeActivity extends AppCompatActivity implements HomeActionListener {
    private FragmentManager fragmentManager;
    private FragmentTransaction fragmentTransaction;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        fragmentManager = getSupportFragmentManager();
        initSubjectFrag();
    }

    private void initSubjectFrag() {
        fragmentTransaction = fragmentManager.beginTransaction();
        SubjectFragment subjectFragment = new SubjectFragment(this);
        subjectFragment.setHomeActionListenerSubjectFrag(this);
        fragmentTransaction.addToBackStack("homeFrag");
        fragmentTransaction.add(R.id.frame_layout_home, subjectFragment).commit();
    }

    private void initAttendanceFrag(Subject subject) {
        fragmentTransaction = fragmentManager.beginTransaction();
        AttendanceFragment attendanceFragment = new AttendanceFragment(subject, this);
        attendanceFragment.setHomeActionListener(this);
        fragmentTransaction.addToBackStack("homeFrag");
        fragmentTransaction.replace(R.id.frame_layout_home, attendanceFragment).commit();
    }

    @Override
    public void studentsListClicked() {
        initStudentsListFrag();
    }

    @Override
    public void attendanceCLicked(Subject subject) {
        initAttendanceFrag(subject);
    }

    @Override
    public void createStudentClicked() {
        fragmentTransaction = fragmentManager.beginTransaction();
        AddStudentsFragment addStudentsFragment = new AddStudentsFragment(this);
        addStudentsFragment.setHomeActionListener(this);
        fragmentTransaction.replace(R.id.frame_layout_home, addStudentsFragment).commit();
    }

    @Override
    public void takeAttendanceClicked(String date, Subject subject) {
        fragmentTransaction = fragmentManager.beginTransaction();
        TakeAttendanceFragment takeAttendanceFragment = new TakeAttendanceFragment(date, subject, this);
        takeAttendanceFragment.setHomeActionListener(this);
        fragmentTransaction.addToBackStack("homeFrag");
        fragmentTransaction.replace(R.id.frame_layout_home, takeAttendanceFragment).commit();
    }

    @Override
    public void goToAttendanceHistoryItem(String date, Subject subject) {
        fragmentTransaction = fragmentManager.beginTransaction();
        SubjectAttendanceFragment subjectAttendanceFragment = new SubjectAttendanceFragment(date, subject, this);
        fragmentTransaction.addToBackStack("homeFrag");
        fragmentTransaction.replace(R.id.frame_layout_home, subjectAttendanceFragment).commit();
    }

    private void initStudentsListFrag() {
        fragmentTransaction = fragmentManager.beginTransaction();
        StudentsListFragment studentsListFragment = new StudentsListFragment(this);
        studentsListFragment.setHomeActionListener(this);
        fragmentTransaction.addToBackStack("homeFrag");
        fragmentTransaction.replace(R.id.frame_layout_home, studentsListFragment).commit();
    }
}