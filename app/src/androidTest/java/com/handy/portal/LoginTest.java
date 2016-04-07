package com.handy.portal;

import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;
import android.test.suitebuilder.annotation.LargeTest;

import com.handy.portal.test.TestUser;
import com.handy.portal.ui.activity.MainActivity;

import org.junit.Before;
import org.junit.Ignore;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.action.ViewActions.closeSoftKeyboard;
import static android.support.test.espresso.action.ViewActions.typeText;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withId;

//note that animations should be disabled on the device running these tests
@Ignore //TODO ignoring for now because we need to set up seed automation file for this to pass, but we need to set up tests to pass with AWS now
@RunWith(AndroidJUnit4.class)
@LargeTest
public class LoginTest
{
    private final TestUser mTestUser = TestUser.TEST_USER_NY;

    @Rule
    public ActivityTestRule<MainActivity> mActivityRule = new ActivityTestRule<>(
            MainActivity.class);

    @Before
    public void init() {
    }

    /**
     * Logs in as the test user
     *
     * Assumptions:
     * - no one is logged into the app
     * - there are no popup modals (for example, promos)
     */
    @Test
    public void loginTest()
    {
        //TODO: for proof of concept. we should make this more readable/reusable
        onView(withId(R.id.phone_number_edit_text)).perform(click(), typeText(mTestUser.getPhoneNumber()), closeSoftKeyboard());
        onView(withId(R.id.login_button)).perform(click());
        onView(withId(R.id.pin_code_edit_text)).perform(click(), typeText(mTestUser.getPinCode()), closeSoftKeyboard());
        onView(withId(R.id.login_button)).perform(click());
        onView(withId(R.id.bookings_content)).check(matches(isDisplayed()));
    }
}
