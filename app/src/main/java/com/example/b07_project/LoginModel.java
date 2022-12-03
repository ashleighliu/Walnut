package com.example.b07_project;

import android.content.Context;
import android.content.SharedPreferences;

import java.util.Set;

public class LoginModel {
    SharedPreferences p;
    SharedPreferences.Editor editor;

    LoginModel(MainActivity activity) {
        p = activity.getSharedPreferences("myPreferences", Context.MODE_PRIVATE);
        editor = p.edit();
    }

    public void addStringsToSharedPreferences(String uID, String email, String password){
        editor.putString("uID", uID);
        editor.putString("email", email);
        editor.putString("password", password);
        editor.apply();
    }

    public void addAcademicHistory(Set<String> history){
        editor.putStringSet("history", history);
        editor.commit();
    }
}
