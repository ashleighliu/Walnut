package com.example.b07_project;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import java.util.*;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;

public class GenerateTimeline {

    HashMap<String, ArrayList<String>> sessionCourse;

    public GenerateTimeline(HashMap<String, ArrayList<String>> sessionCourse){
        this.sessionCourse = sessionCourse;
    }

    public String getSession() {
        return sessionCourse.keySet().toArray()[0].toString();
    }

    public String getCourses() {
        return sessionCourse.values().toString();
    }

    public HashMap<String, ArrayList<String>> getSessionCourse() {
        return sessionCourse;
    }
}



// fall 2022: b07, b09