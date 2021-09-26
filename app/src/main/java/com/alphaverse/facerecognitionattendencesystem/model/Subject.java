package com.alphaverse.facerecognitionattendencesystem.model;

public class Subject {
    private String subjectUniqueId;
    private String subjectId;
    private String subjectName;
    private String totalClasses;
    private String classConducted;
    private String subjectStaff;

    public String getSubjectUniqueId() {
        return subjectUniqueId;
    }

    public void setSubjectUniqueId(String subjectUniqueId) {
        this.subjectUniqueId = subjectUniqueId;
    }

    public String getSubjectId() {
        return subjectId;
    }

    public void setSubjectId(String subjectId) {
        this.subjectId = subjectId;
    }

    public String getSubjectName() {
        return subjectName;
    }

    public void setSubjectName(String subjectName) {
        this.subjectName = subjectName;
    }

    public String getTotalClasses() {
        return totalClasses;
    }

    public void setTotalClasses(String totalClasses) {
        this.totalClasses = totalClasses;
    }

    public String getClassConducted() {
        return classConducted;
    }

    public void setClassConducted(String classConducted) {
        this.classConducted = classConducted;
    }

    public String getSubjectStaff() {
        return subjectStaff;
    }

    public void setSubjectStaff(String subjectStaff) {
        this.subjectStaff = subjectStaff;
    }
}
