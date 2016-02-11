package com.handy.portal.ui.element.dashboard;


import android.annotation.TargetApi;
import android.content.Context;
import android.os.Build;
import android.util.AttributeSet;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.handy.portal.R;

import butterknife.Bind;
import butterknife.ButterKnife;

public class WelcomeProPerformanceView extends FrameLayout
{
    @Bind(R.id.welcome_back_text)
    TextView mWelcomeBackText;
    @Bind(R.id.pro_status_text)
    TextView mProStatusText;

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

    public void setDisplay(String welcomeBackText, String proStatusText){
        mWelcomeBackText.setText(welcomeBackText);
        mProStatusText.setText(proStatusText);
    }

    private void init(final Context context)
    {
        inflate(getContext(), R.layout.element_welcome_pro_performance, this);
        ButterKnife.bind(this);
    }
}
