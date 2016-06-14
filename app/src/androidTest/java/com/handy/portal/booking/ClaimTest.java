package com.handy.portal.booking;

import android.content.Intent;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;
import android.test.suitebuilder.annotation.LargeTest;
import android.view.View;

import com.handy.portal.R;
import com.handy.portal.bookings.model.Booking;
import com.handy.portal.constant.PrefsKey;
import com.handy.portal.test.ViewMatchers;
import com.handy.portal.test.data.TestUsers;
import com.handy.portal.test.model.TestUser;
import com.handy.portal.test.util.ViewUtil;
import com.handy.portal.ui.activity.SplashActivity;

import org.hamcrest.Matcher;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static android.support.test.espresso.Espresso.onData;
import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.Matchers.allOf;
import static org.hamcrest.Matchers.instanceOf;
import static org.hamcrest.Matchers.is;

//note that animations should be disabled on the device running these tests
@RunWith(AndroidJUnit4.class)
@LargeTest
public class ClaimTest
{
    private static final TestUser TEST_USER = TestUsers.BOOKINGS_NY_PROVIDER;

    @Rule
    public ActivityTestRule<SplashActivity> mActivityRule = new ActivityTestRule<SplashActivity>(
            SplashActivity.class)
    {
        @Override
        protected Intent getActivityIntent()
        {
            Intent intent = super.getActivityIntent();
            intent.putExtra(PrefsKey.AUTH_TOKEN, TEST_USER.getPersistenceToken());
            return intent;
        }
    };

    /**
     * assumptions: this user hasn't claimed any bookings yet for 4 days out
     */
    @Test
    public void testBookingClaim()
    {
        ViewUtil.waitForViewVisible(R.id.main_container, ViewUtil.LONG_MAX_WAIT_TIME_MS);

        int claimableJobDateButtonIndex = 4;
        //click the date button element at index 4
        onView(ViewMatchers.childAtIndex(withId(R.id.available_bookings_dates_scroll_view_layout), claimableJobDateButtonIndex))
                .perform(click());

        //click the first available job
        ViewUtil.waitForViewVisible(R.id.available_jobs_list_view, ViewUtil.LONG_MAX_WAIT_TIME_MS);
        onData(is(instanceOf(Booking.class)))
            .atPosition(0)
            .perform(click());

        //click claim
        Matcher<View> bookingActionButtonMatcher
                = allOf(withId(R.id.booking_action_button), withText(R.string.claim_job));
        ViewUtil.waitForViewVisibility(bookingActionButtonMatcher, true, ViewUtil.LONG_MAX_WAIT_TIME_MS);
        onView(bookingActionButtonMatcher).perform(click());

        //wait for loading screen to disappear and bookings to reload
        ViewUtil.waitForViewVisible(R.id.scheduled_bookings_dates_scroll_view, ViewUtil.LONG_MAX_WAIT_TIME_MS);

        //click the date button at index 4
        onView(ViewMatchers.childAtIndex(
                withId(R.id.scheduled_bookings_dates_scroll_view_layout),
                claimableJobDateButtonIndex))
                .perform(click());

        //verify that the booking is claimed
        onData(is(instanceOf(Booking.class)))
                .atPosition(0)
                .check(matches(isDisplayed()));
        //FIXME: ideally also check for the same booking as claimed
        // currently don't know how to extract text values from the view

    }


}
