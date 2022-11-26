package com.example.b07_project;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.TextView;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class AdminLanding extends AppCompatActivity {
    String uID;
    String email;
    String password;
    DatabaseReference fire;
    DatabaseReference user;
    AdminAccount admin;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_adminlanding);

        //Retrieving account info from SharedPreferences
        SharedPreferences p = getSharedPreferences("myPreferences", Context.MODE_PRIVATE);
        uID = p.getString("uID", "N/A");
        email = p.getString("email", "N/A");
        password = p.getString("password", "N/A");


        //Creating student object
        admin = new AdminAccount(email, password, uID);

        //Default just displaying the email
        TextView text = (TextView)findViewById(R.id.welcomeAdmin);
        text.setText("Welcome Admin: " + email);
    }
}