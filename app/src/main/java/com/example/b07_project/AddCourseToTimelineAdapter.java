package com.example.b07_project;

import static com.example.b07_project.courseTimelineList.coursesToTimeline;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.b07_project.History;
import com.example.b07_project.MyAdapter;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Locale;
import java.util.Set;

public class AddCourseToTimelineAdapter extends RecyclerView.Adapter<AddCourseToTimelineAdapter.MyViewHolder> {
    String uID;
    ArrayList<String> history;
    String historyString;
    DatabaseReference fire;
    DatabaseReference user;
    DatabaseReference allCourses;
    DatabaseReference userCoursesTaken;
    SharedPreferences p;
    Context context;
    ArrayList<History> list;
    Set<String> courseToTakeSet = new HashSet<String>();
    ArrayList<String> courseToTake; //= new ArrayList<String>();

//    public ArrayList getCourseToTake(){
//        return courseToTake;
//    }

    public AddCourseToTimelineAdapter(Context context, ArrayList<History> list) {
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

        Log.d("User Courses:", history.toString());

        fire = FirebaseDatabase.getInstance().getReference();
        user = fire.child("Accounts").child(uID);
        userCoursesTaken = user.child("Courses_taken");
        allCourses = fire.child("Courses");

        View v = LayoutInflater.from(context).inflate(R.layout.timeline_item,parent,false);

        return new MyViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        History history1 = list.get(position);
        holder.courseCode.setText(history1.getCourseCode());
        holder.history1 = history1;

    }

    @Override
    public int getItemCount() {
        return list.size();
    }


    public class MyViewHolder extends RecyclerView.ViewHolder{
        TextView courseCode;
        History history1;
        CourseID courseID;

        String addCourseID;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);

            courseCode = itemView.findViewById(R.id.tvcourseCode1);

            itemView.findViewById(R.id.addCourseToTimelineButton).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    allCourses.addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {

                            for (DataSnapshot dataSnapshot : snapshot.getChildren()){
                                History temp = dataSnapshot.getValue(History.class);
                                if (temp.getCourseCode().equals(history1.courseCode)){
                                    courseID = dataSnapshot.getValue(CourseID.class);
                                    addCourseID = courseID.getCourseID();
                                }
                            }
                            boolean toAdd = true;
                            if (history.contains(addCourseID)) {
                                Log.d("Doing it's job", history.toString());
                                toAdd = false;
                            }
//                            else{
//                                toAdd = true;
//                            }
                            if (toAdd) {
//                                history.add(addCourseID);
//                                user.child("Courses_taken").setValue(history);
//                                Set set = p.getStringSet("history", new HashSet<String>());
//                                set.add(addCourseID);
//                                SharedPreferences.Editor editor = p.edit();
//                                editor.putStringSet("history", set);
                                courseToTakeSet.add(history1.courseCode.toLowerCase()); //Now a ArrayList<String> of courseCodes, NOT cIDs

                            }
                            courseToTake = new ArrayList<>(courseToTakeSet);
                            coursesToTimeline = courseToTake;

                            Log.d("Courses to Add:", coursesToTimeline.toString());
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });
                }
            });

        }
    }

}