package com.example.b07_project;

import androidx.appcompat.app.AppCompatActivity;


import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

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
        lp = new LoginPresenter(this, new LoginModel(this));
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btnNewAccount:
                startActivity(new Intent(MainActivity.this, RegisterActivity.class));
                break;
            case R.id.btnLogin:
                commenceLogin();
                break;
        }
    }

    private void commenceLogin() {
        //progressDialog is just for UI purposes, if it causes too many problems feel free to remove
        progressDialog.setMessage("Please Wait While Logging in...");
        progressDialog.setTitle("Login");
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.show();
        String email = inputEmail.getText().toString();
        String password = inputPassword.getText().toString();
        lp.login(email, password);
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