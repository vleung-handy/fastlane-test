package com.handy.portal.ui.fragment;

import android.view.View;

import com.handy.portal.R;
import com.handy.portal.RobolectricGradleTestWrapper;
import com.handy.portal.ui.activity.MainActivity;

import org.junit.Before;
import org.junit.Test;
import org.robolectric.Robolectric;
import org.robolectric.util.ActivityController;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyZeroInteractions;

public class MainActivityFragmentTest extends RobolectricGradleTestWrapper
{
    private ActivityController<MainActivity> activityController;
    private View activityFragmentView;
    private PortalWebViewFragment webViewFragmentSpy;

    @Before
    public void setUp() throws Exception
    {
        // TODO: Test fragment in isolation. Right now, it relies on its container activity.
        activityController = Robolectric.buildActivity(MainActivity.class).create();

        MainActivity mainActivity = activityController.get();
        MainActivityFragment activityFragment = (MainActivityFragment) mainActivity.getSupportFragmentManager().getFragments().get(0);
        webViewFragmentSpy = (PortalWebViewFragment) spy(mainActivity.getSupportFragmentManager().getFragments().get(1));

        resetAndStubWebViewFragmentSpy();
        activityFragmentView = activityFragment.getView();
        activityFragment.webViewFragment = webViewFragmentSpy;

        activityController.start().resume();
    }

    @Test
    public void givenNoTabSelected_whenActivityResumes_thenLoadJobsScreen() throws Exception
    {
        verify(webViewFragmentSpy).openPortalUrl(PortalWebViewFragment.Target.JOBS);
    }

    @Test
    public void givenTabAlreadySelected_whenActivityResumes_thenDoNotLoadJobsScreen() throws Exception
    {
        activityFragmentView.findViewById(R.id.button_schedule).performClick();
        resetAndStubWebViewFragmentSpy();
        activityController.pause().resume();

        verifyZeroInteractions(webViewFragmentSpy);
    }

    @Test
    public void givenTabAlreadySelected_whenSameTabClicked_thenNothingHappens() throws Exception
    {
        resetAndStubWebViewFragmentSpy();
        activityFragmentView.findViewById(R.id.button_jobs).performClick();

        verifyZeroInteractions(webViewFragmentSpy);
    }

    @Test
    public void whenScheduleButtonClicked_thenLoadScheduleScreen() throws Exception
    {
        activityFragmentView.findViewById(R.id.button_schedule).performClick();

        verify(webViewFragmentSpy).openPortalUrl(PortalWebViewFragment.Target.SCHEDULE);
    }

    @Test
    public void whenProfileButtonClicked_thenLoadProfileScreen() throws Exception
    {
        activityFragmentView.findViewById(R.id.button_profile).performClick();

        verify(webViewFragmentSpy).openPortalUrl(PortalWebViewFragment.Target.PROFILE);
    }

    @Test
    public void whenHelpButtonClicked_thenLoadHelpScreen() throws Exception
    {
        activityFragmentView.findViewById(R.id.button_help).performClick();

        verify(webViewFragmentSpy).openPortalUrl(PortalWebViewFragment.Target.HELP);
    }

    @Test
    public void whenJobsButtonClicked_thenLoadJobsScreen() throws Exception
    {
        activityFragmentView.findViewById(R.id.button_schedule).performClick();
        resetAndStubWebViewFragmentSpy();
        activityFragmentView.findViewById(R.id.button_jobs).performClick();

        verify(webViewFragmentSpy).openPortalUrl(PortalWebViewFragment.Target.JOBS);
    }

    private void resetAndStubWebViewFragmentSpy()
    {
        reset(webViewFragmentSpy);
        doNothing().when(webViewFragmentSpy).openPortalUrl(any(PortalWebViewFragment.Target.class));
    }
}
