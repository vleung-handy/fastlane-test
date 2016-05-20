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
    private String mRegion;

    private static final String BOSTON_OPERATING_REGION = "boston_ma";

    @Override
    public void onCreate(final Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        mEvaluation = (ProviderEvaluation) getArguments().getSerializable(BundleKeys.PROVIDER_EVALUATION);
        mRegion = getArguments().getString(BundleKeys.PROVIDER_OPERATING_REGION);
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
            int totalBookingCount = rolling.getTotalBookingCount();

            // TODO: Hardcoded new tiers
            mJobsText.setText(String.valueOf(totalBookingCount));
            if (totalBookingCount >= 7)
            {
                if (mRegion.equals(BOSTON_OPERATING_REGION))
                {
                    mRateText.setText("$19");
                }
                else
                {
                    mRateText.setText("$18");
                }
                mTierThreeDot.setVisibility(View.VISIBLE);
                mTierThreeText.setTextColor(ContextCompat.getColor(getContext(), R.color.handy_blue));
                mTierThreeJobsText.setTextColor(ContextCompat.getColor(getContext(), R.color.handy_blue));
                mTierThreeRateText.setTextColor(ContextCompat.getColor(getContext(), R.color.handy_blue));
            }
            else if (totalBookingCount >= 4)
            {
                if (mRegion.equals(BOSTON_OPERATING_REGION))
                {
                    mRateText.setText("$18");
                }
                else
                {
                    mRateText.setText("$17");
                }
                mTierTwoDot.setVisibility(View.VISIBLE);
                mTierTwoText.setTextColor(ContextCompat.getColor(getContext(), R.color.handy_blue));
                mTierTwoJobsText.setTextColor(ContextCompat.getColor(getContext(), R.color.handy_blue));
                mTierTwoRateText.setTextColor(ContextCompat.getColor(getContext(), R.color.handy_blue));
            }
            else if (totalBookingCount >= 1)
            {
                if (mRegion.equals(BOSTON_OPERATING_REGION))
                {
                    mRateText.setText("$17");
                }
                else
                {
                    mRateText.setText("$16");
                }
                mTierOneDot.setVisibility(View.VISIBLE);
                mTierOneText.setTextColor(ContextCompat.getColor(getContext(), R.color.handy_blue));
                mTierOneJobsText.setTextColor(ContextCompat.getColor(getContext(), R.color.handy_blue));
                mTierOneRateText.setTextColor(ContextCompat.getColor(getContext(), R.color.handy_blue));
            }
        }

        // TODO: Hardcoded new Tiers
        if (mRegion.equals(BOSTON_OPERATING_REGION))
        {
            mTierOneRateText.setText("$17");
            mTierTwoRateText.setText("$18");
            mTierThreeRateText.setText("$19");
        }
        else
        {
            mTierOneRateText.setText("$16");
            mTierTwoRateText.setText("$17");
            mTierThreeRateText.setText("$18");
        }

        mTierOneJobsText.setText("1 \u2013 3");
        mTierTwoJobsText.setText("4 \u2013 6");
        mTierThreeJobsText.setText("7+");
    }
}
