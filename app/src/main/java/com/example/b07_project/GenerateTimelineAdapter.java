package com.example.b07_project;

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

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.HashMap;

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
    ArrayList<History> list;


    //Courses = {courseCode: <{sessions:<courses>}, {prereqs:<courses>}>}

    //e.g., Courses = {b07:{session:<fall,
    //                               Winter>,
    //                      prereqs:<a48,
    //                               a22>}
    //                 b09: ...}

    HashMap<String, HashMap<String, ArrayList<String>>> allCoursesMap = new HashMap<>();

    HashMap<String, ArrayList<String>> mapOfSessionPrereq = new HashMap<>();

//    ArrayList<String> coursesUnderSession = new ArrayList<>();
//    ArrayList<String> coursesUnderPrereq = new ArrayList<>();


    ////////// REMEMBER TO FIX THIS /////////////
    public GenerateTimelineAdapter(Context context, HashMap<String, String> session_courses) {
        this.context = context;
        //this.session_courses = session_courses;
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

        View v = LayoutInflater.from(context).inflate(R.layout.generate_timeline_item, parent, false);
        return new MyViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {

        //History user = list.get(position);
        //holder.firstName.setText(user.getFirstName());


    }

    @Override
    public int getItemCount() {
        //return session_courses.size();
        return 0;
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        OfferingSession offeringSession;
        ArrayList<String> offeringSessionsForCourses;
        ArrayList<String> prereqForCourses;
        CourseID courseID;
        String addCourseID;

        History historyCourseCode;
        String courseCode;

//        TextView firstName, lastName, age;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            allCourses.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    for (DataSnapshot dataSnapshot : snapshot.getChildren()){
                        courseID = dataSnapshot.getValue(CourseID.class);
                        addCourseID = courseID.getCourseID(); //actual value of courseID to be added somewhere
                        historyCourseCode = dataSnapshot.getValue(History.class);
                        courseCode = historyCourseCode.getCourseCode();
                        Prereq prereq = dataSnapshot.getValue(Prereq.class);
                        prereqForCourses = new ArrayList<String>();
                        for (String s: prereq.getPreReqs()){
                            prereqForCourses.add(s); //prereqForCourses contain values of courseIDs to be added
                        }
                        /// building mapallcourses
                        //for pre in prereqforcourses:
                        //      for each datasnapshot:
                        //          courseID2 = datasnapshot...
                        //          addCourseID2 = courseID2.getCourseID()
                        //          historyCourseCode2 = datasnapshot...
                        //          coursecode2 = historycoursecode2.getCourseCode()
                        //          if pre = addCourseID2: prereq.set(prereqforcourses.indexof(pre), courseCode2);
                        //mapOfSessionPrereq.put("prereqs", prereqForCourses);
                        //do mapOfSessionPrereq.put("sessions", offeringSessionsForCourses)
                        //mapAllCourses.put("courseCode", mapOfSessionPrereq)

                        offeringSession = dataSnapshot.getValue(OfferingSession.class);
                        offeringSessionsForCourses = new ArrayList<String>();
                        for (String s: offeringSession.getOfferingSessions()){
                            offeringSessionsForCourses.add(s); //offeringSessionsForCourses contain vals of course sessions t.b.a
                        }
                    }

                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
//            firstName = itemView.findViewById(R.id.tvfirstName);
//            lastName = itemView.findViewById(R.id.tvlastName);
//            age = itemView.findViewById(R.id.tvage);
//            allCoursesMap.put(courseCode,);
//            mapOfSessionPrereq.put("sessions", offeringSessionsForCourses);
//            mapOfSessionPrereq.put("prereqs", prereqForCourses);




        }
    }

    public HashMap buildSched(ArrayList wantToTake, ArrayList history,
                              HashMap<String, ArrayList<String>> sched,
                              HashMap<String, String> lastPre, String followingCourse) {
        ArrayList<String> coursesToSched = new ArrayList<String>();

        if (!(followingCourse.equals(""))) {

        }


        return sched;
    }
}
