package com.handy.portal.ui.activity;

import android.content.Intent;

import com.handy.portal.RobolectricGradleTestWrapper;
import com.handy.portal.core.UpdateDetails;
import com.handy.portal.event.HandyEvent;

import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.robolectric.Robolectric;
import org.robolectric.util.ActivityController;

import static junit.framework.Assert.assertNull;
import static org.hamcrest.CoreMatchers.equalTo;
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
    private ActivityController<LoginActivity> activityController;
    private BaseActivity activity;

    @Before
    public void setUp() throws Exception
    {
        // TODO: Test BaseActivity directly, not one of its subclasses
        activityController = Robolectric.buildActivity(LoginActivity.class).create();
        activity = activityController.get();
    }

    @Test
    public void onResume_shouldPostUpdateCheckEvent() throws Exception
    {
        activityController.resume();
        ArgumentCaptor<Object> argument = ArgumentCaptor.forClass(Object.class);
        verify(activity.bus, atLeastOnce()).post(argument.capture());
        //TODO: Verify that only one instance of RequestUpdateCheck is in the captor value
        assertThat(argument.getAllValues(), hasItem(instanceOf(HandyEvent.RequestUpdateCheck.class)));
    }

    @Test
    public void givenAppShouldUpdate_whenUpdateCheckReceived_thenStartUpdateActivity() throws Exception
    {
        UpdateDetails details = mock(UpdateDetails.class);
        when(details.getShouldUpdate()).thenReturn(true);
        when(details.getSuccess()).thenReturn(true);
        HandyEvent.ReceiveUpdateAvailableSuccess event = new HandyEvent.ReceiveUpdateAvailableSuccess(details);

        activity.onReceiveUpdateAvailableSuccess(event);

        Intent expectedIntent = new Intent(activity, PleaseUpdateActivity.class);
        Intent actualIntent = shadowOf(activity).getNextStartedActivity();

        assertThat(actualIntent, equalTo(expectedIntent));
    }

    @Test
    public void givenAppShouldNotUpdate_whenUpdateCheckReceived_thenDoNotStartUpdateActivity() throws Exception
    {
        UpdateDetails details = mock(UpdateDetails.class);
        when(details.getShouldUpdate()).thenReturn(false);
        HandyEvent.ReceiveUpdateAvailableSuccess event = new HandyEvent.ReceiveUpdateAvailableSuccess(details);

        activity.onReceiveUpdateAvailableSuccess(event);

        assertNull(shadowOf(activity).getNextStartedActivity());
    }

    @Test
    public void givenUpdateCheckFailed_whenUpdateCheckReceived_thenDoNotStartUpdateActivity() throws Exception
    {
        HandyEvent.ReceiveUpdateAvailableError event = new HandyEvent.ReceiveUpdateAvailableError(null);

        activity.onReceiveUpdateAvailableError(event);

        assertNull(shadowOf(activity).getNextStartedActivity());
    }

}
