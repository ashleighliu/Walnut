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

public class courseListForTimeline extends AppCompatActivity {
    Button backButtonForAddToTimeline;
    RecyclerView recyclerView;
    DatabaseReference database;
    AddCourseToTimelineAdapter myAdapter;
    ArrayList<History> list;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_course_list_for_timeline);

        recyclerView = findViewById(R.id.courseListForTimeline);

        database = FirebaseDatabase.getInstance().getReference("Courses");
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        list = new ArrayList<>();
        myAdapter = new AddCourseToTimelineAdapter(this, list);
        recyclerView.setAdapter(myAdapter);

        backButtonForAddToTimeline = findViewById(R.id.backButtonForAddToTimeline);
        backButtonForAddToTimeline.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(courseListForTimeline.this,StudentLanding.class));
            }
        });


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