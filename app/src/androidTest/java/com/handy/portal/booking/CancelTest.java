package com.handy.portal.booking;

import android.content.Intent;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;

import com.handy.portal.R;
import com.handy.portal.bookings.model.Booking;
import com.handy.portal.core.constant.PrefsKey;
import com.handy.portal.core.ui.activity.SplashActivity;
import com.handy.portal.test.data.TestUsers;
import com.handy.portal.test.model.TestUser;
import com.handy.portal.test.util.AppInteractionUtil;
import com.handy.portal.test.util.ViewUtil;

import org.junit.After;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static android.support.test.espresso.Espresso.onData;
import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.action.ViewActions.scrollTo;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.Matchers.allOf;
import static org.hamcrest.Matchers.instanceOf;
import static org.hamcrest.Matchers.is;

@RunWith(AndroidJUnit4.class)
public class CancelTest
{
    private static final TestUser TEST_USER = TestUsers.CANCEL_BOOKING_TEST_PROVIDER;

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

    @After
    public void tearDown()
    {
        AppInteractionUtil.logOut();
    }

    @Test
    public void testBookingCancel()
    {
        ViewUtil.waitForViewVisible(R.id.main_container, ViewUtil.LONG_MAX_WAIT_TIME_MS);
        ViewUtil.waitForViewNotVisible(R.id.loading_overlay, ViewUtil.SHORT_MAX_WAIT_TIME_MS);

        // click the scheduled jobs tab
        onView(allOf(withId(R.id.tab_title), withText(R.string.tab_schedule))).perform(click());

        // click the first scheduled job
        ViewUtil.waitForViewVisible(R.id.scheduled_jobs_list_view, ViewUtil.LONG_MAX_WAIT_TIME_MS);
        onData(is(instanceOf(Booking.class)))
                .atPosition(0)
                .perform(click());

        // wait for booking action button to be visible and verify it says "on my way"
        ViewUtil.waitForViewVisibility(
                allOf(withId(R.id.booking_action_button), withText(R.string.on_my_way)),
                true,
                ViewUtil.LONG_MAX_WAIT_TIME_MS);

        // click job support
        onView(withId(R.id.booking_support_button)).perform(scrollTo(), click());

        // click Cancel Job
        onView(withText(R.string.cancel_job)).perform(click());

        // click Cancel Job on the confirmation dialog
        onView(withText(R.string.cancel_job)).perform(click());

        //wait for returning to scheduled booking list
        ViewUtil.waitForTextVisible(R.string.no_scheduled_jobs, ViewUtil.LONG_MAX_WAIT_TIME_MS);
    }
}
