package com.example.b07_project.Presenter;

import android.app.Activity;

public class LoginContract {
    public interface View {
        void onSuccessfulLogin(String message, boolean isAdmin);
        void onFailedLogin(String message);
    }

    interface Presenter {
        void login(Activity activity, String email, String password);
    }

    interface Interactor {
        void attemptLogin(Activity activity, String email, String password);
    }

    interface onLoginListener {
        void onSuccess(String message, boolean isAdmin);
        void onFailure(String message);
    }
}
