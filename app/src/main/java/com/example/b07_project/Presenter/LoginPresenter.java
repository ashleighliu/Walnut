package com.example.b07_project.Presenter;

import android.app.Activity;

public class LoginPresenter implements LoginContract.Presenter, LoginContract.onLoginListener {
    LoginContract.View lv;
    LoginInteractor li;

    public LoginPresenter(LoginContract.View lv) {
        this.lv = lv;
        this.li = new LoginInteractor(this);
    }

    @Override
    public void login(Activity activity, String email, String password) {
        li.attemptLogin(activity, email, password);
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
