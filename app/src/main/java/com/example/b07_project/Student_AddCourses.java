package com.example.b07_project;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.TextView;

import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class StudentLanding extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {
    DrawerLayout student_drawer;
    ActionBarDrawerToggle student_toggle;
    String uID;
    String email;
    String password;
    FirebaseAuth adminFireAuth;
    DatabaseReference fire;
    DatabaseReference user;
    AdminAccount admin;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.student_add_courses);

        student_drawer = findViewById(R.id.admin_drawer_layout);
        student_toggle = new ActionBarDrawerToggle(this, student_drawer, R.string.nav_open, R.string.nav_close);
        student_drawer.addDrawerListener(student_toggle);
        student_toggle.syncState();
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        NavigationView admin_view = (NavigationView)findViewById(R.id.admin_navigation_view);
        admin_view.setNavigationItemSelectedListener(this);

        //Retrieving account info from SharedPreferences
        SharedPreferences p = getSharedPreferences("myPreferences", Context.MODE_PRIVATE);
        uID = p.getString("uID", "N/A");
        email = p.getString("email", "N/A");
        password = p.getString("password", "N/A");


        //Creating student object
        admin = new AdminAccount(email, password, uID);

        //Default just displaying the email
        transFragment(new ManageCourses());

    }

    public boolean onOptionsItemSelected(@NonNull MenuItem item){
        if(admin_toggle.onOptionsItemSelected(item)){
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.admin_nav_manage:
                Fragment manage_ui = new ManageCourses();
                transFragment(manage_ui);
                break;
            case R.id.admin_nav_add:
                Fragment add_ui = new AddCourses();
                transFragment(add_ui);
                break;
            case R.id.admin_nav_logout:
                admin_logout();
                break;
        }
        return false;
    }
    public void admin_logout(){
        adminFireAuth = FirebaseAuth.getInstance();
        adminFireAuth.signOut();
        Intent intent = new Intent(AdminLanding.this, MainActivity.class);
        startActivity(intent);
        finish();
    }

    private void transFragment(Fragment fragment){
        admin_drawer.closeDrawer(GravityCompat.START);
        FragmentManager fragmentManager = getSupportFragmentManager();
        fragmentManager.beginTransaction().replace(R.id.admin_frame, fragment).commit();
    }

}