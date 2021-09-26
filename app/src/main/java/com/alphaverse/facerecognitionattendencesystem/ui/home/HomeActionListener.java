package com.alphaverse.facerecognitionattendencesystem.ui.home;

import com.alphaverse.facerecognitionattendencesystem.model.Subject;

public interface HomeActionListener {
    void studentsListClicked();
    void attendanceCLicked(Subject subject);
    void createStudentClicked();
    void takeAttendanceClicked(String date,Subject subject);
    void goToAttendanceHistoryItem(String date,Subject subject);
}
