package com.handy.portal.core.ui.activity;

import android.content.Intent;

import com.handy.portal.RobolectricGradleTestWrapper;
import com.handy.portal.updater.AppUpdateEvent;
import com.handy.portal.updater.model.UpdateDetails;
import com.handy.portal.updater.ui.PleaseUpdateActivity;

import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.robolectric.Robolectric;
import org.robolectric.util.ActivityController;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertNull;
import static org.hamcrest.CoreMatchers.hasItem;
import static org.hamcrest.CoreMatchers.instanceOf;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.atLeastOnce;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.robolectric.Shadows.shadowOf;

public class BaseActivityTest extends RobolectricGradleTestWrapper
{
    private ActivityController<TestActivity> activityController;
    private BaseActivity activity;

    @Before
    public void setUp() throws Exception
    {
        activityController = Robolectric.buildActivity(TestActivity.class).create();
        activity = activityController.get();
    }

    @Test
    public void onResume_shouldPostUpdateCheckEvent() throws Exception
    {
        activityController.resume();
        ArgumentCaptor<Object> argument = ArgumentCaptor.forClass(Object.class);
        verify(activity.bus, atLeastOnce()).post(argument.capture());
        assertThat(argument.getAllValues(), hasItem(instanceOf(AppUpdateEvent.RequestUpdateCheck.class)));
    }

    @Test
    public void givenAppShouldUpdate_whenUpdateCheckReceived_thenStartUpdateActivity() throws Exception
    {
        UpdateDetails details = mock(UpdateDetails.class);
        when(details.getShouldUpdate()).thenReturn(true);
        when(details.getSuccess()).thenReturn(true);
        AppUpdateEvent.ReceiveUpdateAvailableSuccess event = new AppUpdateEvent.ReceiveUpdateAvailableSuccess(details);

        activity.getBusEventListener().onReceiveUpdateAvailableSuccess(event);

        Intent expectedIntent = new Intent(activity, PleaseUpdateActivity.class);
        Intent actualIntent = shadowOf(activity).getNextStartedActivity();

        assertEquals(actualIntent.getComponent(), expectedIntent.getComponent());
    }

    @Test
    public void givenAppShouldNotUpdate_whenUpdateCheckReceived_thenDoNotStartUpdateActivity() throws Exception
    {
        UpdateDetails details = mock(UpdateDetails.class);
        when(details.getShouldUpdate()).thenReturn(false);
        AppUpdateEvent.ReceiveUpdateAvailableSuccess event = new AppUpdateEvent.ReceiveUpdateAvailableSuccess(details);

        activity.getBusEventListener().onReceiveUpdateAvailableSuccess(event);

        assertNull(shadowOf(activity).getNextStartedActivity());
    }

    @Test
    public void givenUpdateCheckFailed_whenUpdateCheckReceived_thenDoNotStartUpdateActivity() throws Exception
    {
        AppUpdateEvent.ReceiveUpdateAvailableError event = new AppUpdateEvent.ReceiveUpdateAvailableError(null);

        activity.onReceiveUpdateAvailableError(event);

        assertNull(shadowOf(activity).getNextStartedActivity());
    }

}
