package com.handy.portal.ui.fragment.dashboard;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.handy.portal.R;
import com.handy.portal.constant.MainViewTab;
import com.handy.portal.event.ProviderDashboardEvent;
import com.handy.portal.manager.ProviderManager;
import com.handy.portal.model.Provider;
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
        mRatingsProPerformanceView.setDate("January 5 - February 5, 2016");

        mRatingsProPerformanceView.setJobRatings("8", "10", "15");

        mLifetimeRatingText.setText("4.8");
    }

    @Subscribe
    public void onReceiveProviderEvaluationSuccess(ProviderDashboardEvent.ReceiveProviderEvaluationSuccess event)
    {
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

    }


}
