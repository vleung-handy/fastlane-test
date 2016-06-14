package com.handy.portal.booking;

import android.content.Intent;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;
import android.test.suitebuilder.annotation.LargeTest;

import com.handy.portal.R;
import com.handy.portal.bookings.model.Booking;
import com.handy.portal.constant.PrefsKey;
import com.handy.portal.test.data.TestUsers;
import com.handy.portal.test.model.TestUser;
import com.handy.portal.test.util.ViewUtil;
import com.handy.portal.ui.activity.SplashActivity;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static android.support.test.espresso.Espresso.onData;
import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.Matchers.allOf;
import static org.hamcrest.Matchers.instanceOf;
import static org.hamcrest.Matchers.is;

//note that animations should be disabled on the device running these tests
@RunWith(AndroidJUnit4.class)
@LargeTest
public class CheckInTest
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

    @Test
    public void testBookingCheckIn()
    {
        ViewUtil.waitForViewVisible(R.id.main_container, ViewUtil.LONG_MAX_WAIT_TIME_MS);

        //click the scheduled jobs tab
        onView(allOf(withId(R.id.tab_title), withText(R.string.tab_schedule))).perform(click());

        //click the first scheduled job
        ViewUtil.waitForViewVisible(R.id.scheduled_jobs_list_view, ViewUtil.LONG_MAX_WAIT_TIME_MS);
        onData(is(instanceOf(Booking.class)))
                .atPosition(0)
                .perform(click());

        //wait for booking action button to be visible and verify it says "on my way"
        ViewUtil.waitForViewVisibility(
                allOf(withId(R.id.booking_action_button), withText(R.string.on_my_way)),
                true,
                ViewUtil.LONG_MAX_WAIT_TIME_MS);
        //on my way
        onView(withId(R.id.booking_action_button)).perform(click());

        //wait for booking action button to be visible and verify it says "check in"
        ViewUtil.waitForViewVisibility(
                allOf(withId(R.id.booking_action_button), withText(R.string.check_in)),
                true,
                ViewUtil.LONG_MAX_WAIT_TIME_MS);
        //check in
        onView(withId(R.id.booking_action_button)).perform(click());

        //wait for booking action button to be visible and verify it says "continue to check out"
        ViewUtil.waitForViewVisibility(
                allOf(withId(R.id.in_progress_booking_action_button), withText(R.string.continue_to_check_out)),
                true,
                ViewUtil.LONG_MAX_WAIT_TIME_MS);
    }


}
