package com.example.b07_project;

public class StudentAccount extends Account{

    public StudentAccount(String email, String password, String uID){
        this.email = email;
        this.password = password;
        this.uID = uID; //Needed to retrieve data from firebase
        this.isAdmin = false; //Self-explanatory
    }
    //A new constructor will need to be added for retrieving student info in the landing page
    //since the above constructor (once the course fields are added) will set the course lists
    //to null, but when retrieving info we want the course lists to have all the info from
    //firebase.
}
