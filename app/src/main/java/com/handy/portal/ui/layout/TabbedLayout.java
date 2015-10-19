package com.handy.portal.ui.layout;

import android.annotation.TargetApi;
import android.content.Context;
import android.os.Build;
import android.util.AttributeSet;
import android.view.View;
import android.widget.RelativeLayout;

import com.handy.portal.R;

public class TabbedLayout extends RelativeLayout
{
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
        final int proposedHeight = MeasureSpec.getSize(heightMeasureSpec);
        final int actualHeight = getHeight();

        View tabs = findViewById(R.id.tabs);
        if (tabs != null)
        {
            if (actualHeight > proposedHeight)
            {
                tabs.setVisibility(GONE);
            }
            else if (actualHeight < proposedHeight)
            {
                tabs.setVisibility(VISIBLE);
            }
        }
    }
}
