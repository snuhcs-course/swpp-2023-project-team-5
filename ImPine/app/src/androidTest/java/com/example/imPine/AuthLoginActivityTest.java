package com.example.imPine;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.hasErrorText;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.typeText;
import static androidx.test.espresso.matcher.ViewMatchers.hasErrorText;
import static androidx.test.espresso.intent.Intents.intended;
import static androidx.test.espresso.intent.matcher.IntentMatchers.hasComponent;


import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import androidx.test.core.app.ActivityScenario;
import androidx.test.espresso.intent.Intents;
import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.ext.junit.runners.AndroidJUnit4;

@RunWith(AndroidJUnit4.class)
public class LoginActivityTest {

    @Rule
    public ActivityScenarioRule<LoginActivity> activityRule = new ActivityScenarioRule<LoginActivity>(LoginActivity.class);

    @Test
    public void emailFieldIsDisplayed() {
        // Using Espresso to check if the email EditText is displayed
        onView(withId(R.id.editTextEmail)).check(matches(isDisplayed()));
    }

    @Test
    public void passwordFieldIsDisplayed() {
        onView(withId(R.id.editTextPassword)).check(matches(isDisplayed()));
    }

    @Test
    public void loginButtonIsDisplayed() {
        onView(withId(R.id.buttonLogin)).check(matches(isDisplayed()));
    }

    @Test
    public void signUpButtonIsDisplayed() {
        onView(withId(R.id.buttonSignUp)).check(matches(isDisplayed()));
    }

    @Test
    public void clickLoginWithEmptyFields() {
        // Click on the login button
        onView(withId(R.id.buttonLogin)).perform(click());

        // Check if the error message is displayed for the email field
        onView(withId(R.id.editTextEmail)).check(matches(hasErrorText("Email is required")));

        // TODO: Add more checks if necessary...
    }


    @Before
    public void setUp() {
        Intents.init();
    }
    @Test
    public void navigateToSignUpActivity() {
        onView(withId(R.id.buttonSignUp)).perform(click());
        intended(hasComponent(SignUpActivity.class.getName()));
    }

    @After
    public void tearDown() {
        Intents.release();
    }

}
