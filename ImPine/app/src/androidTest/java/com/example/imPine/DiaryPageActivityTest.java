package com.example.imPine;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.intent.Intents.intended;
import static androidx.test.espresso.intent.matcher.IntentMatchers.hasComponent;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;

import androidx.test.espresso.intent.Intents;
import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(AndroidJUnit4.class)
public class DiaryPageActivityTest {

    @Rule
    public ActivityScenarioRule<DiaryPageActivity> activityRule = new ActivityScenarioRule<>(DiaryPageActivity.class);

    @Before
    public void setUp() {
        Intents.init();
    }

    @Test
    public void testRecyclerViewDisplayed() {
        onView(withId(R.id.recyclerView)).check(matches(isDisplayed()));
    }

    @Test
    public void testFabAddDiaryClick() {
        onView(withId(R.id.fab_add_item)).perform(click());
        intended(hasComponent(DiaryNewActivity.class.getName()));
    }

    @Test
    public void testHomeButtonClick() {
        onView(withId(R.id.home)).perform(click());
        intended(hasComponent(HomePageActivity.class.getName()));
    }

    @Test
    public void testPredictionButtonClick() {
        onView(withId(R.id.prediction)).perform(click());
        intended(hasComponent(PredictionPageActivity.class.getName()));
    }

    @Test
    public void testFriendButtonClick() {
        onView(withId(R.id.friend)).perform(click());
        intended(hasComponent(FriendsPageActivity.class.getName()));
    }

    @Test
    public void testSetButtonClick() {
        onView(withId(R.id.set)).perform(click());
        intended(hasComponent(SettingsPageActivity.class.getName()));
    }

    @Test
    public void testUserButtonClick() {
        onView(withId(R.id.user)).perform(click());
        intended(hasComponent(UsersPageActivity.class.getName()));
    }

    @Test
    public void testNoteButtonClick() {
        onView(withId(R.id.note)).perform(click());
        intended(hasComponent(NotificationsPageActivity.class.getName()));
    }

    // You can also add drag and drop tests for the RecyclerView items,
    // or any other specific behavior you want to verify.

    @After
    public void tearDown() {
        Intents.release();
    }
}
