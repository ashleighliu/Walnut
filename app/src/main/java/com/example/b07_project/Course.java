package com.example.b07_project;


import java.util.HashSet;

public class Course {

    private String courseName;
    private String courseCode;
    private HashSet<String> offeringSessions;
    private HashSet<String> prereqs;

    public Course(String courseName, String courseCode, String offeringSessions, String prereqs){
        this.courseName = courseName;
        this.courseCode = courseCode;
        HashSet<String> courseSessions = new HashSet<String>();
        HashSet<String> coursePrereqs = new HashSet<String>();
        for (String s:
             offeringSessions.split(",")) {
            courseSessions.add(s);
        }
        for (String p:
                prereqs.split(",")) {
            coursePrereqs.add(p);
        }
        this.offeringSessions = courseSessions;
        this.prereqs = coursePrereqs;
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
