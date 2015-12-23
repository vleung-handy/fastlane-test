package com.handy.portal.ui.element;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.RelativeLayout;

import butterknife.ButterKnife;

/**
 * Created by cdavis on 6/2/15.
 */
public class LoadingOverlayView extends RelativeLayout
{
    public LoadingOverlayView(final Context context)
    {
        super(context);
    }

    public LoadingOverlayView(final Context context, final AttributeSet attrs)
    {
        super(context, attrs);
    }

    public LoadingOverlayView(final Context context, final AttributeSet attrs, final int defStyle)
    {
        super(context, attrs, defStyle);
    }

    public void init()
    {
        ButterKnife.bind(this);
        this.setVisibility(GONE);
    }

    public void setOverlayVisibility(boolean isVisible)
    {
        this.setVisibility(isVisible ? VISIBLE : GONE);
    }

}
