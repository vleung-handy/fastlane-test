package com.handy.portal.ui.element;

import android.view.View;

import com.handy.portal.BuildConfig;
import com.handy.portal.R;
import com.handy.portal.TestUtils;
import com.handy.portal.event.HandyEvent;
import com.handy.portal.model.Booking;
import com.squareup.otto.Bus;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.robolectric.RobolectricGradleTestRunner;
import org.robolectric.RuntimeEnvironment;
import org.robolectric.annotation.Config;

import static org.junit.Assert.assertNotNull;
import static org.mockito.Mockito.atLeastOnce;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;

@RunWith(RobolectricGradleTestRunner.class)
@Config(constants = BuildConfig.class,
        packageName = "com.handy.portal",
        sdk = 19)
public class SupportActionViewTest
{
    @Mock
    Bus mBus;

    @InjectMocks
    SupportActionView mView;

    @Before
    public void setUp() throws Exception
    {
        Booking.Action action = mock(Booking.Action.class);
        when(action.getActionName()).thenReturn(Booking.Action.ACTION_NOTIFY_EARLY);
        mView = new SupportActionView(RuntimeEnvironment.application, action);
        initMocks(this);
    }

    @Test
    public void shouldTriggerBusEventWhenClicked()
    {
        View view = mView.findViewById(R.id.support_action);
        view.performClick();

        ArgumentCaptor<HandyEvent> captor = ArgumentCaptor.forClass(HandyEvent.class);
        verify(mBus, atLeastOnce()).post(captor.capture());
        HandyEvent.SupportActionTriggered event =
                TestUtils.getBusCaptorValue(captor, HandyEvent.SupportActionTriggered.class);
        assertNotNull("SupportActionTriggered event was not post to bus", event);
    }
}
