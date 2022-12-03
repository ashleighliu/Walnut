package com.example.b07_project;



import android.content.Context;
import android.content.SharedPreferences;
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

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.Set;

public class AcademicHistory extends Fragment {
    RecyclerView recyclerView;
    DatabaseReference databaseReference;
    MyAdapter myAdapter;
    ArrayList<Course> history_info;
    ArrayList<String> history;
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
        history_info  = new ArrayList<>();
        myAdapter = new MyAdapter(getContext(), history_info);
        recyclerView.setAdapter(myAdapter);
        historyInitialize();
        if(history != null) {
            Log.i("myTa", "hi");
            databaseReference = FirebaseDatabase.getInstance().getReference().child("Courses");
            databaseReference.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    for(int i = 0; i < history.size(); i++)
                    {

                        String name = snapshot.child(history.get(i)).child("courseName").getValue(String.class);
                        String code = snapshot.child(history.get(i)).child("courseCode").getValue(String.class);
                        Course course = new Course(name, code);
                        history_info.add(course);
                    }

                    databaseReference.removeEventListener(this);
                    if(history_info.isEmpty()) {
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
        SharedPreferences p = getActivity().getSharedPreferences("myPreferences", Context.MODE_PRIVATE);
        String historyList = p.getString("history", "N/A");
        history = new ArrayList<>();
        if(!historyList.equals("N/A") && !historyList.equals(""))
        {
            String[] temp = historyList.split(";");
            for(int i=0;i<temp.length;i++){
                history.add(temp[i]);
            }
        }
    }
}