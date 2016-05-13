package com.handy.portal.ui.element.dashboard;

import android.annotation.TargetApi;
import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.handy.portal.R;
import com.handy.portal.constant.BundleKeys;
import com.handy.portal.constant.MainViewTab;
import com.handy.portal.event.NavigationEvent;
import com.handy.portal.logger.handylogger.LogEvent;
import com.handy.portal.logger.handylogger.model.PerformanceLog;
import com.handy.portal.model.dashboard.ProviderEvaluation;
import com.handy.portal.model.dashboard.ProviderFeedback;
import com.handy.portal.model.dashboard.ProviderRating;
import com.handy.portal.library.util.DateTimeUtils;
import com.handy.portal.library.util.Utils;
import com.squareup.otto.Bus;

import java.util.List;

import javax.inject.Inject;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class DashboardOptionsPerformanceView extends FrameLayout
{
    @Inject
    Bus mBus;

    @Bind(R.id.tier_title)
    TextView mTierTitleText;
    @Bind(R.id.tier_hourly_rate)
    TextView mTierHourlyRateText;
    @Bind(R.id.first_feedback_title)
    TextView mFirstFeedbackTitleText;
    @Bind(R.id.dashboard_first_review)
    ViewGroup mFirstReview;
    @Bind(R.id.review_text)
    TextView mReviewText;
    @Bind(R.id.five_star_reviews_count)
    TextView mReviewsCountText;
    @Bind(R.id.review_date)
    TextView mReviewDate;

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
        if (tier.getHourlyRateInCents() > 0)
        {
            String dollarAmount = tier.getCurrencySymbol() + tier.getHourlyRateInCents() / 100;
            mTierHourlyRateText.setText(dollarAmount);
        }
        else
        {
            mTierHourlyRateText.setText(getResources().getString(R.string.no_data));
        }
        List<ProviderFeedback> feedbackList = mProviderEvaluation.getProviderFeedback();
        if (feedbackList != null && feedbackList.size() > 0)
        {
            mFirstFeedbackTitleText.setText(feedbackList.get(0).getTitle());
            mFirstFeedbackTitleText.setTextColor(ContextCompat.getColor(getContext(), R.color.plumber_red));
        }
        else
        {
            mFirstFeedbackTitleText.setText(getResources().getString(R.string.none));
            mFirstFeedbackTitleText.setTextColor(ContextCompat.getColor(getContext(), R.color.tertiary_gray));
        }

        List<ProviderRating> ratings = mProviderEvaluation.getFiveStarRatingsWithComments();
        if (ratings != null && ratings.size() > 0)
        {
            mFirstReview.setVisibility(View.VISIBLE);
            ProviderRating rating = ratings.get(0);
            mReviewText.setText(ratings.get(0).getComment());
            mReviewsCountText.setText(String.valueOf(ratings.size()));
            mReviewDate.setText(DateTimeUtils.getMonthAndYear(rating.getDateRating()));
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
        arguments.putSerializable(BundleKeys.PROVIDER_EVALUATION, mProviderEvaluation);
        mBus.post(new LogEvent.AddLogEvent(new PerformanceLog.TierSelected()));
        mBus.post(new NavigationEvent.NavigateToTab(MainViewTab.DASHBOARD_TIERS, arguments, true));
    }

    @OnClick(R.id.feedback_option)
    public void switchToFeedback()
    {
        Bundle arguments = new Bundle();
        arguments.putSerializable(BundleKeys.PROVIDER_EVALUATION, mProviderEvaluation);
        mBus.post(new LogEvent.AddLogEvent(new PerformanceLog.FeedbackSelected()));
        mBus.post(new NavigationEvent.NavigateToTab(MainViewTab.DASHBOARD_FEEDBACK, arguments, true));
    }


    @OnClick(R.id.reviews_option)
    public void switchToReviews()
    {
        Bundle arguments = new Bundle();
        arguments.putSerializable(BundleKeys.PROVIDER_EVALUATION, mProviderEvaluation);
        mBus.post(new LogEvent.AddLogEvent(new PerformanceLog.FiveStarReviewsSelected()));
        mBus.post(new NavigationEvent.NavigateToTab(MainViewTab.DASHBOARD_REVIEWS, arguments, true));
    }
}
