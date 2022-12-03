package com.example.b07_project;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;

import com.google.firebase.database.DatabaseReference;

import java.util.ArrayList;

public class StudentDashboard extends Fragment {
    DatabaseReference databaseReference;
    ArrayList<Course> upcomingCoursesA;
    ArrayList<Course> upcomingCoursesB;
    ArrayList<Course> suggestedCourses;
    DrawerLayout drawerLayout;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_student_landing, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        StudentLanding landing = new StudentLanding();
        drawerLayout = view.findViewById(R.id.my_drawer_layout);

        //Default just displaying the user part of the email
        TextView text = (TextView) view.findViewById(R.id.welcomeStudent);
        text.setText("Okaeri, " + StudentLanding.email.split("@")[0] + "!");

        //Updates the redirection buttons
        Button currentCourses = (Button) view.findViewById(R.id.fullCourses);
        currentCourses.setOnClickListener(v -> {
            Fragment timeline = new CourseTimeline();
            transFragment(timeline);
        });
        Button currentHistory = (Button) view.findViewById(R.id.history);
        currentHistory.setOnClickListener(v -> {
            Fragment history = new AcademicHistory();
            transFragment(history);
        });
        Button allCourses = (Button) view.findViewById(R.id.viewAllCourses);
        allCourses.setOnClickListener(v -> {
            Fragment timeline = new CourseTimeline();
            transFragment(timeline);
        });
        Button addCourses = (Button) view.findViewById(R.id.addCourses);
        addCourses.setOnClickListener(v -> {
            Fragment add = new AddHistory();
            transFragment(add);
        });
    }

    private void transFragment(Fragment fragment) {
        drawerLayout.closeDrawer(GravityCompat.START);
        FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
        fragmentManager.beginTransaction().replace(R.id.student_frame, fragment).commit();
    }
}

