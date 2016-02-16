package com.handy.portal.ui.element.dashboard;


import android.annotation.TargetApi;
import android.content.Context;
import android.os.Build;
import android.util.AttributeSet;
import android.view.View;
import android.widget.FrameLayout;

import com.handy.portal.R;
import com.handy.portal.constant.MainViewTab;
import com.handy.portal.event.HandyEvent;
import com.handy.portal.util.Utils;
import com.squareup.otto.Bus;

import javax.inject.Inject;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class DashboardOptionsPerformanceView extends FrameLayout
{
    @Inject
    Bus mBus;

    @Bind(R.id.five_star_review_text)
    View fiveStarReviewText;

    public DashboardOptionsPerformanceView(final Context context)
    {
        super(context);
        init();
    }

    public DashboardOptionsPerformanceView(final Context context, final AttributeSet attrs)
    {
        super(context, attrs);
        init();
    }

    public DashboardOptionsPerformanceView(final Context context, final AttributeSet attrs, final int defStyleAttr)
    {
        super(context, attrs, defStyleAttr);
        init();
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public DashboardOptionsPerformanceView(final Context context, final AttributeSet attrs, final int defStyleAttr, final int defStyleRes)
    {
        super(context, attrs, defStyleAttr, defStyleRes);
        init();
    }

    private void init()
    {
        Utils.inject(getContext(), this);

        inflate(getContext(), R.layout.element_dashboard_options, this);
        ButterKnife.bind(this);
    }

    @OnClick(R.id.tier_option)
    public void switchToTiers()
    {
        mBus.post(new HandyEvent.NavigateToTab(MainViewTab.DASHBOARD_TIERS));
    }

    @OnClick(R.id.reviews_option)
    public void switchToReviews()
    {
        mBus.post(new HandyEvent.NavigateToTab(MainViewTab.DASHBOARD_REVIEWS));
    }

    @OnClick(R.id.feedback_option)
    public void switchToFeedback()
    {
        mBus.post(new HandyEvent.NavigateToTab(MainViewTab.DASHBOARD_FEEDBACK));
    }


}
