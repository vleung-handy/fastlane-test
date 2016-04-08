package com.handy.portal.ui.view;

import android.annotation.TargetApi;
import android.content.Context;
import android.os.Build;
import android.util.AttributeSet;
import android.widget.FrameLayout;

import com.handy.portal.util.Utils;
import com.squareup.otto.Bus;

import javax.inject.Inject;

public class InjectedBusView extends FrameLayout
{
    @Inject
    protected Bus mBus;

    public InjectedBusView(final Context context)
    {
        super(context);
        init();
    }

    public InjectedBusView(final Context context, final AttributeSet attrs)
    {
        super(context, attrs);
        init();
    }

    public InjectedBusView(final Context context, final AttributeSet attrs, final int defStyleAttr)
    {
        super(context, attrs, defStyleAttr);
        init();
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public InjectedBusView(final Context context, final AttributeSet attrs, final int defStyleAttr, final int defStyleRes)
    {
        super(context, attrs, defStyleAttr, defStyleRes);
        init();
    }

    public void registerBus()
    {
        mBus.register(this);
    }

    public void unregisterBus()
    {
        mBus.unregister(this);
    }

    private void init()
    {
        Utils.inject(getContext(), this);
    }
}
