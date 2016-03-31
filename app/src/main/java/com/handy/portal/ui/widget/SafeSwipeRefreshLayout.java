package com.handy.portal.ui.widget;

import android.content.Context;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.AttributeSet;
import android.view.MotionEvent;

import com.crashlytics.android.Crashlytics;

/*
 * A hack until Google fixes SwipeRefreshLayout:
 * https://code.google.com/p/android/issues/detail?id=64553
 * https://code.google.com/p/android/issues/detail?id=163954
 */
public class SafeSwipeRefreshLayout extends SwipeRefreshLayout
{
    public SafeSwipeRefreshLayout(final Context context)
    {
        super(context);
    }

    public SafeSwipeRefreshLayout(final Context context, final AttributeSet attrs)
    {
        super(context, attrs);
    }

    @Override
    public boolean onInterceptTouchEvent(final MotionEvent ev)
    {
        try
        {
            return super.onInterceptTouchEvent(ev);
        }
        catch (IllegalArgumentException e)
        {
            Crashlytics.log(e.getMessage());
        }
        return false;
    }

    @Override
    public boolean onTouchEvent(final MotionEvent ev)
    {
        try
        {
            return super.onTouchEvent(ev);
        }
        catch (IllegalArgumentException e)
        {
            Crashlytics.log(e.getMessage());
        }
        return false;
    }
}
