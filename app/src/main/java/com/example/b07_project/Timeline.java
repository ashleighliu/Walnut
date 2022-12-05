package com.example.b07_project;

import static com.example.b07_project.GenerateTimelineAdapter.schedule;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;

public class Timeline extends AppCompatActivity {
    RecyclerView recyclerView;
    GenerateTimelineAdapter myAdapter;
    ArrayList<GenerateTimeline> list;
    Button timelineBackButton;
    HashMap<String, ArrayList<String>> schedTimeline = schedule;
    DatabaseReference database;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_timeline);

        recyclerView = findViewById(R.id.Timeline);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        list = new ArrayList<>();
        myAdapter = new GenerateTimelineAdapter(this,list);
        recyclerView.setAdapter(myAdapter);

        // sched in buildSched in GenerateTimelineAdapter is our desired hashmap
        // we want to take each key-value pair of sched and append it to list
        // myAdapter.notifyDataSetChanged();

        for (String key: schedTimeline.keySet()) {
            HashMap<String, ArrayList<String>> keyValue= new HashMap<>();
            keyValue.put(key, schedTimeline.get(key));
            GenerateTimeline generateTimeline = new GenerateTimeline(keyValue);
            list.add(generateTimeline);
        }

        timelineBackButton = findViewById(R.id.timelineBackButton);
        timelineBackButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d("If this runs: ", "It's empty");
                for(String key : schedule.keySet()){
                    Log.d("HM Session: ", key);
                    Log.d("HM Courses: ", schedule.get(key).toString());
                }

                startActivity(new Intent(Timeline.this,StudentLanding.class));
            }
        });
    }
}