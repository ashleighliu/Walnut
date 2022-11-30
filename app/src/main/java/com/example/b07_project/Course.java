package com.example.b07_project;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;

public class Course {

    private String courseName;
    private String courseCode;
    private ArrayList<String> offeringSessions;
    private ArrayList<String> prereqs;

    public Course(String courseName, String courseCode, String offeringSessions, String prereqs){
        this.courseName = courseName;
        this.courseCode = courseCode;
        String[] temp1 = offeringSessions.split(",");
        this.offeringSessions = new ArrayList<String>();
        this.prereqs = new ArrayList<String>();
        for (int i=0;i<temp1.length;i++) {
            this.offeringSessions.add(temp1[i].trim().toLowerCase());
        }
        String[] temp2 = prereqs.split(",");
        for (int i=0;i<temp2.length;i++) {
            this.prereqs.add(temp2[i].trim());
        }
    }


    public String getCourseName() {
        return courseName;
    }

    public String getCourseCode() {
        return courseCode;
    }

    public ArrayList<String> getOfferingSessions() {
        return offeringSessions;
    }

    public ArrayList<String> getPrereqs() {
        return prereqs;
    }

    public void setCourseCode(String courseCode) {
        this.courseCode = courseCode;
    }

    public void setOfferingSessions(ArrayList<String> offeringSessions) {
        this.offeringSessions = offeringSessions;
    }

    public void setPrereqs(ArrayList<String> prereqs) {
        this.prereqs = prereqs;
    }

    public void setCourseName(String courseName) {
        this.courseName = courseName;
    }
}
