package com.example.b07_project;

import static com.example.b07_project.courseTimelineList.coursesToTimeline;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.Locale;

public class GenerateTimelineAdapter extends RecyclerView.Adapter<GenerateTimelineAdapter.MyViewHolder> {

    Context context;
    // have arraylist of hashmaps
    //HashMap<String, String> session_courses = new HashMap<String, String>();

    String session;
    ArrayList<String> courses = new ArrayList<String>();

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

    //Courses = {courseCode: <{sessions:<courses>}, {prereqs:<courses>}>}

    //e.g., Courses = {b07:{session:<fall,
    //                               Winter>,
    //                      prereqs:<a48,
    //                               a22>}
    //                 b09: ...}

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

    //arraylist of history course ids
    //


    ////////// REMEMBER TO FIX THIS /////////////
    public GenerateTimelineAdapter(Context context, ArrayList<GenerateTimeline> list) {
        this.context = context;
        this.list = list;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        p = context.getSharedPreferences("myPreferences", Context.MODE_PRIVATE);
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
        userCoursesTaken = user.child("Courses_taken");
        allCourses = fire.child("Courses");

        allCourses.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot dataSnapshot : snapshot.getChildren()){
                    courseID = dataSnapshot.getValue(CourseID.class);
                    addCourseID = courseID.getCourseID(); //actual value of courseID to be added somewhere
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
                    allCoursesMap.put(courseCode, mapOfSessionPrereq);
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        Log.d("Testing onview", "HI");

        schedule = buildSched(coursesToTimeline, historyCodes, sched, lastPre, followingCourse);

        for(String key:schedule.keySet()){
            Log.d("HM Sessions: ", key);
            Log.d("HM Courses: ", schedule.get(key).toString());
        }

        View v = LayoutInflater.from(context).inflate(R.layout.generate_timeline_item, parent, false);
        return new MyViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        GenerateTimeline generateTimeline = list.get(position);
        holder.sessionCourse.setText(generateTimeline.getSession() + ": " + generateTimeline.getCourses());


    }

    @Override
    public int getItemCount() {
        //return session_courses.size();
        return 0;
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        TextView sessionCourse;

//        TextView firstName, lastName, age;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);

            sessionCourse = itemView.findViewById(R.id.tvsessionCourse);

        }
    }


    //remember to convert history to lowercase
    public HashMap buildSched(ArrayList<String> wantToTake, ArrayList<String> history,
                              HashMap<String, ArrayList<String>> sched,
                              HashMap<String, String> lastPre, String followingCourse) {

        Log.d("Top of buildSched: ", wantToTake.toString());

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
        if (wantToTake.size() == 1 && allCoursesMap.get(wantToTake.get(0)).get("prereqs").contains("null")) {

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
                    if (!history.contains(pre)){
                        ArrayList<String> listOfSinglePre = new ArrayList<String>(Arrays.asList(pre));
                        sched = buildSched(listOfSinglePre, history, sched, lastPre, course);
                    }
                    else{
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
                String nextOfferedSession = nextSession(course, lastPre.get(course));
                if (!history.contains(course)){
                    ArrayList<String> singleCourseList = new ArrayList<>(Arrays.asList(course));
                    //remember to append if key exists
                    sched.put(nextOfferedSession, singleCourseList);
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
}


//want to take





