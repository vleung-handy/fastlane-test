package com.handy.portal.ui.fragment;

import android.support.v4.app.Fragment;
import android.view.View;

import com.handy.portal.R;
import com.handy.portal.RobolectricGradleTestWrapper;
import com.handy.portal.ui.activity.MainActivity;

import org.junit.Before;
import org.junit.Test;
import org.robolectric.Robolectric;
import org.robolectric.util.ActivityController;

import java.util.List;

import static org.hamcrest.CoreMatchers.instanceOf;
import static org.junit.Assert.assertThat;

public class MainActivityFragmentTest extends RobolectricGradleTestWrapper
{
    private View activityFragmentView;
    private MainActivityFragment activityFragment;

    @Before
    public void setUp() throws Exception
    {
        // TODO: Test fragment in isolation. Right now, it relies on its container activity.
        ActivityController<MainActivity> activityController = Robolectric.buildActivity(MainActivity.class).create();
        activityController.start().resume().visible();

        activityFragment = (MainActivityFragment) activityController.get().getSupportFragmentManager().getFragments().get(0);
        activityFragmentView = activityFragment.getView();
    }

    @Test
    public void givenNoTabSelected_whenActivityResumes_thenLoadJobsScreen() throws Exception
    {
        assertThat(getScreenFragment(), instanceOf(AvailableBookingsFragment.class));
    }

    @Test
    public void whenScheduleButtonClicked_thenLoadScheduledBookingsFragment() throws Exception
    {
        activityFragmentView.findViewById(R.id.button_schedule).performClick();
        assertThat(getScreenFragment(), instanceOf(ScheduledBookingsFragment.class));
    }

    @Test
    public void whenProfileButtonClicked_thenLoadWebView() throws Exception
    {
        activityFragmentView.findViewById(R.id.button_profile).performClick();
        assertThat(getScreenFragment(), instanceOf(PortalWebViewFragment.class));
    }

    @Test
    public void whenHelpButtonClicked_thenLoadWebView() throws Exception
    {
        activityFragmentView.findViewById(R.id.button_help).performClick();

        assertThat(getScreenFragment(), instanceOf(PortalWebViewFragment.class));
    }

    @Test
    public void whenJobsButtonClicked_thenLoadAvailableBookingsFragment() throws Exception
    {
        activityFragmentView.findViewById(R.id.button_schedule).performClick();
        activityFragmentView.findViewById(R.id.button_jobs).performClick();

        assertThat(getScreenFragment(), instanceOf(AvailableBookingsFragment.class));
    }

    public Fragment getScreenFragment()
    {
        List<Fragment> fragments = activityFragment.getActivity().getSupportFragmentManager().getFragments();
        return fragments.get(fragments.size() - 1);
    }
}
