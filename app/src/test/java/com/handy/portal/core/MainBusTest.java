package com.handy.portal.core;

import com.handy.portal.RobolectricGradleTestWrapper;
import com.handy.portal.event.Event;
import com.squareup.otto.Subscribe;

import org.junit.Test;
import org.robolectric.shadows.ShadowLooper;

import static junit.framework.Assert.assertTrue;

public class MainBusTest extends RobolectricGradleTestWrapper
{

    @Test
    public void testForceRegistrationOnMainLooper() throws Exception
    {
        final MainBus bus = new MainBus();
        final boolean[] eventTriggered = {false};
        final Object object = new Object()
        {
            @Subscribe
            public void triggerEvent(Event event)
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

        bus.post(new Event());

        assertTrue(eventTriggered[0]);
    }
}
