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
    public static final int PAST_28_DAYS_POSITION = 0;
    public static final int LIFETIME_POSITION = 1;

    private static final int PAGE_COUNT = 2;
    private Context mContext;
    private ProviderEvaluation mProviderEvaluation;
    private boolean mShouldAnimate;

    public DashboardRatingsPagerAdapter(final Context context, ProviderEvaluation providerEvaluation, boolean shouldAnimate)
    {
        mContext = context;
        mProviderEvaluation = providerEvaluation;
        mShouldAnimate = shouldAnimate;
    }

    @Override
    public Object instantiateItem(final ViewGroup container, final int position)
    {
        DashboardRatingsView view = new DashboardRatingsView(mContext);
        ProviderEvaluation.Rating rating;
        if (position == PAST_28_DAYS_POSITION)
        {
            rating = mProviderEvaluation.getRolling();
            view.setTitle(mContext.getString(R.string.past_28_days));
        }
        else
        {
            rating = mProviderEvaluation.getLifeTime();
            view.setTitle(mContext.getString(R.string.lifetime_rating));
        }

        view.setContentColor(rating.getStatusColorId());
        view.setDate(mContext.getString(R.string.time_interval_formatted,
                DateTimeUtils.formatMonthDateYear(rating.getStartDate()),
                DateTimeUtils.formatMonthDateYear(rating.getEndDate())));
        view.setJobRatings(rating.getFiveStarRatedBookingCount(),
                rating.getRatedBookingCount(), rating.getTotalBookingCount());
        if (rating.getTotalBookingCount() == PAST_28_DAYS_POSITION)
        {
            view.setFiveStarRatingPercentage(0.0f);
        }
        else
        {
            if (rating.getRatedBookingCount() > 0)
            {
                view.setFiveStarRatingPercentage(
                        ((float) rating.getFiveStarRatedBookingCount()) / rating.getRatedBookingCount());
            }
            else
            {
                view.setFiveStarRatingPercentage(0);
            }
        }

        if (mShouldAnimate)
        {
            view.startAnimation();
        }
        else
        {
            view.setOnResumeState();
        }

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
