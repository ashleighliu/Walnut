package com.example.b07_project;

public class AdminAccount extends Account{

    public AdminAccount(String email, String password, String uID){
        this.email = email;
        this.password = password;
        this.uID = uID; //Needed to retrieve data from firebase
        this.isAdmin = true; //Self-explanatory
    }
}
