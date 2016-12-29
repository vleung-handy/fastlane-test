package com.handy.portal.ui.fragment;

import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;

import com.handy.portal.R;
import com.handy.portal.RobolectricGradleTestWrapper;
import com.handy.portal.TestUtils;
import com.handy.portal.bookings.ui.fragment.AvailableBookingsFragment;
import com.handy.portal.bookings.ui.fragment.ScheduledBookingsFragment;
import com.handy.portal.dashboard.fragment.DashboardVideoLibraryFragment;
import com.handy.portal.dashboard.fragment.RatingsAndFeedbackFragment;
import com.handy.portal.helpcenter.ui.fragment.HelpWebViewFragment;
import com.handy.portal.notification.ui.fragment.NotificationsFragment;
import com.handy.portal.payments.ui.fragment.PaymentsFragment;
import com.handy.portal.ui.activity.MainActivity;

import org.junit.Before;
import org.junit.Test;
import org.robolectric.Robolectric;
import org.robolectric.util.ActivityController;

import static org.hamcrest.CoreMatchers.instanceOf;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;

public class MainActivityFragmentTest extends RobolectricGradleTestWrapper
{
    private MainActivityFragment mFragment;

    @Before
    public void setUp() throws Exception
    {
        ActivityController<MainActivity> activityController = Robolectric.buildActivity(MainActivity.class).create();
        mFragment = (MainActivityFragment) activityController.get().getSupportFragmentManager().getFragments().get(0);
        activityController.start().resume().visible();
    }

    @Test
    public void shouldBeginWithAvailableTab() throws Exception
    {
        Fragment currentFragment = TestUtils.getScreenFragment(mFragment.getFragmentManager());
        assertThat(currentFragment, instanceOf(AvailableBookingsFragment.class));
        assertEquals(mFragment.getString(R.string.available_jobs),
                ((AppCompatActivity) mFragment.getActivity()).getSupportActionBar().getTitle());
    }

    @Test
    public void testScheduleTab() throws Exception
    {
        TestUtils.testFragmentNavigation(mFragment, R.id.tab_nav_schedule, ScheduledBookingsFragment.class, R.string.scheduled_jobs);
    }

    @Test
    public void testAlertTab() throws Exception
    {
        try
        {
            mFragment.getView().findViewById(R.id.tab_nav_alert).performClick();
        }
        catch (Exception e)
        {
            // Robolectric has trouble rendering NotificationsListView
        }
        Fragment currentFragment = TestUtils.getScreenFragment(mFragment.getFragmentManager());
        assertThat(currentFragment, instanceOf(NotificationsFragment.class));
    }

    @Test
    public void testRatingFeedbackNav() throws Exception
    {
        TestUtils.testFragmentNavigation(mFragment, R.id.nav_link_ratings_and_feedback, RatingsAndFeedbackFragment.class, R.string.ratings_and_feedback);
    }

    @Test
    public void testPaymentsNav() throws Exception
    {
        TestUtils.testFragmentNavigation(mFragment, R.id.nav_link_payments, PaymentsFragment.class, R.string.payments);
    }

    @Test
    public void testReferAFriendNav() throws Exception
    {
        TestUtils.testFragmentNavigation(mFragment, R.id.nav_link_refer_a_friend, ReferAFriendFragment.class, R.string.refer_a_friend);
    }

    @Test
    public void testAccountSettingsNav() throws Exception
    {
        TestUtils.testFragmentNavigation(mFragment, R.id.nav_link_account_settings, AccountSettingsFragment.class, R.string.account_settings);
    }

    @Test
    public void testVideoLibraryNav() throws Exception
    {
        TestUtils.testFragmentNavigation(mFragment, R.id.nav_link_video_library, DashboardVideoLibraryFragment.class, R.string.video_library);
    }

    @Test
    public void testHelpNav() throws Exception
    {
        TestUtils.testFragmentNavigation(mFragment, R.id.nav_link_help, HelpWebViewFragment.class, R.string.help);
    }

}
