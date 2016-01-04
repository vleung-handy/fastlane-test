package com.handy.portal.ui.fragment;

import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;

import com.handy.portal.R;
import com.handy.portal.RobolectricGradleTestWrapper;
import com.handy.portal.ui.activity.MainActivity;

import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.robolectric.Robolectric;
import org.robolectric.util.ActivityController;

import java.util.List;

import static org.hamcrest.CoreMatchers.instanceOf;
import static org.junit.Assert.assertEquals;
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
    public void shouldSeeOverlayTutorialWhenFirstLaunch()
    {
        // figure out how to set securepreferences in unit test
        View view = activityFragmentView.findViewById(R.id.tutorial_overlay);
        assertNotNull(view);
        assertEquals(View.VISIBLE, view.getVisibility());
    }

    @Ignore
    @Test
    public void shouldDismissTutorialOverlayAfterDismissButtonClicked()
    {
        // figure out how to set securepreferences in unit test
        View view = activityFragmentView.findViewById(R.id.tutorial_overlay);
        assertNotNull(view);
        assertEquals(View.VISIBLE, view.getVisibility());
        Button dismiss = (Button) view.findViewById(R.id.tutorial_dismiss_btn);
        dismiss.performClick();
        assertEquals(View.GONE, view.getVisibility());
    }

    @Ignore
    @Test
    public void shouldNotSeeOverlayTutorialAfterFirstLaunch()
    {
        // figure out how to set securepreferences in unit test
        View view = activityFragmentView.findViewById(R.id.tutorial_overlay);
        assertEquals(View.GONE, view.getVisibility());
    }

    @Ignore
    @Test
    public void givenNoTabSelected_whenActivityResumes_thenLoadJobsScreen() throws Exception
    {
        assertThat(getScreenFragment(), instanceOf(AvailableBookingsFragment.class));
        assertTrue(activityFragment.mJobsButton.isChecked());
    }

    public Fragment getScreenFragment()
    {
        List<Fragment> fragments = activityFragment.getActivity().getSupportFragmentManager().getFragments();
        return fragments.get(fragments.size() - 1);
    }
}
