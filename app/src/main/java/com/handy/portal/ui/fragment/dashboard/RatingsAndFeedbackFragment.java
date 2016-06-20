package com.handy.portal.ui.fragment.dashboard;


import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.handy.portal.R;
import com.handy.portal.constant.BundleKeys;
import com.handy.portal.constant.AppPage;
import com.handy.portal.data.DataManager;
import com.handy.portal.event.HandyEvent;
import com.handy.portal.event.NavigationEvent;
import com.handy.portal.event.ProviderDashboardEvent;
import com.handy.portal.logger.handylogger.LogEvent;
import com.handy.portal.logger.handylogger.model.PerformanceLog;
import com.handy.portal.manager.ProviderManager;
import com.handy.portal.model.Provider;
import com.handy.portal.model.dashboard.ProviderEvaluation;
import com.handy.portal.ui.adapter.DashboardRatingsPagerAdapter;
import com.handy.portal.ui.element.dashboard.CirclePageIndicatorView;
import com.handy.portal.ui.element.dashboard.DashboardOptionsPerformanceView;
import com.handy.portal.ui.element.dashboard.DashboardRatingsView;
import com.handy.portal.ui.element.dashboard.DashboardWelcomeView;
import com.handy.portal.ui.fragment.ActionBarFragment;
import com.squareup.otto.Subscribe;

import javax.inject.Inject;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class RatingsAndFeedbackFragment extends ActionBarFragment
{
    @Inject
    ProviderManager mProviderManager;

    @Bind(R.id.dashboard_layout)
    ViewGroup mDashboardLayout;
    @Bind(R.id.fetch_error_view)
    View mFetchErrorView;
    @Bind(R.id.fetch_error_text)
    TextView mFetchErrorTextView;
    @Bind(R.id.dashboard_welcome_view)
    DashboardWelcomeView mDashboardWelcomeView;
    @Bind(R.id.dashboard_ratings_view_pager)
    ViewPager mRatingsProPerformanceViewPager;
    @Bind(R.id.dashboard_ratings_view_pager_indicator_view)
    CirclePageIndicatorView mCirclePageIndicatorView;
    @Bind(R.id.dashboard_options_view)
    DashboardOptionsPerformanceView mDashboardOptionsPerformanceView;
    @Bind(R.id.lifetime_rating_text)
    TextView mLifetimeRatingText;
    @Bind(R.id.dashboard_rating_threshold)
    TextView mRatingThresholdText;

    ProviderEvaluation mProviderEvaluation;
    private int mNumberOfGraphsAnimated = 0;

    @Override
    protected AppPage getTab()
    {
        return AppPage.DASHBOARD;
    }

    @Nullable
    @Override
    public View onCreateView(final LayoutInflater inflater, final ViewGroup container, final Bundle savedInstanceState)
    {
        super.onCreateView(inflater, container, savedInstanceState);

        View view = inflater.inflate(R.layout.fragment_dashboard, container, false);
        ButterKnife.bind(this, view);

        return view;
    }

    @Override
    public void onResume()
    {
        super.onResume();
        setActionBar(R.string.ratings_and_feedback, false);
        getProviderEvaluation();
    }

    private void createDashboardView(ProviderEvaluation evaluation)
    {
        String welcomeString;
        Provider provider = mProviderManager.getCachedActiveProvider();
        if (provider != null && provider.getFirstName() != null)
        {
            welcomeString = getString(R.string.welcome_back_formatted, provider.getFirstName());
        }
        else
        {
            welcomeString = getString(R.string.welcome_back);
        }

        mDashboardWelcomeView.setDisplay(welcomeString,
                evaluation.getRolling().getRatingEvaluation(),
                evaluation.getRolling().getStatusColorId());

        mRatingsProPerformanceViewPager
                .setAdapter(new DashboardRatingsPagerAdapter(getContext(), evaluation, shouldAnimateFiveStarPercentageGraphs()));
        mRatingsProPerformanceViewPager.setClipToPadding(false);
        mRatingsProPerformanceViewPager.setPageMargin((int) getResources().getDimension(R.dimen.ratings_view_pager_margin));

        mRatingsProPerformanceViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener()
        {
            @Override
            public void onPageScrolled(final int position, final float positionOffset, final int positionOffsetPixels) {}

            @Override
            public void onPageSelected(final int position)
            {
                bus.post(new ProviderDashboardEvent.AnimateFiveStarPercentageGraph());
                switch (position)
                {
                    case DashboardRatingsPagerAdapter.LIFETIME_PAGE_POSITION:
                        bus.post(new LogEvent.AddLogEvent(new PerformanceLog.LifetimeRatingsShown()));
                        break;
                    case DashboardRatingsPagerAdapter.PAST_28_DAYS_PAGE_POSITION:
                        bus.post(new LogEvent.AddLogEvent(new PerformanceLog.RollingRatingsShown()));
                        break;
                }
            }

            @Override
            public void onPageScrollStateChanged(final int state) {}
        });
        mCirclePageIndicatorView.setViewPager(mRatingsProPerformanceViewPager);

        mDashboardOptionsPerformanceView.setDisplay(evaluation);

        mLifetimeRatingText.setText(getString(R.string.rating_lifetime_formatted,
                Double.toString(evaluation.getLifeTime().getProRating())));
        mRatingThresholdText.setText(getString(R.string.rating_threshold_formatted,
                Double.toString(evaluation.getDangerRatingThreshold())));
    }

    @Subscribe
    public void onReceiveProviderEvaluationSuccess(ProviderDashboardEvent.ReceiveProviderEvaluationSuccess event)
    {
        bus.post(new HandyEvent.SetLoadingOverlayVisibility(false));

        mDashboardLayout.setVisibility(View.VISIBLE);
        mFetchErrorView.setVisibility(View.GONE);

        if (event.providerEvaluation != null)
        {
            mProviderEvaluation = event.providerEvaluation;
            createDashboardView(mProviderEvaluation);
        }
    }

    @Subscribe
    public void onReceiveProviderEvaluationFailure(ProviderDashboardEvent.ReceiveProviderEvaluationError event)
    {
        bus.post(new HandyEvent.SetLoadingOverlayVisibility(false));

        mDashboardLayout.setVisibility(View.GONE);
        mFetchErrorView.setVisibility(View.VISIBLE);

        if (event.error != null && event.error.getType() == DataManager.DataManagerError.Type.NETWORK)
        {
            mFetchErrorTextView.setText(R.string.error_fetching_connectivity_issue);
        }
        else
        {
            mFetchErrorTextView.setText(R.string.error_dashboard);
        }
    }

    @Subscribe
    public void onAnimateFiveStarPercentageGraph(ProviderDashboardEvent.AnimateFiveStarPercentageGraph event)
    {
        int currentPageIndex = mRatingsProPerformanceViewPager.getCurrentItem();
        DashboardRatingsView dashboardRatingsView = (DashboardRatingsView) mRatingsProPerformanceViewPager.getChildAt(currentPageIndex);

        if (dashboardRatingsView != null && !dashboardRatingsView.hasBeenAnimated())
        {
            mNumberOfGraphsAnimated++;
            dashboardRatingsView.animateProgressBar();
        }
    }

    private boolean shouldAnimateFiveStarPercentageGraphs()
    {
        return mNumberOfGraphsAnimated == 0;
    }

    @OnClick(R.id.try_again_button)
    public void getProviderEvaluation()
    {
        if (mProviderEvaluation == null)
        {
            bus.post(new HandyEvent.SetLoadingOverlayVisibility(true));
            bus.post(new ProviderDashboardEvent.RequestProviderEvaluation());
        }
        else
        {
            createDashboardView(mProviderEvaluation);
        }
    }

    @OnClick(R.id.feedback_option)
    public void switchToFeedback()
    {
        Bundle arguments = new Bundle();
        arguments.putSerializable(BundleKeys.PROVIDER_EVALUATION, mProviderEvaluation);

        bus.post(new LogEvent.AddLogEvent(new PerformanceLog.FeedbackSelected()));
        bus.post(new NavigationEvent.NavigateToTab(AppPage.DASHBOARD_FEEDBACK, arguments, true));
    }
}
