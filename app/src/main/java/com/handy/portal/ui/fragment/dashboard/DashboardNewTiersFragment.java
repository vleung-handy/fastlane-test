package com.handy.portal.ui.fragment.dashboard;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.handy.portal.R;
import com.handy.portal.constant.BundleKeys;
import com.handy.portal.model.dashboard.ProviderEvaluation;
import com.handy.portal.ui.fragment.ActionBarFragment;

import butterknife.Bind;
import butterknife.ButterKnife;

public class DashboardNewTiersFragment extends ActionBarFragment
{
    @Bind(R.id.jobs_text)
    TextView mJobsText;
    @Bind(R.id.rate_text)
    TextView mRateText;
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

    private ProviderEvaluation mEvaluation;

    @Override
    public void onCreate(final Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        mEvaluation = (ProviderEvaluation) getArguments().getSerializable(BundleKeys.PROVIDER_EVALUATION);
    }

    @Nullable
    @Override
    public View onCreateView(final LayoutInflater inflater, final ViewGroup container, final Bundle savedInstanceState)
    {
        super.onCreateView(inflater, container, savedInstanceState);

        View view = inflater.inflate(R.layout.fragment_dashboard_new_tiers, container, false);
        ButterKnife.bind(this, view);

        return view;
    }

    @Override
    public void onViewCreated(final View view, @Nullable final Bundle savedInstanceState)
    {
        super.onViewCreated(view, savedInstanceState);
        setOptionsMenuEnabled(true);
        setBackButtonEnabled(true);
        setActionBarTitle(R.string.my_tier);

        if (mEvaluation == null) { return; }

        ProviderEvaluation.Rating rolling = mEvaluation.getRolling();
        if (rolling != null)
        {
            mJobsText.setText(String.valueOf(rolling.getTotalBookingCount()));
        }

        ProviderEvaluation.Tier tier = mEvaluation.getTier();
        if (tier != null && tier.getHourlyRateInCents() > 0)
        {
            String dollarAmount = tier.getCurrencySymbol() + tier.getHourlyRateInCents() / 100;
            mRateText.setText(dollarAmount);
        }

        // TODO: hardcoded for now
        mTierOneJobsText.setText("0 \u2013 3");
        mTierTwoJobsText.setText("4 \u2013 8");
        mTierThreeJobsText.setText("9+");

        mTierOneRateText.setText("$15");
        mTierTwoRateText.setText("$17");
        mTierThreeRateText.setText("$19");

        // TODO: Hardcoded Tier 1 selected
        mTierOneDot.setVisibility(View.VISIBLE);
        mTierOneText.setTextColor(ContextCompat.getColor(getContext(), R.color.handy_blue));
        mTierOneJobsText.setTextColor(ContextCompat.getColor(getContext(), R.color.handy_blue));
        mTierOneRateText.setTextColor(ContextCompat.getColor(getContext(), R.color.handy_blue));
    }
}
