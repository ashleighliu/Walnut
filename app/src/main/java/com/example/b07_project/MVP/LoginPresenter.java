package com.example.b07_project.MVP;

import android.app.Activity;

import com.example.b07_project.MainActivity;

public class LoginPresenter implements LoginContract.Presenter, LoginContract.onLoginListener {
    LoginContract.View lv;
    LoginInteractor li;
    Model model;
    MainActivity main;

    public LoginPresenter(LoginContract.View lv) {
        this.lv = lv;
        this.li = new LoginInteractor(this);
    }

    @Override
    public void login(Activity activity, String email, String password) {
        li.attemptLogin(activity, email, password, model);
    }

    @Override
    public void onSuccess(String message, boolean isAdmin) {
        lv.onSuccessfulLogin(message, isAdmin);
    }

    @Override
    public void onFailure(String message) {
        lv.onFailedLogin(message);
    }
}
