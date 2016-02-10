package com.handy.portal.ui.fragment.dashboard;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.handy.portal.R;
import com.handy.portal.constant.MainViewTab;
import com.handy.portal.event.HandyEvent;
import com.handy.portal.event.ProviderDashboardEvent;
import com.handy.portal.manager.ProviderManager;
import com.handy.portal.model.dashboard.ProviderEvaluation;
import com.handy.portal.ui.element.dashboard.DashboardOptionsPerformanceView;
import com.handy.portal.ui.element.dashboard.RatingsProPerformanceView;
import com.handy.portal.ui.element.dashboard.WelcomeProPerformanceView;
import com.handy.portal.ui.fragment.ActionBarFragment;
import com.squareup.otto.Subscribe;

import javax.inject.Inject;

import butterknife.Bind;
import butterknife.ButterKnife;


public class DashboardFragment extends ActionBarFragment
{
    @Inject
    ProviderManager mProviderManager;

    @Bind(R.id.dashboard_layout)
    ViewGroup mDashboardLayout;
    @Bind(R.id.welcome_pro_performance_view)
    WelcomeProPerformanceView mWelcomeProPerformanceView;
    @Bind(R.id.ratings_performance_view)
    RatingsProPerformanceView mRatingsProPerformanceView;
    @Bind(R.id.dashboard_options_view)
    DashboardOptionsPerformanceView mDashboardOptionsPerformanceView;
    @Bind(R.id.lifetime_rating_text)
    TextView mLifetimeRatingText;

    private static final String FIVE_STAR_RATINGS = "5 star ratings";
    private static final String RATED_JOBS = "Rated jobs";
    private static final String TOTAL_JOBS = "Total jobs";

    @Override
    protected MainViewTab getTab()
    {
        return MainViewTab.DASHBOARD;
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
    public void onViewCreated(final View view, @Nullable final Bundle savedInstanceState)
    {
        super.onViewCreated(view, savedInstanceState);
        createDashboardView();
    }

    @Override
    public void onResume()
    {
        super.onResume();
        setActionBar(R.string.my_performance, false);
    }

    private void createDashboardView()
    {
        String welcomeString = null;
        String providerFirstName = mProviderManager.getCachedActiveProvider().getFirstName();
        if (providerFirstName != null)
        { welcomeString = "Welcome back, " + providerFirstName; }
        else
        { welcomeString = "Welcome back"; }

        // TODO: Determine how things are via response
        String status = "Things are lookin good!";
        mWelcomeProPerformanceView.setDisplay(welcomeString, status);

    }

    @Subscribe
    public void onReceiveProviderEvaluationSuccess(ProviderDashboardEvent.ReceiveProviderEvaluationSuccess event)
    {
        ProviderEvaluation providerEvaluation = event.providerEvaluation;
        String lifetimeRating = Double.toString(providerEvaluation.getLifeTime().getProRating());
        mLifetimeRatingText.setText(lifetimeRating);

        ProviderEvaluation.Rolling rollingProviderEvaluation = providerEvaluation.getRolling();
        mRatingsProPerformanceView.addItem(Integer.toString(rollingProviderEvaluation.getFiveStarRatedBookingCount()), FIVE_STAR_RATINGS);
        mRatingsProPerformanceView.addItem(Integer.toString(rollingProviderEvaluation.getRatedBookingCount()), RATED_JOBS);
        mRatingsProPerformanceView.addItem(Integer.toString(rollingProviderEvaluation.getTotalBookingCount()), TOTAL_JOBS);

        // TODO: Determine date via response
        mRatingsProPerformanceView.setDate("January 5 - February 5, 2016");
    }

    @Subscribe
    public void onReceiveProviderEvaluationFailure(ProviderDashboardEvent.ReceiveProviderEvaluationError event)
    {
    }

    @Subscribe
    public void onReceiveProviderFiveStarRatingsSuccess(ProviderDashboardEvent.ReceiveProviderFiveStarRatingsSuccess event)
    {

    }

    @Subscribe
    public void onReceiveProviderFiveStarRatingsFailure(ProviderDashboardEvent.ReceiveProviderFiveStarRatingsError event)
    {
    }
}
