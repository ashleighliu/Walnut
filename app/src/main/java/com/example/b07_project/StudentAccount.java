package com.example.b07_project;

import java.util.ArrayList;
import java.util.LinkedHashSet;

public class StudentAccount extends Account{
    ArrayList<String> history;
    //TODO: add academic history field (LinkedHashSet or HashSet)
    public StudentAccount(String email, String password, String uID, ArrayList<String> history){
        this.email = email;
        this.password = password;
        this.uID = uID; //Needed to retrieve data from firebase
        this.isAdmin = false; //Self-explanatory
        this.history = history;
    }

    public boolean isEligible(Course course){
        //TODO: Implement method that checks student eligibility for a course
        return false;
    }

    public void hasTaken(Course course){
        //TODO: Implement method that adds a course to student's academic history
    }
    //A new constructor will need to be added for retrieving student info in the landing page
    //since the above constructor (once the course fields are added) will set the course lists
    //to null, but when retrieving info we want the course lists to have all the info from
    //firebase.
}
