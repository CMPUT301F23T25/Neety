package com.team25.neety;

import android.content.Intent;

import androidx.test.espresso.Espresso;
import androidx.test.espresso.action.ViewActions;
import androidx.test.espresso.matcher.RootMatchers;
import androidx.test.espresso.matcher.ViewMatchers;
import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

 import androidx.test.espresso.intent.Intents;
 import androidx.test.espresso.intent.matcher.IntentMatchers;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
 

@RunWith(AndroidJUnit4.class)
public class LoginActivityTest {

    @Rule
    public ActivityScenarioRule<LoginActivity> activityScenarioRule = new ActivityScenarioRule<>(LoginActivity.class);

    @Before
    public void setUp() {
        Intents.init();
        // Other setup if needed
    }

    @After
    public void tearDown() {
        Intents.release();
        // Other cleanup if needed
    }

    @Test
    public void testRegisterSuccess() throws InterruptedException {
        // Perform register with a valid username

        // ** we have to make sure the testUsername for this test to work
        String testUsername="User_from_test";
        Espresso.onView(ViewMatchers.withId(R.id.username_edittext)).perform(ViewActions.typeText(testUsername));
        Thread.sleep(500);
        Espresso.closeSoftKeyboard();
        Espresso.onView(ViewMatchers.withId(R.id.continue_button)).perform(ViewActions.click());
        Thread.sleep(500);

        Espresso.onView(ViewMatchers.withId(R.id.notification_text)).check(matches(withText("Your username hasn't been registered yet!")));
        Espresso.onView(ViewMatchers.withId(R.id.register_button)).perform(ViewActions.click());
        Intents.intended(IntentMatchers.hasComponent(MainActivity.class.getName()));
        
        // Succesful registration

    }


    @Test
    public void testLoginSuccess() throws InterruptedException {

        // testCaseUser
        String testCaseUser="testCaseUser";
        Espresso.onView(ViewMatchers.withId(R.id.username_edittext)).perform(ViewActions.typeText(testCaseUser));
        Thread.sleep(10000);
        Espresso.closeSoftKeyboard();
        Espresso.onView(ViewMatchers.withId(R.id.continue_button)).perform(ViewActions.click());
        Thread.sleep(500);
        Intents.intended(IntentMatchers.hasComponent(MainActivity.class.getName()));
        //succesfull login
    }
    

    @Test
    public void testLogout() throws InterruptedException {
        // Perform login with a valid username
        String testCaseUser="testCaseUser";
        Espresso.onView(ViewMatchers.withId(R.id.username_edittext)).perform(ViewActions.typeText(testCaseUser));
        Thread.sleep(2000);
        Espresso.closeSoftKeyboard();
        Espresso.onView(ViewMatchers.withId(R.id.continue_button)).perform(ViewActions.click());
        Thread.sleep(2000);
        Intents.intended(IntentMatchers.hasComponent(MainActivity.class.getName()));
        //succesfull login

        // Perform logout
        Espresso.onView(withId(R.id.action_button)).perform(ViewActions.click());
        Thread.sleep(2000);
        Espresso.onView(ViewMatchers.withText("Log Out"))
            .inRoot(RootMatchers.isDialog())
            .check(matches(isDisplayed()))
            .perform(ViewActions.click());
        Thread.sleep(500);
//        intened
//        Intents.intended(hasComponent(LoginActivity.class.getName()),Intents.times(2));
        //succesfull logout
    }
}

