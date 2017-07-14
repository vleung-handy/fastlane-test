package com.handy.portal.dashboard.fragment;

import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.view.ViewPager;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.crashlytics.android.Crashlytics;
import com.google.common.base.Strings;
import com.handy.portal.R;
import com.handy.portal.core.constant.BundleKeys;
import com.handy.portal.core.ui.fragment.ActionBarFragment;
import com.handy.portal.dashboard.adapter.DashboardTiersPagerAdapter;
import com.handy.portal.dashboard.model.ProviderEvaluation;
import com.handy.portal.dashboard.view.CirclePageIndicatorView;
import com.handy.portal.dashboard.view.DashboardTierViewPager;
import com.handy.portal.dashboard.view.DashboardTiersHeaderView;
import com.handy.portal.dashboard.view.DashboardTiersHelp;
import com.handy.portal.dashboard.view.DashboardViewPagerListener;
import com.handy.portal.library.util.DateTimeUtils;
import com.handy.portal.library.util.TextUtils;
import com.handy.portal.logger.handylogger.model.DashboardTiersLog;

import butterknife.BindView;
import butterknife.ButterKnife;

public class DashboardTiersFragment extends ActionBarFragment implements DashboardViewPagerListener {
    @BindView(R.id.dashboard_tiers_header_view)
    DashboardTiersHeaderView mDashboardTiersHeaderView;
    @BindView(R.id.region_tiers_view_pager)
    DashboardTierViewPager mRegionTiersViewPager;
    @BindView(R.id.region_tiers_view_pager_indicator_view)
    CirclePageIndicatorView mRegionTiersIndicatorView;
    @BindView(R.id.tiers_help_layout)
    ViewGroup mTiersHelpLayout;
    @BindView(R.id.tiers_legal_text)
    TextView mTiersLegalText;

    private ProviderEvaluation mEvaluation;
    private String mTiersTitle;
    private int mCurrentPage = 0;

    @Override
    public void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mEvaluation = (ProviderEvaluation) getArguments().getSerializable(BundleKeys.PROVIDER_EVALUATION);
        mTiersTitle = getArguments().getString(BundleKeys.TIERS_TITLE);
    }

    @Nullable
    @Override
    public View onCreateView(final LayoutInflater inflater, final ViewGroup container, final Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);

        View view = inflater.inflate(R.layout.fragment_dashboard_tiers, container, false);
        ButterKnife.bind(this, view);

        return view;
    }

    @SuppressWarnings("deprecation")
    @Override
    public void onViewCreated(final View view, @Nullable final Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        setOptionsMenuEnabled(true);
        setBackButtonEnabled(true);
        setViewPager(mRegionTiersViewPager);

        if (TextUtils.isNullOrEmpty(mTiersTitle)) { setActionBarTitle(R.string.tier); }
        else { setActionBarTitle(mTiersTitle); }

        if (mEvaluation == null || mEvaluation.getPayRates() == null) { return; }

        updateHeader();

        mRegionTiersViewPager.setAdapter(new DashboardTiersPagerAdapter(getContext(), mEvaluation));
        mRegionTiersViewPager.setClipToPadding(false);
        mRegionTiersViewPager.setPageMargin((int) getResources().getDimension(R.dimen.ratings_view_pager_margin));
        mRegionTiersIndicatorView.setViewPager(mRegionTiersViewPager);

        for (ProviderEvaluation.TiersServiceDescription tiersServiceDescription : mEvaluation.getPayRates().getTiersServiceDescriptions()) {
            DashboardTiersHelp dashboardTiersHelpView = new DashboardTiersHelp(getContext());
            dashboardTiersHelpView.setDisplay(tiersServiceDescription.getTitle(), tiersServiceDescription.getBody());
            mTiersHelpLayout.addView(dashboardTiersHelpView);
        }

        // Doesn't work when setting to textview, works programmatically
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            mTiersLegalText.setText(Html.fromHtml(getString(R.string.tiers_legal_text_html),
                    Html.FROM_HTML_MODE_LEGACY));
        }
        else {
            mTiersLegalText.setText(Html.fromHtml(getString(R.string.tiers_legal_text_html)));
        }
    }

    @Override
    public void setViewPager(final ViewPager view) {
        mRegionTiersViewPager.addOnPageChangeListener(this);
    }

    @Override
    public void setCurrentItem(final int item) {
        mCurrentPage = item;
    }

    @Override
    public void onPageScrolled(final int position, final float positionOffset,
                               final int positionOffsetPixels) { }

    @Override
    public void onPageSelected(final int position) {
        mCurrentPage = position;
        updateHeader();
    }

    @Override
    public void onPageScrollStateChanged(final int state) { }

    private void updateHeader() {
        if (mEvaluation != null && mEvaluation.getPayRates() != null &&
                mEvaluation.getPayRates().getIncentives() != null &&
                !mEvaluation.getPayRates().getIncentives().isEmpty()) {
            ProviderEvaluation.Incentive currentIncentive =
                    mEvaluation.getPayRates().getIncentives().get(mCurrentPage);

            if (!Strings.isNullOrEmpty(currentIncentive.getType())) {
                switch (currentIncentive.getType()) {
                    case ProviderEvaluation.Incentive.TIERED_TYPE:
                    case ProviderEvaluation.Incentive.HANDYMEN_TIERED_TYPE:
                        ProviderEvaluation.Rating weeklyRating = mEvaluation.getWeeklyRating();
                        mDashboardTiersHeaderView.setDisplay(
                                weeklyRating.getCompletedBookings(), DateTimeUtils.formatDateRange(
                                        DateTimeUtils.SHORT_DAY_OF_WEEK_MONTH_DAY_FORMATTER,
                                        weeklyRating.getStartDate(), weeklyRating.getEndDate()));
                        break;
                    case ProviderEvaluation.Incentive.ROLLING_TYPE:
                    case ProviderEvaluation.Incentive.HANDYMEN_ROLLING_TYPE:
                        ProviderEvaluation.Rating rollingRating = mEvaluation.getRolling();
                        mDashboardTiersHeaderView.setDisplay(
                                rollingRating.getCompletedBookings(), DateTimeUtils.formatDateRange(
                                        DateTimeUtils.SHORT_DAY_OF_WEEK_MONTH_DAY_FORMATTER,
                                        rollingRating.getStartDate(), rollingRating.getEndDate()));
                        break;

                }
            }

            bus.post(new DashboardTiersLog.TiersCardViewedLog(
                    currentIncentive.getRegionName(), currentIncentive.getServiceName()));
        }
        else {
            Crashlytics.logException(new NullPointerException("Evaluation or incentives was" +
                    "null/empty"));
        }
    }
}
