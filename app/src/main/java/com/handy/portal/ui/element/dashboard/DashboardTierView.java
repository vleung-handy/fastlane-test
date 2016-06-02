package com.handy.portal.ui.element.dashboard;


import android.annotation.TargetApi;
import android.content.Context;
import android.os.Build;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.handy.portal.R;
import com.handy.portal.model.dashboard.ProviderEvaluation;

import butterknife.Bind;
import butterknife.ButterKnife;

public class DashboardTierView extends FrameLayout
{
    @Bind(R.id.tier_dot)
    ImageView mTierDot;
    @Bind(R.id.tier_text)
    TextView mTierText;
    @Bind(R.id.tier_middle_text)
    TextView mTierMiddleText;
    @Bind(R.id.tier_rate_text)
    TextView mTierRateText;
    @Bind(R.id.tier_row_layout)
    ViewGroup mTierRowLayout;

    public DashboardTierView(final Context context)
    {
        super(context);
        init();
    }

    public DashboardTierView(final Context context, final AttributeSet attrs)
    {
        super(context, attrs);
        init();
    }

    public DashboardTierView(final Context context, final AttributeSet attrs, final int defStyleAttr)
    {
        super(context, attrs, defStyleAttr);
        init();
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public DashboardTierView(final Context context, final AttributeSet attrs, final int defStyleAttr, final int defStyleRes)
    {
        super(context, attrs, defStyleAttr, defStyleRes);
        init();
    }

    private void init()
    {
        inflate(getContext(), R.layout.element_tier, this);
        ButterKnife.bind(this);
    }

    public void setDisplay(String incentiveType, @Nullable String rating, String tierName, int minJobs,
                           int maxJobs, String currencySymbol, int hourlyRateInCents,
                           boolean enabled, boolean isTwoColumns)
    {
        mTierText.setText(tierName);
        if (incentiveType.equals(ProviderEvaluation.Incentive.ROLLING_TYPE)
                && rating != null)
        {
            mTierMiddleText.setText(rating);
        }
        else if (incentiveType.equals(ProviderEvaluation.Incentive.HANDYMEN_ROLLING_TYPE) ||
                incentiveType.equals(ProviderEvaluation.Incentive.HANDYMEN_TIERED_TYPE))
        {
            mTierMiddleText.setVisibility(GONE);
        }
        else
        {
            mTierMiddleText.setText(maxJobs == 0 ? Integer.toString(minJobs) + "+" :
                    getContext().getString(R.string.dash_formatted_numbers,
                            minJobs, maxJobs));
        }
        mTierRateText.setText("" + currencySymbol + (hourlyRateInCents / 100));

        if (enabled)
        {
            mTierDot.setVisibility(View.VISIBLE);
            mTierText.setTextColor(ContextCompat.getColor(getContext(), R.color.handy_blue));
            mTierMiddleText.setTextColor(ContextCompat.getColor(getContext(), R.color.handy_blue));
            mTierRateText.setTextColor(ContextCompat.getColor(getContext(), R.color.handy_blue));
        }
        else
        {
            mTierDot.setVisibility(View.GONE);
            mTierText.setTextColor(ContextCompat.getColor(getContext(), R.color.tertiary_gray));
            mTierMiddleText.setTextColor(ContextCompat.getColor(getContext(), R.color.tertiary_gray));
            mTierRateText.setTextColor(ContextCompat.getColor(getContext(), R.color.tertiary_gray));
        }

        if (isTwoColumns)
        {
            mTierRowLayout.setLayoutParams(new LinearLayout.LayoutParams(0, LayoutParams.WRAP_CONTENT, 3f));
        }
    }
}
