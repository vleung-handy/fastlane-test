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
import com.handy.portal.manager.ProviderManager;
import com.handy.portal.model.ProviderPersonalInfo;
import com.handy.portal.model.ProviderProfile;
import com.handy.portal.model.dashboard.ProviderEvaluation;
import com.handy.portal.ui.adapter.DashboardTiersPagerAdapter;
import com.handy.portal.ui.element.dashboard.CirclePageIndicatorView;
import com.handy.portal.ui.fragment.ActionBarFragment;

import javax.inject.Inject;

import butterknife.Bind;
import butterknife.ButterKnife;

public class DashboardTiersFragment extends ActionBarFragment
{
    @Inject
    ProviderManager mProviderManager;

    @Bind(R.id.current_week_completed_jobs_text)
    TextView mCurrentWeekCompletedJobsText;
    @Bind(R.id.current_week_text)
    TextView mCurrentWeekText;
    @Bind(R.id.complete_jobs_unlock_text)
    TextView mCompleteJobsUnlockText;
    @Bind(R.id.region_tiers_view_pager)
    ViewPager mRegionTiersViewPager;
    @Bind(R.id.region_tiers_view_pager_indicator_view)
    CirclePageIndicatorView mRegionTiersIndicatorView;

    private ProviderEvaluation mEvaluation;
    private String mRegion;

    @Override
    public void onCreate(final Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        mEvaluation = (ProviderEvaluation) getArguments().getSerializable(BundleKeys.PROVIDER_EVALUATION);

        ProviderProfile providerProfile = mProviderManager.getCachedProviderProfile();
        if (providerProfile != null)
        {
            ProviderPersonalInfo personalInfo = providerProfile.getProviderPersonalInfo();
            if (personalInfo != null)
            {
                mRegion = personalInfo.getOperatingRegion();
            }
        }
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
        setOptionsMenuEnabled(true);
        setBackButtonEnabled(true);
        setActionBarTitle(R.string.tiers);

//        if (mEvaluation == null) { return; }

//        ProviderEvaluation.Rating rolling = mEvaluation.getRolling();
//        if (rolling == null || mRegion == null) { return; }

//        mCurrentWeekCompletedJobsText.setText(String.valueOf(rolling.getTotalBookingCount()));

        // TODO: set the current week
//        mCurrentWeekText.setText(getString(R.string.parentheses_formatted, DateTimeUtils.formatDateRange(
//                DateTimeUtils.SHORT_DAY_OF_WEEK_MONTH_DAY_FORMATTER,
//                rolling.getStartDate(), rolling.getEndDate())));

//        int count = rolling.getTotalBookingCount();
//        int jobsToComplete = 5; //TODO: fix this by getting region specific tier rates
//        mCompleteJobsUnlockText.setText(getResources().getQuantityString(
//                R.plurals.complete_jobs_unlock_higher_rate_formatted, count, jobsToComplete - count,
//                mRegion));

        mRegionTiersViewPager.setAdapter(new DashboardTiersPagerAdapter(getContext(), 1,
                mProviderManager.getCachedProviderProfile().getProviderPersonalInfo()));
        mRegionTiersViewPager.setClipToPadding(false);
        mRegionTiersViewPager.setPageMargin((int) getResources().getDimension(R.dimen.ratings_view_pager_margin));

        mRegionTiersIndicatorView.setViewPager(mRegionTiersViewPager);

        //TODO: Hardcoding
        mCurrentWeekCompletedJobsText.setText("2");
        mCurrentWeekText.setText("(Mon, May 9 \u2013 Sun, May 16)");
        mCompleteJobsUnlockText.setText("Complete 1 more job this week to unlock higher rates in New York!");
    }
}
