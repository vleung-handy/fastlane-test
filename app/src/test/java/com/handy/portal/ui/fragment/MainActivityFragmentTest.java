package com.handy.portal.ui.fragment;

import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import com.handy.portal.R;
import com.handy.portal.RobolectricGradleTestWrapper;
import com.handy.portal.ui.activity.MainActivity;
import com.handy.portal.ui.fragment.payments.PaymentsFragment;
import com.handy.portal.ui.fragment.profile.ProfileFragment;

import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.robolectric.Robolectric;
import org.robolectric.util.ActivityController;

import java.util.List;

import static org.hamcrest.CoreMatchers.instanceOf;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;

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
    public void shouldHaveActionBar() throws Exception
    {
        assertNotNull(((AppCompatActivity) activityFragment.getActivity()).getSupportActionBar());
    }

    @Ignore
    @Test
    public void givenNoTabSelected_whenActivityResumes_thenLoadJobsScreen() throws Exception
    {
        assertThat(getScreenFragment(), instanceOf(AvailableBookingsFragment.class));
        assertTrue(activityFragment.jobsButton.isChecked());
    }

    @Ignore
    @Test
    public void whenScheduleButtonClicked_thenLoadScheduledBookingsFragment() throws Exception
    {
        activityFragmentView.findViewById(R.id.button_schedule).performClick();
        assertThat(getScreenFragment(), instanceOf(ScheduledBookingsFragment.class));
        assertTrue(activityFragment.scheduleButton.isChecked());
    }

    @Ignore
    @Test
    public void whenPaymentsButtonClicked_thenLoadPaymentsFragment() throws Exception
    {
        activityFragmentView.findViewById(R.id.button_payments).performClick();
        assertThat(getScreenFragment(), instanceOf(PaymentsFragment.class));
        assertTrue(activityFragment.paymentsButton.isChecked());
    }

    @Ignore
    @Test
    public void whenProfileButtonClicked_thenLoadProfileFragment() throws Exception
    {
        activityFragmentView.findViewById(R.id.button_profile).performClick();
        assertThat(getScreenFragment(), instanceOf(ProfileFragment.class));
        assertTrue(activityFragment.profileButton.isChecked());
    }

    @Ignore
    @Test
    public void whenHelpButtonClicked_thenLoadHelpFragment() throws Exception
    {
        activityFragmentView.findViewById(R.id.button_help).performClick();
        assertThat(getScreenFragment(), instanceOf(HelpFragment.class));
        assertTrue(activityFragment.helpButton.isChecked());
    }

    @Ignore
    @Test
    public void whenJobsButtonClicked_thenLoadAvailableBookingsFragment() throws Exception
    {
        activityFragmentView.findViewById(R.id.button_jobs).performClick();
        assertThat(getScreenFragment(), instanceOf(AvailableBookingsFragment.class));
        assertTrue(activityFragment.jobsButton.isChecked());
    }

    public Fragment getScreenFragment()
    {
        List<Fragment> fragments = activityFragment.getActivity().getSupportFragmentManager().getFragments();
        return fragments.get(fragments.size() - 1);
    }
}
