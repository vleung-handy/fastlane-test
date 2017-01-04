package com.handy.portal.dashboard.view;


import android.annotation.TargetApi;
import android.content.Context;
import android.os.Build;
import android.support.v4.content.ContextCompat;
import android.util.AttributeSet;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.handy.portal.R;
import com.handy.portal.core.manager.AppseeManager;

import butterknife.BindView;
import butterknife.ButterKnife;

public class DashboardWelcomeView extends FrameLayout
{
    @BindView(R.id.welcome_back_text)
    TextView mWelcomeBackText;
    @BindView(R.id.pro_status_text)
    TextView mProStatusText;

    public DashboardWelcomeView(final Context context)
    {
        super(context);
        init();
    }

    public DashboardWelcomeView(final Context context, final AttributeSet attrs)
    {
        super(context, attrs);
        init();
    }

    public DashboardWelcomeView(final Context context, final AttributeSet attrs, final int defStyleAttr)
    {
        super(context, attrs, defStyleAttr);
        init();
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public DashboardWelcomeView(final Context context, final AttributeSet attrs, final int defStyleAttr, final int defStyleRes)
    {
        super(context, attrs, defStyleAttr, defStyleRes);
        init();
    }

    public void setDisplay(String welcomeBackText, String proStatusText, int colorId)
    {
        mWelcomeBackText.setText(welcomeBackText);
        mProStatusText.setText(proStatusText);
        mProStatusText.setTextColor(ContextCompat.getColor(getContext(), colorId));
    }

    private void init()
    {
        inflate(getContext(), R.layout.element_welcome_pro_performance, this);
        ButterKnife.bind(this);
        AppseeManager.markViewsAsSensitive(mWelcomeBackText); //sensitive because contains name
    }
}
