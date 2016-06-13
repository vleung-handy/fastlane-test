package com.handy.portal.ui.element.dashboard;


import android.annotation.TargetApi;
import android.content.Context;
import android.os.Build;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.text.Html;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.util.AttributeSet;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.handy.portal.R;
import com.handy.portal.model.dashboard.ProviderEvaluation;

import butterknife.Bind;
import butterknife.ButterKnife;

public class DashboardRegionTierView extends FrameLayout
{
    @Bind(R.id.complete_jobs_unlock_text)
    TextView mCompleteJobsUnlockText;
    @Bind(R.id.region_name_service_text)
    TextView mRegionNameServiceText;
    @Bind(R.id.tier_header_text)
    TextView mTierHeaderText;
    @Bind(R.id.region_tier_middle_column)
    TextView mRegionTierMiddleColumn;
    @Bind(R.id.rate_header_text)
    TextView mRateHeaderText;
    @Bind(R.id.tiers_layout)
    LinearLayout mTiersLayout;

    private boolean mIsTwoColumns = false;


    public DashboardRegionTierView(final Context context)
    {
        super(context);
        init();
    }

    public DashboardRegionTierView(final Context context, final AttributeSet attrs)
    {
        super(context, attrs);
        init();
    }

    public DashboardRegionTierView(final Context context, final AttributeSet attrs, final int defStyleAttr)
    {
        super(context, attrs, defStyleAttr);
        init();
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public DashboardRegionTierView(final Context context, final AttributeSet attrs, final int defStyleAttr, final int defStyleRes)
    {
        super(context, attrs, defStyleAttr, defStyleRes);
        init();
    }

    private void init()
    {
        inflate(getContext(), R.layout.element_region_tier, this);
        ButterKnife.bind(this);
    }

    public void setDisplay(int tiersSize, int jobsToComplete, String regionName, String serviceName,
                           String incentiveType)
    {
        String rateAsteriskString = getResources().getString(R.string.rate_asterisk_superscript);
        Spannable rate_asterisk_spannable =
                new SpannableString(rateAsteriskString);
        rate_asterisk_spannable.setSpan(
                new ForegroundColorSpan(ContextCompat.getColor(getContext(), R.color.black)), 0,
                rateAsteriskString.length() - 1,
                Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        rate_asterisk_spannable.setSpan(
                new ForegroundColorSpan(ContextCompat.getColor(getContext(), R.color.tertiary_gray)),
                rateAsteriskString.length() - 1, rateAsteriskString.length(),
                Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        mRateHeaderText.setText(rate_asterisk_spannable);

        mRegionNameServiceText.setText(getContext().getString(R.string.colon_formatted,
                regionName, serviceName));

        if (incentiveType.equals(ProviderEvaluation.Incentive.ROLLING_TYPE))
        {
            mRegionTierMiddleColumn.setText(getResources().getString(R.string.rating));
            mTierHeaderText.setText(getResources().getString(R.string.job_type));
        }
        else if (incentiveType.equals(ProviderEvaluation.Incentive.HANDYMEN_TIERED_TYPE) ||
                incentiveType.equals(ProviderEvaluation.Incentive.HANDYMEN_ROLLING_TYPE))
        {
            mTierHeaderText.setText(getResources().getString(R.string.job_type));
            mRegionTierMiddleColumn.setVisibility(GONE);

            // Change column ratio to 3:1 if two columns
            mTierHeaderText.setLayoutParams(new LinearLayout.LayoutParams(0, LayoutParams.WRAP_CONTENT, 3f));
            mIsTwoColumns = true;
        }

        if (incentiveType.equals(ProviderEvaluation.Incentive.TIERED_TYPE))
        {
            mCompleteJobsUnlockText.setText(jobsToComplete == 0 ?
                    getResources().getString(R.string.highest_rate_this_week_formatted, regionName) :
                    Html.fromHtml(getContext().getResources()
                            .getQuantityString(R.plurals.complete_jobs_unlock_higher_rate_formatted,
                                    jobsToComplete, jobsToComplete, regionName)));
        }
        else
        {
            mCompleteJobsUnlockText.setText(getResources().getQuantityString(
                    R.plurals.rates_per_job_in_region_formatted, tiersSize, serviceName,
                    regionName));
        }
    }

    public void addTier(String incentiveType, @Nullable String rating, String tierName, int minJobs,
                        int maxJobs, String currencySymbol, int hourlyRateInCents, boolean enabled)
    {
        DashboardTierView tierView = new DashboardTierView(getContext());
        tierView.setDisplay(incentiveType, rating, tierName, minJobs, maxJobs, currencySymbol,
                hourlyRateInCents, enabled, mIsTwoColumns);
        mTiersLayout.addView(tierView);
    }
}
