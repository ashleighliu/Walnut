package com.example.b07_project;

import android.content.SharedPreferences;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import android.content.Context;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.w3c.dom.Text;

public class AddCourseToTimelineAdapter extends RecyclerView.Adapter<AddCourseToTimelineAdapter.MyViewHolder> {
    String uID;
    ArrayList<String> history;
    DatabaseReference fire;
    DatabaseReference user;
    DatabaseReference allCourses;
    DatabaseReference userCoursesTaken;
    ArrayList<String> coursesToTake = new ArrayList<>();

    Context context;
    ArrayList<History> list;

    public AddCourseToTimelineAdapter(Context context, ArrayList<History> list) {
        this.context = context;
        this.list = list;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        //Retrieving account info from SharedPreferences
        SharedPreferences p = context.getSharedPreferences("myPreferences", Context.MODE_PRIVATE);
        uID = p.getString("uID", "N/A");
        Set<String> set = p.getStringSet("history", new HashSet<String>());
        Log.i("myTag", String.valueOf(set.size()));
        history = new ArrayList<>(set);


        //Probably will be needed for updating the student account data (eg. adding courses, adding academic history);
        fire = FirebaseDatabase.getInstance().getReference();
        user = fire.child("Accounts").child(uID);
        userCoursesTaken = user.child("Courses_taken");
        allCourses = fire.child("Courses");

        View v = LayoutInflater.from(context).inflate(R.layout.item,parent,false);
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

    public class MyViewHolder extends RecyclerView.ViewHolder {
        TextView courseCode;
        History history1;
        CourseID courseID;

        String addCourseID;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);

            courseCode = itemView.findViewById(R.id.tvcourseCode2);

            itemView.findViewById(R.id.addCourseToTimelineButton).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    allCourses.addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {

                            for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                                History temp = dataSnapshot.getValue(History.class);
                                if (temp.getCourseCode().equals(history1.courseCode)) {
                                    courseID = dataSnapshot.getValue(CourseID.class);
                                    addCourseID = courseID.getCourseID();
                                    Log.d("tag", "this is " + courseID.getCourseID());

                                }
                            }
                            boolean toAdd = true;
                            if (history.contains(history1.courseCode)) {
                                toAdd = false;
                            } else {
                                toAdd = true;
                            }
                            if (toAdd) {
                                userCoursesTaken.child(addCourseID).setValue("");
                                coursesToTake.add(history1.courseCode);
                            }
                        }


                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });
                }
            });
            //if you click generate:
            //  go to next activity
        }
    }
}
