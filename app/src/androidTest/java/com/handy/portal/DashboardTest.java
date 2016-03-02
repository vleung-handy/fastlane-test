package com.handy.portal;

import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;
import android.test.suitebuilder.annotation.LargeTest;

import com.handy.portal.ui.activity.MainActivity;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.Espresso.pressBack;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.action.ViewActions.scrollTo;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withId;

@RunWith(AndroidJUnit4.class)
@LargeTest
public class DashboardTest
{
    @Rule
    public ActivityTestRule<MainActivity> mActivityRule = new ActivityTestRule<>(MainActivity.class);

    @Test
    public void testDashboardIsWorking() throws InterruptedException
    {
        // Go to dashboard.
        onView(withId(R.id.button_more)).perform(click());
        onView(withId(R.id.button_more)).perform(click()); // prevent flakiness
        onView(withId(R.id.nav_link_ratings_and_feedback)).perform(click());

        // See if all elements are there
        onView(withId(R.id.dashboard_welcome_view)).check(matches(isDisplayed()));
        onView(withId(R.id.dashboard_ratings_view_pager)).check(matches(isDisplayed()));
        onView(withId(R.id.dashboard_options_view)).perform(scrollTo());
        onView(withId(R.id.dashboard_options_view)).check(matches(isDisplayed()));

        // Go to to tier page
        onView(withId(R.id.tier_option)).perform(click());
        onView(withId(R.id.tier_label)).check(matches(isDisplayed()));
        pressBack();

        // Go to to feedback page
        onView(withId(R.id.feedback_option)).perform(click());
        onView(withId(R.id.layout_dashboard_feedback)).check(matches(isDisplayed()));
        pressBack();

        // Go to to review page
        onView(withId(R.id.reviews_option)).perform(click());
        onView(withId(R.id.reviews_list)).check(matches(isDisplayed()));
        pressBack();
    }
}
