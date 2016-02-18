package com.handy.portal.ui.fragment.dashboard;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.handy.portal.R;
import com.handy.portal.constant.MainViewTab;
import com.handy.portal.data.DataManager;
import com.handy.portal.event.HandyEvent;
import com.handy.portal.event.ProviderDashboardEvent;
import com.handy.portal.manager.ProviderManager;
import com.handy.portal.model.Provider;
import com.handy.portal.ui.adapter.RatingsPerformancePagerAdapter;
import com.handy.portal.ui.element.dashboard.DashboardOptionsPerformanceView;
import com.handy.portal.ui.element.dashboard.WelcomeProPerformanceView;
import com.handy.portal.ui.fragment.ActionBarFragment;
import com.handy.portal.util.DateTimeUtils;
import com.squareup.otto.Subscribe;

import javax.inject.Inject;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;


public class DashboardFragment extends ActionBarFragment
{
    @Inject
    ProviderManager mProviderManager;

    @Bind(R.id.dashboard_layout)
    ViewGroup mDashboardLayout;
    @Bind(R.id.fetch_error_view)
    View mFetchErrorView;
    @Bind(R.id.fetch_error_text)
    TextView mFetchErrorTextView;
    @Bind(R.id.welcome_pro_performance_view)
    WelcomeProPerformanceView mWelcomeProPerformanceView;
    @Bind(R.id.ratings_performance_view_pager)
    ViewPager mRatingsProPerformanceView;
    @Bind(R.id.dashboard_options_view)
    DashboardOptionsPerformanceView mDashboardOptionsPerformanceView;
    @Bind(R.id.lifetime_rating_text)
    TextView mLifetimeRatingText;

    @Bind(R.id.review_text)
    TextView mReviewText;
    @Bind(R.id.review_date)
    TextView mReviewDate;

    @Override
    protected MainViewTab getTab()
    {
        return MainViewTab.DASHBOARD;
    }

    @Override
    public void onCreate(final Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setActionBar(R.string.my_performance, false);
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
        getProviderEvaluation();
    }

    private void createDashboardView()
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

        // TODO: Everything below is placeholder stuff
        String status = "Things are lookin good!";
        mWelcomeProPerformanceView.setDisplay(welcomeString, status);
        mRatingsProPerformanceView.setAdapter(new RatingsPerformancePagerAdapter(getContext()));

        mReviewText.setText("Jane is the best! We are happy with the cleaning.");
        mReviewDate.setText("Sam, May 2015");

        mLifetimeRatingText.setText("4.8");
    }

    @Subscribe
    public void onReceiveProviderEvaluationSuccess(ProviderDashboardEvent.ReceiveProviderEvaluationSuccess event)
    {
        mDashboardLayout.setVisibility(View.VISIBLE);
        mFetchErrorView.setVisibility(View.GONE);
        bus.post(new HandyEvent.SetLoadingOverlayVisibility(false));

        /*
        ProviderEvaluation providerEvaluation = event.providerEvaluation;
        String lifetimeRating = Double.toString(providerEvaluation.getLifeTime().getProRating());
        mLifetimeRatingText.setText(lifetimeRating);
        */

        /*
        ProviderEvaluation.Rolling rollingProviderEvaluation = providerEvaluation.getRolling();
        mRatingsProPerformanceView.setJobRatings(
        Integer.toString(rollingProviderEvaluation.getFiveStarRatedBookingCount()),
            Integer.toString(rollingProviderEvaluation.getRatedBookingCount()),
             Integer.toString(rollingProviderEvaluation.getTotalBookingCount())
            );
        */

        /*
        String startDate = rollingProviderEvaluation.getStartDate().toString();
        String endDate = rollingProviderEvaluation.getEndDate().toString();
        String dateString = startDate + endDate;
        mRatingsProPerformanceView.setDate(dateString);
        */
    }

    @Subscribe
    public void onReceiveProviderEvaluationFailure(ProviderDashboardEvent.ReceiveProviderEvaluationError event)
    {
        mDashboardLayout.setVisibility(View.GONE);
        mFetchErrorView.setVisibility(View.VISIBLE);
        bus.post(new HandyEvent.SetLoadingOverlayVisibility(false));

        if (event.error != null && event.error.getType() == DataManager.DataManagerError.Type.NETWORK)
        {
            mFetchErrorTextView.setText(R.string.error_fetching_connectivity_issue);
        }
        else
        {
            mFetchErrorTextView.setText(R.string.error_dashboard);
        }
    }

    @OnClick(R.id.try_again_button)
    public void getProviderEvaluation()
    {
        bus.post(new HandyEvent.SetLoadingOverlayVisibility(true));
        bus.post(new ProviderDashboardEvent.RequestProviderEvaluation());
    }
}
