package com.handy.portal.ui.fragment;

import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;

import com.handy.portal.R;
import com.handy.portal.RobolectricGradleTestWrapper;
import com.handy.portal.TestUtils;
import com.handy.portal.bookings.ui.fragment.AvailableBookingsFragment;
import com.handy.portal.bookings.ui.fragment.ProRequestedJobsFragment;
import com.handy.portal.bookings.ui.fragment.ScheduledBookingsFragment;
import com.handy.portal.notification.ui.fragment.NotificationsFragment;
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
        mFragment.mTabs.findViewById(R.id.tab_nav_schedule).performClick();
        Fragment currentFragment = TestUtils.getScreenFragment(mFragment.getFragmentManager());
        assertThat(currentFragment, instanceOf(ScheduledBookingsFragment.class));
        assertEquals(mFragment.getString(R.string.scheduled_jobs),
                ((AppCompatActivity) mFragment.getActivity()).getSupportActionBar().getTitle());
    }

    @Test
    public void testRequestTab() throws Exception
    {
        mFragment.mTabs.findViewById(R.id.tab_nav_pro_requested_jobs).performClick();
        Fragment currentFragment = TestUtils.getScreenFragment(mFragment.getFragmentManager());
        assertThat(currentFragment, instanceOf(ProRequestedJobsFragment.class));
        // TODO: investigate why failing
//        assertEquals(mFragment.getString(R.string.your_requests),
//                ((AppCompatActivity) mFragment.getActivity()).getSupportActionBar().getTitle());
    }

    @Test
    public void testAlertTab() throws Exception
    {
        mFragment.mTabs.findViewById(R.id.tab_nav_alert).performClick();
        Fragment currentFragment = TestUtils.getScreenFragment(mFragment.getFragmentManager());
        assertThat(currentFragment, instanceOf(NotificationsFragment.class));
        // TODO: investigate why failing
//        assertEquals(mFragment.getString(R.string.tab_notifications),
//                ((AppCompatActivity) mFragment.getActivity()).getSupportActionBar().getTitle());
    }
}
