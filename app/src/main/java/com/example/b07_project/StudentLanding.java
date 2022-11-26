package com.example.b07_project;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.TextView;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class StudentLanding extends AppCompatActivity {
    String uID;
    DatabaseReference fire;
    DatabaseReference user;
    StudentAccount student;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_student_landing);

        //Retrieving uID from SharedPreferences
        SharedPreferences p = getSharedPreferences("myPreferences", Context.MODE_PRIVATE);
        uID = p.getString("uID", "N/A");
        //Just displaying uID on the UI just because
        TextView text = (TextView)findViewById(R.id.welcomeStudent);
        text.setText("Welcome " + uID);


        //You're gonna want to use this to actually be able to get the full StudentAccount instance
        //to use for other functionalities
        fire = FirebaseDatabase.getInstance().getReference();
        user = fire.child("Students").child(uID);


    }
}