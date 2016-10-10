package com.handy.portal;


import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;

import com.handy.portal.ui.activity.MainActivity;

import org.junit.Ignore;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.Espresso.pressBack;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withId;

@RunWith(AndroidJUnit4.class)
public class AccountSettingsFragmentTest
{
    @Rule
    public ActivityTestRule<MainActivity> mActivityRule = new ActivityTestRule<>(MainActivity.class);

    /*
    NOTE: for some reason AWS will run the test regardless of @Ignore
    if the prefix is "test"
    TODO: investigate
     */
    //TODO ignoring for now because we need to set up seed automation file for this to pass, but we need to set up tests to pass with AWS now
    @Ignore
    @Test
    public void accountSettingsIsWorkingTest() throws InterruptedException
    {
        // Go to dashboard.
//        onView(withId(R.id.button_more)).perform(click()); //TODO uncomment and fix reference
//        onView(withId(R.id.button_more)).perform(click()); // prevent flakiness
        onView(withId(R.id.nav_link_account_settings)).perform(click());

        // See if all elements are there
        onView(withId(R.id.account_settings_layout)).check(matches(isDisplayed()));
        onView(withId(R.id.contact_info_layout)).check(matches(isDisplayed()));
        onView(withId(R.id.edit_payment_option)).check(matches(isDisplayed()));
        onView(withId(R.id.order_resupply_layout)).check(matches(isDisplayed()));
        onView(withId(R.id.income_verification_layout)).check(matches(isDisplayed()));

        // Go to contact info
        onView(withId(R.id.contact_info_layout)).perform(click());
        onView(withId(R.id.provider_email_edit_text)).check(matches(isDisplayed()));
        pressBack();

        // Go to edit payment
        onView(withId(R.id.edit_payment_option)).perform(click());
        onView(withId(R.id.bank_account_option)).check(matches(isDisplayed()));
        pressBack();

        // Go to order resupply, this won't always be clickable
        onView(withId(R.id.order_resupply_layout)).check(matches(isDisplayed()));
        pressBack();
    }
}
