package com.handy.portal.core;

import android.os.Handler;
import android.os.Looper;

import com.squareup.otto.Bus;

final class MainBus extends Bus
{
    private final Handler mHandler = new Handler(Looper.getMainLooper());

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
}
