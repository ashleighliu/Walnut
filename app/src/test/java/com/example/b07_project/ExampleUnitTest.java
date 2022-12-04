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
        verify(view).loginSuccess("Login Successful", false);
    }

    @Test
    public void testInvalidPasswordLength(){
        LoginPresenter lp = new LoginPresenter(view, model);
        doAnswer(invocation -> {
            view.loginFailure("Password must be at least 6 characters.");
            return null;
        }).when(model).login("student@test.com", "12345");
        lp.validate("student@test.com", "12345");
        verify(view).loginFailure("Password must be at least 6 characters.");
    }

    @Test
    public void testValidAdminLogin() {
        LoginPresenter lp = new LoginPresenter(view, model);
        doAnswer(invocation -> {
            view.sendUserToNextAdminActivity();
            return null;
        }).when(model).login("admin@test.com", "123456");
        lp.validate("admin@test.com", "123456");
        verify(view).loginSuccess("Login Successful", true);

    }

}
//
//    when(view.getUsername()).thenReturn("abc");
//        //username is found
//        when(model.isFound("abc")).thenReturn(true);
//        LoginPresenter presenter = new LoginPresenter(model, view);
//        presenter.checkUsername();
//        verify(view).displayMessage(anyString());
//
//
//        ArgumentCaptor<String> captor = ArgumentCaptor.forClass(String.class);
//        verify(view).displayMessage(captor.capture());
//        assertEquals(captor.getValue(), "user found");
//
//        InOrder order = inOrder(model, view);
//        order.verify(model).isFound("abc");
//        order.verify(view).displayMessage("user found");