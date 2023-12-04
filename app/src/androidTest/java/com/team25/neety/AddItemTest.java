package com.team25.neety;

import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;

import androidx.test.espresso.Espresso;
import androidx.test.espresso.action.ViewActions;
import androidx.test.espresso.assertion.ViewAssertions;
import androidx.test.espresso.intent.Intents;
import androidx.test.espresso.intent.matcher.IntentMatchers;
import androidx.test.espresso.matcher.RootMatchers;
import androidx.test.espresso.matcher.ViewMatchers;
import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.espresso.contrib.RecyclerViewActions;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

public class AddItemTest {
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
    @Before
    public void login() throws InterruptedException {

        // testCaseUser
        String testCaseUser="testCaseUser";
        Espresso.onView(ViewMatchers.withId(R.id.username_edittext)).perform(ViewActions.typeText(testCaseUser));
        Thread.sleep(500);
        Espresso.closeSoftKeyboard();
        Espresso.onView(ViewMatchers.withId(R.id.continue_button)).perform(ViewActions.click());
        Thread.sleep(500);
        Intents.intended(IntentMatchers.hasComponent(MainActivity.class.getName()));
        //succesfull login
    }
    @Test
    public void testAddItemSuccess() throws InterruptedException {
        // Perform register with a valid username
        Espresso.onView(ViewMatchers.withId(R.id.button_additem)).perform(ViewActions.click());


        Espresso.onView(ViewMatchers.withId(R.id.model_edittext)).perform(ViewActions.typeText("testCaseModel"));

        Espresso.closeSoftKeyboard();
        Espresso.onView(ViewMatchers.withId(R.id.make_edittext)).perform(ViewActions.typeText("testCaseMake"));
        Espresso.closeSoftKeyboard();
        Espresso.onView(ViewMatchers.withId(R.id.estimated_value_edittext)).perform(ViewActions.typeText("999.99"));
        Espresso.closeSoftKeyboard();
        Espresso.onView(ViewMatchers.withId(R.id.description_edittext)).perform(ViewActions.typeText("testCaseDescription"));
        Espresso.closeSoftKeyboard();
        Espresso.onView(ViewMatchers.withId(R.id.purchase_date_edittext)).perform(ViewActions.typeText("2022-01-01"));
        Espresso.closeSoftKeyboard();
        Espresso.onView(ViewMatchers.withId(R.id.serial_number_edittext)).perform(ViewActions.typeText("testCaseSerialNumber"));
        Thread.sleep(5000);
        Espresso.closeSoftKeyboard();
        Espresso.onView(ViewMatchers.withId(R.id.comments_edittext)).perform(ViewActions.typeText("testCaseComments"));
        Espresso.closeSoftKeyboard();
        Thread.sleep(1000);
        Espresso.onView(ViewMatchers.withText("OK"))
                .inRoot(RootMatchers.isDialog())
                .check(matches(isDisplayed()))
                .perform(ViewActions.click());
        Thread.sleep(1000);

    }
    @Test
    public void deleteItemTest() throws InterruptedException {




        
        // Perform register with a valid username
        Espresso.onView(ViewMatchers.withId(R.id.button_deleteitem)).perform(ViewActions.click());
        Thread.sleep(3000);
        // Select the item to delete
        Espresso.onView(ViewMatchers.withId(R.id.items_list_view)).perform(RecyclerViewActions.actionOnItemAtPosition(0, ViewActions.click()));
        Thread.sleep(3000);

        
        Espresso.onView(ViewMatchers.withId(R.id.button_deleteitem)).perform(ViewActions.click());
        Thread.sleep(3000);
    }
}