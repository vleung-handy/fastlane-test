package com.handy.portal.ui.fragment.dashboard;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.handy.portal.R;
import com.handy.portal.constant.BundleKeys;
import com.handy.portal.constant.MainViewTab;
import com.handy.portal.model.dashboard.ProviderEvaluation;
import com.handy.portal.ui.fragment.ActionBarFragment;

import butterknife.Bind;
import butterknife.ButterKnife;

public class DashboardTiersFragment extends ActionBarFragment
{
    @Bind(R.id.trailing_rating_text)
    TextView mTrailingRatingText;
    @Bind(R.id.trailing_jobs_text)
    TextView mTrailingJobsText;
    @Bind(R.id.trailing_rate_text)
    TextView mTrailingRateText;

    private ProviderEvaluation mEvaluation;

    @Override
    protected MainViewTab getTab()
    {
        return MainViewTab.DASHBOARD_TIERS;
    }

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
        setActionBarTitle(R.string.my_tier);

        if (mEvaluation == null) { return; }

        ProviderEvaluation.Rating rolling = mEvaluation.getRolling();
        if (rolling != null)
        {
            mTrailingRatingText.setText(String.valueOf(rolling.getProRating()));
            mTrailingJobsText.setText(String.valueOf(rolling.getTotalBookingCount()));
        }

        ProviderEvaluation.Tier tier = mEvaluation.getTier();
        if (tier != null && tier.getHourlyRateInCents() > 0)
        {
            String dollarAmount = tier.getCurrencySymbol() + tier.getHourlyRateInCents() / 100;
            mTrailingRateText.setText(dollarAmount);
        }
        else
        {
            mTrailingRateText.setText(getResources().getString(R.string.no_data));
        }
    }
}
