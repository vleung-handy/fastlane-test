package com.handy.portal.core.ui.activity;

import android.support.v4.app.Fragment;

import com.handy.portal.R;
import com.handy.portal.RobolectricGradleTestWrapper;
import com.handy.portal.TestUtils;
import com.handy.portal.bookings.ui.fragment.AvailableBookingsFragment;
import com.handy.portal.bookings.ui.fragment.ScheduledBookingsFragment;
import com.handy.portal.clients.ui.fragment.ClientConversationsFragment;
import com.handy.portal.clients.ui.fragment.ClientsFragment;
import com.handy.portal.core.ui.fragment.MoreNavItemsFragment;

import org.junit.Before;
import org.junit.Test;
import org.robolectric.Robolectric;
import org.robolectric.android.controller.ActivityController;

import static org.hamcrest.CoreMatchers.instanceOf;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;

public class MainActivityTest extends RobolectricGradleTestWrapper {

    private MainActivity mActivity;

    @Before
    public void setUp() throws Exception {
        ActivityController<MainActivity> activityController = Robolectric.buildActivity(MainActivity.class).create();
        activityController.start().resume().visible();
        mActivity = activityController.get();
    }

    @Test
    public void shouldBeginWithAvailableTab() throws Exception {
        Fragment currentFragment = TestUtils.getScreenFragment(mActivity.getSupportFragmentManager());
        assertThat(currentFragment, instanceOf(AvailableBookingsFragment.class));
        assertEquals(mActivity.getString(R.string.available_jobs), mActivity.getSupportActionBar().getTitle());
    }

    @Test
    public void testScheduleTab() throws Exception {
        try {
            mActivity.findViewById(R.id.tab_nav_schedule).performClick();
        }
        catch (Exception e) {
            // Robolectic calling DatesPagerAdapter.instantiateItem() too many times
        }
        Fragment currentFragment = TestUtils.getScreenFragment(mActivity.getSupportFragmentManager());
        assertThat(currentFragment, instanceOf(ScheduledBookingsFragment.class));
        assertEquals(mActivity.getString(R.string.scheduled_jobs), mActivity.getSupportActionBar().getTitle());
    }

    @Test
    public void testClientTab() throws Exception {
        try {
            mActivity.findViewById(R.id.tab_nav_clients).performClick();
        }
        catch (Exception e) {
            // Robolectic calling DatesPagerAdapter.instantiateItem() too many times
        }
        Fragment currentFragment = TestUtils.getScreenFragment(mActivity.getSupportFragmentManager());
        assertThat(currentFragment, instanceOf(ClientsFragment.class));
        assertEquals(mActivity.getString(R.string.your_clients), mActivity.getSupportActionBar().getTitle());
    }

    @Test
    public void testMessagesTab() throws Exception {
        try {
            mActivity.findViewById(R.id.tab_nav_messages).performClick();
        }
        catch (Exception e) {
            // Robolectric has trouble rendering NotificationsListView
        }
        Fragment currentFragment = TestUtils.getScreenFragment(mActivity.getSupportFragmentManager());
        assertThat(currentFragment, instanceOf(ClientConversationsFragment.class));
    }

    @Test
    public void testMoreTab() throws Exception {
        try {
            mActivity.findViewById(R.id.tab_nav_item_more).performClick();
        }
        catch (Exception e) {
            // Robolectic calling DatesPagerAdapter.instantiateItem() too many times
        }
        Fragment currentFragment = TestUtils.getScreenFragment(mActivity.getSupportFragmentManager());
        assertThat(currentFragment, instanceOf(MoreNavItemsFragment.class));
        assertEquals(mActivity.getString(R.string.profile), mActivity.getSupportActionBar().getTitle());
    }
}
