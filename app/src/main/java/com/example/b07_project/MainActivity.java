package com.example.b07_project;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;


import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

//<<<<<<< HEAD
import com.example.b07_project.MVP.LoginContract;
import com.example.b07_project.MVP.LoginPresenter;
//=======
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
//>>>>>>> parent of 2906321 (Merge pull request #9 from ashleighliu/RefactorMVP)

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

public class MainActivity extends AppCompatActivity {
    Button btnNewAccount;
    EditText inputEmail,inputPassword;
    Button btnLogin;
    String emailPattern = "[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+";
    ProgressDialog progressDialog;
    FirebaseAuth fire;
    FirebaseUser user;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        btnNewAccount = findViewById(R.id.btnNewAccount);
        inputEmail = findViewById(R.id.inputLoginEmail);
        inputPassword = findViewById(R.id.inputLoginPassword);
        btnLogin = findViewById(R.id.btnLogin);
        progressDialog = new ProgressDialog(this);
        fire = FirebaseAuth.getInstance();
        user = fire.getCurrentUser();
        btnNewAccount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v){
                startActivity(new Intent(MainActivity.this, RegisterActivity.class));
            }
        });

        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v){
                login();
            }
        });

    }

    private void login(){
        //Retrieving email and password from what user typed
        String email=inputEmail.getText().toString();
        String password=inputPassword.getText().toString();
        //Checking validity
        if (!email.matches(emailPattern)) {
            inputEmail.setError("Enter Valid Email");
        }
        else if(password.isEmpty() || password.length()<6){
            inputPassword.setError("Enter Proper Password of At Least 6 Characters");
        }
        else { //progressDialog is just for UI purposes, if it causes too many problems feel free to remove
            progressDialog.setMessage("Please Wait While Logging in...");
            progressDialog.setTitle("Login");
            progressDialog.setCanceledOnTouchOutside(false);
            progressDialog.show();
            //Signing in with firebase
            fire.signInWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    if (task.isSuccessful()) {
                        String uID = FirebaseAuth.getInstance().getCurrentUser().getUid();
                        //SharedPreferences stuff
                        //Needed to retrieve data once you're in student landing page
                        FirebaseDatabase.getInstance().getReference().child("Accounts").child(uID).get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<DataSnapshot> task) {
                                if(task.isSuccessful()){
                                    //Getting info of account
                                    String isAdmin = String.valueOf(task.getResult().child("isAdmin").getValue());
                                    String email = String.valueOf(task.getResult().child("email").getValue());
                                    String password = String.valueOf(task.getResult().child("password").getValue());
                                    //Passing info of account through SharedPreferences to landing page
                                    //More of these will be needed for other info (eg. AcademicHistory)
                                    SharedPreferences p = getSharedPreferences("myPreferences", Context.MODE_PRIVATE);
                                    SharedPreferences.Editor editor = p.edit();
                                    editor.putString("uID", uID);
                                    editor.putString("email", email);
                                    editor.putString("password", password);
                                    editor.apply();

                                    if (isAdmin.equals("false")){
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
                                                editor.putStringSet("history", set);
                                                editor.commit();
                                                progressDialog.dismiss();
                                                sendUserToNextStudentActivity();
                                                Toast.makeText(MainActivity.this, "Login Successful", Toast.LENGTH_SHORT).show();
                                                history_ref.removeEventListener(this);
                                            }

                                            @Override
                                            public void onCancelled(@NonNull DatabaseError error) {

                                            }

                                        });

                                    }
                                    else{
                                        //Redirect to admin landing page
                                        progressDialog.dismiss();
                                        sendUserToNextAdminActivity();
                                        Toast.makeText(MainActivity.this, "Login Successful", Toast.LENGTH_SHORT).show();
                                    }
                                }
                                else{
                                    //this should never occur
                                }
                            }
                        });

                    }
                    else{
                        progressDialog.dismiss();
                        Toast.makeText(MainActivity.this, ""+task.getException(), Toast.LENGTH_SHORT).show(); //If login credentials incorrect
                    }
                }
            });
        }
    }
    private void sendUserToNextStudentActivity(){
        Intent intent = new Intent(MainActivity.this, StudentLanding.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK|Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }
    private void sendUserToNextAdminActivity(){
        Intent intent = new Intent(MainActivity.this, AdminLanding.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK|Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }
}