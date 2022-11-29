package com.example.b07_project;


import java.util.HashMap;
import java.util.HashSet;

public class Course {

    private String courseName;
    private String courseCode;
    private HashMap<String, String> offeringSessions;
    private HashMap<String, String> prereqs;

    public Course(String courseName, String courseCode, String offeringSessions, String prereqs){
        this.courseName = courseName;
        this.courseCode = courseCode;
        String[] temp1 = offeringSessions.split(",");
        this.offeringSessions = new HashMap<>();
        this.prereqs = new HashMap<>();
        for (int i=0;i<temp1.length;i++) {
            this.offeringSessions.put("OfferingSession " + i, temp1[i].trim());
        }
        String[] temp2 = prereqs.split(",");
        for (int i=0;i<temp2.length;i++) {
            this.prereqs.put("Prerequisite " + i, temp2[i].trim());
        }
    }


    public String getCourseName() {
        return courseName;
    }

    public String getCourseCode() {
        return courseCode;
    }

    public HashMap<String, String> getOfferingSessions() {
        return offeringSessions;
    }

    public HashMap<String, String> getPrereqs() {
        return prereqs;
    }

    public void setCourseCode(String courseCode) {
        this.courseCode = courseCode;
    }

    public void setOfferingSessions(HashMap<String, String> offeringSessions) {
        this.offeringSessions = offeringSessions;
    }

    public void setPrereqs(HashMap<String, String> prereqs) {
        this.prereqs = prereqs;
    }

    public void setCourseName(String courseName) {
        this.courseName = courseName;
    }
}
