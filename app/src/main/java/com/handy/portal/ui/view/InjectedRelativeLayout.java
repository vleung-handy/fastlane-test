package com.handy.portal.ui.view;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.RelativeLayout;

import com.handy.portal.ui.activity.BaseActivity;

import butterknife.ButterKnife;

/**
 * Created by cdavis on 7/28/15.
 */
public class InjectedRelativeLayout extends RelativeLayout
{
    protected BaseActivity activity;

    public InjectedRelativeLayout(final Context context)
    {
        super(context);
        this.activity = (BaseActivity) context;
    }

    public InjectedRelativeLayout(final Context context, final AttributeSet attrs)
    {
        super(context, attrs);
        this.activity = (BaseActivity) context;
    }

    public InjectedRelativeLayout(final Context context, final AttributeSet attrs, final int defStyle)
    {
        super(context, attrs, defStyle);
        this.activity = (BaseActivity) context;
    }

    @Override
    protected void onFinishInflate()
    {
        super.onFinishInflate();
        ButterKnife.inject(this);
    }

    //convenience method to return the newly made view, unlike regular inflator which returns the root view for some reason
//    public View inflate(int resourceId)
//    {
//        View rootView = View.inflate(getContext(), resourceId, this);
//
//
//        return
//    }
//
//    public View inflate(int resourceId, ViewGroup parent)
//    {
//        View.inflate(getContext(), resourceId, parent);
//    }


}
