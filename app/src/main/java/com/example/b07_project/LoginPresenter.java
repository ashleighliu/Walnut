package com.example.b07_project;

public class LoginPresenter {
    String emailPattern = "[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+";
    MainActivity view;
    LoginModel model;

    public LoginPresenter(MainActivity view, LoginModel model) {
        this.view = view;
        this.model = model;
    }

    public void validate(String email, String password) {
        //Checking validity
        boolean valid = true;
        if (!email.matches(emailPattern)) {
            view.emitEmailError();
            valid = false;
        }
        if (password.isEmpty() || password.length() < 6){
            view.emitPasswordError();
            valid = false;
        }
        if (!valid) {
            view.loginFailure("Login Unsuccessful");
            return;
        }
        //Signing in with firebase
        model.login(email, password);
    }

    public void allowLogin(String message, boolean isAdmin) {
        view.loginSuccess(message, isAdmin);
    }

    public void rejectLogin(String message) {
        view.loginFailure(message);
    }
}
