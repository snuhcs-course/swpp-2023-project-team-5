package com.example.imPine;

import androidx.test.espresso.intent.rule.IntentsTestRule;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.filters.LargeTest;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.typeText;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static androidx.test.espresso.intent.Intents.intended;
import static androidx.test.espresso.intent.matcher.IntentMatchers.hasComponent;

@RunWith(AndroidJUnit4.class)
@LargeTest
public class AuthSignUpActivityTest {

    @Rule
    public IntentsTestRule<AuthSignUpActivity> intentsTestRule = new IntentsTestRule<>(AuthSignUpActivity.class);

    @Test
    public void testSignUpWithEmptyFields() {
        onView(withId(R.id.buttonSignUp)).perform(click());
    }

    @Test
    public void testSignUpWithMismatchedPasswords() {
        onView(withId(R.id.editTextEmail)).perform(typeText("test@example.com"));
        onView(withId(R.id.editTextPassword)).perform(typeText("password"));
        onView(withId(R.id.editTextPasswordConfirm)).perform(typeText("differentpassword"));
        onView(withId(R.id.editTextUsername)).perform(typeText("TestUser"));
        onView(withId(R.id.buttonSignUp)).perform(click());
        // Verify that the correct error is set on the password confirm field
    }
}
