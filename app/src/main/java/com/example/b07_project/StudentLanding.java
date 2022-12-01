package com.example.b07_project;
import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.util.Log;
import android.view.MenuItem;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.sql.Array;
import java.util.ArrayList;
import java.util.LinkedHashSet;

public class StudentLanding extends AppCompatActivity {
    public DrawerLayout drawerLayout;
    public ActionBarDrawerToggle actionBarDrawerToggle;

    String uID;
    String email;
    String password;
    ArrayList<String> history;
    DatabaseReference fire;
    DatabaseReference user;
    DatabaseReference history_ref;
    StudentAccount student;
    String course_name;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_student_landing);

        // drawer layout instance to toggle the menu icon to open
        // drawer and back button to close drawer
        drawerLayout = findViewById(R.id.my_drawer_layout);
        actionBarDrawerToggle = new ActionBarDrawerToggle(this, drawerLayout, R.string.nav_open, R.string.nav_close);

        // pass the Open and Close toggle for the drawer layout listener
        // to toggle the button
        drawerLayout.addDrawerListener(actionBarDrawerToggle);
        actionBarDrawerToggle.syncState();

        // to make the Navigation drawer icon always appear on the action bar

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        //Retrieving account info from SharedPreferences
        SharedPreferences p = getSharedPreferences("myPreferences", Context.MODE_PRIVATE);
        uID = p.getString("uID", "N/A");
        email = p.getString("email", "N/A");
        password = p.getString("password", "N/A");
        course_name = "i'm sick of this shit";

        //Probably will be needed for updating the student account data (eg. adding courses, adding academic history);
        fire = FirebaseDatabase.getInstance().getReference();
        user = fire.child("Students").child(uID);
        history_ref = FirebaseDatabase.getInstance().getReference().child("Accounts").child(uID).child("Courses_taken");
        history = new ArrayList<>();
        history_ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    history.add(dataSnapshot.getKey());
                }
                history_ref.removeEventListener(this);
                Fragment pls = new AcademicHistory();
                Bundle pls2 = new Bundle();
                pls2.putStringArrayList("history", history);
                pls.setArguments(pls2);
                FragmentManager fragmentManager = getSupportFragmentManager();
                fragmentManager.beginTransaction().replace(R.id.student_frame, pls).commit();

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        //Creating student object
        student = new StudentAccount(email, password, uID, history);

        //Default just displaying the email

    }

    // override the onOptionsItemSelected()
    // function to implement
    // the item click listener callback
    // to open and close the navigation
    // drawer when the icon is clicked
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        if (actionBarDrawerToggle.onOptionsItemSelected(item)) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}