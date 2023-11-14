package com.example.imPine;

import androidx.test.espresso.intent.rule.IntentsTestRule;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.filters.LargeTest;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.intent.Intents.intended;
import static androidx.test.espresso.intent.matcher.IntentMatchers.hasComponent;
import static androidx.test.espresso.matcher.ViewMatchers.withId;

@RunWith(AndroidJUnit4.class)
@LargeTest
public class TutorialActivityTest {

    @Rule
    public IntentsTestRule<TutorialActivity> intentsTestRule = new IntentsTestRule<>(TutorialActivity.class);

    @Test
    public void clickGetStartedButton_opensMakePlantActivity() {
        // Perform a click on the get started button
        onView(withId(R.id.btn_get_started)).perform(click());

        // Check if an Intent to MakePlantActivity was sent
        intended(hasComponent(MakePlantActivity.class.getName()));
    }
}
