package com.example.b07_project;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.google.firebase.auth.FirebaseAuth;

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
@RunWith(MockitoJUnitRunner.class)
public class ExampleUnitTest {
    @Mock
    MainActivity view;

    @Mock
    LoginModel model;

    @Test
    public void testValidStudentLogin() {
        LoginPresenter lp = new LoginPresenter(view, model);
        doAnswer(invocation -> {
            view.sendUserToNextStudentActivity();
            return null;
        }).when(model).login("student@test.com", "123456");
        lp.validate("student@test.com", "123456");
        verify(view).sendUserToNextStudentActivity();
    }

    @Test
    public void testValidAdminLogin() {
        LoginPresenter lp = new LoginPresenter(view, model);
        doAnswer(invocation -> {
            view.sendUserToNextAdminActivity();
            return null;
        }).when(model).login("admin@test.com", "123456");
        lp.validate("admin@test.com", "123456");
        verify(view).sendUserToNextAdminActivity();
    }

    @Test
    public void testInvalidLogin() {
        LoginPresenter lp = new LoginPresenter(view, model);
        doAnswer(invocation -> {
            view.loginFailure("Unsuccessful Login");
            return null;
        }).when(model).login("ligma@test.com", "123456");
        lp.validate("ligma@test.com", "123456");
        verify(view).loginFailure("Unsuccessful Login");
    }

    @Test
    public void testInvalidStudentPasswordLength(){
        LoginPresenter lp = new LoginPresenter(view, model);
        lp.validate("student@test.com", "12345");
        verify(view).loginFailure("Login Unsuccessful");
    }

    @Test
    public void testInvalidEmail(){
        LoginPresenter lp = new LoginPresenter(view, model);
        lp.validate("ligma", "123456");
        verify(view).loginFailure("Login Unsuccessful");
    }

    @Test
    public void testSuccessfulLogin() {
        LoginPresenter lp = new LoginPresenter(view, model);
        lp.allowLogin("Login Successful", true);
        verify(view).loginSuccess("Login Successful", true);
    }

    @Test
    public void testFailedLogin() {
        LoginPresenter lp = new LoginPresenter(view, model);
        lp.rejectLogin("Login Unsuccessful");
        verify(view).loginFailure("Login Unsuccessful");
    }
}