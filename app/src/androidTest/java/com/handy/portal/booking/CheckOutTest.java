package com.handy.portal.booking;

import android.content.Context;
import android.content.Intent;
import android.support.test.espresso.PerformException;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;
import android.view.View;

import com.handy.portal.R;
import com.handy.portal.core.constant.PrefsKey;
import com.handy.portal.core.manager.PrefsManager;
import com.handy.portal.core.ui.activity.SplashActivity;
import com.handy.portal.test.ViewMatchers;
import com.handy.portal.test.data.TestUsers;
import com.handy.portal.test.model.TestUser;
import com.handy.portal.test.util.AppInteractionUtil;
import com.handy.portal.test.util.ViewUtil;

import org.hamcrest.Matcher;
import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.action.ViewActions.scrollTo;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.Matchers.allOf;

//note that animations should be disabled on the device running these tests
@RunWith(AndroidJUnit4.class)
public class CheckOutTest {
    private static final TestUser TEST_USER = TestUsers.CHECK_OUT_TEST_PROVIDER;

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

    @Before
    public void setUp() {
        mActivityRule.getActivity().getApplicationContext()
                .getSharedPreferences(PrefsManager.BOOKING_INSTRUCTIONS_PREFS, Context.MODE_PRIVATE)
                .edit().clear().commit();
    }

    @After
    public void tearDown() {
        AppInteractionUtil.logOut();
    }

    @Test
    public void testBookingCheckOut() {
        ViewUtil.waitForViewVisible(R.id.main_container, ViewUtil.LONG_MAX_WAIT_TIME_MS);
        ViewUtil.waitForViewNotVisible(R.id.loading_overlay, ViewUtil.SHORT_MAX_WAIT_TIME_MS);

        //click the scheduled jobs tab
        onView(allOf(withId(R.id.tab_title), withText(R.string.tab_schedule))).perform(click());

        // This dialog only shows up sometimes, don't fail if it does not show
        try {
            ViewUtil.waitForViewVisible(R.id.confirm_action_button, ViewUtil.SHORT_MAX_WAIT_TIME_MS);
            onView(withId(R.id.confirm_action_button)).perform(click());
        }
        catch (PerformException e) { }

        //click the first scheduled job
        ViewUtil.waitForViewVisible(R.id.booking_entry_details_layout, ViewUtil.LONG_MAX_WAIT_TIME_MS);
        onView(withId(R.id.booking_entry_details_layout)).perform(click());

        //wait for booking action button to be visible and verify it says "Continue to check out"
        ViewUtil.waitForTextVisible(R.string.continue_to_check_out, ViewUtil.LONG_MAX_WAIT_TIME_MS);
        ViewUtil.waitForViewVisible(R.id.in_progress_booking_checklist, ViewUtil.LONG_MAX_WAIT_TIME_MS);

        // click a check list item
        Matcher<View> checkListMatcher = withId(R.id.in_progress_booking_checklist);
        Matcher<View> firstChild = ViewMatchers.childAtIndex(checkListMatcher, 0);
        onView(firstChild).perform(click());

        //check out
        onView(withText(R.string.continue_to_check_out)).perform(click());

        //wait for the "Send Your Receipt" page
        ViewUtil.waitForViewVisible(R.id.signature_pad, ViewUtil.LONG_MAX_WAIT_TIME_MS);

        //sign
        onView(withId(R.id.signature_pad)).perform(scrollTo(), click());

        //complete checkout
        onView(withId(R.id.complete_checkout_button)).perform(click());

        //rate the booking and submit it
        ViewUtil.waitForViewVisible(R.id.rating_button_5, ViewUtil.LONG_MAX_WAIT_TIME_MS);
        onView(withId(R.id.rating_button_5)).perform(click());
        onView(withId(R.id.rate_booking_submit_button)).perform(click());

        // If nearby jobs shows up, skip it
        try {
            ViewUtil.waitForViewVisible(R.id.nearby_bookings_description, ViewUtil.LONG_MAX_WAIT_TIME_MS);
            onView(withId(R.id.action_exit)).perform(click());
        }
        catch (PerformException e) { }

        //wait for returning to scheduled booking list
        ViewUtil.waitForViewVisible(R.id.booking_entry_details_layout, ViewUtil.LONG_MAX_WAIT_TIME_MS);
    }
}
