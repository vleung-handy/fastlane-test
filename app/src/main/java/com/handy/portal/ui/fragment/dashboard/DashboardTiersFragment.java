package com.handy.portal.ui.fragment.dashboard;


import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.handy.portal.R;
import com.handy.portal.constant.MainViewTab;
import com.handy.portal.manager.ProviderManager;
import com.handy.portal.model.PerformanceInfo;
import com.handy.portal.model.ProviderProfile;
import com.handy.portal.ui.fragment.ActionBarFragment;

import javax.inject.Inject;

import butterknife.Bind;
import butterknife.ButterKnife;

public class DashboardTiersFragment extends ActionBarFragment
{
    @Inject
    ProviderManager mProviderManager;

    @Bind(R.id.trailing_rating_text)
    TextView mTrailingRatingText;
    @Bind(R.id.trailing_jobs_text)
    TextView mTrailingJobsText;
    @Bind(R.id.trailing_rate_text)
    TextView mTrailingRateText;

    @Override
    protected MainViewTab getTab()
    {
        return MainViewTab.DASHBOARD_TIERS;
    }

    @Override
    public void onCreate(final Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setOptionsMenuEnabled(true);
    }

    @Nullable
    @Override
    public View onCreateView(final LayoutInflater inflater, final ViewGroup container, final Bundle savedInstanceState)
    {
        super.onCreateView(inflater, container, savedInstanceState);

        View view = inflater.inflate(R.layout.fragment_dashboard_tiers, container, false);
        ButterKnife.bind(this, view);

        return view;
    }

    @Override
    public void onViewCreated(final View view, @Nullable final Bundle savedInstanceState)
    {
        super.onViewCreated(view, savedInstanceState);
        setActionBarTitle(R.string.my_tier);
    }

    @Override
    public void onResume()
    {
        super.onResume();
        setBackButtonEnabled(true);

        ProviderProfile providerProfile = mProviderManager.getCachedProviderProfile();
        if (providerProfile != null)
        {
            PerformanceInfo performanceInfo = providerProfile.getPerformanceInfo();
            mTrailingRatingText.setText(Float.toString(performanceInfo.getTrailing28DayRating()));
            mTrailingJobsText.setText(Integer.toString(performanceInfo.getTrailing28DayJobsCount()));
            mTrailingRateText.setText(performanceInfo.getRate());
        }
    }
}
