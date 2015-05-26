package com.handy.portal.ui.activity;

import android.content.Intent;

import com.handy.portal.RobolectricGradleTestWrapper;
import com.handy.portal.core.UpdateDetails;
import com.handy.portal.event.Event;

import org.junit.Before;
import org.junit.Test;
import org.robolectric.Robolectric;
import org.robolectric.util.ActivityController;

import static junit.framework.Assert.assertNull;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.junit.Assert.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.robolectric.Shadows.shadowOf;

public class LoginActivityTest extends RobolectricGradleTestWrapper
{
    private ActivityController<LoginActivity> activityController;
    private LoginActivity activity;

    @Before
    public void setUp() throws Exception
    {
        activityController = Robolectric.buildActivity(LoginActivity.class).create();
        activity = activityController.get();

    }

    @Test
    public void onResume_shouldPostUpdateCheckEvent() throws Exception
    {
        activityController.resume();

        verify(activity.bus).post(any(Event.UpdateCheckEvent.class));
    }

    @Test
    public void givenAppShouldUpdate_whenUpdateCheckReceived_thenStartUpdateActivity() throws Exception
    {
        UpdateDetails details = mock(UpdateDetails.class);
        when(details.getShouldUpdate()).thenReturn(true);
        Event.UpdateCheckRequestReceivedEvent event = new Event.UpdateCheckRequestReceivedEvent(details, true);

        activity.onUpdateCheckReceived(event);

        Intent expectedIntent = new Intent(activity, PleaseUpdateActivity.class);
        Intent actualIntent = shadowOf(activity).getNextStartedActivity();
        assertThat(actualIntent, equalTo(expectedIntent));
    }

    @Test
    public void givenAppShouldNotUpdate_whenUpdateCheckReceived_thenDoNotStartUpdateActivity() throws Exception
    {
        UpdateDetails details = mock(UpdateDetails.class);
        when(details.getShouldUpdate()).thenReturn(false);
        Event.UpdateCheckRequestReceivedEvent event = new Event.UpdateCheckRequestReceivedEvent(details, true);

        activity.onUpdateCheckReceived(event);

        assertNull(shadowOf(activity).getNextStartedActivity());
    }

    @Test
    public void givenUpdateCheckFailed_whenUpdateCheckReceived_thenDoNotStartUpdateActivity() throws Exception
    {
        Event.UpdateCheckRequestReceivedEvent event = new Event.UpdateCheckRequestReceivedEvent(null, false);

        activity.onUpdateCheckReceived(event);

        assertNull(shadowOf(activity).getNextStartedActivity());
    }

}
