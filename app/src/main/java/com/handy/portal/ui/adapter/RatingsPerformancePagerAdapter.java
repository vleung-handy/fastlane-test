package com.handy.portal.ui.adapter;

import android.content.Context;
import android.support.v4.view.PagerAdapter;
import android.view.View;
import android.view.ViewGroup;

import com.handy.portal.R;
import com.handy.portal.model.dashboard.ProviderEvaluation;
import com.handy.portal.ui.element.dashboard.RatingsProPerformanceView;
import com.handy.portal.util.DateTimeUtils;

public class RatingsPerformancePagerAdapter extends PagerAdapter
{
    private static final int PAGE_COUNT = 2;
    private Context mContext;
    private ProviderEvaluation mProviderEvaluation;

    public RatingsPerformancePagerAdapter(final Context context, ProviderEvaluation providerEvaluation)
    {
        mContext = context;
        mProviderEvaluation = providerEvaluation;
    }

    @Override
    public Object instantiateItem(final ViewGroup container, final int position)
    {
        RatingsProPerformanceView view = new RatingsProPerformanceView(mContext);
        // TODO: consider combine the code below once the final api is decided
        if (position == 0)
        {
            ProviderEvaluation.Rolling rolling = mProviderEvaluation.getRolling();
            view.setTitle(mContext.getString(R.string.past_28_days));
            view.setDate(mContext.getString(R.string.time_interval_formatted,
                    DateTimeUtils.formatDateMonthDay(rolling.getStartDate()),
                    DateTimeUtils.formatMonthDateYear(rolling.getEndDate())));
            view.setJobRatings(rolling.getFiveStarRatedBookingCount(),
                    rolling.getRatedBookingCount(), rolling.getTotalBookingCount());
            view.setPercentage(
                    ((float) rolling.getFiveStarRatedBookingCount()) / rolling.getTotalBookingCount());
            container.addView(view);
        }
        else if (position == 1)
        {
            ProviderEvaluation.LifeTime lifeTime = mProviderEvaluation.getLifeTime();
            view.setTitle(mContext.getString(R.string.lifetime_rating));
            view.setDate(mContext.getString(R.string.time_interval_formatted,
                    DateTimeUtils.formatDateMonthDay(lifeTime.getStartDate()),
                    DateTimeUtils.formatMonthDateYear(lifeTime.getEndDate())));
            view.setJobRatings(lifeTime.getFiveStarRatedBookingCount(),
                    lifeTime.getRatedBookingCount(), lifeTime.getTotalBookingCount());
            view.setPercentage(
                    ((float) lifeTime.getFiveStarRatedBookingCount()) / lifeTime.getTotalBookingCount());
            container.addView(view);
        }
        return view;
    }

    @Override
    public int getCount()
    {
        return PAGE_COUNT;
    }

    @Override
    public boolean isViewFromObject(final View view, final Object object)
    {
        return view == object;
    }
}
