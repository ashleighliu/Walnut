package com.example.b07_project.MVP;

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
        void attemptLogin(Activity activity, String email, String password, Model model);
    }

    interface onLoginListener {
        void onSuccess(String message, boolean isAdmin);
        void onFailure(String message);
    }
}
