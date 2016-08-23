package com.handy.portal.core;

import com.crashlytics.android.Crashlytics;

import org.greenrobot.eventbus.EventBus;

public final class MainBus extends EventBus
{
    public MainBus()
    {

    }

    @Override
    public void unregister(final Object object)
    {
        try
        {
            super.unregister(object);
        }
        catch (Exception e)
        {
            Crashlytics.logException(e);
        }
    }

    @Override
    public void post(final Object event)
    {
        super.post(event);
    }
}
