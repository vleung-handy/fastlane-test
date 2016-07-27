package com.handy.portal.ui.fragment;

import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;

import com.handy.portal.R;
import com.handy.portal.RobolectricGradleTestWrapper;
import com.handy.portal.TestUtils;
import com.handy.portal.bookings.ui.fragment.AvailableBookingsFragment;
import com.handy.portal.bookings.ui.fragment.ProRequestedJobsFragment;
import com.handy.portal.bookings.ui.fragment.ScheduledBookingsFragment;
import com.handy.portal.dashboard.fragment.DashboardVideoLibraryFragment;
import com.handy.portal.dashboard.fragment.RatingsAndFeedbackFragment;
import com.handy.portal.helpcenter.ui.fragment.HelpWebViewFragment;
import com.handy.portal.notification.ui.fragment.NotificationsFragment;
import com.handy.portal.payments.ui.fragment.PaymentsFragment;
import com.handy.portal.ui.activity.MainActivity;

import org.junit.Before;
import org.junit.Test;
import org.robolectric.shadows.support.v4.SupportFragmentTestUtil;

import static org.hamcrest.CoreMatchers.instanceOf;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;

public class MainActivityFragmentTest extends RobolectricGradleTestWrapper
{
    private MainActivityFragment mFragment;

    // NOTE: Robolectric does not support nested fragments. So the methods such as onCreate() of
    // the new fragments are not called.

    @Before
    public void setUp() throws Exception
    {
        mFragment = new MainActivityFragment();
        SupportFragmentTestUtil.startFragment(mFragment, MainActivity.class);
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
        mFragment.mTabs.findViewById(R.id.tab_nav_schedule).performClick();
        Fragment currentFragment = TestUtils.getScreenFragment(mFragment.getFragmentManager());
        assertThat(currentFragment, instanceOf(ScheduledBookingsFragment.class));
        assertEquals(mFragment.getString(R.string.scheduled_jobs),
                ((AppCompatActivity) mFragment.getActivity()).getSupportActionBar().getTitle());
    }

    // TODO: add title check if newer Robolectric supports nested fragments
    @Test
    public void testRequestTab() throws Exception
    {
        mFragment.mTabs.findViewById(R.id.tab_nav_pro_requested_jobs).performClick();
        Fragment currentFragment = TestUtils.getScreenFragment(mFragment.getFragmentManager());
        assertThat(currentFragment, instanceOf(ProRequestedJobsFragment.class));
//        assertEquals(mFragment.getString(R.string.your_requests),
//                ((AppCompatActivity) mFragment.getActivity()).getSupportActionBar().getTitle());
    }

    @Test
    public void testAlertTab() throws Exception
    {
        mFragment.mTabs.findViewById(R.id.tab_nav_alert).performClick();
        Fragment currentFragment = TestUtils.getScreenFragment(mFragment.getFragmentManager());
        assertThat(currentFragment, instanceOf(NotificationsFragment.class));
    }

    @Test
    public void testRatingFeedbackNav() throws Exception
    {
        mFragment.mNavTrayLinks.findViewById(R.id.nav_link_ratings_and_feedback).performClick();
        Fragment currentFragment = TestUtils.getScreenFragment(mFragment.getFragmentManager());
        assertThat(currentFragment, instanceOf(RatingsAndFeedbackFragment.class));
    }

    @Test
    public void testPaymentsNav() throws Exception
    {
        mFragment.mNavTrayLinks.findViewById(R.id.nav_link_payments).performClick();
        Fragment currentFragment = TestUtils.getScreenFragment(mFragment.getFragmentManager());
        assertThat(currentFragment, instanceOf(PaymentsFragment.class));
    }

    @Test
    public void testReferAFriendNav() throws Exception
    {
        mFragment.mNavTrayLinks.findViewById(R.id.nav_link_refer_a_friend).performClick();
        Fragment currentFragment = TestUtils.getScreenFragment(mFragment.getFragmentManager());
        assertThat(currentFragment, instanceOf(ReferAFriendFragment.class));
    }

    @Test
    public void testAccountSettingsNav() throws Exception
    {
        mFragment.mNavTrayLinks.findViewById(R.id.nav_link_account_settings).performClick();
        Fragment currentFragment = TestUtils.getScreenFragment(mFragment.getFragmentManager());
        assertThat(currentFragment, instanceOf(AccountSettingsFragment.class));
    }

    @Test
    public void testVideoLibraryNav() throws Exception
    {
        mFragment.mNavTrayLinks.findViewById(R.id.nav_link_video_library).performClick();
        Fragment currentFragment = TestUtils.getScreenFragment(mFragment.getFragmentManager());
        assertThat(currentFragment, instanceOf(DashboardVideoLibraryFragment.class));
    }

    @Test
    public void testHelpNav() throws Exception
    {
        mFragment.mNavTrayLinks.findViewById(R.id.nav_link_help).performClick();
        Fragment currentFragment = TestUtils.getScreenFragment(mFragment.getFragmentManager());
        assertThat(currentFragment, instanceOf(HelpWebViewFragment.class));
    }

}
