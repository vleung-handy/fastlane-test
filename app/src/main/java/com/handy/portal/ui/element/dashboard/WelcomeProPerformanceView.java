package com.handy.portal.ui.element.dashboard;


import android.annotation.TargetApi;
import android.content.Context;
import android.os.Build;
import android.util.AttributeSet;
import android.widget.FrameLayout;

import com.handy.portal.R;
import com.handy.portal.util.Utils;

import butterknife.ButterKnife;

public class WelcomeProPerformanceView extends FrameLayout
{

    public WelcomeProPerformanceView(final Context context)
    {
        super(context);
        init(context);
    }

    public WelcomeProPerformanceView(final Context context, final AttributeSet attrs)
    {
        super(context, attrs);
        init(context);
    }

    public WelcomeProPerformanceView(final Context context, final AttributeSet attrs, final int defStyleAttr)
    {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public WelcomeProPerformanceView(final Context context, final AttributeSet attrs, final int defStyleAttr, final int defStyleRes)
    {
        super(context, attrs, defStyleAttr, defStyleRes);
        init(context);
    }

    private void init(final Context context)
    {
        Utils.inject(context, this);

        inflate(getContext(), R.layout.element_welcome_pro_performance, this);
        ButterKnife.bind(this);


    }
}
