package com.handy.portal.ui.adapter;

import android.content.Context;
import android.support.v4.view.PagerAdapter;
import android.view.View;
import android.view.ViewGroup;

import com.handy.portal.R;
import com.handy.portal.model.dashboard.ProviderEvaluation;
import com.handy.portal.ui.element.dashboard.DashboardRatingsView;
import com.handy.portal.util.DateTimeUtils;

public class DashboardRatingsPagerAdapter extends PagerAdapter
{
    private static final int PAGE_COUNT = 2;
    private Context mContext;
    private ProviderEvaluation mProviderEvaluation;

    public DashboardRatingsPagerAdapter(final Context context, ProviderEvaluation providerEvaluation)
    {
        mContext = context;
        mProviderEvaluation = providerEvaluation;
    }

    @Override
    public Object instantiateItem(final ViewGroup container, final int position)
    {
        DashboardRatingsView view = new DashboardRatingsView(mContext);
        ProviderEvaluation.Rating rating;
        if (position == 0)
        {
            rating = mProviderEvaluation.getRolling();
            view.setTitle(mContext.getString(R.string.past_28_days));
        }
        else
        {
            rating = mProviderEvaluation.getLifeTime();
            view.setTitle(mContext.getString(R.string.lifetime_rating));
        }

        view.setDate(mContext.getString(R.string.time_interval_formatted,
                DateTimeUtils.formatDateMonthDay(rating.getStartDate()),
                DateTimeUtils.formatMonthDateYear(rating.getEndDate())));
        view.setJobRatings(rating.getFiveStarRatedBookingCount(),
                rating.getRatedBookingCount(), rating.getTotalBookingCount());
        if (rating.getTotalBookingCount() == 0)
        { view.setPercentage(0.0f); }
        else
        {
            if (rating.getRatedBookingCount() > 0)
            {
                view.setPercentage(
                        ((float) rating.getFiveStarRatedBookingCount()) / rating.getRatedBookingCount());
            }
            else
            {
                view.setPercentage(0);
            }
        }
        view.setContentColor(rating.getStatusColorId());
        container.addView(view);

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

    @Override
    public void destroyItem(final ViewGroup container, final int position, final Object object)
    {
        container.removeView((View) object);
    }
}
