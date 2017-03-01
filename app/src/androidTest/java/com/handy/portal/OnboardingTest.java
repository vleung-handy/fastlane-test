package com.handy.portal;

import android.content.Intent;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;
import android.view.View;
import android.widget.FrameLayout;

import com.handy.portal.core.constant.PrefsKey;
import com.handy.portal.core.ui.activity.SplashActivity;
import com.handy.portal.test.ViewMatchers;
import com.handy.portal.test.data.TestUsers;
import com.handy.portal.test.model.TestUser;
import com.handy.portal.test.util.AppInteractionUtil;
import com.handy.portal.test.util.TermsPageUtil;
import com.handy.portal.test.util.ViewUtil;

import org.hamcrest.Matcher;
import org.junit.After;
import org.junit.Ignore;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.isAssignableFrom;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withParent;
import static android.support.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.Matchers.allOf;

@RunWith(AndroidJUnit4.class)
public class OnboardingTest {
    private static final TestUser TEST_USER = TestUsers.ONBOARDING_TEST_PROVIDER;

    @Rule
    public ActivityTestRule<SplashActivity> mActivityRule = new ActivityTestRule<SplashActivity>(
            SplashActivity.class) {
        @Override
        protected Intent getActivityIntent() {
            Intent intent = super.getActivityIntent();
            intent.putExtra(PrefsKey.AUTH_TOKEN, TEST_USER.getPersistenceToken());
            return intent;
        }
    };

    @After
    public void tearDown() {
        try {
            AppInteractionUtil.logOut();
        }
        catch (Exception e) {}
    }

    /**
     * Tests the onboarding preactivation claim flow
     * - accepts all terms
     * - claims a job
     * - verifies job was claimed
     */
    // Seed data for onboarding is broken. We'll need to fix that first
    @Test
    @Ignore
    public void onboardingClaimTest() {
        //wait for the main container
        ViewUtil.waitForViewVisible(R.id.main_content, ViewUtil.LONG_MAX_WAIT_TIME_MS);

        //accept all terms for this user
        TermsPageUtil.acceptAllTermsIfPresent();

        //wait for the help links to show up
        ViewUtil.waitForViewVisible(R.id.links_container, ViewUtil.SHORT_MAX_WAIT_TIME_MS);

        //click the next button
        onView(withId(R.id.single_action_button)).perform(click());

        //fill out date field
        onView(withId(R.id.date_field)).perform(click());
        onView(withId(android.R.id.button1)).perform(click());

        //fill out location field
        onView(withId(R.id.location_field)).perform(click());
        onView(withText("Manhattan")).perform(click());
        onView(withId(android.R.id.button1)).perform(click());

        //click the next button
        onView(withId(R.id.single_action_button)).perform(click());
        ViewUtil.waitForViewNotVisible(R.id.loading_overlay, ViewUtil.SHORT_MAX_WAIT_TIME_MS);

        // If no jobs match the preferences, don't proceed further
        try {
            ViewUtil.waitForViewVisible(R.string.no_jobs_matching_preferences,
                    ViewUtil.SHORT_MAX_WAIT_TIME_MS);
        }
        catch (Exception e) {
            //wait for the jobs container
            ViewUtil.waitForViewVisible(R.id.jobs_container, ViewUtil.LONG_MAX_WAIT_TIME_MS);

            //click the first job
            Matcher<View> firstJobContainerMatcher = ViewMatchers.childAtIndex(withId(R.id.jobs_container), 0);
            //need to wait for the animation to finish (disabling animations system-wide doesn't disable this one)
            ViewUtil.waitForViewVisibility(firstJobContainerMatcher, true, ViewUtil.SHORT_MAX_WAIT_TIME_MS);
            onView(allOf(withParent(firstJobContainerMatcher),
                    isAssignableFrom(FrameLayout.class))).perform(click());

            //click the next button
            onView(withId(R.id.single_action_button)).perform(click());

            //click the finish button
            onView(withId(R.id.single_action_button)).perform(click());

            //wait for the collapsible jobs button to show up and click it
            ViewUtil.waitForViewVisible(R.id.jobs_collapsible, ViewUtil.LONG_MAX_WAIT_TIME_MS);
            onView(withId(R.id.jobs_collapsible)).perform(click());

            //confirm that this user has a claimed booking
            onView(ViewMatchers.childAtIndex(withId(R.id.jobs_container), 0))
                    .check(matches(isDisplayed()));
        }
    }
}
