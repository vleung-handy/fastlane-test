package com.handy.portal;

import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;
import android.test.suitebuilder.annotation.LargeTest;

import com.handy.portal.ui.activity.SplashActivity;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withId;

//note that animations should be disabled on the device running these tests
@RunWith(AndroidJUnit4.class)
@LargeTest
public class SampleTest
{
    @Rule
    public ActivityTestRule<SplashActivity> mActivityRule = new ActivityTestRule<>(
            SplashActivity.class);

    /**
     * TODO: super simple test for making sure Espresso works with AWS only
     *
     * assumes user is logged out
     */
    @Test
    public void testSample()
    {
        onView(withId(R.id.logo)).check(matches(isDisplayed()));
    }
}
