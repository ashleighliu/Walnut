package com.example.b07_project;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.SignInMethodQueryResult;
import com.google.firebase.database.FirebaseDatabase;


public class RegisterActivity extends AppCompatActivity {

    Button alreadyHaveAccount;
    EditText inputEmail,inputPassword;
    Button btnRegister;
    String emailPattern = "[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+";
    ProgressDialog progressDialog;
    FirebaseAuth fire;
    FirebaseUser user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        alreadyHaveAccount = findViewById(R.id.alreadyHaveAccount);
        inputEmail = findViewById(R.id.inputEmail);
        inputPassword = findViewById(R.id.inputPassword);
        btnRegister = findViewById(R.id.registerbtnstudent);
        progressDialog = new ProgressDialog(this);
        fire = FirebaseAuth.getInstance();
        user = fire.getCurrentUser();

        alreadyHaveAccount.setOnClickListener(new View.OnClickListener() {
        @Override
            public void onClick(View v) {
            startActivity(new Intent(RegisterActivity.this, MainActivity.class));
            }
        });

        btnRegister.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                PerforAuth();
            }
        });

    }

    private void PerforAuth(){
        String email=inputEmail.getText().toString();
        String password=inputPassword.getText().toString();
        if (!email.matches(emailPattern)) {
            inputEmail.setError("Enter Valid Email");
        }
        else if(password.isEmpty() || password.length()<6){
            inputPassword.setError("Enter Proper Password of At Least 6 Characters");
        }
        else{
            progressDialog.setMessage("Please Wait While Registering...");
            progressDialog.setTitle("Registration");
            progressDialog.setCanceledOnTouchOutside(false);
            progressDialog.show();
            fire.fetchSignInMethodsForEmail(email).addOnCompleteListener(new OnCompleteListener<SignInMethodQueryResult>() {
                @Override
                public void onComplete(@NonNull Task<SignInMethodQueryResult> task) {
                    if(task.getResult().getSignInMethods().size() == 0){
                        fire.createUserWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task){
                                if (task.isSuccessful()){
                                    StudentAccount studentAccount = new StudentAccount(email, password);
                                    String uID = FirebaseAuth.getInstance().getCurrentUser().getUid();
                                    FirebaseDatabase.getInstance().getReference("Students").child(
                                        FirebaseAuth.getInstance().getCurrentUser().getUid()).setValue(studentAccount).addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            if(task.isSuccessful()){
                                                Toast.makeText(RegisterActivity.this, "Registration Successful", Toast.LENGTH_SHORT).show();
                                                sendUserToNextActivity();
                                                progressDialog.dismiss();
                                            }
                                            else{
                                                progressDialog.dismiss();
                                                Toast.makeText(RegisterActivity.this, ""+task.getException(), Toast.LENGTH_SHORT).show();
                                            }
                                        }
                                    });
                                }
                                else{
                                    progressDialog.dismiss();
                                    Toast.makeText(RegisterActivity.this, ""+task.getException(), Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
                    }
                    else{
                        inputEmail.setError("Account with this email already exists");
                        progressDialog.dismiss();
                        return;
                    }
                }
            });





        }
    }
    private void sendUserToNextActivity(){
        Intent intent = new Intent(RegisterActivity.this, adminlanding.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK|Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }
}