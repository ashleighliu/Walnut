package com.example.b07_project;



import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.LinkedHashSet;

public class AcademicHistory extends Fragment {
    RecyclerView recyclerView;
    DatabaseReference databaseReference;
    MyAdapter myAdapter;
    ArrayList<Course> please_info;
    ArrayList<String> please;
    TextView no_data;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_academic_history, container, false);

    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState){
        super.onViewCreated(view, savedInstanceState);
        recyclerView = view.findViewById(R.id.recycler_view);
        no_data = view.findViewById((R.id.empty_set));
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setHasFixedSize(true);

        please_info = new ArrayList<>();
        myAdapter = new MyAdapter(getContext(), please_info);
        recyclerView.setAdapter(myAdapter);
        historyInitialize();
        if(please != null) {
            Log.i("myTag", "hi");
            databaseReference = FirebaseDatabase.getInstance().getReference().child("Courses");
            databaseReference.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                        if (please.contains(dataSnapshot.getKey())) {

                            Course taken = new Course(dataSnapshot.child("courseName").getValue(String.class),
                                    dataSnapshot.child("courseCode").getValue(String.class));
                            please_info.add(taken);
                        }
                    }
                    databaseReference.removeEventListener(this);
                    if(please_info.isEmpty()) {
                        no_data.setVisibility(View.VISIBLE);
                        recyclerView.setVisibility(View.GONE);

                    }
                    else
                    {
                        no_data.setVisibility(View.GONE);
                        recyclerView.setVisibility(View.VISIBLE);
                    }
                    myAdapter.notifyDataSetChanged();
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
        }




    }

    public void historyInitialize(){
        if(getArguments().getStringArrayList("history") != null) {
            please = getArguments().getStringArrayList("history");
            Log.i("myTag", "hello");
        }
    }
}