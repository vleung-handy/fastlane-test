package com.handy.portal.core;

import com.handy.portal.RobolectricGradleTestWrapper;
import com.handy.portal.data.Mixpanel;
import com.handy.portal.event.HandyEvent;
import com.squareup.otto.Subscribe;

import org.junit.Test;
import org.robolectric.shadows.ShadowLooper;

import static junit.framework.Assert.assertTrue;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.mock;

public class MainBusTest extends RobolectricGradleTestWrapper
{

    @Test
    public void testForceRegistrationOnMainLooper() throws Exception
    {
        Mixpanel mockMixpanel = mock(Mixpanel.class);
        doNothing().when(mockMixpanel).trackEvent(any());

        final MainBus bus = new MainBus(mockMixpanel);
        final boolean[] eventTriggered = {false};
        final Object object = new Object()
        {
            @Subscribe
            public void triggerEvent(HandyEvent.RequestLogin event)
            {
                eventTriggered[0] = true;
            }
        };

        // register on bus outside of the main looper
        Thread newThread = new Thread(new Runnable()
        {
            @Override
            public void run()
            {
                bus.register(object); // assumes the same bus is shared between threads
            }
        });
        newThread.start();
        newThread.join();

        ShadowLooper.idleMainLooper();

        bus.post(mock(HandyEvent.RequestLogin.class));

        assertTrue(eventTriggered[0]);
    }
}
