package com.handy.portal.ui.element.dashboard;


import android.annotation.TargetApi;
import android.content.Context;
import android.os.Build;
import android.support.v4.content.ContextCompat;
import android.util.AttributeSet;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.handy.portal.R;

import butterknife.Bind;
import butterknife.ButterKnife;

public class DashboardRegionTierView extends FrameLayout
{
    @Bind(R.id.region_name_text)
    TextView mRegionNameText;
    @Bind(R.id.tier_one_text)
    TextView mTierOneText;
    @Bind(R.id.tier_two_text)
    TextView mTierTwoText;
    @Bind(R.id.tier_three_text)
    TextView mTierThreeText;
    @Bind(R.id.tier_one_dot)
    ImageView mTierOneDot;
    @Bind(R.id.tier_two_dot)
    ImageView mTierTwoDot;
    @Bind(R.id.tier_three_dot)
    ImageView mTierThreeDot;
    @Bind(R.id.tier_one_jobs_text)
    TextView mTierOneJobsText;
    @Bind(R.id.tier_two_jobs_text)
    TextView mTierTwoJobsText;
    @Bind(R.id.tier_three_jobs_text)
    TextView mTierThreeJobsText;
    @Bind(R.id.tier_one_rate_text)
    TextView mTierOneRateText;
    @Bind(R.id.tier_two_rate_text)
    TextView mTierTwoRateText;
    @Bind(R.id.tier_three_rate_text)
    TextView mTierThreeRateText;


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

    public void setRegion(String regionName)
    {
        mRegionNameText.setText(regionName);
    }

    public void setTier(int tier)
    {
        // 0, 1 or 2
        if (tier == 0)
        {
            mTierOneDot.setVisibility(View.VISIBLE);
            mTierOneText.setTextColor(ContextCompat.getColor(getContext(), R.color.handy_blue));
            mTierOneJobsText.setTextColor(ContextCompat.getColor(getContext(), R.color.handy_blue));
            mTierOneRateText.setTextColor(ContextCompat.getColor(getContext(), R.color.handy_blue));
        }
        else if (tier == 1)
        {
            mTierTwoDot.setVisibility(View.VISIBLE);
            mTierTwoText.setTextColor(ContextCompat.getColor(getContext(), R.color.handy_blue));
            mTierTwoJobsText.setTextColor(ContextCompat.getColor(getContext(), R.color.handy_blue));
            mTierTwoRateText.setTextColor(ContextCompat.getColor(getContext(), R.color.handy_blue));
        }
        else
        {
            mTierThreeDot.setVisibility(View.VISIBLE);
            mTierThreeText.setTextColor(ContextCompat.getColor(getContext(), R.color.handy_blue));
            mTierThreeJobsText.setTextColor(ContextCompat.getColor(getContext(), R.color.handy_blue));
            mTierThreeRateText.setTextColor(ContextCompat.getColor(getContext(), R.color.handy_blue));
        }
    }

    public void setTiersInfo(String tierOneJobsText, String tierOneRateText,
                             String tierTwoJobsText, String tierTwoRateText,
                             String tierThreeJobsText, String tierThreeRateText)
    {
        mTierOneJobsText.setText(tierOneJobsText);
        mTierOneRateText.setText(tierOneRateText);
        mTierTwoJobsText.setText(tierTwoJobsText);
        mTierTwoRateText.setText(tierTwoRateText);
        mTierThreeJobsText.setText(tierThreeJobsText);
        mTierThreeRateText.setText(tierThreeRateText);
    }
}
