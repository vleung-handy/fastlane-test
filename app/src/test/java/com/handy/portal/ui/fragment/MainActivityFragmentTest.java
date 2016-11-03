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
import org.robolectric.shadows.support.v4.SupportFragmentTestUtil;

import static org.hamcrest.CoreMatchers.instanceOf;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;

public class MainActivityFragmentTest extends RobolectricGradleTestWrapper
{
    private MainActivityFragment mFragment;

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
        testNavigation(R.id.tab_nav_schedule, ScheduledBookingsFragment.class, R.string.scheduled_jobs);
    }

    @Test
    public void testAlertTab() throws Exception
    {
        mFragment.mTabs.findViewById(R.id.tab_nav_alert).performClick();
        Fragment currentFragment = TestUtils.getScreenFragment(mFragment.getFragmentManager());
        assertThat(currentFragment, instanceOf(NotificationsFragment.class));
        // Robolectric having trouble rendering NotificationsListView, cannot test for title
    }

    @Test
    public void testRatingFeedbackNav() throws Exception
    {
        testNavigation(R.id.nav_link_ratings_and_feedback, RatingsAndFeedbackFragment.class, R.string.ratings_and_feedback);
    }

    @Test
    public void testPaymentsNav() throws Exception
    {
        testNavigation(R.id.nav_link_payments, PaymentsFragment.class, R.string.payments);
    }

    @Test
    public void testReferAFriendNav() throws Exception
    {
        testNavigation(R.id.nav_link_refer_a_friend, ReferAFriendFragment.class, R.string.refer_a_friend);
    }

    @Test
    public void testAccountSettingsNav() throws Exception
    {
        testNavigation(R.id.nav_link_account_settings, AccountSettingsFragment.class, R.string.account_settings);
    }

    @Test
    public void testVideoLibraryNav() throws Exception
    {
        testNavigation(R.id.nav_link_video_library, DashboardVideoLibraryFragment.class, R.string.video_library);
    }

    @Test
    public void testHelpNav() throws Exception
    {
        testNavigation(R.id.nav_link_help, HelpWebViewFragment.class, R.string.help);
    }

    private void testNavigation(final int viewId, final Class<?> fragmentClass, final int stringId)
    {
        mFragment.getActivity().findViewById(viewId).performClick();
        Fragment currentFragment = TestUtils.getScreenFragment(mFragment.getFragmentManager());
        assertThat(currentFragment, instanceOf(fragmentClass));
        assertEquals(mFragment.getString(stringId),
                ((AppCompatActivity) mFragment.getActivity()).getSupportActionBar().getTitle());
    }
}
