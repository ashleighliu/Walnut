package com.example.b07_project;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import android.content.Context;
import com.example.b07_project.Presenter.LoginInteractor;
import junit.framework.TestCase;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;


@RunWith(MockitoJUnitRunner.class)
public class LoginInteractorTest {

    private static final String FAKE_STRING = "Login was successful";

    @Mock
    MainActivity view;

    @Mock
    User model;

    @Test
    public void readStringFromContext_LocalizedString() {

        LoginInteractor myObjectUnderTest = new LoginInteractor(mMockContext);

        // ...when the string is returned from the object under test...
        String result = myObjectUnderTest.validate("user","user");

        // ...then the result should be the expected one.
        assertThat(result, is(FAKE_STRING));
    }

}