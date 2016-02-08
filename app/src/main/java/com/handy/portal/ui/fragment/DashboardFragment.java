package com.handy.portal.ui.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.handy.portal.R;
import com.handy.portal.constant.MainViewTab;
import com.handy.portal.ui.element.dashboard.RatingsProPerformanceView;
import com.handy.portal.ui.element.dashboard.WelcomeProPerformanceView;

import butterknife.Bind;
import butterknife.ButterKnife;


public class DashboardFragment extends ActionBarFragment
{
    @Bind(R.id.dashboard_layout)
    ViewGroup mDashboardLayout;


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

        createDashboardView();

        return view;
    }

    @Override
    public void onResume()
    {
        super.onResume();
        setActionBar(R.string.my_performance, false);
    }

    private void createDashboardView()
    {
        mDashboardLayout.addView(new WelcomeProPerformanceView(getContext()));

        RatingsProPerformanceView ratingsProPerformanceView = new RatingsProPerformanceView(getContext());
        ratingsProPerformanceView.addItem("8", "5 star ratings");
        ratingsProPerformanceView.addItem("10", "Rated jobs");
        ratingsProPerformanceView.addItem("15", "Total jobs");
        mDashboardLayout.addView(ratingsProPerformanceView);
    }
}
