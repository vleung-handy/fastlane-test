package com.handy.portal.ui.widget;

import android.annotation.TargetApi;
import android.content.Context;
import android.os.Build;
import android.util.AttributeSet;
import android.widget.LinearLayout;

public class TabButtonGroup extends LinearLayout
{
    private TabButton[] mTabButtons;

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
        mTabButtons = tabButtons;
        for (TabButton tabButton : tabButtons)
        {
            tabButton.setGroup(this);
            addView(tabButton);
        }
    }

    public void toggle(final TabButton tabButtonToToggle)
    {
        if (mTabButtons != null)
        {
            for (TabButton tabButton : mTabButtons)
            {
                tabButton.dim(0.5f);
            }
        }
        tabButtonToToggle.dim(1.0f);
    }
}
