package com.handy.portal.ui.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

import butterknife.ButterKnife;

/**
 * Created by cdavis on 7/28/15.
 */
public class InjectedRelativeLayout extends RelativeLayout
{
    public InjectedRelativeLayout(final Context context)
    {
        super(context);
    }

    public InjectedRelativeLayout(final Context context, final AttributeSet attrs)
    {
        super(context, attrs);
    }

    public InjectedRelativeLayout(final Context context, final AttributeSet attrs, final int defStyle)
    {
        super(context, attrs, defStyle);
    }

    @Override
    protected void onFinishInflate()
    {
        super.onFinishInflate();
        ButterKnife.inject(this);
    }

    //convenience methods to return the newly made view, unlike regular inflator which returns the root view for some reason
    //defaults to self as parent
    protected View inflate(int resourceId)
    {
        int newChildIndex = this.getChildCount();
        View.inflate(getContext(), resourceId, this);
        return this.getChildAt(newChildIndex);
    }

    protected View inflate(int resourceId, ViewGroup parent)
    {
        int newChildIndex = parent.getChildCount();
        View.inflate(getContext(), resourceId, parent);
        return parent.getChildAt(newChildIndex);
    }
}
