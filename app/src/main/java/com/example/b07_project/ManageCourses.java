package com.example.b07_project;

import android.content.ClipData;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.sql.Array;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link ManageCourses#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ManageCourses extends Fragment {
    RecyclerView recyclerView;
    DatabaseReference databaseReference;
    AdminCourseAdapter adminAdapter;
    ArrayList<Course> course_info;
    private SearchView searchView;
    TextView noCourses;

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public ManageCourses() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment ManageCourses.
     */
    // TODO: Rename and change types and number of parameters
    public static ManageCourses newInstance(String param1, String param2) {
        ManageCourses fragment = new ManageCourses();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);

            searchView = searchView.findViewById(R.id.searchView);
            searchView.clearFocus();
            searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
                @Override
                public boolean onQueryTextSubmit(String query) {
                    filterList(query);
                    return false;
                }

                @Override
                public boolean onQueryTextChange(String newText) {
                    System.out.println("shitter");
                    filterList(newText);
                    return true;
                }
            });

        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_manage_courses, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        recyclerView = view.findViewById(R.id.viewCourses);
        noCourses = view.findViewById((R.id.noCoursesAvailable));
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setHasFixedSize(true);
        course_info  = new ArrayList<>();
        adminAdapter = new AdminCourseAdapter(getContext(), course_info);
        recyclerView.setAdapter(adminAdapter);

        if (course_info != null) {
            Log.i("myTa", "Found all courses...");
            databaseReference = FirebaseDatabase.getInstance().getReference().child("Courses");
            databaseReference.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    for(DataSnapshot x : snapshot.getChildren()){
                        String courseName = x.child("courseName").getValue(String.class);
                        String courseCode = x.child("courseCode").getValue(String.class);
                        String prereqsString = "";
                        String offeringSessionsString = "";
                        DataSnapshot gotoPrereqs = x.child("prereqs");
                        for(DataSnapshot y: gotoPrereqs.getChildren()){
                            prereqsString += y.getValue(String.class) + ", ";
                        }
                        DataSnapshot gotoOffering = x.child("offeringSessions");
                        for(DataSnapshot z : gotoOffering.getChildren()){
                            offeringSessionsString += z.getValue(String.class) + ", ";
                        }
                        prereqsString = prereqsString.trim();
                        offeringSessionsString = offeringSessionsString.trim();
                        Log.i("myTag", offeringSessionsString);
                        Log.i("req", prereqsString);
                        Course course = new Course(courseName, courseCode, offeringSessionsString, prereqsString, x.getKey());
                        course_info.add(course);

                    }


                    databaseReference.removeEventListener(this);
                    if (course_info.isEmpty()) {
                        noCourses.setVisibility(View.VISIBLE);
                        recyclerView.setVisibility(View.GONE);
                    }
                    else {
                        noCourses.setVisibility(View.GONE);
                        recyclerView.setVisibility(View.VISIBLE);
                    }


                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    Log.i("ERROR:", "Data call unsuccessful");
                }
            });
        }
    }
    private void filterList (String text) {
        List<Course> filteredList = new ArrayList<Course>();
        for (Course item : course_info){
            if (item.getCourseCode().toLowerCase().contains(text.toLowerCase())){
                filteredList.add(item);
            }
        }

        System.out.println("Working...");
        for (Course item : filteredList) {
            System.out.println(item.getCourseCode());
        }
        if (filteredList.isEmpty()) {
        }
        else{
            adminAdapter.setFilteredList((ArrayList<Course>)filteredList);
        }


    }
}