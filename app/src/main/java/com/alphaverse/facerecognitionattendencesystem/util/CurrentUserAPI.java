package com.alphaverse.facerecognitionattendencesystem.util;

import android.app.Application;

import com.alphaverse.facerecognitionattendencesystem.model.Subject;

import java.util.List;

public class CurrentUserAPI extends Application {
    private String currentUserUid;
    private String userEmail;
    private String userClassSection;
    private int userYear;
    private int userNoOfStudents;
    private String userName;
    private int userTotalSubjects;
    private List<Subject> subjectList;

    public int getUserNoOfStudents() {
        return userNoOfStudents;
    }

    public void setUserNoOfStudents(int userNoOfStudents) {
        this.userNoOfStudents = userNoOfStudents;
    }

    public int getUserTotalSubjects() {
        return userTotalSubjects;
    }

    public void setUserTotalSubjects(int userTotalSubjects) {
        this.userTotalSubjects = userTotalSubjects;
    }

    public List<Subject> getSubjectList() {
        return subjectList;
    }

    public void setSubjectList(List<Subject> subjectList) {
        this.subjectList = subjectList;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    private static CurrentUserAPI instance;
    public static CurrentUserAPI getInstance() {
        if (instance == null) {
            instance = new CurrentUserAPI();
        }
        return instance;
    }

    public CurrentUserAPI() {
    }

    public String getCurrentUserUid() {
        return currentUserUid;
    }

    public void setCurrentUserUid(String currentUserUid) {
        this.currentUserUid = currentUserUid;
    }

    public String getUserEmail() {
        return userEmail;
    }

    public void setUserEmail(String userEmail) {
        this.userEmail = userEmail;
    }

    public String getUserClassSection() {
        return userClassSection;
    }

    public void setUserClassSection(String userClassSection) {
        this.userClassSection = userClassSection;
    }

    public int getUserYear() {
        return userYear;
    }

    public void setUserYear(int userYear) {
        this.userYear = userYear;
    }





}
