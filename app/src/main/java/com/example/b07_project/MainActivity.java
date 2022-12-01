package com.example.b07_project;

import androidx.appcompat.app.AppCompatActivity;


import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.b07_project.Presenter.LoginContract;
import com.example.b07_project.Presenter.LoginPresenter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

    public class MainActivity extends AppCompatActivity implements View.OnClickListener, LoginContract.View {
    Button btnNewAccount;
    EditText inputEmail, inputPassword;
    Button btnLogin;
    String emailPattern = "[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+";
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
        lp = new LoginPresenter(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btnNewAccount:
                startActivity(new Intent(MainActivity.this, RegisterActivity.class));
                break;
            case R.id.btnLogin:
                validate();
                break;
        }
    }

    private void commenceLogin(String email, String password) {
        //progressDialog is just for UI purposes, if it causes too many problems feel free to remove
        progressDialog.setMessage("Please Wait While Logging in...");
        progressDialog.setTitle("Login");
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.show();
        lp.login(this, email, password);
    }

    private void validate() {
        String email = inputEmail.getText().toString();
        String password = inputPassword.getText().toString();
        //Checking validity
        boolean valid = true;
        if (!email.matches(emailPattern)) {
            inputEmail.setError("Enter Valid Email");
            valid = false;
        }
        if (password.isEmpty() || password.length() < 6){
            inputPassword.setError("Enter Proper Password of At Least 6 Characters");
            valid = false;
        }
        if (!valid) {
            return;
        }
        commenceLogin(email, password);
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

    @Override
    public void onSuccessfulLogin(String message, boolean isAdmin) {
        progressDialog.dismiss();
        Toast.makeText(MainActivity.this, message, Toast.LENGTH_SHORT).show();
        if (isAdmin) {
            sendUserToNextAdminActivity();
        } else {
            sendUserToNextStudentActivity();
        }
    }

    @Override
    public void onFailedLogin(String message) {
        progressDialog.dismiss();
        Toast.makeText(MainActivity.this, message, Toast.LENGTH_SHORT).show();
    }
}