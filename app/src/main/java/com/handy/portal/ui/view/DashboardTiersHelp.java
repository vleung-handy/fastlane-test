package com.handy.portal.ui.view;


import android.annotation.TargetApi;
import android.content.Context;
import android.os.Build;
import android.text.method.LinkMovementMethod;
import android.util.AttributeSet;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.handy.portal.R;

import butterknife.Bind;
import butterknife.ButterKnife;

public class DashboardTiersHelp extends FrameLayout
{
    @Bind(R.id.tiers_help_title)
    TextView mTiersHelpTitle;
    @Bind(R.id.tiers_help_body)
    TextView mTiersHelpBody;

    public DashboardTiersHelp(final Context context)
    {
        super(context);
        init();
    }

    public DashboardTiersHelp(final Context context, final AttributeSet attrs)
    {
        super(context, attrs);
        init();
    }

    public DashboardTiersHelp(final Context context, final AttributeSet attrs, final int defStyleAttr)
    {
        super(context, attrs, defStyleAttr);
        init();
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public DashboardTiersHelp(final Context context, final AttributeSet attrs, final int defStyleAttr, final int defStyleRes)
    {
        super(context, attrs, defStyleAttr, defStyleRes);
        init();
    }

    private void init()
    {
        inflate(getContext(), R.layout.element_tiers_help, this);
        ButterKnife.bind(this);
    }

    public void setDisplay(String title, String body)
    {
        mTiersHelpTitle.setText(title);
        mTiersHelpBody.setText(body);
        mTiersHelpBody.setMovementMethod(LinkMovementMethod.getInstance()); // Clickable links
    }
}
