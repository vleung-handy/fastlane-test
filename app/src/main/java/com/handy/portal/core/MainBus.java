package com.handy.portal.core;

import com.crashlytics.android.Crashlytics;
import com.handy.portal.logger.mixpanel.Mixpanel;

import org.greenrobot.eventbus.EventBus;

public final class MainBus extends EventBus
{
    private Mixpanel mixpanel;

    public MainBus(final Mixpanel mixpanel)
    {
        this.mixpanel = mixpanel;
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
        mixpanel.trackEvent(event);
        super.post(event);
    }
}
