package com.handy.portal.ui.element;

import android.view.View;

import com.handy.portal.R;
import com.handy.portal.RobolectricGradleTestWrapper;
import com.handy.portal.TestUtils;
import com.handy.portal.bookings.model.Booking;
import com.handy.portal.event.HandyEvent;

import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.robolectric.RuntimeEnvironment;

import static org.junit.Assert.assertNotNull;
import static org.mockito.Mockito.atLeastOnce;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class SupportActionViewTest extends RobolectricGradleTestWrapper
{
    SupportActionView mView;

    @Before
    @Override
    public void setUp() throws Exception
    {
        super.setUp();
        Booking.Action action = mock(Booking.Action.class);
        when(action.getActionName()).thenReturn(Booking.Action.ACTION_NOTIFY_EARLY);
        mView = new SupportActionView(RuntimeEnvironment.application, action);
    }

    @Test
    public void shouldTriggerBusEventWhenClicked()
    {
        View view = mView.findViewById(R.id.support_action);
        view.performClick();

        ArgumentCaptor<HandyEvent> captor = ArgumentCaptor.forClass(HandyEvent.class);
        verify(mView.mBus, atLeastOnce()).post(captor.capture());
        HandyEvent.SupportActionTriggered event =
                TestUtils.getBusCaptorValue(captor, HandyEvent.SupportActionTriggered.class);
        assertNotNull("SupportActionTriggered event was not post to bus", event);
    }
}
