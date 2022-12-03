package com.example.b07_project;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class courseTimelineList extends AppCompatActivity {
    Button backButton1;
    Button generateTimelineButton;
    RecyclerView recyclerView;
    DatabaseReference database;
    AddCourseToTimelineAdapter myAdapter;
    ArrayList<History> list;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_course_timeline_list);

        recyclerView = findViewById(R.id.courseTimelineList);

        database = FirebaseDatabase.getInstance().getReference("Courses");
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        list = new ArrayList<>();
        myAdapter = new AddCourseToTimelineAdapter(this, list);
        recyclerView.setAdapter(myAdapter);

        backButton1 = findViewById(R.id.backButton1);
        generateTimelineButton = findViewById(R.id.generateTimelineButton);
        backButton1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(courseTimelineList.this,StudentLanding.class));
            }
        });

        /* --> WIP
        generateTimelineButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(courseTimelineList.this,[INSERTHERE].class));
            }
        });
        */

        database.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                for (DataSnapshot dataSnapshot : snapshot.getChildren()){
                    History history1 = dataSnapshot.getValue(History.class);
                    list.add(history1);
                }

                myAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
}