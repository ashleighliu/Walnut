package com.example.b07_project;

import java.util.HashMap;

public class AdminAccount extends Account{
    //public static HashMap<String, String> allCourses;
    public AdminAccount(String email, String password, String uID){
        this.email = email;
        this.password = password;
        this.uID = uID; //Needed to retrieve data from firebase
        this.isAdmin = true; //Self-explanatory
    }

}
