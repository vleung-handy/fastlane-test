package com.handy.portal.ui.widget;

import android.annotation.TargetApi;
import android.content.Context;
import android.os.Build;
import android.util.AttributeSet;
import android.widget.LinearLayout;

public class TabButtonGroup extends LinearLayout
{
    public TabButtonGroup(final Context context)
    {
        super(context);
    }

    public TabButtonGroup(final Context context, final AttributeSet attrs)
    {
        super(context, attrs);
    }

    public TabButtonGroup(final Context context, final AttributeSet attrs, final int defStyleAttr)
    {
        super(context, attrs, defStyleAttr);
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public TabButtonGroup(final Context context, final AttributeSet attrs, final int defStyleAttr, final int defStyleRes)
    {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    public void setTabs(final TabButton... tabButtons)
    {
        removeAllViews();
        for (TabButton tabButton : tabButtons)
        {
            addView(tabButton);
        }
    }
}
