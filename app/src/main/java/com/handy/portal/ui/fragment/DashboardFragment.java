package com.handy.portal.ui.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.handy.portal.R;
import com.handy.portal.constant.MainViewTab;
import com.handy.portal.ui.element.dashboard.DashboardOptionsPerformanceView;
import com.handy.portal.ui.element.dashboard.RatingsProPerformanceView;
import com.handy.portal.ui.element.dashboard.WelcomeProPerformanceView;

import butterknife.Bind;
import butterknife.ButterKnife;


public class DashboardFragment extends ActionBarFragment
{
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
        mWelcomeProPerformanceView.setDisplay("Welcome back, [Name]", "Things are lookin good!");

        mRatingsProPerformanceView.addItem("8", "5 star ratings");
        mRatingsProPerformanceView.addItem("10", "Rated jobs");
        mRatingsProPerformanceView.addItem("15", "Total jobs");

        mLifetimeRatingText.setText("4.8");
    }
}
