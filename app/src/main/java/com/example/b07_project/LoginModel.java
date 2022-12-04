package com.example.b07_project;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

public class LoginModel {
    SharedPreferences p;
    SharedPreferences.Editor editor;
    LoginPresenter lp;

    LoginModel(MainActivity activity) {
        p = activity.getSharedPreferences("myPreferences", Context.MODE_PRIVATE);
        editor = p.edit();
    }

    public void setLoginPresenter(LoginPresenter lp) {
        this.lp = lp;
    }

    public void addStringsToSharedPreferences(String uID, String email, String password){
        editor.putString("uID", uID);
        editor.putString("email", email);
        editor.putString("password", password);
        editor.apply();
    }

    public void addAcademicHistory(Set<String> history){
        editor.putStringSet("history", history);
        editor.commit();
    }

    public void login(String email, String password) {
        FirebaseAuth.getInstance().signInWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()) {
                    String uID = FirebaseAuth.getInstance().getCurrentUser().getUid();
                    //SharedPreferences stuff
                    //Needed to retrieve data once you're in student landing page
                    FirebaseDatabase.getInstance().getReference().child("Accounts").child(uID).get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<DataSnapshot> task) {
                            if (task.isSuccessful()) {
                                //Getting info of account
                                String isAdmin = String.valueOf(task.getResult().child("isAdmin").getValue());
                                String email = String.valueOf(task.getResult().child("email").getValue());
                                String password = String.valueOf(task.getResult().child("password").getValue());
                                //Passing info of account through SharedPreferences to landing page
                                //More of these will be needed for other info (eg. AcademicHistory)
                                addStringsToSharedPreferences(uID, email, password);

                                if (isAdmin.equals("false")) {
                                    ArrayList<String> history = new ArrayList<>();
                                    //Redirect to student landing page
                                    DatabaseReference history_ref = FirebaseDatabase.getInstance().getReference().child("Accounts").child(uID).child("Courses_taken");
                                    history_ref.addValueEventListener(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                                            for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                                                history.add(dataSnapshot.getKey());
                                            }
                                            Set<String> set = new HashSet<>();
                                            set.addAll(history);
                                            Log.i("myTag", String.valueOf(set.size()));
                                            addAcademicHistory(set);
                                            lp.allowLogin("Login Successful", false);
                                            history_ref.removeEventListener(this);
                                        }

                                        @Override
                                        public void onCancelled(@NonNull DatabaseError error) {

                                        }
                                    });
                                } else {
                                    //Redirect to admin landing page
                                    lp.allowLogin("Login Successful", true);
                                }
                            } else {
                                //this should never occur
                            }
                        }
                    });
                } else {
                    lp.rejectLogin("Login Unsuccessful"); //If login credentials incorrect
                }
            }
        });
    }
}
