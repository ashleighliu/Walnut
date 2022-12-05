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

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Locale;
import java.util.TreeMap;

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


                schedule = orderMap(buildSched(allCoursesMap, coursesToTimeline, historyCodes, sched, lastPre, followingCourse));
                int numSessions = 0;
                ArrayList<String> presentTimeline = new ArrayList<>();

                for(String key: schedule.keySet()){
                    String courses = schedule.get(key).toString();
                    presentTimeline.add(key + ": " + courses);
                    numSessions+=1;
                }


                String sessionCourses[] = new String[numSessions];
                for(int i = 0; i < numSessions; i++){
                    sessionCourses[i] = presentTimeline.get(i);
                }

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

        Log.d("Top of buildSched: ", wantToTake.toString());
        String currentSession;
        String[] date;
        date = java.time.LocalDate.now().toString().split("-");
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
        if (wantToTake.size() == 1 && allCoursesMap.get(wantToTake.get(0)).get("prereqs").contains("null")) {

            String nextOfferedSession;
            nextOfferedSession = nextSession(wantToTake.get(0), currentSession);
            System.out.println("NEXT OFFERED SESSION IN BASIS: " + nextOfferedSession);
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
                    current = lastPre.get(followingCourse); //nextOfferedSession = "winter 2022"
                    if (getSessionYear(nextOfferedSession)
                            > getSessionYear(current)) {
                        lastPre.put(followingCourse, nextOfferedSession);
                    } else if (getSessionYear(nextOfferedSession) == getSessionYear(current)
                            && ((getSessionSeason(nextOfferedSession).equals("fall")) ||
                            ((getSessionSeason(nextOfferedSession).equals("summer") && (getSessionSeason(current).equals("winter")))))) {
                        lastPre.put(followingCourse, nextOfferedSession);
                    }
                }
            }
            return sched;
        }
        for (String course : wantToTake) {
            if(!history.contains(course)) {
                for (String pre: allCoursesMap.get(course).get("prereqs")){
                    if (!history.contains(pre)){
                        ArrayList<String> listOfSinglePre = new ArrayList<String>(Arrays.asList(pre));
                        sched = buildSched(allCoursesMap,listOfSinglePre, history, sched, lastPre, course);
                    }
                    else{
                        String current;
                        if (lastPre.containsKey(course)){
                            current = lastPre.get(course);
                            if (getSessionYear(currentSession) > getSessionYear(current)) {
                                lastPre.put(course, currentSession);
                            } else if (getSessionYear(currentSession)
                                    == getSessionYear(current)
                                    && ((getSessionSeason(currentSession).equals("fall")) ||
                                    ((getSessionSeason(currentSession).equals("summer") && (getSessionSeason(current).equals("winter")))))) {
                                lastPre.put(course, currentSession);
                            }
                        }
                        else{
                            boolean preInSched = false;
                            for (String key : sched.keySet()) {
                                if (sched.get(key).contains(pre)) {
                                    preInSched = true;
                                }
                            }
                            if (preInSched) {
                                String nextSession;
                                for (String key : sched.keySet()) {
                                    if (sched.get(key).contains(pre)) {
                                        nextSession = key;
                                        lastPre.put(course, nextSession);
                                    }
                                }
                            }
                            else { lastPre.put(course, currentSession); }

                        }
                    }
                }
                String nextOfferedSession = nextSession(course, lastPre.get(course));
                if (!history.contains(course)){
                    if (!sched.containsKey(nextOfferedSession)){
                        ArrayList<String> singleCourseList = new ArrayList<>(Arrays.asList(course));
                        sched.put(nextOfferedSession, singleCourseList);
                    }
                    else{
                        ArrayList<String> coursesToPutInSched;
                        coursesToPutInSched = sched.get(nextOfferedSession);
                        coursesToPutInSched.add(course);
                        sched.put(nextOfferedSession, coursesToPutInSched);
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
                        if (getSessionYear(nextOfferedSession) > getSessionYear(currentSess)) {
                            lastPre.put(followingCourse, nextOfferedSession);
                        } else if (getSessionYear(nextOfferedSession) == getSessionYear(currentSess)
                                && ((getSessionSeason(nextOfferedSession).equals("fall")) ||
                                ((getSessionSeason(nextOfferedSession).equals("summer") && (getSessionSeason(currentSess).equals("winter")))))) {
                            lastPre.put(followingCourse, nextOfferedSession);
                        }
                    }
                }
            }
        }
        return sched;
    }


    //a -> b -> c -> d


    public String nextSession(String course, String sessionOfPre) {
        String season = getSessionSeason(sessionOfPre);
        int year = getSessionYear(sessionOfPre);

        if (season.equals("fall")) {
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
        else if(season.equals("winter")) {
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
    public String getSessionSeason(String session) {
        String[] temp = session.split(" ");
        return temp[0];
    }
    public int getSessionYear(String session) {
        String[] temp = session.split(" ");
        return Integer.parseInt(temp[1]);
    }
    // want to sort schedule
    // how to sort: hashmap{season year: <>, season year: <>}
    // new linkedhashmap
    // write a method that compares 2 sessions
//    HashMap<String, Integer> sessionsMap = new HashMap<String, Integer>();
//    public void sortSessions(HashMap<String, Integer> sessionsMap) {
//        sessionsMap.put("winter", 1);
//        sessionsMap.put("summer", 2);
//        sessionsMap.put("fall", 3);
//    }
//    public compareDates(ArrayList<String> sessions) {
//        for (String sess: sessions) {
//
//
//        }
//    }
    public HashMap orderMap(HashMap<String, ArrayList<String>> schedule) {
        HashMap<Integer, String> intMap = new HashMap<>();
        LinkedHashMap<String, ArrayList<String>> answer = new LinkedHashMap<>();
        for (String key:schedule.keySet()) {
            intMap.put(convertSessionToInt(key), key);
        }
        TreeMap<Integer, String> tMap = new TreeMap<>(intMap);
        for (int key: tMap.keySet()) {
            answer.put(tMap.get(key), schedule.get(tMap.get(key)));
        }
        return answer;
    }
    public int convertSessionToInt(String session) {
        if (getSessionSeason(session).equals("winter")){
            return 1 + 10 * getSessionYear(session);
        }
        else if (getSessionSeason(session).equals("summer")) {
            return 2 + 10 * getSessionYear(session);
        }
        else {
            return 3 + 10 * getSessionYear(session);
        }
    }

    //END OF NEW METHODS
}