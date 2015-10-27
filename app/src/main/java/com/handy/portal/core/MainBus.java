package com.handy.portal.core;

import android.os.Handler;
import android.os.Looper;

import com.handy.portal.analytics.Mixpanel;
import com.squareup.otto.Bus;
import com.squareup.otto.DeadEvent;

public final class MainBus extends Bus
{
    private final Handler mHandler = new Handler(Looper.getMainLooper());
    private Mixpanel mixpanel;

    public MainBus(final Mixpanel mixpanel)
    {
        this.mixpanel = mixpanel;
    }

    @Override
    public final void register(final Object object)
    {
        if (Looper.myLooper() == Looper.getMainLooper()) super.register(object);
        else
        {
            mHandler.post(new Runnable()
            {
                @Override
                public void run()
                {
                    MainBus.super.register(object);
                }
            });
        }
    }

    @Override
    public void post(final Object event)
    {
        if (Looper.myLooper() == Looper.getMainLooper())
        {
            if (!(event instanceof DeadEvent))
            {
                mixpanel.trackEvent(event); // side effect
            }
            super.post(event);
        }
        else
        {
            mHandler.post(new Runnable()
            {
                @Override
                public void run()
                {
                    MainBus.super.post(event);
                }
            });
        }
    }

}
