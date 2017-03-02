package com.handy.portal.test;

import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;

import com.handy.portal.R;
import com.handy.portal.core.ui.activity.MainActivity;

import org.junit.Ignore;
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
public class DashboardTest {
    @Rule
    public ActivityTestRule<MainActivity> mActivityRule = new ActivityTestRule<>(MainActivity.class);

    /*
    NOTE: for some reason AWS will run the test regardless of @Ignore
    if the prefix is "test"
    TODO: investigate
     */
    //TODO ignoring for now because we need to set up seed automation file for this to pass, but we need to set up tests to pass with AWS now
    @Test
    @Ignore
    public void dashboardIsWorkingTest() throws InterruptedException {
        // Go to dashboard.
//        onView(withId(R.id.button_more)).perform(click()); //TODO uncomment and fix reference
//        onView(withId(R.id.button_more)).perform(click()); // prevent flakiness
        onView(withId(R.id.nav_link_ratings_and_feedback)).perform(click());

        // See if all elements are there
        onView(withId(R.id.dashboard_welcome_view)).check(matches(isDisplayed()));
        onView(withId(R.id.dashboard_ratings_view_pager)).check(matches(isDisplayed()));
        onView(withId(R.id.dashboard_options_view)).perform(scrollTo());
        onView(withId(R.id.dashboard_options_view)).check(matches(isDisplayed()));

        // Go to to tier page
        onView(withId(R.id.tier_option)).perform(click());
        onView(withId(R.id.current_week_completed_jobs_text)).check(matches(isDisplayed()));
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
