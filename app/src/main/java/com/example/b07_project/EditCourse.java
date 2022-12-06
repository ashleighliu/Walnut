package com.example.b07_project;

import android.app.Activity;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import android.provider.ContactsContract;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;


public class EditCourse extends Fragment {
    View editCourseView;
    private Button editCourseBtn;
    private Bundle bundle;
    private ArrayList<String> prereqs;
    private ArrayList<String> offered;
    private String courseID;
    final String[] OFFERINGSESSIONS = {"summer", "fall", "winter"};
    private EditText editTitle, editCode, editSessions, editPrereqs;
    private DatabaseReference dB;
    private Activity activity = this.getActivity();

    private void attachFields(){
        editCourseBtn = editCourseView.findViewById(R.id.editCourseBtn);
        editTitle = editCourseView.findViewById(R.id.editTitle);
        editCode = editCourseView.findViewById(R.id.editCode);
        editSessions = editCourseView.findViewById(R.id.editSessions);
        editPrereqs = editCourseView.findViewById(R.id.editPrereqs);
        dB = FirebaseDatabase.getInstance().getReference().child("Courses");
        bundle = this.getArguments();
        editTitle.setText(bundle.getString("courseName", "Course Name"));
        editCode.setText(bundle.getString("courseCode", "Course Code"));
        prereqs = bundle.getStringArrayList("prereqs");
        activity = this.getActivity();
        String displayPre = display(prereqs);
        Log.i("weird", displayPre);
        if(displayPre.equals("null")){
            editPrereqs.setText("");
        }else {
            editPrereqs.setText(displayPre);
        }

        offered = bundle.getStringArrayList("offeringSessions");
        String displayOffering = display(offered);

        editSessions.setText(displayOffering);
        courseID = (bundle.getString("courseID", "Course ID"));
        editCourseBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                editCourse(editCourseView);
            }
        });

    }
    public void editCourse(View view){
        String courseName = editTitle.getText().toString();
        String courseCode = editCode.getText().toString();
        String offeringSessions = editSessions.getText().toString();
        String prereqs = editPrereqs.getText().toString();
        String[] prereqArr = trimAll(prereqs.split(","));
        String[] offeringArr = lowerAll(trimAll(offeringSessions.split(",")));
        String[] prereqIDArr = new String[prereqArr.length];
        Log.i("asdsa", String.valueOf(prereqArr.length));
        boolean allOfferingsValid = true;
        for(int i = 0; i<offeringArr.length; i++)
        {
            if(!(arrayContains(OFFERINGSESSIONS, offeringArr[i]))){
                allOfferingsValid = false;
            }
        }
        if (courseName.isEmpty()) {
            editTitle.setError("Enter Valid Course Title");
        }

        else if(courseCode.isEmpty()){
            editCode.setError("Enter Proper Course Code");
        }

        else if (offeringSessions.isEmpty()) {
            editSessions.setError("Offering Sessions cannot be empty");
        }
        else if(!allOfferingsValid){
            editSessions.setError("Enter Valid Offering Sessions");
        }
        else{
            dB.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    boolean allPrereqsValid = true;
                    boolean courseExists = false;
                    for(int i = 0; i <prereqArr.length; i++)
                    {
                        Boolean prereqExists = false;
                        if(!(prereqArr[i].equals(courseCode))){
                            for(DataSnapshot x : snapshot.getChildren()) {
                                Log.i("courseID", courseID);
                                if ((x.child("courseCode").getValue(String.class)).equals(prereqArr[i])) {
                                    Log.i("true", "true");
                                    prereqExists = true;
                                    prereqIDArr[i] = x.child("courseID").getValue(String.class);
                                }
                            }

                        }
                        if(!prereqExists){
                            allPrereqsValid = false;
                        }
                    }

                    for(DataSnapshot y: snapshot.getChildren()){
                        if(y.child("courseCode").getValue(String.class).equals(courseCode) && !(y.getKey().equals(courseID))){
                            courseExists = true;
                        }
                    }
                    if(courseExists){
                        editCode.setError("This Course Already Exists");
                    }
                    else if (!allPrereqsValid && !prereqs.equals("")){
                        editPrereqs.setError("Prerequisite Course(s) Does Not Exist or refers to itself");
                    }
                    else if(duplicates(prereqArr)){
                        editPrereqs.setError("Cannot Have Duplicate Prerequisites");
                    }
                    else if(duplicates(offeringArr)){
                        editSessions.setError("Cannot Have Duplicate Offering Sessions");
                    }
                    else{
                        String prereqIDString = "";
                        for (int i = 0;i<prereqIDArr.length;i++){
                            prereqIDString = prereqIDString  + prereqIDArr[i]+ ",";
                        }
                        Course edittedCourse = new Course(courseName, courseCode, offeringSessions, prereqIDString, courseID);
                        dB.child(courseID).setValue(edittedCourse);
                        Toast.makeText(getActivity(), "Course Edit Successful", Toast.LENGTH_SHORT).show();
                        Fragment management_ui = new ManageCourses();
                        transFragment(management_ui);
                        AdminLanding.hideKeyboard(activity);
                    }
                    dB.removeEventListener(this);
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
        }
    }

    private void transFragment(Fragment frag) {
        AdminLanding.admin_drawer.closeDrawer(GravityCompat.START);
        FragmentManager fragmentManager = this.getActivity().getSupportFragmentManager();
        fragmentManager.beginTransaction().replace(R.id.admin_frame, frag).commit();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        editCourseView = inflater.inflate(R.layout.fragment_edit_course, container, false);
        attachFields();
        return editCourseView;
    }
    public String display(ArrayList<String> info){
        String display = "";
        if(info != null && info.size() != 0) {
            for (int i = 0; i < info.size() - 1; i++) {
                display += info.get(i) + ", ";
            }
            display += info.get(info.size() - 1);
        }
        return display;
    }
    boolean duplicates (final String[] zipcodelist)
    {
        Set<String> lump = new HashSet<String>();
        for (String i : zipcodelist)
        {
            if(lump.contains(i)) return true;
            lump.add(i);
        }
        return false;
    }

    String[] trimAll(String[] arr){
        for (int i = 0; i<arr.length; i++){
            arr[i] = arr[i].trim();
        }
        return arr;
    }
    String[] lowerAll(String[] arr){
        for (int i = 0; i<arr.length;i++){
            arr[i] = arr[i].toLowerCase();
        }
        return arr;
    }

    boolean arrayContains(String[] arr, String str){
        for(int i = 0;i<arr.length;i++){
            if(arr[i].equals(str)){
                return true;
            }
        }
        return false;
    }
}