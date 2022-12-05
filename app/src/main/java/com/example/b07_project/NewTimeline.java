package com.example.b07_project;

import static com.example.b07_project.courseTimelineList.coursesToTimeline;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.renderscript.Sampler;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Locale;

public class NewTimeline extends AppCompatActivity {

    // STARTING NEW STUFF HERE
    Button goBackButton;

    String uID;
    ArrayList<String> history;  // user's academic history
    String historyString;
    DatabaseReference fire;
    DatabaseReference user;
    DatabaseReference allCourses;
    DatabaseReference userCoursesTaken;
    SharedPreferences p;


    ArrayList<String> historyCodes = new ArrayList<>();

    ArrayList<GenerateTimeline> list;

    HashMap<String, ArrayList<String>> sched = new HashMap<>();
    HashMap<String, String> lastPre = new HashMap<>();
    String followingCourse = "";
    public static HashMap<String, ArrayList<String>> schedule = new HashMap<>();

    HashMap<String, HashMap<String, ArrayList<String>>> allCoursesMap = new HashMap<>();

    HashMap<String, ArrayList<String>> mapOfSessionPrereq = new HashMap<>();

    OfferingSession offeringSession;
    ArrayList<String> offeringSessionsForCourses;
    ArrayList<String> prereqForCourses;
    CourseID courseID;
    CourseID courseID2;
    String addCourseID;
    String addCourseID2;

    History historyCourseCode;
    History historyCourseCode2;

    String courseCode;
    String courseCode2;


    //END OF STARTING NEW STUFF

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_timeline);
        ListView myListView=findViewById(R.id.myList);

        // STARTING NEW STUFF HERE
        p = this.getSharedPreferences("myPreferences", Context.MODE_PRIVATE);
        uID = p.getString("uID", "N/A");
        historyString = p.getString("history", "N/A");
        history = new ArrayList<>();
        if(!historyString.equals("")) {
            String[] temp = historyString.split(";");

            for (int i = 0; i < temp.length; i++) {
                history.add(temp[i]);
            }
        }

        fire = FirebaseDatabase.getInstance().getReference();
        user = fire.child("Accounts").child(uID);
        goBackButton = findViewById(R.id.goBackButton);
        goBackButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent k = new Intent(NewTimeline.this, StudentLanding.class);
                startActivity(k);
                finish();
            }
        });
        userCoursesTaken = user.child("Courses_taken");
        allCourses = fire.child("Courses");

        allCourses.addValueEventListener(new ValueEventListener() {

            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                Log.d("Testing VEL: ", "FEBSREBE");

                for (DataSnapshot dataSnapshot : snapshot.getChildren()){
                    courseID = dataSnapshot.getValue(CourseID.class);
                    addCourseID = courseID.getCourseID(); //actual value of courseID to be added somewhere
                    Log.d("Testing: ", addCourseID); //<-
                    historyCourseCode = dataSnapshot.getValue(History.class);
                    courseCode = historyCourseCode.getCourseCode();

                    if (history.contains(addCourseID)) {
                        historyCodes.add(courseCode.toLowerCase());
                    }

                    mapOfSessionPrereq = new HashMap<>();
                    Prereq prereq = dataSnapshot.getValue(Prereq.class);
                    prereqForCourses = new ArrayList<String>();
                    for (String s: prereq.getPreReqs()){
                        prereqForCourses.add(s); //prereqForCourses contain values of courseIDs to be added
                    }
                    for(String pre : prereqForCourses){
                        for(DataSnapshot preSnap : snapshot.getChildren()){
                            courseID2 = preSnap.getValue(CourseID.class);
                            addCourseID2 = courseID2.getCourseID();
                            historyCourseCode2 = preSnap.getValue(History.class);
                            courseCode2 = historyCourseCode2.getCourseCode();
                            if(pre.equals(addCourseID2)){
                                prereqForCourses.set(prereqForCourses.indexOf(pre), courseCode2.toLowerCase());
                            }
                        }
                    }
                    mapOfSessionPrereq.put("prereqs", prereqForCourses);

                    offeringSession = dataSnapshot.getValue(OfferingSession.class);
                    offeringSessionsForCourses = new ArrayList<String>();
                    for (String s: offeringSession.getOfferingSessions()){
                        offeringSessionsForCourses.add(s.toLowerCase()); //offeringSessionsForCourses contain vals of course sessions t.b.a
                    }

                    mapOfSessionPrereq.put("sessions", offeringSessionsForCourses);
                    allCoursesMap.put(courseCode.toLowerCase(), mapOfSessionPrereq);
                }
                schedule = buildSched(allCoursesMap, coursesToTimeline, historyCodes, sched, lastPre, followingCourse);

                int numSessions = 0;
                ArrayList<String> presentTimeline = new ArrayList<>();

                for(String key: schedule.keySet()){
                    String courses = schedule.get(key).toString();
                    presentTimeline.add(key + ": " + courses);
                    numSessions+=1;
                }

                Log.d("Tracking presentTimeline: ", presentTimeline.toString());

                String sessionCourses[] = new String[numSessions];
                for(int i = 0; i < numSessions; i++){
                    sessionCourses[i] = presentTimeline.get(i);
                }
                Log.d("Testing sessionCourses: ", sessionCourses.toString());

                ArrayAdapter<String> myAdapter=new ArrayAdapter<String>(NewTimeline.this,
                        android.R.layout.simple_list_item_1, sessionCourses);
                myListView.setAdapter(myAdapter);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });



        //END OF STARTING NEW STUFF

        //String sessionCourses = new String[]{};
        /*ArrayAdapter<String> myAdapter=new ArrayAdapter<>(String)(NewTimeline.this,
                    android.R.layout.simple_list_item_1, sessionCourses);
         myListView.setAdapter(myAdapter);
         */


//        String items[]=new String[]{"1", "2", "3"};
//        ArrayAdapter<String> myAdapter = new ArrayAdapter<>(NewTimeline.this,
//                android.R.layout.simple_list_item_1,items);
//        myListView.setAdapter(myAdapter);

    }


    //NEW METHODS HERE
    public HashMap buildSched(HashMap<String, HashMap<String, ArrayList<String>>> allCoursesMap,
                              ArrayList<String> wantToTake, ArrayList<String> history,
                              HashMap<String, ArrayList<String>> sched,
                              HashMap<String, String> lastPre, String followingCourse) {


        Log.i("Top of buildSched: ", wantToTake.toString());
        //Log.d("Testing allCoursesMap: ",  String.valueOf(allCoursesMap.size()));

        String[] date;
        date = java.time.LocalDate.now().toString().split("-");
        String currentSession;
        if (Integer.parseInt(date[1]) >= 9 && Integer.parseInt(date[1])<=12) {
            currentSession = "fall " + date[0];
        }
        else if (Integer.parseInt(date[1]) >= 1 && Integer.parseInt(date[1])<=4){
            currentSession = "winter " + date[0];
        }
        else{
            currentSession = "summer " + date[0];
        }
        //basecase

        //Log.d("Testing wTT: ", wantToTake.get(0));
        for(String key:allCoursesMap.keySet()){
            Log.d("Testing aCM: ", allCoursesMap.get(key).get("prereqs").toString());
            Log.d("Testing aCM Keys: ", key);
        }

        if (allCoursesMap.get(wantToTake.get(0)).get("prereqs").size() == 1 && allCoursesMap.get(wantToTake.get(0)).get("prereqs").contains("null")) {

            String nextOfferedSession;
            nextOfferedSession = nextSession(wantToTake.get(0), currentSession);
            history.add(wantToTake.get(0));
            ArrayList<String> singleCourseList = new ArrayList<>(Arrays.asList(wantToTake.get(0)));
            if (!sched.containsKey(nextOfferedSession)){
                sched.put(nextOfferedSession, singleCourseList);
            }
            else{
                ArrayList<String> coursesToPutInSched;
                coursesToPutInSched = sched.get(nextOfferedSession);
                coursesToPutInSched.add(wantToTake.get(0));
                sched.put(nextOfferedSession, coursesToPutInSched);
            }

            if (!followingCourse.equals("")){
                if (!lastPre.containsKey(followingCourse)) {
                    lastPre.put(followingCourse, nextOfferedSession);
                }
                else{
                    String current;
                    current = lastPre.get(followingCourse);
                    if (Integer.parseInt(nextOfferedSession.substring(nextOfferedSession.length() - 4))
                            > Integer.parseInt(current.substring(current.length() - 4))) {
                        lastPre.put(followingCourse, nextOfferedSession);
                    } else if (Integer.parseInt(nextOfferedSession.substring(nextOfferedSession.length() - 4))
                            == Integer.parseInt(current.substring(current.length() - 4))
                            && (nextOfferedSession.substring(0, 4).equals("fall")) ||
                            (nextOfferedSession.substring(0, 6).equals("summer") && (current.substring(0, 6).equals("winter")))) {
                        lastPre.put(followingCourse, nextOfferedSession);
                    }
                }
            }
            return sched;
        }
        for (String course : wantToTake) {
            if(!history.contains(course)) {
                for (String pre: allCoursesMap.get(course).get("prereqs")){
                    //Log.d("Testing prereqs: ", allCoursesMap.get(course).get("prereqs").toString());
                    if (!history.contains(pre)){

                        Log.d("Testing if: ", "11111");

                        Log.d("Testing pre: ", pre);
                        ArrayList<String> listOfSinglePre = new ArrayList<String>(Arrays.asList(pre));
                        Log.d("Testing listOfSinglePre: ", listOfSinglePre.toString());
                        sched = buildSched(allCoursesMap, listOfSinglePre, history, sched, lastPre, course);
                        Log.d("Testing after sched: ", "33333");
                    }
                    else{

                        Log.d("Testing else: ", "22222");
                        String current;
                        if (lastPre.containsKey(course)){
                            current = lastPre.get(course);
                            if (Integer.parseInt(currentSession.substring(currentSession.length() - 4))
                                    > Integer.parseInt(current.substring(current.length() - 4))) {
                                lastPre.put(course, currentSession);
                            } else if (Integer.parseInt(currentSession.substring(currentSession.length() - 4))
                                    == Integer.parseInt(current.substring(current.length() - 4))
                                    && (currentSession.substring(0, 4).equals("fall")) ||
                                    (currentSession.substring(0, 6).equals("summer") && (current.substring(0, 6).equals("winter")))) {
                                lastPre.put(course, currentSession);
                            }
                        }
                        else{
                            lastPre.put(course, currentSession);
                        }
                    }
                }

                Log.i("Testing before: ", "fawawwgwgwagwg");


                String nextOfferedSession = nextSession(course, lastPre.get(course));
                if (!history.contains(course)){
                    ArrayList<String> singleCourseList = new ArrayList<>(Arrays.asList(course));
                    //remember to append if key exists
                    sched.put(nextOfferedSession, singleCourseList);

                    for(String key:sched.keySet()){
                        Log.i("Testing sched: ", key);
                        Log.i("Testing sched VALUE: ", sched.get(key).toString());
                    }

                }
                history.add(course);
                if (!followingCourse.equals("")){
                    if (!lastPre.containsKey(followingCourse)) {
                        lastPre.put(followingCourse, nextOfferedSession);
                    }
                    else{
                        String currentSess;
                        currentSess = lastPre.get(followingCourse);
                        if (Integer.parseInt(nextOfferedSession.substring(nextOfferedSession.length() - 4))
                                > Integer.parseInt(currentSess.substring(currentSess.length() - 4))) {
                            lastPre.put(followingCourse, nextOfferedSession);
                        } else if (Integer.parseInt(nextOfferedSession.substring(nextOfferedSession.length() - 4))
                                == Integer.parseInt(currentSess.substring(currentSess.length() - 4))
                                && (nextOfferedSession.substring(0, 4).equals("fall")) ||
                                (nextOfferedSession.substring(0, 6).equals("summer") && (currentSess.substring(0, 6).equals("winter")))) {
                            lastPre.put(followingCourse, nextOfferedSession);
                        }
                    }
                }
            }
        }
        return sched;
    }


    //a -> b -> c -> d

    public String earliestSession(String course) { // "session" + "year"
        String[] date;
        date = java.time.LocalDate.now().toString().split("-");
        String year, month, day;
        year = date[0];
        month = date[1];
        day = date[2];

        if (Integer.parseInt(month) >= 9 && Integer.parseInt(month)<=12){ // if session = fall
            if (allCoursesMap.get(course).get("sessions").contains("winter")){
                return "winter " + Integer.toString(Integer.parseInt(year)+1);
            }
            else if(allCoursesMap.get(course).get("sessions").contains("summer")){
                return "summer " + Integer.toString(Integer.parseInt(year)+1);
            }
            else{
                return "fall " + Integer.toString(Integer.parseInt(year)+1);
            }
        }
        else if (Integer.parseInt(month) >= 1 && Integer.parseInt(month)<= 4){ // if session == winter
            if (allCoursesMap.get(course).get("sessions").contains("summer")){
                return "summer " + year;
            }
            else if(allCoursesMap.get(course).get("sessions").contains("fall")){
                return "fall " + year;
            }
            else{
                return "winter " + Integer.toString(Integer.parseInt(year)+1);
            }
        }
        else{ // if session is summer
            if (allCoursesMap.get(course).get("sessions").contains("fall")){
                return "fall " + year;
            }
            else if(allCoursesMap.get(course).get("sessions").contains("winter")){
                return "winter " + Integer.toString(Integer.parseInt(year)+1);
            }
            else{
                return "summer " + Integer.toString(Integer.parseInt(year)+1);
            }
        }
    }
    public String nextSession(String course, String sessionOfPre) {
        String session = sessionOfPre.substring(0,sessionOfPre.length() - 4);
        int year = Integer.parseInt(sessionOfPre.substring((sessionOfPre.length()-4)));

        if (session.equals("fall")) {
            if (allCoursesMap.get(course).get("sessions").contains("winter")){
                return "winter " + Integer.toString(year+1);
            }
            else if(allCoursesMap.get(course).get("sessions").contains("summer")){
                return "summer " + Integer.toString(year+1);
            }
            else{
                return "fall " + Integer.toString(year+1);
            }
        }
        else if(session.equals("winter")) {
            if (allCoursesMap.get(course).get("sessions").contains("summer")){
                return "summer " + year;
            }
            else if(allCoursesMap.get(course).get("sessions").contains("fall")){
                return "fall " + year;
            }
            else{
                return "winter " + Integer.toString(year+1);
            }
        }
        else{
            if (allCoursesMap.get(course).get("sessions").contains("fall")){
                return "fall " + year;
            }
            else if(allCoursesMap.get(course).get("sessions").contains("winter")){
                return "winter " + Integer.toString(year+1);
            }
            else{
                return "summer " + Integer.toString(year+1);
            }
        }
    }


    //END OF NEW METHODS
}