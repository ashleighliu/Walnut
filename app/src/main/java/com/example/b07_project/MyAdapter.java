package com.example.b07_project;

import android.content.Context;
import android.content.SharedPreferences;
import android.provider.ContactsContract;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.RecyclerView;


import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

public class MyAdapter extends RecyclerView.Adapter<MyAdapter.MyViewHolder> {
    Context myContext;
    private ArrayList<Course> history_list;


    public MyAdapter(Context context, ArrayList<Course> history)
    {
        myContext = context;
        history_list = history;
        //could change this to be an arraylist adapter or some shit
    }
    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(myContext).inflate(R.layout.course_info, parent, false);
        SharedPreferences p = myContext.getSharedPreferences("myPreferences", Context.MODE_PRIVATE);
        return new MyViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        Course course = history_list.get(position);
        holder.courseCodeDisplay.setText(course.getCourseCode());
        holder.courseNameDisplay.setText(course.getCourseName());
    }

    @Override
    public int getItemCount() {
        return history_list.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        TextView courseNameDisplay, courseCodeDisplay;
        ImageButton delete_course;


        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            courseNameDisplay = itemView.findViewById(R.id.historyCourseName);
            courseCodeDisplay = itemView.findViewById(R.id.historyCourseCode);
            delete_course = itemView.findViewById(R.id.historyDeleteCourse);

            delete_course.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    SharedPreferences p = myContext.getSharedPreferences("myPreferences", Context.MODE_PRIVATE);
                    String uID = p.getString("uID", "oh no");
                    String historyList = p.getString("history", "N/A");
                    String[] temp = historyList.split(";");
                    DatabaseReference studentHistory = FirebaseDatabase.getInstance().getReference();
                    ArrayList<String> courseTaken = new ArrayList<>();
                    for(int i = 0; i<temp.length;i++){
                        courseTaken.add(temp[i]);
                    }
                    studentHistory.addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            DataSnapshot gotoCourses = snapshot.child("Courses");
                            String courseID = courseTaken.get(getAdapterPosition());
                            boolean deletable = true;
                            for (DataSnapshot x : gotoCourses.getChildren()) {
                                if (!((x.getKey()).equals(courseID))) {
                                    DataSnapshot lookAtPrereqs = x.child("prereqs");
                                    for (DataSnapshot y : lookAtPrereqs.getChildren()) {
                                        if ((y.getValue(String.class)).equals(courseID) && courseTaken.contains(x.getKey())) {
                                            deletable = false;
                                            break;
                                        }
                                    }
                                }

                            }
                            if (deletable) {
                                history_list.remove(getAdapterPosition());
                                SharedPreferences.Editor editor = p.edit();
                                courseTaken.remove(getAdapterPosition());
                                studentHistory.child("Accounts").child(uID).child("Courses_taken").setValue(courseTaken);
                                String historyList = "";
                                for(int i=0;i<courseTaken.size()-1;i++){
                                    historyList = historyList + courseTaken.get(i) + ";";
                                }
                                if(courseTaken.size() >=1){historyList = historyList + courseTaken.get(courseTaken.size()-1);}
                                editor.putString("history", historyList);
                                editor.commit();


                            }
                            studentHistory.removeEventListener(this);
                            notifyDataSetChanged();
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
