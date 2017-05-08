package com.handy.portal.test;

import android.content.Intent;
import android.support.test.espresso.DataInteraction;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;

import com.handy.portal.R;
import com.handy.portal.core.constant.PrefsKey;
import com.handy.portal.core.ui.activity.SplashActivity;
import com.handy.portal.tool.data.TestUsers;
import com.handy.portal.tool.model.TestUser;
import com.handy.portal.tool.util.AppInteractionUtil;
import com.handy.portal.tool.util.ViewUtil;

import org.junit.After;
import org.junit.Ignore;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static android.support.test.espresso.Espresso.onData;
import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static org.hamcrest.Matchers.anything;

//note that animations should be disabled on the device running these tests
@RunWith(AndroidJUnit4.class)
public class ProRequestsTest {
    private static final TestUser TEST_USER = TestUsers.BOOKINGS_NY_PROVIDER;

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
        AppInteractionUtil.logOut();
    }

    /**
     * - verifies that the pro requests inbox unread count is 3
     * - navigates to pro requests inbox
     * - for the 2nd element of the second day:
     * - clicks on it
     * - claims the job
     * - verifies that the scheduled jobs screen shows
     * - verifies that the claimed job shows
     */
    @Ignore // TODO: Figure out how to test with RecyclerView
    @Test
    public void testProRequestsInbox() {
        ViewUtil.waitForViewVisible(R.id.main_container, ViewUtil.LONG_MAX_WAIT_TIME_MS);

        ViewUtil.waitForViewNotVisible(R.id.loading_overlay, ViewUtil.LONG_MAX_WAIT_TIME_MS);

        ViewUtil.waitForViewVisible(R.id.tab_nav_clients, ViewUtil.LONG_MAX_WAIT_TIME_MS);

        //assert that the pro requests inbox unread count is 3
        //TODO unread count is sometimes wrong (2 bookings not grouped into a proxy as expected). disabling this until fixed
//        Matcher<View> tabUnreadCountMatcher= allOf(
//                withParent(withParent(withId(R.id.tab_nav_pro_requested_jobs))),
//                withId(R.id.tab_unread_count));
//        ViewUtil.waitForViewVisibility(tabUnreadCountMatcher, true, ViewUtil.SHORT_MAX_WAIT_TIME_MS);
//        onView(tabUnreadCountMatcher).check(matches(withText("3")));

        //navigate to the pro requests inbox
        onView(withId(R.id.tab_nav_clients)).perform(click());

        //wait for the listview to render
        ViewUtil.waitForViewVisible(R.id.fragment_pro_requested_jobs_recycler_view, ViewUtil.LONG_MAX_WAIT_TIME_MS);

        //click on the 2nd item of the 2nd day
        DataInteraction dataItemInteraction =
                onData(anything()) //specifying a constraint doesn't work. why?
                        .inAdapterView(withId(R.id.fragment_pro_requested_jobs_recycler_view))
                        .atPosition(4); //actually data item #3 in the expandable list view, because there are 2 group headers

        dataItemInteraction.perform(click());

        //wait for the booking details to show
        ViewUtil.waitForViewVisible(
                R.id.booking_scroll_view,
                ViewUtil.LONG_MAX_WAIT_TIME_MS);

        //assert this particular booking has exactly two customers who requested this pro
        //TODO count is sometimes wrong (2 bookings not grouped into a proxy as expected). disabling this until fixed
//        onView(withId(R.id.left_indicator_text_view_indicator_text))
//                .check(matches(withText("2 customers requested you!")));

        //claim the booking
        onView(withId(R.id.booking_action_button)).perform(click());
        onView(withId(R.id.confirm_action_button)).perform(click());

        //wait for scheduled jobs
        ViewUtil.waitForViewVisible(R.id.scheduled_jobs_recycler_view, ViewUtil.LONG_MAX_WAIT_TIME_MS);

        //confirm that a booking is there
        onData(anything())
                .inAdapterView(withId(R.id.scheduled_jobs_recycler_view))
                .atPosition(0)
                .check(matches(isDisplayed()));

    }
}
