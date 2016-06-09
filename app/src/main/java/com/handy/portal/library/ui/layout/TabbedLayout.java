package com.handy.portal.library.ui.layout;

import android.annotation.TargetApi;
import android.content.Context;
import android.os.Build;
import android.util.AttributeSet;
import android.view.View;
import android.widget.RelativeLayout;

import com.handy.portal.R;

public class TabbedLayout extends RelativeLayout
{
    //This is a hack to help the other hack
    private boolean mAutoHideShowTabs = true;

    private final double HIDE_TABS_SCREEN_HEIGHT_PERCENT = .7;

    public TabbedLayout(Context context)
    {
        super(context);
    }

    public TabbedLayout(Context context, AttributeSet attrs)
    {
        super(context, attrs);
    }

    public TabbedLayout(Context context, AttributeSet attrs, int defStyleAttr)
    {
        super(context, attrs, defStyleAttr);
    }

    public void setAutoHideShowTabs(boolean autoHideShowTabs)
    {
        mAutoHideShowTabs = autoHideShowTabs;
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public TabbedLayout(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes)
    {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec)
    {
        setTabsVisibility(heightMeasureSpec);
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }

    private void setTabsVisibility(int heightMeasureSpec)
    {
        final double proposedHeight = MeasureSpec.getSize(heightMeasureSpec);
        final double actualHeight = getRootView().getHeight();

        //quick check empties and avoid divide by zero
        if (proposedHeight == 0 || actualHeight == 0) { return; }

        final View tabs = findViewById(R.id.tabs);
        if (tabs != null && mAutoHideShowTabs)
        {
            //HACK : If we lost 30% of the screen to something, likely the keyboard, hide the tabs for extra space
            if ((proposedHeight / actualHeight) < HIDE_TABS_SCREEN_HEIGHT_PERCENT)
            {
                tabs.setVisibility(GONE);
            }
            else
            {
                tabs.setVisibility(VISIBLE);
            }
        }
    }
}
