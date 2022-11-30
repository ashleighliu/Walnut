package com.example.b07_project;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;


import java.util.ArrayList;
import java.util.LinkedHashSet;

public class MyAdapter extends RecyclerView.Adapter<MyAdapter.MyViewHolder> {
    Context myContext;
    ArrayList<Course> history_list;

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
        return new MyViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        Course course = history_list.get(position);
        holder.course_code.setText(course.getCourseCode());
        holder.course_name.setText(course.getCourseName());

    }

    @Override
    public int getItemCount() {
        return history_list.size();
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder{
        TextView course_name, course_code;
        public MyViewHolder(@NonNull View itemView) {
            super(itemView);

            course_name = itemView.findViewById(R.id.course_name);
            course_code = itemView.findViewById(R.id.course_code);

        }
    }
}
