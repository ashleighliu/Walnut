package com.example.b07_project;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import java.util.ArrayList;

public class Timeline extends AppCompatActivity {

    Button timelineBackButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_timeline);

        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            ArrayList<String> timelineCourses = extras.getStringArrayList("key");
            Log.d("Courses in Table", timelineCourses.toString());
            //The key argument here must match that used in the other activity
        }

        timelineBackButton = findViewById(R.id.timelineBackButton);
        timelineBackButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(Timeline.this,StudentLanding.class));
            }
        });
    }
}