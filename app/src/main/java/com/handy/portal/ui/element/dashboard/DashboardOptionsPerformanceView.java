package com.handy.portal.ui.element.dashboard;


import android.annotation.TargetApi;
import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.handy.portal.R;
import com.handy.portal.constant.BundleKeys;
import com.handy.portal.constant.MainViewTab;
import com.handy.portal.event.HandyEvent;
import com.handy.portal.model.dashboard.ProviderEvaluation;
import com.handy.portal.model.dashboard.ProviderRating;
import com.handy.portal.util.DateTimeUtils;
import com.handy.portal.util.Utils;
import com.squareup.otto.Bus;

import java.util.List;

import javax.inject.Inject;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class DashboardOptionsPerformanceView extends FrameLayout
{
    @Bind(R.id.tier_title)
    TextView mTierTitleText;
    @Bind(R.id.tier_hourly_rate)
    TextView mTierHourlyRateText;
    @Bind(R.id.dashboard_first_review)
    ViewGroup mFirstReview;
    @Bind(R.id.review_text)
    TextView mReviewText;
    @Bind(R.id.review_date)
    TextView mReviewDate;

    @Inject
    Bus mBus;

    private ProviderEvaluation mProviderEvaluation;

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

        inflate(getContext(), R.layout.view_dashboard_options_performance, this);
        ButterKnife.bind(this);
    }

    public void setDisplay(ProviderEvaluation evaluation)
    {
        mProviderEvaluation = evaluation;
        ProviderEvaluation.Tier tier = mProviderEvaluation.getTier();
        mTierTitleText.setText(tier.getName());
        String dollarAmount = tier.getCurrencySymbol() + tier.getHourlyRate() / 100;
        mTierHourlyRateText.setText(dollarAmount);

        List<ProviderRating> ratings = mProviderEvaluation.getFiveStarRatings();
        if (ratings != null && ratings.size() > 0)
        {
            mFirstReview.setVisibility(View.VISIBLE);
            mReviewText.setText(ratings.get(0).getComment());
            mReviewDate.setText(DateTimeUtils.getMonthAndYear(ratings.get(0).getDateRating()));
        }
        else
        {
            mFirstReview.setVisibility(View.GONE);
        }
    }

    @OnClick(R.id.tier_option)
    public void switchToTiers()
    {
        Bundle arguments = new Bundle();
        arguments.putSerializable(BundleKeys.EVALUATION, mProviderEvaluation);
        mBus.post(new HandyEvent.NavigateToTab(MainViewTab.DASHBOARD_TIERS, arguments));
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
