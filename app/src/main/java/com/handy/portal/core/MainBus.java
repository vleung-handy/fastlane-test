package com.handy.portal.core;

import android.os.Handler;
import android.os.Looper;

import com.handy.portal.logger.mixpanel.Mixpanel;

import org.greenrobot.eventbus.EventBus;

public final class MainBus extends EventBus
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
        if (Looper.myLooper() == Looper.getMainLooper()) { super.register(object); }
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
