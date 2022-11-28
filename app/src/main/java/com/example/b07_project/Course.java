package com.example.b07_project;


import java.util.HashSet;

public class Course {

    private String courseName;
    private String courseCode;
    private HashSet<String> offeringSessions;
    private HashSet<String> prereqs;

    private Course(String courseName, String courseCode, HashSet<String> offeringSessions, HashSet<String> prereqs){
        this.courseName = courseName;
        this.courseCode = courseCode;
        this.offeringSessions = new HashSet<String>();
        this.offeringSessions = (HashSet<String>) offeringSessions.clone();
        this.prereqs = new HashSet<String>();
        this.prereqs = (HashSet<String>) prereqs.clone();
    }


    public String getCourseName() {
        return courseName;
    }

    public String getCourseCode() {
        return courseCode;
    }

    public HashSet<String> getOfferingSessions() {
        return offeringSessions;
    }

    public HashSet<String> getPrereqs() {
        return prereqs;
    }

    public void setCourseCode(String courseCode) {
        this.courseCode = courseCode;
    }

    public void setOfferingSessions(HashSet<String> offeringSessions) {
        this.offeringSessions = offeringSessions;
    }

    public void setPrereqs(HashSet<String> prereqs) {
        this.prereqs = prereqs;
    }

    public void setCourseName(String courseName) {
        this.courseName = courseName;
    }
}
