package com.example.b07_project;


import java.util.HashSet;

public class Course {

    private String courseName;
    private String courseCode;
    private String[] offeringSessions;
    private String[] prereqs;

    public Course(String courseName, String courseCode, String offeringSessions, String prereqs){
        this.courseName = courseName;
        this.courseCode = courseCode;
        this.offeringSessions = offeringSessions.split(",");
        for (int i=0;i<this.offeringSessions.length;i++) {
            this.offeringSessions[i] = this.offeringSessions[i].trim();
        }
        this.prereqs = prereqs.split(",");
        for (int i=0;i<this.prereqs.length;i++) {
            this.prereqs[i] = this.prereqs[i].trim();
        }
    }


    public String getCourseName() {
        return courseName;
    }

    public String getCourseCode() {
        return courseCode;
    }

    public String[] getOfferingSessions() {
        return offeringSessions;
    }

    public String[] getPrereqs() {
        return prereqs;
    }

    public void setCourseCode(String courseCode) {
        this.courseCode = courseCode;
    }

    public void setOfferingSessions(String[] offeringSessions) {
        this.offeringSessions = offeringSessions;
    }

    public void setPrereqs(String[] prereqs) {
        this.prereqs = prereqs;
    }

    public void setCourseName(String courseName) {
        this.courseName = courseName;
    }
}
