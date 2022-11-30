package com.example.b07_project;


import android.os.Parcel;
import android.os.Parcelable;

import java.util.HashSet;

public class Course {

    private String courseName;
    private String courseCode;
    private HashSet<String> offeringSessions;
    private HashSet<String> prereqs;
    private String courseID;



    private Course(String courseName, String courseCode, HashSet<String> offeringSessions, HashSet<String> prereqs, String courseID) {
        this.courseName = courseName;
        this.courseCode = courseCode;
        this.offeringSessions = new HashSet<String>();
        this.offeringSessions = (HashSet<String>) offeringSessions.clone();
        this.prereqs = new HashSet<String>();
        this.prereqs = (HashSet<String>) prereqs.clone();
        this.courseID = courseID;
    }

    public Course(String courseName, String courseCode){
        this.courseName = courseName;
        this.courseCode = courseCode;
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




    public String getCourseCode() {
        return courseCode;
    }
    public String getCourseName() {
        return courseName;
    }

    public void setCourse_name(String course_name) {
        this.courseName = course_name;
    }

    public void setCourse_code(String course_code) {
        this.courseCode = course_code;
    }

}
