package com.example.b07_project;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import java.util.ArrayList;

public class Timeline extends AppCompatActivity {
    RecyclerView recyclerView;
    GenerateTimelineAdapter myAdapter;
    ArrayList<GenerateTimeline> list;
    Button timelineBackButton;

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

        timelineBackButton = findViewById(R.id.timelineBackButton);
        timelineBackButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(Timeline.this,StudentLanding.class));
            }
        });
    }
}