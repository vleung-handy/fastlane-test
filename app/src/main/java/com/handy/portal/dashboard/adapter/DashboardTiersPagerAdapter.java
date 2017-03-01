package com.handy.portal.dashboard.adapter;

import android.content.Context;
import android.support.v4.view.PagerAdapter;
import android.view.View;
import android.view.ViewGroup;

import com.crashlytics.android.Crashlytics;
import com.handy.portal.dashboard.model.ProviderEvaluation;
import com.handy.portal.dashboard.view.DashboardRegionTierView;

import java.util.List;

public class DashboardTiersPagerAdapter extends PagerAdapter {
    private Context mContext;
    private ProviderEvaluation mProviderEvaluation;

    public DashboardTiersPagerAdapter(final Context context, ProviderEvaluation providerEvaluation) {
        mContext = context;
        mProviderEvaluation = providerEvaluation;
    }

    @Override
    public Object instantiateItem(final ViewGroup container, final int position) {
        ProviderEvaluation.Incentive currentIncentive =
                mProviderEvaluation.getPayRates().getIncentives().get(position);
        List<ProviderEvaluation.Tier> currentTiers = currentIncentive.getTiers();

        DashboardRegionTierView view = new DashboardRegionTierView(mContext);
        view.setDisplay(currentTiers, currentIncentive.getJobsUntilNextTier(),
                currentIncentive.getRegionName(), currentIncentive.getServiceName(),
                currentIncentive.getType());

        if (currentTiers != null) {
            for (int i = 0; i < currentTiers.size(); i++) {
                ProviderEvaluation.Tier currentTier = currentTiers.get(i);

                if (currentIncentive.getType().equals(ProviderEvaluation.Incentive.ROLLING_TYPE)) {
                    view.addTier(currentIncentive.getType(),
                            Double.toString(mProviderEvaluation.getRolling().getProRating()),
                            currentTier.getName(), currentTier.getJobRequirementRangeMinimum(),
                            currentTier.getJobRequirementRangeMaximum(),
                            currentIncentive.getCurrencySymbol(),
                            currentTier.getHourlyRateInCents(), false);
                }
                else if (currentIncentive.getCurrentTier() != 0 &&
                        i + 1 == currentIncentive.getCurrentTier()) {
                    view.addTier(currentIncentive.getType(), null, currentTier.getName(),
                            currentTier.getJobRequirementRangeMinimum(),
                            currentTier.getJobRequirementRangeMaximum(),
                            currentIncentive.getCurrencySymbol(),
                            currentTier.getHourlyRateInCents(), true);

                }
                else {
                    view.addTier(currentIncentive.getType(), null, currentTier.getName(),
                            currentTier.getJobRequirementRangeMinimum(),
                            currentTier.getJobRequirementRangeMaximum(),
                            currentIncentive.getCurrencySymbol(),
                            currentTier.getHourlyRateInCents(), false);
                }
            }
        }
        else {
            Crashlytics.logException(new NullPointerException("Tiers is null"));
        }

        container.addView(view);
        return view;
    }

    @Override
    public int getCount() {
        return mProviderEvaluation.getPayRates().getIncentives().size();
    }

    @Override
    public boolean isViewFromObject(final View view, final Object object) {
        return view == object;
    }

    @Override
    public void destroyItem(final ViewGroup container, final int position, final Object object) {
        container.removeView((View) object);
    }
}
