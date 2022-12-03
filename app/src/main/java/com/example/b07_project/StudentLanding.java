package com.example.b07_project;
import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.constraintlayout.widget.ConstraintLayout;

import android.content.Context;
import android.content.SharedPreferences;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

public class StudentLanding extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener{
    public DrawerLayout drawerLayout;
    public ActionBarDrawerToggle actionBarDrawerToggle;

    static String uID;
    static String email;
    static String password;
    ArrayList<String> history;
    FirebaseAuth studentFireAuth;
    DatabaseReference fire;
    DatabaseReference user;
    StudentAccount student;

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
        NavigationView student_view = (NavigationView)findViewById(R.id.navigation_view);
        student_view.setNavigationItemSelectedListener(this);

        //Retrieving account info from SharedPreferences
        SharedPreferences p = getSharedPreferences("myPreferences", Context.MODE_PRIVATE);
        uID = p.getString("uID", "N/A");
        email = p.getString("email", "N/A");
        password = p.getString("password", "N/A");
        Set<String> set = p.getStringSet("history", new HashSet<String>());
        Log.i("myTag", String.valueOf(set.size()));
        history = new ArrayList<>(set);


        //Probably will be needed for updating the student account data (eg. adding courses, adding academic history);
        fire = FirebaseDatabase.getInstance().getReference();
        user = fire.child("Accounts").child(uID);

        //Creating student object
        student = new StudentAccount(email, password, uID, history);
        transFragment(new StudentDashboard());
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

    private void transFragment(Fragment fragment) {
        drawerLayout.closeDrawer(GravityCompat.START);
        FragmentManager fragmentManager = getSupportFragmentManager();
        fragmentManager.beginTransaction().replace(R.id.student_frame, fragment).commit();
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item)
    {
        switch(item.getItemId()) {
            case R.id.nav_dashboard:
                Fragment dashboard_ui = new StudentDashboard();
                transFragment(dashboard_ui);
                break;
            case R.id.nav_history:
                Fragment history_ui = new AcademicHistory();
                transFragment(history_ui);
                break;
            case R.id.nav_addcourses:
                goToAddCoursesToHistory();
                break;
            case R.id.nav_timeline:
                goToAddCoursesToTimeline();
                break;
            case R.id.nav_logout:
                student_logout();
                break;
        }
        return false;
    }

    public void student_logout(){
        studentFireAuth = FirebaseAuth.getInstance();
        studentFireAuth.signOut();
        Intent intent = new Intent(StudentLanding.this, MainActivity.class);
        startActivity(intent);
        finish();
    }

    public void goToAddCoursesToHistory(){
        Intent intent = new Intent(StudentLanding.this, courseList.class);
        startActivity(intent);
        finish();
    }

    public void goToAddCoursesToTimeline(){
        Intent intent = new Intent(StudentLanding.this, courseTimelineList.class);
        startActivity(intent);
        finish();
    }

}