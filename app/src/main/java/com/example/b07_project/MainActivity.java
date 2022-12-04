package com.example.b07_project;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;


import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

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

public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    Button btnNewAccount;
    EditText inputEmail, inputPassword;
    Button btnLogin;
    ProgressDialog progressDialog;
    FirebaseAuth fire;
    FirebaseUser user;
    LoginPresenter lp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        btnLogin = findViewById(R.id.btnLogin);
        btnLogin.setOnClickListener(this);
        btnNewAccount = findViewById(R.id.btnNewAccount);
        btnNewAccount.setOnClickListener(this);
        inputEmail = findViewById(R.id.inputLoginEmail);
        inputPassword = findViewById(R.id.inputLoginPassword);
        progressDialog = new ProgressDialog(this);
        fire = FirebaseAuth.getInstance();
        user = fire.getCurrentUser();
        LoginModel lm = new LoginModel(this);
        lp = new LoginPresenter(this, lm);
        lm.setLoginPresenter(lp);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btnNewAccount:
                startActivity(new Intent(MainActivity.this, RegisterActivity.class));
                break;
            case R.id.btnLogin:
                attemptLogin();
                break;
        }
    }

    private void attemptLogin() {
        //progressDialog is just for UI purposes, if it causes too many problems feel free to remove
        progressDialog.setMessage("Please Wait While Logging in...");
        progressDialog.setTitle("Login");
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.show();
        String email = inputEmail.getText().toString();
        String password = inputPassword.getText().toString();
        lp.validate(email, password);
    }

    public void sendUserToNextStudentActivity(){
        Intent intent = new Intent(MainActivity.this, StudentLanding.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK|Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }

    public void sendUserToNextAdminActivity(){
        Intent intent = new Intent(MainActivity.this, AdminLanding.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK|Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }

    public void emitEmailError() {
        inputEmail.setError("Enter Valid Email");
    }

    public void emitPasswordError() {
        inputPassword.setError("Enter Proper Password of At Least 6 Characters");
    }

    public void loginSuccess(String message, boolean isAdmin) {
        progressDialog.dismiss();
        Toast.makeText(MainActivity.this, message, Toast.LENGTH_SHORT).show();
        if (isAdmin) {
            sendUserToNextAdminActivity();
        } else {
            sendUserToNextStudentActivity();
        }
    }

    public void loginFailure(String message) {
        progressDialog.dismiss();
        Toast.makeText(MainActivity.this, message, Toast.LENGTH_SHORT).show();
    }
}