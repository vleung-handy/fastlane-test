package com.handy.portal.ui.fragment;

import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;

import com.handy.portal.R;
import com.handy.portal.RobolectricGradleTestWrapper;
import com.handy.portal.constant.PrefsKey;
import com.handy.portal.ui.activity.MainActivity;

import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.robolectric.shadows.support.v4.SupportFragmentTestUtil;

import java.util.List;

import static org.hamcrest.CoreMatchers.instanceOf;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;

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
    public void shouldHaveActionBar() throws Exception
    {
        assertNotNull(((AppCompatActivity) mFragment.getActivity()).getSupportActionBar());
    }

    @Test
    public void shouldSeeTutorialOverlayWhenFirstLaunchAndBeDismissItAfterButtonClick() throws Exception
    {
        // should see it when first launch
        View tutorialOverlay = mFragment.getView().findViewById(R.id.tutorial_overlay);
        assertNotNull(tutorialOverlay);
        assertEquals(View.VISIBLE, tutorialOverlay.getVisibility());
        // should be able to dismiss it
        Button dismiss = (Button) tutorialOverlay.findViewById(R.id.tutorial_dismiss_btn);
        dismiss.performClick();
        assertEquals(View.GONE, tutorialOverlay.getVisibility());
    }

    @Test
    public void shouldNotSeeOverlayTutorialAfterFirstLaunch() throws Exception
    {
        mFragment.mPrefsManager.setBoolean(PrefsKey.NAVIGATION_TUTORIAL_SHOWN, true);
        // restart the fragment
        mFragment = new MainActivityFragment();
        SupportFragmentTestUtil.startFragment(mFragment);

        View tutorialOverlay = mFragment.getView().findViewById(R.id.tutorial_overlay);
        assertEquals(View.GONE, tutorialOverlay.getVisibility());
    }

    @Ignore
    @Test
    public void givenNoTabSelected_whenActivityResumes_thenLoadJobsScreen() throws Exception
    {
        assertThat(getScreenFragment(), instanceOf(AvailableBookingsFragment.class));
        assertTrue(mFragment.mJobsButton.isChecked());
    }

    public Fragment getScreenFragment()
    {
        List<Fragment> fragments = mFragment.getActivity().getSupportFragmentManager().getFragments();
        return fragments.get(fragments.size() - 1);
    }
}
